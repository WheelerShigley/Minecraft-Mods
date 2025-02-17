package me.wheelershigley.unlimitedanvil2.mixins;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import me.wheelershigley.unlimitedanvil2.EnchantmentsHelper;
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
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;

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
        return (int)Math.min((long)cost * 2L + 1L, 2147483647L);
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
                        Iterator<Object2IntMap.Entry<RegistryEntry<Enchantment>>> var26 = itemEnchantmentsComponent.getEnchantmentEntries().iterator();

                        while(var26.hasNext()) {
                            Object2IntMap.Entry<RegistryEntry<Enchantment>> entry = var26.next();
                            RegistryEntry<Enchantment> registryEntry = entry.getKey();
                            int q = builder.getLevel(registryEntry);
                            int r = entry.getIntValue();
                            r = q == r ? r + 1 : Math.max(r, q);
                            Enchantment enchantment = (Enchantment)registryEntry.value();
                            boolean bl4 = enchantment.isAcceptableItem(PrimaryInput);
                            if (this.player.getAbilities().creativeMode || PrimaryInput.isOf(Items.ENCHANTED_BOOK)) {
                                bl4 = true;
                            }

                            Iterator<RegistryEntry<Enchantment>> var20 = builder.getEnchantments().iterator();

                            while(var20.hasNext()) {
                                RegistryEntry<Enchantment> registryEntry2 = var20.next();
                                if (!registryEntry2.equals(registryEntry) && !Enchantment.canBeCombined(registryEntry, registryEntry2)) {
                                    bl4 = false;
                                    ++i;
                                }
                            }

                            if (!bl4) {
                                bl3 = true;
                            } else {
                                bl2 = true;
                                if( r > enchantment.getMaxLevel() ) {
                                    r = enchantment.getMaxLevel();
                                }

                                builder.set(registryEntry, r);
                                int s = enchantment.getAnvilCost();
                                if (bl) {
                                    s = Math.max(1, s / 2);
                                }

                                i += s * r;
                                if (PrimaryInput.getCount() > 1) {
                                    i = 40;
                                }
                            }
                        }

                        if (bl3 && !bl2) {
                            this.output.setStack(0, ItemStack.EMPTY);
                            this.levelCost.set(0);
                        }
                    }
                }

                if (this.newItemName != null && !StringHelper.isBlank(this.newItemName)) {
                    if (!this.newItemName.equals(PrimaryInput.getName().getString())) {
                        j = 1;
                        i += j;
                        itemStack2.set(DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName));
                    }
                } else if (PrimaryInput.contains(DataComponentTypes.CUSTOM_NAME)) {
                    j = 1;
                    i += j;
                    itemStack2.remove(DataComponentTypes.CUSTOM_NAME);
                }

                int t = i <= 0 ? 0 : (int) MathHelper.clamp(l + (long)i, 0L, 2147483647L);
                this.levelCost.set(t);
                if (i <= 0) {
                    itemStack2 = ItemStack.EMPTY;
                }

                if (j == i && j > 0) {
                    if (this.levelCost.get() >= 40) {
                        this.levelCost.set(39);
                    }

                    this.keepSecondSlot = true;
                }

                if (this.levelCost.get() >= 40 && !this.player.getAbilities().creativeMode) {
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
            boolean useStoredEnchants = PrimaryInput.contains(DataComponentTypes.STORED_ENCHANTMENTS);
            int updatedLevel = EnchantmentsHelper.getEnchantingCost(Output, useStoredEnchants);

            boolean has_new_custom_name =
                this.newItemName != null
                && !StringHelper.isBlank(this.newItemName)
                && Output.contains(DataComponentTypes.CUSTOM_NAME)
            ;
            if(has_new_custom_name) {
                ++updatedLevel;
            }

            //Remove compounding cost multiplier
            Output.set(DataComponentTypes.REPAIR_COST, 0);

            this.levelCost.set(updatedLevel);
            this.sendContentUpdates();
        }
    }
}
