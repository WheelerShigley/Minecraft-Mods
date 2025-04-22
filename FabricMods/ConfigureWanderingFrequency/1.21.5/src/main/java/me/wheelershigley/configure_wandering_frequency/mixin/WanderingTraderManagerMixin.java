package me.wheelershigley.configure_wandering_frequency.mixin;

import me.wheelershigley.configure_wandering_frequency.ConfigureWanderingFrequency;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.WanderingTraderManager;
import net.minecraft.world.WorldView;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;
import net.minecraft.world.spawner.SpecialSpawner;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(WanderingTraderManager.class)
public abstract class WanderingTraderManagerMixin implements SpecialSpawner {
	@Shadow @Final @Mutable private ServerWorldProperties properties;
	@Shadow @Mutable private int spawnDelay;
	@Shadow @Mutable private int spawnTimer;

	@Shadow
	@Nullable
	private BlockPos getNearbySpawnPos(WorldView world, BlockPos pos, int range) {
		return null;
	}

	@Shadow
	private boolean doesNotSuffocateAt(BlockView world, BlockPos pos) {
		return false;
	}

	@Shadow
	private void spawnLlama(ServerWorld world, WanderingTraderEntity wanderingTrader, int range) {}

	@Shadow
	private boolean trySpawn(ServerWorld world) {
		return false;
	}

	@Inject(
		method = "spawn",
		at = @At("HEAD"),
		cancellable = true
	)
	private void spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals, CallbackInfo ci) {
		if( world.getGameRules().getBoolean(GameRules.DO_TRADER_SPAWNING) ) {
			if(--this.spawnTimer <= 0) {
				this.spawnTimer = 600;
				this.spawnDelay -= 600;
				this.properties.setWanderingTraderSpawnDelay(this.spawnDelay);
				if(this.spawnDelay <= 0) {
					this.spawnDelay = 1200;
					if( world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) ) {
						ConfigureWanderingFrequency.LOGGER.info("trySpawn()");
						boolean success = false;
						while(!success) {
							success = trySpawn(world);
						}
					}
				}
			}
		}

		ci.cancel();
	}

	@Inject(
		method = "<init>",
		at = @At("TAIL")
	)
	private void WanderingTraderManager(ServerWorldProperties properties, CallbackInfo ci) {
		spawnTimer = 600;
		this.spawnDelay = 1200;
	}

	/**
	 *
	 * @author WanderingTraderManager.trySpawn
	 * @reason Basecode gives a 10% chance of even trying, this changes it to 100% chance of at least trying
	 */
	@Inject(
		method = "trySpawn",
		at = @At("TAIL"),
		cancellable = true
	)
	private void trySpawn(ServerWorld world, CallbackInfoReturnable<Boolean> cir) {
		PlayerEntity playerEntity = world.getRandomAlivePlayer();
		if(playerEntity == null) {
			cir.setReturnValue(true);
		}

		BlockPos blockPos = playerEntity.getBlockPos();
		PointOfInterestStorage pointOfInterestStorage = world.getPointOfInterestStorage();
		Optional<BlockPos> optional = pointOfInterestStorage.getPosition(
				poiType -> poiType.matchesKey(PointOfInterestTypes.MEETING),
				pos -> true, blockPos, 48, PointOfInterestStorage.OccupationStatus.ANY
		);
		BlockPos blockPos2 = optional.orElse(blockPos);
		BlockPos blockPos3 = this.getNearbySpawnPos(world, blockPos2, 48);
		if(blockPos3 != null && this.doesNotSuffocateAt(world, blockPos3)) {
			if(world.getBiome(blockPos3).isIn(BiomeTags.WITHOUT_WANDERING_TRADER_SPAWNS)) {
				cir.setReturnValue(false);
			}

			WanderingTraderEntity wanderingTraderEntity = EntityType.WANDERING_TRADER.spawn(world, blockPos3, SpawnReason.EVENT);
			if(wanderingTraderEntity != null) {
				for(int j = 0; j < 2; j++) {
					this.spawnLlama(world, wanderingTraderEntity, 4);
				}

				this.properties.setWanderingTraderId(wanderingTraderEntity.getUuid());
				wanderingTraderEntity.setDespawnDelay(48000);
				wanderingTraderEntity.setWanderTarget(blockPos2);
				wanderingTraderEntity.setPositionTarget(blockPos2, 16);
				cir.setReturnValue(true);
			}
		}

		cir.setReturnValue(false);
	}

}