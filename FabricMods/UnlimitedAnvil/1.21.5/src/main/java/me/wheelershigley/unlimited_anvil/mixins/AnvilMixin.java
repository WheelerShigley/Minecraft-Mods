package me.wheelershigley.unlimited_anvil.mixins;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import me.wheelershigley.unlimited_anvil.UnlimitedAnvil;
import me.wheelershigley.unlimited_anvil.helpers.EnchantmentsHelper;
import me.wheelershigley.unlimited_anvil.helpers.ExperienceHelper;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static me.wheelershigley.unlimited_anvil.helpers.ExperienceHelper.*;

@Mixin(value = AnvilScreenHandler.class, priority = 800)
public abstract class AnvilMixin extends ForgingScreenHandler {
    private static final int MAX_COST = 40;

    public AnvilMixin(
            @Nullable ScreenHandlerType<?> type,
            int syncId,
            PlayerInventory playerInventory,
            ScreenHandlerContext context,
            ForgingSlotsManager forgingSlotsManager
    ) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    @Shadow
    @Final
    private Property levelCost;
    @Shadow
    @Nullable
    private String newItemName;
    @Shadow
    private boolean keepSecondSlot;
    @Shadow
    private int repairItemUsage;

    @Shadow
    public static int getNextCost(int cost) {
        return 1;
    }

    @Shadow public abstract int getLevelCost();

    @Inject(
        method = "updateResult",
        at = @At("TAIL")
    )
    public void updateResult(CallbackInfo ci) {
        /*Modified Vanilla Implementation*/ {
            ItemStack primaryInput = this.input.getStack(0);
            this.keepSecondSlot = false;
            this.levelCost.set(1);
            int i = 0;
            long sumRepairCost = 0L;
            int j = 0;
            if( !primaryInput.isEmpty() && EnchantmentHelper.canHaveEnchantments(primaryInput) ) {
                ItemStack modifyableStack = primaryInput.copy();
                ItemStack secondaryInput = this.input.getStack(1);
                ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(EnchantmentHelper.getEnchantments(modifyableStack));
                sumRepairCost += (long)(Integer)primaryInput.getOrDefault(DataComponentTypes.REPAIR_COST, 0) + (long)(Integer)secondaryInput.getOrDefault(DataComponentTypes.REPAIR_COST, 0);
                this.repairItemUsage = 0;
                int resultDamage;
                if (!secondaryInput.isEmpty()) {
                    boolean useStoredEnchantments = secondaryInput.contains(DataComponentTypes.STORED_ENCHANTMENTS);
                    int resultDamageIndex;
                    int updatedDamage;
                    if (modifyableStack.isDamageable() && primaryInput.canRepairWith(secondaryInput)) {
                        resultDamage = Math.min(modifyableStack.getDamage(), modifyableStack.getMaxDamage() / 4);
                        if (resultDamage <= 0) {
                            this.output.setStack(0, ItemStack.EMPTY);
                            this.levelCost.set(0);
                            return;
                        }

                        for(resultDamageIndex = 0; 0 < resultDamage && resultDamageIndex < secondaryInput.getCount(); ++resultDamageIndex) {
                            updatedDamage = modifyableStack.getDamage() - resultDamage;
                            modifyableStack.setDamage(updatedDamage);
                            ++i;
                            resultDamage = Math.min(modifyableStack.getDamage(), modifyableStack.getMaxDamage() / 4);
                        }

                        this.repairItemUsage = resultDamageIndex;
                    } else {
                        if (!useStoredEnchantments && (!modifyableStack.isOf(secondaryInput.getItem()) || !modifyableStack.isDamageable())) {
                            this.output.setStack(0, ItemStack.EMPTY);
                            this.levelCost.set(0);
                            return;
                        }

                        if (modifyableStack.isDamageable() && !useStoredEnchantments) {
                            resultDamage = primaryInput.getMaxDamage() - primaryInput.getDamage();
                            resultDamageIndex = secondaryInput.getMaxDamage() - secondaryInput.getDamage();
                            updatedDamage = resultDamageIndex + modifyableStack.getMaxDamage() * 12 / 100;
                            int finalDamage = resultDamage + updatedDamage;
                            int reversedFinalDamage = modifyableStack.getMaxDamage() - finalDamage;
                            reversedFinalDamage = Math.max(0, reversedFinalDamage);

                            if( reversedFinalDamage < modifyableStack.getDamage() ) {
                                modifyableStack.setDamage(reversedFinalDamage);
                                i += 2;
                            }
                        }

                        ItemEnchantmentsComponent itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(secondaryInput);
                        boolean bl2 = false;
                        boolean bl3 = false;

                        for(Object2IntMap.Entry< RegistryEntry<Enchantment> > registryEntryEntry : itemEnchantmentsComponent.getEnchantmentEntries() ) {
                            RegistryEntry<Enchantment> registryEntry = registryEntryEntry.getKey();
                            int currentLevel = builder.getLevel(registryEntry);
                            int advisedLevel = registryEntryEntry.getIntValue();
                            advisedLevel = currentLevel == advisedLevel ? advisedLevel + 1 : Math.max(advisedLevel, currentLevel);
                            Enchantment enchantment = registryEntry.value();
                            boolean isAcceptableItem = enchantment.isAcceptableItem(primaryInput);
                            if( this.player.isInCreativeMode() || primaryInput.isOf(Items.ENCHANTED_BOOK) ) {
                                isAcceptableItem = true;
                            }

                            for(RegistryEntry<Enchantment> enchantmentRegistryEntry : builder.getEnchantments() ) {
                                if(
                                    !enchantmentRegistryEntry.equals(registryEntry)
                                    && !Enchantment.canBeCombined(registryEntry, enchantmentRegistryEntry)
                                ) {
                                    isAcceptableItem = false;
                                    ++i;
                                }
                            }

                            if(!isAcceptableItem) {
                                bl3 = true;
                            } else {
                                bl2 = true;

                                //custom maximum levels
                                int maximum_effective_level = EnchantmentsHelper.getMaximumEffectiveLevel(
                                    Identifier.of(
                                        enchantment.description().getString().toLowerCase().replace(' ', '_')
                                    )
                                );
                                if (maximum_effective_level < advisedLevel) {
                                    advisedLevel = maximum_effective_level;
                                }

                                builder.set(registryEntry, advisedLevel);
                                int s = enchantment.getAnvilCost();
                                if (useStoredEnchantments) {
                                    s = Math.max(1, s / 2);
                                }

                                i += s * advisedLevel;
//                                if (itemStack.getCount() > 1) {
//                                    i = MAX_COST;
//                                }

                                //allow custom stack-sizes
                                if( !output.getStack(0).getItem().equals(Items.AIR) ) {
                                    output.getStack(0).setCount(
                                        Math.min(
                                            primaryInput.getCount(),
                                            secondaryInput.getCount()
                                        )
                                    );
                                }
                            }
                        }

                        if(bl3 && !bl2) {
                            this.output.setStack(0, ItemStack.EMPTY);
                            this.levelCost.set(0);
                            return;
                        }
                    }
                }

                if( this.newItemName != null && !StringHelper.isBlank(this.newItemName) ) {
                    if(  !this.newItemName.equals( primaryInput.getName().getString() )  ) {
                        j = 1;
                        i += j;
                        modifyableStack.set( DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName) );
                    }
                } else if( primaryInput.contains(DataComponentTypes.CUSTOM_NAME) ) {
                    j = 1;
                    i += j;
                    modifyableStack.remove(DataComponentTypes.CUSTOM_NAME);
                }

                int t = i <= 0 ? 0 : (int) MathHelper.clamp(sumRepairCost + (long)i, 0L, 2147483647L);
                this.levelCost.set(t);
                if (i <= 0) {
                    modifyableStack = ItemStack.EMPTY;
                }

                if (j == i && 0 < j) {
//                    if (this.levelCost.get() >= MAX_COST) {
//                        this.levelCost.set(MAX_COST-1);
//                    }

                    this.keepSecondSlot = true;
                }

//                if(
//                    this.levelCost.get() >= MAX_COST &&
//                        !this.player.isInCreativeMode()
//                ) {
//                    modifyableStack = ItemStack.EMPTY;
//                }

                if (!modifyableStack.isEmpty()) {
                    resultDamage = (Integer)modifyableStack.getOrDefault(DataComponentTypes.REPAIR_COST, 0);
                    if (resultDamage < (Integer)secondaryInput.getOrDefault(DataComponentTypes.REPAIR_COST, 0)) {
                        resultDamage = (Integer)secondaryInput.getOrDefault(DataComponentTypes.REPAIR_COST, 0);
                    }

                    if (j != i || j == 0) {
                        resultDamage = getNextCost(resultDamage);
                    }

                    modifyableStack.set(DataComponentTypes.REPAIR_COST, resultDamage);
                    EnchantmentHelper.set(modifyableStack, builder.build());
                }

                this.output.setStack(0, modifyableStack);
//                this.sendContentUpdates();
            } else {
                this.output.setStack(0, ItemStack.EMPTY);
                this.levelCost.set(0);
            }
        }
        /*Custom Behavior*/ {
            //quadruple level-cost
            int final_level_cost = EnchantmentsHelper.getEnchantingCost( this.output.getStack(0) );
            boolean has_new_custom_name = (
                /*Has some custom name*/
                this.newItemName != null
                && !StringHelper.isBlank(this.newItemName)
                /*Custom name is new*/
                && !Objects.equals( this.input.getStack(0).getCustomName(), this.output.getStack(0).getCustomName() )
            );

            if(has_new_custom_name) {
                ++final_level_cost;
            }

            //remove compounding cost
            ItemStack output = this.output.getStack(0);
            output.remove(DataComponentTypes.REPAIR_COST);
            this.output.setStack(0, output);

            //send result to user
            this.levelCost.set(final_level_cost);
            this.sendContentUpdates();
        }
    }

    @Inject(
        method = "onTakeOutput",
        at = @At("HEAD"),
        cancellable = true
    )
    protected void onTakeOutput(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if( !player.isInCreativeMode() ) {
            int cost = levelToPoints( this.levelCost.get() );
            takeExperience(player, cost);
        }

        if(this.repairItemUsage > 0) {
            ItemStack itemStack = this.input.getStack(1);
            if( !itemStack.isEmpty() && this.repairItemUsage < itemStack.getCount() ) {
                itemStack.decrement(this.repairItemUsage);
                this.input.setStack(1, itemStack);
            } else {
                this.input.setStack(1, ItemStack.EMPTY);
            }
        } else if(!this.keepSecondSlot) {
            this.input.setStack(1, ItemStack.EMPTY);
        }

        this.levelCost.set(0);
        this.input.setStack(0, ItemStack.EMPTY);
        this.context.run(
            (world, pos) -> {
                BlockState blockState = world.getBlockState(pos);
                if(
                    !player.isInCreativeMode()
                    && blockState.isIn(BlockTags.ANVIL)
                    && player.getRandom().nextFloat() < 0.12F
                ) {
                    BlockState blockState2 = AnvilBlock.getLandingState(blockState);
                    if(blockState2 == null) {
                        world.removeBlock(pos, false);
                        world.syncWorldEvent(1029, pos, 0);
                    } else {
                        world.setBlockState(pos, blockState2, 2);
                        world.syncWorldEvent(1030, pos, 0);
                    }
                } else {
                    world.syncWorldEvent(1030, pos, 0);
                }
            }
        );

        ci.cancel();
    }
}