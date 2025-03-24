package me.wheelershigley.unlimitedanvil2.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import me.wheelershigley.unlimitedanvil2.EnchantmentsHelper;
import me.wheelershigley.unlimitedanvil2.UnlimitedAnvil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.Iterator;

import static me.wheelershigley.unlimitedanvil2.EnchantmentsHelper.getMaximumEffectiveLevel;

@Mixin(value = AnvilScreenHandler.class, priority = 800)
public abstract class AnvilMixin extends ForgingScreenHandler  {
    public AnvilMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, ForgingSlotsManager forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    @Shadow @Final private Property levelCost;
    @Shadow @Nullable private String newItemName;
    @Shadow private boolean keepSecondSlot;
    @Shadow private int repairItemUsage;

    @Shadow
    public static int getNextCost(int cost) {
        return (int)Math.min(
            (long)cost * 2L + 1L,
            (long)Integer.MIN_VALUE
        );
    }

    /**
     * @author Wheeler-Shigley
     * @reason Double level cost! (for balancing medium/low-level enchanting)
     */
    @Overwrite
    public void updateResult() {
        ItemStack PrimaryInput = this.input.getStack(0);
        ItemStack SecondaryInput = this.input.getStack(1);
        ItemStack Output = this.output.getStack(0);
        int final_cost = 0;
        /*Vanilla* Implementation*/ {
            this.keepSecondSlot = false;
            this.levelCost.set(1);
            int i = 0;
            long l = 0L;
            int j = 0;
            if (!PrimaryInput.isEmpty() && EnchantmentHelper.canHaveEnchantments(PrimaryInput)) {
                ItemStack itemStack2 = PrimaryInput.copy();
                ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(EnchantmentHelper.getEnchantments(itemStack2));
                l += (long)(Integer)PrimaryInput.getOrDefault(DataComponentTypes.REPAIR_COST, 0) + (long)(Integer)SecondaryInput.getOrDefault(DataComponentTypes.REPAIR_COST, 0);
                this.repairItemUsage = 0;
                int k;
                if (!SecondaryInput.isEmpty()) {
                    boolean bl = SecondaryInput.contains(DataComponentTypes.STORED_ENCHANTMENTS);
                    int m;
                    int n;
                    if (itemStack2.isDamageable() && PrimaryInput.canRepairWith(SecondaryInput)) {
                        k = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
                        if (k <= 0) {
                            this.output.setStack(0, ItemStack.EMPTY);
                            this.levelCost.set(0);
                            return;
                        }

                        for(m = 0; k > 0 && m < SecondaryInput.getCount(); ++m) {
                            n = itemStack2.getDamage() - k;
                            itemStack2.setDamage(n);
                            ++i;
                            k = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
                        }

                        this.repairItemUsage = m;
                    } else {
                        if (!bl && (!itemStack2.isOf(SecondaryInput.getItem()) || !itemStack2.isDamageable())) {
                            this.output.setStack(0, ItemStack.EMPTY);
                            this.levelCost.set(0);
                            return;
                        }

                        if (itemStack2.isDamageable() && !bl) {
                            k = PrimaryInput.getMaxDamage() - PrimaryInput.getDamage();
                            m = SecondaryInput.getMaxDamage() - SecondaryInput.getDamage();
                            n = m + itemStack2.getMaxDamage() * 12 / 100;
                            int o = k + n;
                            int p = itemStack2.getMaxDamage() - o;
                            if (p < 0) {
                                p = 0;
                            }

                            if (p < itemStack2.getDamage()) {
                                itemStack2.setDamage(p);
                                i += 2;
                            }
                        }

                        ItemEnchantmentsComponent itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(SecondaryInput);
                        boolean bl2 = false;
                        boolean bl3 = false;
                        Iterator<  Object2IntMap.Entry< RegistryEntry<Enchantment> >  > var26 = itemEnchantmentsComponent.getEnchantmentEntries().iterator();

                        while(var26.hasNext()) {
                            Object2IntMap.Entry<RegistryEntry<Enchantment>> entry = var26.next();
                            RegistryEntry<Enchantment> registryEntry = entry.getKey();
                            int q = builder.getLevel(registryEntry);
                            int level = entry.getIntValue();
                            level = q == level ? level + 1 : Math.max(level, q);
                            Enchantment enchantment = registryEntry.value();
                            boolean bl4 = enchantment.isAcceptableItem(PrimaryInput);
                            if (this.player.getAbilities().creativeMode || PrimaryInput.isOf(Items.ENCHANTED_BOOK)) {
                                bl4 = true;
                            }

                            for( RegistryEntry<Enchantment> registryEntry2 : builder.getEnchantments() ) {
                                if( !registryEntry2.equals(registryEntry) && !Enchantment.canBeCombined(registryEntry, registryEntry2) ) {
                                    bl4 = false;
                                    ++i;
                                }
                            }

                            if (!bl4) {
                                bl3 = true;
                            } else {
                                bl2 = true;

                                //use custom maximum levels
                                int max_level = getMaximumEffectiveLevel(
                                    Identifier.of(
                                        enchantment.description().getString().toLowerCase().replace(' ', '_')
                                    )
                                );
                                level = Math.min(max_level, level);

                                builder.set(registryEntry, level);
                                int cost = 2*enchantment.getAnvilCost(); //Double level-price
                                if (bl) {
                                    cost = Math.max(1, cost / 2);
                                }

                                i += cost * level;
                            }
                        }

                        if (bl3 && !bl2) {
                            this.output.setStack(0, ItemStack.EMPTY);
                            this.levelCost.set(0);
                        }
                    }
                }

                if( this.newItemName != null && !StringHelper.isBlank(this.newItemName) ) {
                    if( !this.newItemName.equals(PrimaryInput.getName().getString()) ) {
                        j = 1;
                        i++;
                        itemStack2.set(DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName));
                    }
                } else if( PrimaryInput.contains(DataComponentTypes.CUSTOM_NAME) ) {
                    j = 1;
                    i++;
                    itemStack2.remove(DataComponentTypes.CUSTOM_NAME);
                }

                final_cost = i <= 0 ? 0 : (int) MathHelper.clamp(2*(l + (long)i), 0L, 2147483647L);
//                UnlimitedAnvil.LOGGER.info( "a "+Integer.toString(final_cost) );
//                UnlimitedAnvil.LOGGER.info( "b "+Integer.toString( this.levelCost.get() ) );
                this.levelCost.set(final_cost);
                if (i <= 0) {
                    itemStack2 = ItemStack.EMPTY;
                }

                if (j == i && j > 0) {
                    this.keepSecondSlot = true;
                }

                if( !this.player.getAbilities().creativeMode ) {
                    itemStack2 = ItemStack.EMPTY;
                }

                if (!itemStack2.isEmpty()) {
                    k = (Integer)itemStack2.getOrDefault(DataComponentTypes.REPAIR_COST, 0);
                    if (k < (Integer)SecondaryInput.getOrDefault(DataComponentTypes.REPAIR_COST, 0)) {
                        k = (Integer)SecondaryInput.getOrDefault(DataComponentTypes.REPAIR_COST, 0);
                    }

                    if (j != i || j == 0) {
                        k = getNextCost(k);
                    }

                    itemStack2.set(DataComponentTypes.REPAIR_COST, k);
                    EnchantmentHelper.set(itemStack2, builder.build());
                }

                this.output.setStack(0, itemStack2);
                //this.sendContentUpdates();
            } else {
                this.output.setStack(0, ItemStack.EMPTY);
                this.levelCost.set(0);
                return;
            }
        }
        /*Custom Addition(s)*/ {
            //boolean useStoredEnchants = PrimaryInput.contains(DataComponentTypes.STORED_ENCHANTMENTS);
            //int updatedLevel = EnchantmentsHelper.getEnchantingCost(Output, useStoredEnchants);

//            boolean has_new_custom_name =
//                this.newItemName != null
//                && !StringHelper.isBlank(this.newItemName)
//                && Output.contains(DataComponentTypes.CUSTOM_NAME)
//            ;
//            if(has_new_custom_name) {
//                ++updatedLevel;
//            }

            //Remove compounding cost multiplier
            //this.levelCost.set(final_cost);
            Output.set(DataComponentTypes.REPAIR_COST, 0);

            //this.levelCost.set(updatedLevel);
        }
        this.sendContentUpdates();
    }
}
