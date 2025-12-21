package me.wheelershigley.unlimited_anvil.mixins;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import me.wheelershigley.unlimited_anvil.UnlimitedAnvil;
import me.wheelershigley.unlimited_anvil.helpers.EnchantmentsHelper;
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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static me.wheelershigley.unlimited_anvil.helpers.EnchantmentsHelper.getEnchantingCost;
import static me.wheelershigley.unlimited_anvil.helpers.ExperienceHelper.*;

@Debug(export = true)
@Mixin(
    value = AnvilScreenHandler.class,
    priority = 800
)
public abstract class AnvilMixin extends ForgingScreenHandler {
    private static final int MAX_COST = 40; //TODO

    public AnvilMixin(
        @Nullable ScreenHandlerType<?> type,
        int syncId,
        PlayerInventory playerInventory,
        ScreenHandlerContext context,
        ForgingSlotsManager forgingSlotsManager
    ) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    @Shadow @Final private Property levelCost;
    @Shadow @Nullable private String newItemName;
    @Shadow private boolean keepSecondSlot;
    @Shadow private int repairItemUsage;

    @Shadow
    public static int getNextCost(int cost) {
        return 1;
    }

    @Shadow
    public abstract int getLevelCost();

    /**
     * @author Wheeler-Shigley
     * @reason changed Maximum Levels (without modifying Enchantment functions)
     */
    @Inject(
        method = "updateResult",
        at = @At("HEAD"),
        cancellable = true
    )
    public void updateResult(CallbackInfo ci) {
//        ItemStack primaryInput = this.input.getStack(0);
//        ItemStack secondaryInput = this.input.getStack(1);
        ItemStack output = this.output.getStack(0);

        modifiedVanillaUpdateResult();
        if( output.getItem().equals(Items.AIR) ) {
            this.sendContentUpdates();
            ci.cancel();
            return;
        }

        //double level-cost
        int final_level_cost = 2*getEnchantingCost(output);
        boolean has_new_custom_name = (
            /*Has some custom name*/
            this.newItemName != null
            && !StringHelper.isBlank(this.newItemName)
            /*Custom name is new*/
            && !Objects.equals( this.input.getStack(0).getCustomName(), output.getCustomName() )
        );
        if(has_new_custom_name) {
            ++final_level_cost;
        }
        this.levelCost.set(final_level_cost);

        //remove compounding cost
        output.remove(DataComponentTypes.REPAIR_COST);
        this.output.setStack(0, output);

        //return
        this.sendContentUpdates();
        ci.cancel();
    }

    @Unique
    private void modifiedVanillaUpdateResult() {
        ItemStack primaryInput = this.input.getStack(0);
        ItemStack secondaryInput = this.input.getStack(1);
        this.keepSecondSlot = false;
        this.levelCost.set(1);
        int i = 0;
        long cost = 0L;
        int j = 0;
        if(
            !primaryInput.isEmpty()
//            && EnchantmentHelper.canHaveEnchantments(primaryInput)
        ) {
            ItemStack modifyablePrimaryInput = primaryInput.copy();
            ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(EnchantmentHelper.getEnchantments(modifyablePrimaryInput));
//            cost += (long)(Integer)itemStack.getOrDefault(DataComponentTypes.REPAIR_COST, 0) + (long)(Integer)itemStack3.getOrDefault(DataComponentTypes.REPAIR_COST, 0);
            this.repairItemUsage = 0;
            if( !secondaryInput.isEmpty() ) {
                boolean useStoredEnchants = secondaryInput.contains(DataComponentTypes.STORED_ENCHANTMENTS);
                if(
                    modifyablePrimaryInput.isDamageable()
                    && primaryInput.canRepairWith(secondaryInput)
                ) {
                    int k = Math.min(modifyablePrimaryInput.getDamage(), modifyablePrimaryInput.getMaxDamage()/4);
                    if (k <= 0) {
                        this.output.setStack(0, ItemStack.EMPTY);
                        this.levelCost.set(0);
                        return;
                    }

                    int m;
                    for(m = 0; k > 0 && m < secondaryInput.getCount(); ++m) {
                        int n = modifyablePrimaryInput.getDamage() - k;
                        modifyablePrimaryInput.setDamage(n);
                        ++i;
                        k = Math.min(modifyablePrimaryInput.getDamage(), modifyablePrimaryInput.getMaxDamage() / 4);
                    }

                    this.repairItemUsage = m;
                } else {
                    if(
                        !useStoredEnchants
                        && (
                            !modifyablePrimaryInput.isOf( secondaryInput.getItem() )
                            || !modifyablePrimaryInput.isDamageable()
                        )
                    ) {
                        this.output.setStack(0, ItemStack.EMPTY);
                        this.levelCost.set(0);
                        return;
                    }

                    if(modifyablePrimaryInput.isDamageable() && !useStoredEnchants) {
                        int k = primaryInput.getMaxDamage() - primaryInput.getDamage();
                        int m = secondaryInput.getMaxDamage() - secondaryInput.getDamage();
                        int n = m + modifyablePrimaryInput.getMaxDamage() * 12 / 100;
                        int o = k + n;
                        int p = modifyablePrimaryInput.getMaxDamage() - o;
                        if (p < 0) {
                            p = 0;
                        }

                        if (p < modifyablePrimaryInput.getDamage()) {
                            modifyablePrimaryInput.setDamage(p);
                            i += 2;
                        }
                    }

                    ItemEnchantmentsComponent itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(secondaryInput);
                    boolean bl2 = false;
                    boolean bl3 = false;

                    for(Object2IntMap.Entry< RegistryEntry<Enchantment> > entry : itemEnchantmentsComponent.getEnchantmentEntries()) {
                        RegistryEntry<Enchantment> registryEntry = (RegistryEntry<Enchantment>)entry.getKey();
                        int primaryEnchantLevel = builder.getLevel(registryEntry);
                        int enchantmentLevel = entry.getIntValue();
                        enchantmentLevel = (primaryEnchantLevel == enchantmentLevel) ? (enchantmentLevel + 1) : Math.max(enchantmentLevel, primaryEnchantLevel);
                        Enchantment enchantment = (Enchantment)registryEntry.value();
                        boolean isValidItemForEnchantment = enchantment.isAcceptableItem(primaryInput);
                        if(/*this.player.isInCreativeMode() || */primaryInput.isOf(Items.ENCHANTED_BOOK)) {
                            isValidItemForEnchantment = true;
                        }

                        for(RegistryEntry<Enchantment> registryEntry2 : builder.getEnchantments()) {
                            if (!registryEntry2.equals(registryEntry) && !Enchantment.canBeCombined(registryEntry, registryEntry2)) {
                                isValidItemForEnchantment = false;
                                ++i;
                            }
                        }

                        if(!isValidItemForEnchantment) {
                            bl3 = true;
                        } else {
                            bl2 = true;

                            //Set maximum level
                            int effective_maximum_level;
                            if( registryEntry.getKey().isPresent() ) {
                                effective_maximum_level = EnchantmentsHelper.getMaximumEffectiveLevel( registryEntry.getKey().get() );
                            } else {
                                effective_maximum_level = enchantment.getMaxLevel();
                            }
                            if(effective_maximum_level < enchantmentLevel) {
                                enchantmentLevel = effective_maximum_level;
                            }

                            builder.set(registryEntry, enchantmentLevel);
                            int enchantmentCost = enchantment.getAnvilCost();
                            if (useStoredEnchants) {
                                enchantmentCost = Math.max(1, enchantmentCost/2);
                            }

                            i += enchantmentCost * enchantmentLevel;
//                            if (primaryInput.getCount() > 1) {
//                                i = 40;
//                            }
                        }
                    }

                    if (bl3 && !bl2) {
                        this.output.setStack(0, ItemStack.EMPTY);
                        this.levelCost.set(0);
                        return;
                    }
                }
            }

            if (this.newItemName != null && !StringHelper.isBlank(this.newItemName)) {
                if (!this.newItemName.equals(primaryInput.getName().getString())) {
                    j = 1;
                    i += j;
                    modifyablePrimaryInput.set(DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName));
                }
            } else if (primaryInput.contains(DataComponentTypes.CUSTOM_NAME)) {
                j = 1;
                i += j;
                modifyablePrimaryInput.remove(DataComponentTypes.CUSTOM_NAME);
            }

            int t = i <= 0 ? 0 : (int)MathHelper.clamp(cost + (long)i, 0L, 2147483647L);
            this.levelCost.set(t);
            if (i <= 0) {
                modifyablePrimaryInput = ItemStack.EMPTY;
            }

            if (j == i && j > 0) {
//                if (this.levelCost.get() >= 40) {
//                    this.levelCost.set(39);
//                }

                this.keepSecondSlot = true;
            }

//            if (this.levelCost.get() >= 40 && !this.player.isInCreativeMode()) {
//                itemStack2 = ItemStack.EMPTY;
//            }

//            if( !secondaryInput.isEmpty() ) {
//                int k = (Integer)secondaryInput.getOrDefault(DataComponentTypes.REPAIR_COST, 0);
//                if (k < (Integer)secondaryInput.getOrDefault(DataComponentTypes.REPAIR_COST, 0)) {
//                    k = (Integer)secondaryInput.getOrDefault(DataComponentTypes.REPAIR_COST, 0);
//                }

//                if (j != i || j == 0) {
//                    k = getNextCost(k);
//                }

//                secondaryInput.set(DataComponentTypes.REPAIR_COST, k);
//                EnchantmentHelper.set( secondaryInput, builder.build() );
//            }

            EnchantmentHelper.set( modifyablePrimaryInput, builder.build() );
            this.output.setStack(0, modifyablePrimaryInput);
//            this.sendContentUpdates();
        } else {
            this.output.setStack(0, ItemStack.EMPTY);
            this.levelCost.set(0);
        }
    }

    /**
     * @author Wheeler-Shigley
     * @reason Allow for taking items,
     */
    @Inject(
        method = "canTakeOutput",
        at = @At("RETURN"),
        cancellable = true
    )
    protected void canTakeOutput(
        PlayerEntity player,
        boolean original,
        CallbackInfoReturnable<Boolean> cir
    ) {
        int cost_points = levelToPoints( this.levelCost.get() );
        int player_experience_points = getExperiencePoints(player);
        boolean canTakeOutput =
            player.isInCreativeMode()
            || cost_points <= player_experience_points
        ;
        cir.setReturnValue(canTakeOutput);
    }

    /**
     * @author Wheeler-Shigley
     * @reason Effective Overwrite; Force Taking of Item and set custom Pricing
     */
    @Inject(
        method = "onTakeOutput",
        at = @At("HEAD"),
        cancellable = true
    )
    protected void onTakeOutput(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if( !player.isInCreativeMode() ) {
            int cost_level = this.levelCost.get();
            boolean takenOutput = takeExperience(player, cost_level);
            if(!takenOutput) {
                UnlimitedAnvil.LOGGER.warn(
                    "Error Taking Experience from Player, {}.",
                    player.getName().toString()
                );
                ci.cancel();
                return;
            }
        }

        /*
        if(0 < this.repairItemUsage) {
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
         */

        this.levelCost.set(0);
        if(
            player instanceof ServerPlayerEntity
            && !StringHelper.isBlank(this.newItemName)
            && !this.input.getStack(0).getName().getString().equals(this.newItemName)
        ) {
            ( (ServerPlayerEntity)player ).getTextStream().filterText(this.newItemName);
        }

        //remove inputs
        this.input.setStack(0, ItemStack.EMPTY);
        this.input.setStack(1, ItemStack.EMPTY);

        //damage anvil
        this.context.run(
            (world, pos) -> {
                BlockState blockState = world.getBlockState(pos);
                if(
                    !player.isInCreativeMode()
                    && blockState.isIn(BlockTags.ANVIL)
                    && player.getRandom().nextFloat() < 0.12F
                ) {
                    BlockState blockState2 = AnvilBlock.getLandingState(blockState);
                    if (blockState2 == null) {
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

        //without this, there can be "ghost" items in the output
        this.sendContentUpdates();
        //return
        ci.cancel();
    }
}