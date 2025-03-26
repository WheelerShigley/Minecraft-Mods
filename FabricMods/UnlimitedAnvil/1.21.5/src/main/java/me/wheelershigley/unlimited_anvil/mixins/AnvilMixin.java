package me.wheelershigley.unlimited_anvil.mixins;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import me.wheelershigley.unlimited_anvil.EnchantmentsHelper;
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

import java.util.Iterator;
import java.util.Objects;

@Mixin(value = AnvilScreenHandler.class, priority = 800)
public abstract class AnvilMixin extends ForgingScreenHandler  {
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

    @Shadow @Final    private Property levelCost;
    @Shadow @Nullable private String   newItemName;
    @Shadow           private boolean  keepSecondSlot;
    @Shadow           private int      repairItemUsage;

    @Shadow public static int getNextCost(int cost) { return 1; }

    /**
     * @author Wheeler-Shigley
     * @reason Double level cost! (for balancing medium/low-level enchanting)
     */
    @Overwrite
    public void updateResult() {
        /*Modified Vanilla Implementation*/ {
            ItemStack itemStack = this.input.getStack(0);
            this.keepSecondSlot = false;
            this.levelCost.set(1);
            int i = 0;
            long l = 0L;
            int j = 0;
            if (!itemStack.isEmpty() && EnchantmentHelper.canHaveEnchantments(itemStack)) {
                ItemStack itemStack2 = itemStack.copy();
                ItemStack itemStack3 = this.input.getStack(1);
                ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(EnchantmentHelper.getEnchantments(itemStack2));
                l += (long)(Integer)itemStack.getOrDefault(DataComponentTypes.REPAIR_COST, 0) + (long)(Integer)itemStack3.getOrDefault(DataComponentTypes.REPAIR_COST, 0);
                this.repairItemUsage = 0;
                int k;
                if (!itemStack3.isEmpty()) {
                    boolean bl = itemStack3.contains(DataComponentTypes.STORED_ENCHANTMENTS);
                    int m;
                    int n;
                    if (itemStack2.isDamageable() && itemStack.canRepairWith(itemStack3)) {
                        k = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
                        if (k <= 0) {
                            this.output.setStack(0, ItemStack.EMPTY);
                            this.levelCost.set(0);
                            return;
                        }

                        for(m = 0; k > 0 && m < itemStack3.getCount(); ++m) {
                            n = itemStack2.getDamage() - k;
                            itemStack2.setDamage(n);
                            ++i;
                            k = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
                        }

                        this.repairItemUsage = m;
                    } else {
                        if (!bl && (!itemStack2.isOf(itemStack3.getItem()) || !itemStack2.isDamageable())) {
                            this.output.setStack(0, ItemStack.EMPTY);
                            this.levelCost.set(0);
                            return;
                        }

                        if (itemStack2.isDamageable() && !bl) {
                            k = itemStack.getMaxDamage() - itemStack.getDamage();
                            m = itemStack3.getMaxDamage() - itemStack3.getDamage();
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

                        ItemEnchantmentsComponent itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(itemStack3);
                        boolean bl2 = false;
                        boolean bl3 = false;

                        for(Object2IntMap.Entry< RegistryEntry<Enchantment> > registryEntryEntry : itemEnchantmentsComponent.getEnchantmentEntries() ) {
                            RegistryEntry<Enchantment> registryEntry = registryEntryEntry.getKey();
                            int q = builder.getLevel(registryEntry);
                            int r = registryEntryEntry.getIntValue();
                            r = q == r ? r + 1 : Math.max(r, q);
                            Enchantment enchantment = registryEntry.value();
                            boolean bl4 = enchantment.isAcceptableItem(itemStack);
                            if (this.player.isInCreativeMode() || itemStack.isOf(Items.ENCHANTED_BOOK)) {
                                bl4 = true;
                            }

                            for(RegistryEntry<Enchantment> enchantmentRegistryEntry : builder.getEnchantments() ) {
                                if(
                                    !enchantmentRegistryEntry.equals(registryEntry)
                                    && !Enchantment.canBeCombined(registryEntry, enchantmentRegistryEntry)
                                ) {
                                    bl4 = false;
                                    ++i;
                                }
                            }

                            if (!bl4) {
                                bl3 = true;
                            } else {
                                bl2 = true;

                                //custom maximum levels
                                int maximum_effective_level = EnchantmentsHelper.getMaximumEffectiveLevel(
                                        Identifier.of(
                                                enchantment.description().getString().toLowerCase().replace(' ', '_')
                                        )
                                );
                                if (maximum_effective_level < r) {
                                    r = maximum_effective_level;
                                }

                                builder.set(registryEntry, r);
                                int s = enchantment.getAnvilCost();
                                if (bl) {
                                    s = Math.max(1, s / 2);
                                }

                                i += s * r;
//                                if (itemStack.getCount() > 1) {
//                                    i = MAX_COST;
//                                }
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
                    if (!this.newItemName.equals(itemStack.getName().getString())) {
                        j = 1;
                        i += j;
                        itemStack2.set(DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName));
                    }
                } else if (itemStack.contains(DataComponentTypes.CUSTOM_NAME)) {
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
//                    if (this.levelCost.get() >= MAX_COST) {
//                        this.levelCost.set(MAX_COST-1);
//                    }

                    this.keepSecondSlot = true;
                }

                if(
//                    this.levelCost.get() >= MAX_COST &&
                    !this.player.isInCreativeMode()
                ) {
                    itemStack2 = ItemStack.EMPTY;
                }

                if (!itemStack2.isEmpty()) {
                    k = (Integer)itemStack2.getOrDefault(DataComponentTypes.REPAIR_COST, 0);
                    if (k < (Integer)itemStack3.getOrDefault(DataComponentTypes.REPAIR_COST, 0)) {
                        k = (Integer)itemStack3.getOrDefault(DataComponentTypes.REPAIR_COST, 0);
                    }

                    if (j != i || j == 0) {
                        k = getNextCost(k);
                    }

                    itemStack2.set(DataComponentTypes.REPAIR_COST, k);
                    EnchantmentHelper.set(itemStack2, builder.build());
                }

                this.output.setStack(0, itemStack2);
//                this.sendContentUpdates();
            } else {
                this.output.setStack(0, ItemStack.EMPTY);
                this.levelCost.set(0);
            }
        }
        /*Custom Behavior*/ {
            //quadruple level-cost
            int final_cost = EnchantmentsHelper.getEnchantingCost( this.output.getStack(0) );
            boolean has_new_custom_name = (
                /*Has some custom name*/
                this.newItemName != null
                && !StringHelper.isBlank(this.newItemName)
                /*Custom name is new*/
                && !Objects.equals( this.input.getStack(0).getCustomName(), this.output.getStack(0).getCustomName() )
            );
            if(has_new_custom_name) {
                ++final_cost;
            }

            //remove compounding cost
            ItemStack Output = this.output.getStack(0);
            Output.remove(DataComponentTypes.REPAIR_COST);
            this.output.setStack(0, Output);

            //send result to user
            this.levelCost.set(final_cost);
            this.sendContentUpdates();
        }
    }
}
