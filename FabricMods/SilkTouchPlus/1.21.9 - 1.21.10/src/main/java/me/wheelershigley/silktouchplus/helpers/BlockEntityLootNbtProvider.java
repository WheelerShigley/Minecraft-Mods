package me.wheelershigley.silktouchplus.helpers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.provider.nbt.LootNbtProvider;
import net.minecraft.loot.provider.nbt.LootNbtProviderType;
import net.minecraft.loot.provider.nbt.LootNbtProviderTypes;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.context.ContextParameter;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

//copy of BlockEntityLootNbtProvider, as of 1.21.8
public class BlockEntityLootNbtProvider implements LootNbtProvider {
    private static final String BLOCK_ENTITY_TARGET_NAME = "block_entity";
    private static final BlockEntityLootNbtProvider.Target BLOCK_ENTITY_TARGET = new BlockEntityLootNbtProvider.Target() {
        public NbtElement getNbt(LootContext context) {
            BlockEntity blockEntity = (BlockEntity)context.get(LootContextParameters.BLOCK_ENTITY);
            if(blockEntity != null && blockEntity.getWorld() != null) {
                return blockEntity.createNbtWithIdentifyingData(
                    blockEntity.getWorld().getRegistryManager()
                );
            }
            return null;
        }

        public String getName() {
            return "block_entity";
        }

        public Set<ContextParameter<?>> getRequiredParameters() {
            return Set.of(LootContextParameters.BLOCK_ENTITY);
        }
    };
    public static final BlockEntityLootNbtProvider BLOCK_ENTITY;
    private static final Codec<BlockEntityLootNbtProvider.Target> TARGET_CODEC;
    public static final MapCodec<BlockEntityLootNbtProvider> CODEC;
    public static final Codec<BlockEntityLootNbtProvider> INLINE_CODEC;
    private final BlockEntityLootNbtProvider.Target target;

    public BlockEntityLootNbtProvider(Target target) {
        this.target = target;
    }

    /*
    private static BlockEntityLootNbtProvider.Target getTarget(final LootContext.EntityTarget entityTarget) {
        return new BlockEntityLootNbtProvider.Target(entityTarget) {
            @Nullable
            public NbtElement getNbt(LootContext context) {
                Entity entity = (Entity)context.get(entityTarget.getParameter());
                return entity != null ? NbtPredicate.entityToNbt(entity) : null;
            }

            public String getName() {
                return entityTarget.name();
            }

            public Set<ContextParameter<?>> getRequiredParameters() {
                return Set.of(entityTarget.getParameter());
            }
        };
    }
     */

    public LootNbtProviderType getType() {
        return LootNbtProviderTypes.CONTEXT;
    }

    @Nullable
    public NbtElement getNbt(LootContext context) {
        return this.target.getNbt(context);
    }

    public Set<ContextParameter<?>> getRequiredParameters() {
        return this.target.getRequiredParameters();
    }

    /*
    public static LootNbtProvider fromTarget(LootContext.EntityTarget target) {
        return new BlockEntityLootNbtProvider(getTarget(target));
    }
     */

    static {
        BLOCK_ENTITY = new BlockEntityLootNbtProvider(BLOCK_ENTITY_TARGET);
        TARGET_CODEC = Codec.STRING.xmap(
            (type) -> {
                if (type.equals("block_entity")) {
                    return BLOCK_ENTITY_TARGET;
    //            } else {
    //                LootContext.EntityTarget entityTarget = EntityTarget.fromString(type);
    //                return getTarget(entityTarget);
                }
                return null;
            },
            BlockEntityLootNbtProvider.Target::getName
        );
        CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(TARGET_CODEC.fieldOf("target").forGetter((provider) -> provider.target)).apply(instance, BlockEntityLootNbtProvider::new));
        INLINE_CODEC = TARGET_CODEC.xmap(BlockEntityLootNbtProvider::new, (provider) -> provider.target);
    }

    interface Target {
        @Nullable
        NbtElement getNbt(LootContext context);

        String getName();

        Set<ContextParameter<?>> getRequiredParameters();
    }
}
