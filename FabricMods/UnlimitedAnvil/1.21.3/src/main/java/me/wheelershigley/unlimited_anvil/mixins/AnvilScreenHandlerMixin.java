package me.wheelershigley.unlimited_anvil.mixins;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import me.wheelershigley.unlimited_anvil.AnvilMode;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.util.StringHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static me.wheelershigley.unlimited_anvil.HelperFunctions.*;
import static me.wheelershigley.unlimited_anvil.ToolMaterials.ToolMaterialMap;

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

    private AnvilMode getMode(ItemStack PrimaryInput, ItemStack SecondaryInput) {
        AnvilMode mode = AnvilMode.Disabled;

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

        return mode;
    }

    @Shadow @Final
    public static final int INPUT_1_ID = 0;
    @Shadow @Final
    public static final int INPUT_2_ID = 1;
    @Shadow @Final
    public static final int OUTPUT_ID = 2;

    private static int TRUE_OUTPUT_ID = OUTPUT_ID-(INPUT_2_ID+1);


    @Shadow @Final
    private Property levelCost;
    @Shadow
    private int repairItemUsage;
    @Shadow
    @Nullable
    private String newItemName;
    @Shadow
    public boolean setNewItemName(String newItemName) { return false; }
    /**
     * @author wheeler-shigley
     * @reason complete anvil-functionality overhaul
     */
    @Overwrite
    public void updateResult() {
        this.output.setStack(TRUE_OUTPUT_ID, new ItemStack(Items.AIR) );

        ItemStack PrimaryInput, SecondaryInput;
        AnvilMode mode;
        /*Validate Inputs through Mode*/ {
            PrimaryInput = this.input.getStack(INPUT_1_ID);
            if( PrimaryInput.isEmpty() ) { return; } //There MUST be an item to augment
            SecondaryInput = this.input.getStack(INPUT_2_ID);

            mode = getMode(PrimaryInput, SecondaryInput);
        }
        if(mode == AnvilMode.Disabled) { return; }

        //Handles modes
        ItemStack output = PrimaryInput.copy();
        int levelCost = 0;
        switch(mode) {
            case AnvilMode.Repair: {
                int repair_amount_per_material = PrimaryInput.getMaxDamage()/3;
                output.setDamage(
                    Math.max(
                        PrimaryInput.getDamage() - SecondaryInput.getCount() * repair_amount_per_material,
                        0
                    )
                );
                this.repairItemUsage = Math.min(SecondaryInput.getCount(), 3);
                break;
            }
            case AnvilMode.Enchant:
            case AnvilMode.Combine: {
                ItemEnchantmentsComponent enchants = combineEnchants(PrimaryInput, SecondaryInput);

                boolean isEnchantedBook = PrimaryInput.contains(DataComponentTypes.STORED_ENCHANTMENTS);
                if(isEnchantedBook) {
                    //TODO : FIX THIS
                    output.set(DataComponentTypes.STORED_ENCHANTMENTS, enchants);
                } else {
                    EnchantmentHelper.set(output, enchants);
                }
                break;
            }
        }

        levelCost = getEnchantingCost(output);
        if(
               newItemName != null
            && !StringHelper.isBlank(this.newItemName)
            && !this.newItemName.equals( PrimaryInput.getName().getString() )
        ) {
            //output.set(DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName));
            if( setNewItemName(this.newItemName) ) {
                levelCost += 1;
            }
        }

        /*Outputs*/
        this.output.setStack(TRUE_OUTPUT_ID, output);
        this.levelCost.set(levelCost);
        this.sendContentUpdates();

        //vanilla; I have no idea why, but if I comment out bl4, the mixin breaks.
        ItemStack itemStack = new ItemStack(Items.AIR);
        if (!itemStack.isEmpty() && EnchantmentHelper.canHaveEnchantments(itemStack)) {
            ItemStack itemStack2 = new ItemStack(Items.AIR);
            ItemStack itemStack3 = new ItemStack(Items.AIR);
            if (!itemStack3.isEmpty()) {
                if (itemStack2.isDamageable() && itemStack.canRepairWith(itemStack3)) {
                } else {
                    ItemEnchantmentsComponent itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(itemStack3);
                    Iterator var26 = itemEnchantmentsComponent.getEnchantmentEntries().iterator();

                    while(var26.hasNext()) {
                        Object2IntMap.Entry<RegistryEntry<Enchantment>> entry = (Object2IntMap.Entry)var26.next();
                        RegistryEntry<Enchantment> registryEntry = (RegistryEntry)entry.getKey();
                        Enchantment enchantment = (Enchantment)registryEntry.value();
                        boolean bl4 = enchantment.isAcceptableItem(itemStack);
                    }
                }
            }
        }
    }
}
