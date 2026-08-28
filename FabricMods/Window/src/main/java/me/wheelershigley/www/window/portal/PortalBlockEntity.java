package me.wheelershigley.www.window.portal;

import com.mojang.math.Transformation;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.ChunkAttachment;
import eu.pb4.polymer.virtualentity.api.elements.BlockDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import me.wheelershigley.www.window.api.DisplayHelper;
import me.wheelershigley.www.window.registrations.WindowBlockEntities;
import me.wheelershigley.www.window.registrations.WindowBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashMap;

import static me.wheelershigley.www.window.Window.getWindowIdentifier;
import static me.wheelershigley.www.window.api.DisplayHelper.getPaneDisplay;

public class PortalBlockEntity extends BlockEntity {
    private Block frame;
    private Block igniter;

    private final ElementHolder holder = new ElementHolder();
    private ChunkAttachment attachment;

    public PortalBlockEntity(BlockPos pos, BlockState state, DyeColor color) {
        super(getBlockEntityType(color), pos, state);
    }

    public void initializeHolder(ServerLevel level) {
        if(attachment != null) {
            return;
        }

        PortalBlock block = (PortalBlock)level.getBlockState( this.getBlockPos() ).getBlock();
        holder.addElement(
            getPaneDisplay(
                block.COLOR,
                getBlockState().getValue(BlockStateProperties.AXIS)
            )
        );

        attachment = (ChunkAttachment)ChunkAttachment.ofTicking(holder, level, worldPosition);
    }

    public static BlockEntityType<PortalBlockEntity> getBlockEntityType(DyeColor color) {
        return switch (color) {
            case WHITE ->       WindowBlockEntities.WHITE_PORTAL;
            case LIGHT_GRAY ->  WindowBlockEntities.LIGHT_GRAY_PORTAL;
            case GRAY ->        WindowBlockEntities.GRAY_PORTAL;
            case BLACK ->       WindowBlockEntities.BLACK_PORTAL;
            case BROWN ->       WindowBlockEntities.BROWN_PORTAL;
            case RED ->         WindowBlockEntities.RED_PORTAL;
            case ORANGE ->      WindowBlockEntities.ORANGE_PORTAL;
            case YELLOW ->      WindowBlockEntities.YELLOW_PORTAL;
            case LIME ->        WindowBlockEntities.LIME_PORTAL;
            case GREEN ->       WindowBlockEntities.GREEN_PORTAL;
            case CYAN ->        WindowBlockEntities.CYAN_PORTAL;
            case LIGHT_BLUE ->  WindowBlockEntities.LIGHT_BLUE_PORTAL;
            case BLUE ->        WindowBlockEntities.BLUE_PORTAL;
            case PURPLE ->      WindowBlockEntities.PURPLE_PORTAL;
            case MAGENTA ->     WindowBlockEntities.MAGENTA_PORTAL;
            case PINK ->        WindowBlockEntities.PINK_PORTAL;
        };
    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);

        output.store(
            "frame",
            Identifier.CODEC,
            BuiltInRegistries.BLOCK.getKey(frame)
        );
        output.store(
            "igniter",
            Identifier.CODEC,
            BuiltInRegistries.BLOCK.getKey(igniter)
        );
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);

        input.read("frame", Identifier.CODEC)
            .flatMap(BuiltInRegistries.BLOCK::getOptional)
            .ifPresent(
                block -> this.frame = block
            )
        ;
        input.read("igniter", Identifier.CODEC)
            .flatMap(BuiltInRegistries.BLOCK::getOptional)
            .ifPresent(
                block -> this.igniter = block
            )
        ;
    }

    public static void tick(
        Level level, BlockPos pos, BlockState state,
        PortalBlockEntity blockEntity
    ) {
        if( level.isClientSide() ) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;

        //every tick
        blockEntity.initializeHolder(serverLevel);

        RandomSource random = serverLevel.getRandom();
        if( random.nextInt(20) != 0) {
            //animateTick() is 20x less often than tick()
            return;
        }

        // Sever-side Portal Animation
        if( random.nextInt(100) == 0 ) {
            serverLevel.playSound(
                null,
                (double)pos.getX() + (double)0.5F,
                (double)pos.getY() + (double)0.5F,
                (double)pos.getZ() + (double)0.5F,
                SoundEvents.PORTAL_AMBIENT,
                SoundSource.BLOCKS,
                0.5F,
                random.nextFloat() * 0.4F + 0.8F
            );
        }
        for(int i = 0; i < 4; ++i) {
            double x = (double)pos.getX() + random.nextDouble();
            double y = (double)pos.getY() + random.nextDouble();
            double z = (double)pos.getZ() + random.nextDouble();
            double xa = ((double)random.nextFloat() - (double)0.5F) * (double)0.5F;
            double ya = ((double)random.nextFloat() - (double)0.5F) * (double)0.5F;
            double za = ((double)random.nextFloat() - (double)0.5F) * (double)0.5F;

            int flip = random.nextInt(2) * 2 - 1;

            switch( state.getValue(BlockStateProperties.AXIS) ) {
                case X:
                    x  = (double)pos.getX() + (double)0.5F + (double)0.25F * (double)flip;
                    xa = (double)(random.nextFloat() * 2.0F * (float)flip);
                    break;
                case Y:
                    y  = (double)pos.getY() + (double)0.5F + (double)0.25F * (double)flip;
                    ya = (double)(random.nextFloat() * 2.0F * (float)flip);
                    break;
                case Z:
                    z  = (double)pos.getZ() + (double)0.5F + (double)0.25F * (double)flip;
                    za = (double)(random.nextFloat() * 2.0F * (float)flip);
                    break;
            }
            //TODO: revisit trying to make particles appear to move towards the portal
            ( (ServerLevel)level ).sendParticles(
                new DustParticleOptions(
                    ( (PortalBlock)state.getBlock() ).COLOR.getMapColor().col,
                    1.0F
                ),
                x, y, z, 1,
                xa, ya, za, 1.0
            );
        }
    }
    private static boolean isPortal(Level level, BlockPos pos, Direction direction) {
        return level.getBlockState( pos.relative(direction, 1) ).getBlock() instanceof PortalBlock;
    }

    public Block getFrame() {
        return frame;
    }
    public Block getIgniter() {
        return igniter;
    }

    public void setFrame(Block frame) {
        this.frame = frame;
        this.setChanged();
    }
    public void setIgniter(Block igniter) {
        this.igniter = igniter;
        this.setChanged();
    }

    @Override
    public void setRemoved() {
        if(attachment != null) {
            attachment.destroy();
            attachment = null;
        }

        super.setRemoved();
    }
}
