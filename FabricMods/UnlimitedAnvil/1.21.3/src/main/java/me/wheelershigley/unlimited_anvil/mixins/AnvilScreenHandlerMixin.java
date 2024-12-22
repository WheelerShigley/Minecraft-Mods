package me.wheelershigley.unlimited_anvil.mixins;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import me.wheelershigley.unlimited_anvil.AnvilMode;
import me.wheelershigley.unlimited_anvil.UnlimitedAnvil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static me.wheelershigley.unlimited_anvil.helpers.HelperFunctions.*;
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
        //TODO: override rules in ItemStack.canRepairWith(ItemStack)
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

    @Shadow private boolean keepSecondSlot;

    /**
     * @author wheeler-shigley
     * @reason complete anvil-functionality overhaul
     */
    @Overwrite
    public void updateResult() {
        this.output.setStack(TRUE_OUTPUT_ID, ItemStack.EMPTY);
        this.levelCost.set(1);
        this.keepSecondSlot = false;

        ItemStack PrimaryInput, SecondaryInput;
        AnvilMode mode;
        /*Validate Inputs through Mode*/ {
            PrimaryInput = this.input.getStack(INPUT_1_ID);
            if( PrimaryInput.isEmpty() ) { return; } //There MUST be an item to augment
            SecondaryInput = this.input.getStack(INPUT_2_ID);

            mode = getMode(PrimaryInput, SecondaryInput);
        }
        if(mode == AnvilMode.Disabled) { return; }

        boolean useStoredEnchants = PrimaryInput.contains(DataComponentTypes.STORED_ENCHANTMENTS);

        //Handles modes
        ItemStack output = PrimaryInput.copy();
        int doubleLevelCost = 0;
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

                //Repair
                if(
                       PrimaryInput.isDamageable()
                    && PrimaryInput.getItem().equals( SecondaryInput.getItem() )
                ) {
                    output.setDamage(
                        Math.max(
                            /*Add healths with damages*/
                            PrimaryInput.getDamage() + SecondaryInput.getDamage() - PrimaryInput.getMaxDamage(),
                            0
                        )
                    );
                }

                if(useStoredEnchants) {
                    output.set(DataComponentTypes.STORED_ENCHANTMENTS, enchants);
                } else {
                    EnchantmentHelper.set(output, enchants);
                }
                break;
            }
        }

        doubleLevelCost = getEnchantingCost(output, useStoredEnchants);
        boolean hasNewName = (
            newItemName != null
            && !StringHelper.isBlank(this.newItemName)
            && !this.newItemName.equals( PrimaryInput.getName().getString() )
        );
        if(hasNewName) {
            output.set( DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName) );
            doubleLevelCost += 1;
        }

        /*Outputs*/
        boolean outputIsTheSame = (
            !hasNewName
            && enchantsAreTheSame(PrimaryInput, output)
            && PrimaryInput.getDamage() == output.getDamage()
        );
        boolean repairOnly = (
            hasNewName
            && enchantsAreTheSame(PrimaryInput, output)
            && PrimaryInput.getDamage() == output.getDamage()
        );
        if(repairOnly) {
            //mode = AnvilMode.Repair;
            this.keepSecondSlot = true;
        }
        if(outputIsTheSame) {
            this.output.setStack(TRUE_OUTPUT_ID, ItemStack.EMPTY );
            this.levelCost.set(0);
        } else {
            this.output.setStack(TRUE_OUTPUT_ID, output);
            this.levelCost.set(doubleLevelCost);
        }
        this.sendContentUpdates();

        //vanilla; I have no idea why, but if I comment out bl4, the mixin breaks.
        ItemStack itemStack = ItemStack.EMPTY;
        ItemEnchantmentsComponent itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(itemStack);
        Iterator var26 = itemEnchantmentsComponent.getEnchantmentEntries().iterator();

        while( var26.hasNext() ) {
            Object2IntMap.Entry<RegistryEntry<Enchantment>> entry = (Object2IntMap.Entry)var26.next();
            RegistryEntry<Enchantment> registryEntry = (RegistryEntry)entry.getKey();
            Enchantment enchantment = (Enchantment)registryEntry.value();
            boolean bl4 = enchantment.isAcceptableItem(itemStack);
        }
    }

    @Inject(method = "onTakeOutput", at = @At("HEAD") )
    protected void onTakeOutput(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        UnlimitedAnvil.LOGGER.info("onTakeOutput");
    }
}
