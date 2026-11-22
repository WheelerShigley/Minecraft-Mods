package me.wheelershigley.www.solace_crates.menus;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

public abstract class ImmutableSimpleGui extends SimpleGui {
    public static final int ROW_LENGTH = 9;

    private final ImmutableSimpleGui parent;

    public abstract String getTranslationKey();
    public abstract MenuType<?> getMenuType();
    public abstract void initializeMenu();

    public ImmutableSimpleGui(ServerPlayer player, MenuType<?> menuType, ImmutableSimpleGui parent) {
        super(menuType, player, false);
        this.parent = parent;

        this.setTitle(  Component.translatable( getTranslationKey() )  );
        this.initializeMenu();
    }

    @Override
    public void afterRemoval() {
        if(parent != null) {
            parent.open();
        }
    }

    public static MenuType<ChestMenu> getMinimumChestMenu(@Nullable Integer slot_count) {
        if(slot_count == null) {
            return MenuType.GENERIC_9x1;
        }

        if(slot_count <= 1*ROW_LENGTH) { return MenuType.GENERIC_9x1; }
        if(slot_count <= 2*ROW_LENGTH) { return MenuType.GENERIC_9x2; }
        if(slot_count <= 3*ROW_LENGTH) { return MenuType.GENERIC_9x3; }
        if(slot_count <= 4*ROW_LENGTH) { return MenuType.GENERIC_9x4; }
        if(slot_count <= 5*ROW_LENGTH) { return MenuType.GENERIC_9x5; }
        return MenuType.GENERIC_9x6;
    }

    static <T> void setComponent(
        ItemStack itemStack,
        TypedDataComponent<T> component
    ) {
        itemStack.set( component.type(), component.value() );
    }
    private static ItemStack getItemWithGlint(Item item) {
        ItemStack itemStack = new ItemStack(item);
        return getItemWithGlint(itemStack);
    }
    private static ItemStack getItemWithGlint(ItemStack item) {
        ItemStack copy = item.copy();
        copy.set(
            DataComponents.ENCHANTMENT_GLINT_OVERRIDE,
            true
        );
        return copy;
    }
    private static ItemStack stackWithTranslatedName(Item item, @Nullable String key) {
        ItemStack stack = new ItemStack(item);
        return stackWithTranslatedName(stack, key);
    }
    private static ItemStack stackWithTranslatedName(ItemStack itemStack, @Nullable String key) {
        ItemStack copy = itemStack.copy();
        if(key != null) {
            copy.set(
                DataComponents.CUSTOM_NAME,
                Component.translatable(key).withStyle(
                    Style.EMPTY.withItalic(false)
                )
            );
        }
        return copy;
    }

    public ItemStack getMenuItem(
        Item textureItem,
        boolean enchantable,
        @Nullable String translationKey
    ) {
        return getMenuItem(textureItem, null, enchantable, translationKey);
    }
    public ItemStack getMenuItem(
        Item textureItem,
        @Nullable DataComponentMap components,
        boolean enchantable,
        @Nullable String translationKey
    ) {
        Identifier textureIdentifier = BuiltInRegistries.ITEM.getKey(textureItem);
        return getMenuItem(
            translationKey != null ? translationKey : textureItem.getDescriptionId(),
            textureIdentifier,
            components,
            enchantable
        );
    }
    public ItemStack getMenuItem(
        @Nullable String translationKey,
        Identifier textureIdentifier,
        @Nullable DataComponentMap components,
        boolean enchantable
    ) {
        ItemStack itemStack = new ItemStack(Items.PAPER);

        itemStack.set(DataComponents.ITEM_MODEL, textureIdentifier);
        if(components != null) {
            for(TypedDataComponent<?> component : components) {
                if(component.type() == DataComponents.ITEM_MODEL) {
                    continue;
                }

                setComponent(itemStack, component);
            }
        }

        if(translationKey != null) {
            itemStack.set(
                DataComponents.CUSTOM_NAME,
                Component.translatable(translationKey).withStyle( Style.EMPTY.withItalic(false) )
            );
        }

        CompoundTag menuItemTag = new CompoundTag();
        menuItemTag.putBoolean("MenuItem", true);
        itemStack.set(
            DataComponents.CUSTOM_DATA,
            CustomData.of(menuItemTag)
        );

        if(enchantable) {
            itemStack = getItemWithGlint(itemStack);
        }

        return stackWithTranslatedName(itemStack, translationKey);
    }
}
