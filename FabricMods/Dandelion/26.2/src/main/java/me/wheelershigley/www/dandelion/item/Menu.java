package me.wheelershigley.www.dandelion.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class Menu extends SimplePolymerItem {
    public static final Item.Properties PROPERTIES = ( new Item.Properties() )
        .rarity(Rarity.RARE)
        .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
    ;
    public Menu(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(
        final @NonNull Level level,
        final @NonNull Player player,
        final @NonNull InteractionHand hand
    ) {
        if( !(player instanceof ServerPlayer serverPlayer) ) {
            return InteractionResult.FAIL;
        }

        final MinecraftServer server = level.getServer();
        if(server == null) {
            return InteractionResult.FAIL;
        }

        try {
            server.getCommands().getDispatcher().execute(
                "menu",
                serverPlayer.createCommandSourceStack()
            );
        } catch(CommandSyntaxException exception) {
            throw new RuntimeException(exception);
        }

        return InteractionResult.PASS;
    }
}
