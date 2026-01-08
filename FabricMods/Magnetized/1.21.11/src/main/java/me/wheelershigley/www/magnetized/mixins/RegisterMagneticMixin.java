package me.wheelershigley.www.magnetized.mixins;

import me.wheelershigley.www.magnetized.Magnetized;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.effect.AllOfEnchantmentEffects;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.enchantment.effect.entity.ApplyExhaustionEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.ApplyImpulseEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.ChangeItemDamageEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.PlaySoundEnchantmentEffect;
import net.minecraft.item.Item;
import net.minecraft.loot.condition.AllOfLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.InvertedLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Enchantments.class)
public class RegisterMagneticMixin {
    @Shadow
    private static void register(Registerable<Enchantment> registry, RegistryKey<Enchantment> key, Enchantment.Builder builder) {}

    @Inject(
        method = "bootstrap",
        at = @At("TAIL")
    )
    private static void bootstrap(
            Registerable<Enchantment> registry,
            CallbackInfo ci
    ) {
        RegistryEntryLookup<Item> registryEntryLookup3 = registry.getRegistryLookup(RegistryKeys.ITEM);

        register(
            registry,
            Magnetized.MAGNETIC,
            Enchantment
                .builder(
                    Enchantment.definition(
                        registryEntryLookup3.getOrThrow(ItemTags.LUNGE_ENCHANTABLE),
                        5,
                        3,
                        Enchantment.leveledCost(5, 8),
                        Enchantment.leveledCost(25, 8),
                        2,
                        new AttributeModifierSlot[]{AttributeModifierSlot.HAND}
                    )
                )
            .addEffect(
                EnchantmentEffectComponentTypes.POST_PIERCING_ATTACK,
                AllOfEnchantmentEffects.allOf(
                    new EnchantmentEntityEffect[]{
                        new ChangeItemDamageEnchantmentEffect(
                            new EnchantmentLevelBasedValue.Constant(1.0F)
                        ),
                        new ApplyExhaustionEnchantmentEffect(
                            EnchantmentLevelBasedValue.linear(4.0F)
                        ),
                        new ApplyImpulseEnchantmentEffect(
                            new Vec3d((double)0.0F, (double)0.0F, (double)1.0F),
                            new Vec3d((double)1.0F, (double)0.0F, (double)1.0F),
                            EnchantmentLevelBasedValue.linear(0.458F)
                        ),
                        new PlaySoundEnchantmentEffect(
                            List.of(
                                SoundEvents.ITEM_SPEAR_LUNGE_1,
                                SoundEvents.ITEM_SPEAR_LUNGE_2,
                                SoundEvents.ITEM_SPEAR_LUNGE_3
                            ),
                            ConstantFloatProvider.create(1.0F),
                            ConstantFloatProvider.create(1.0F)
                        )
                    }
                ),
                AllOfLootCondition.builder(
                    new LootCondition.Builder[]{
                        InvertedLootCondition.builder(
                            EntityPropertiesLootCondition.builder(
                                LootContext.EntityReference.THIS,
                                net.minecraft.predicate.entity.EntityPredicate.Builder.create().vehicle( net.minecraft.predicate.entity.EntityPredicate.Builder.create() )
                            )
                        ),
                        EntityPropertiesLootCondition.builder(
                            LootContext.EntityReference.THIS,
                            net.minecraft.predicate.entity.EntityPredicate.Builder.create().flags( net.minecraft.predicate.entity.EntityFlagsPredicate.Builder.create().isFallFlying(false) )
                        ),
                        EntityPropertiesLootCondition.builder(
                            LootContext.EntityReference.THIS,
                            net.minecraft.predicate.entity.EntityPredicate.Builder.create().flags( net.minecraft.predicate.entity.EntityFlagsPredicate.Builder.create().isInWater(false) )
                        )
                    }
                )
            )
        );
    }
}
