package me.wheelershigley.unlimited_anvil.mixins;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import me.wheelershigley.unlimited_anvil.AnvilMode;
import me.wheelershigley.unlimited_anvil.UnlimitedAnvil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.RepairableComponent;
import net.minecraft.data.server.tag.ItemTagProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
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
import oshi.jna.platform.mac.SystemB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import static me.wheelershigley.unlimited_anvil.HelperFunctions.contains;
import static me.wheelershigley.unlimited_anvil.ToolMaterials.ToolMaterialMap;
import static net.minecraft.screen.AnvilScreenHandler.getNextCost;

@Mixin(value = AnvilScreenHandler.class, priority = 800)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, ForgingSlotsManager forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    private HashMap<ItemTags, Item[]> RepairMap; static {
        //dynamic repair items
        ArrayList<Item> wooden_planks = new ArrayList<>();
        ArrayList<Item> stones = new ArrayList<>();

        ItemStack CurrentItem;
        for(Item item : Registries.ITEM) {
            CurrentItem = new ItemStack(item);
            if( CurrentItem.isIn(ItemTags.PLANKS) ) {
                wooden_planks.add(item);
            }
            if( CurrentItem.isIn(ItemTags.STONE_TOOL_MATERIALS) ) {
                stones.add(item);
            }
        }

        //non-dynamic tool maps
    }

    @Shadow @Final
    public static final int INPUT_1_ID = 0;
    @Shadow @Final
    public static final int INPUT_2_ID = 1;
    @Shadow @Final
    public static final int OUTPUT_ID = 2;
    @Shadow
    private boolean keepSecondSlot;
    @Shadow @Final
    private Property levelCost;
    @Shadow
    private int repairItemUsage;
    @Shadow
    @Nullable
    private String newItemName;
    /**
     * @author wheeler-shigley
     * @reason complete anvil-functionality overhaul
     */
    @Overwrite
    public void updateResult() {
        UnlimitedAnvil.LOGGER.info("updateResult call");

        ItemStack PrimaryInput, SecondaryInput;
        AnvilMode mode;
        /*Validate Inputs through Mode*/ {
            PrimaryInput = this.input.getStack(INPUT_1_ID);
            if( PrimaryInput.isEmpty() ) { return; } //There MUST be an item to augment
            SecondaryInput = this.input.getStack(INPUT_2_ID);

            UnlimitedAnvil.LOGGER.info(
                    "{"+ PrimaryInput.getItem().toString() + ":\"" + PrimaryInput.getName().toString() + "\""
                +" + {"+ SecondaryInput.getItem().toString() + ": \"" + SecondaryInput.getName().toString() +"\""
            );

            mode = AnvilMode.Disabled;
            if( SecondaryInput.getItem() == Items.ENCHANTED_BOOK) { mode = AnvilMode.Enchant; }
            if( SecondaryInput.getItem() == PrimaryInput.getItem() ) { mode = AnvilMode.Combine; }
            if( contains(ToolMaterialMap.get( PrimaryInput.getItem() ), SecondaryInput.getItem() ) ) { mode = AnvilMode.Repair; }
            if(
                mode == AnvilMode.Disabled
                && ( newItemName != null && !newItemName.isBlank() )
            ) {
                mode = AnvilMode.RenameOnly;
            }

            /*PrimaryInput MUST be valid (known)*/
            if(
                   ToolMaterialMap.get( PrimaryInput.getItem() ) != null
                && ToolMaterialMap.get( PrimaryInput.getItem() ).length == 0
            ) {
                mode = AnvilMode.Disabled;
            }

            UnlimitedAnvil.LOGGER.info( "-> "+ mode );
        }
        if(mode == AnvilMode.Disabled) { return; }

        //Handles modes


        //Previous Implementation (Independent)
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
                    Iterator var26 = itemEnchantmentsComponent.getEnchantmentEntries().iterator();

                    while(var26.hasNext()) {
                        Object2IntMap.Entry<RegistryEntry<Enchantment>> entry = (Object2IntMap.Entry)var26.next();
                        RegistryEntry<Enchantment> registryEntry = (RegistryEntry)entry.getKey();
                        int q = builder.getLevel(registryEntry);
                        int r = entry.getIntValue();
                        r = q == r ? r + 1 : Math.max(r, q);
                        Enchantment enchantment = (Enchantment)registryEntry.value();
                        boolean bl4 = enchantment.isAcceptableItem(itemStack);
                        if (this.player.getAbilities().creativeMode || itemStack.isOf(Items.ENCHANTED_BOOK)) {
                            bl4 = true;
                        }

                        Iterator var20 = builder.getEnchantments().iterator();

                        while(var20.hasNext()) {
                            RegistryEntry<Enchantment> registryEntry2 = (RegistryEntry)var20.next();
                            if (!registryEntry2.equals(registryEntry) && !Enchantment.canBeCombined(registryEntry, registryEntry2)) {
                                bl4 = false;
                                ++i;
                            }
                        }

                        if (!bl4) {
                            bl3 = true;
                        } else {
                            bl2 = true;
                            if (r > enchantment.getMaxLevel()) {
                                r = enchantment.getMaxLevel();
                            }

                            builder.set(registryEntry, r);
                            int s = enchantment.getAnvilCost();
                            if (bl) {
                                s = Math.max(1, s / 2);
                            }

                            i += s * r;
                            if (itemStack.getCount() > 1) {
                                i = 40;
                            }
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
            this.sendContentUpdates();
        } else {
            this.output.setStack(0, ItemStack.EMPTY);
            this.levelCost.set(0);
        }
    }
}
