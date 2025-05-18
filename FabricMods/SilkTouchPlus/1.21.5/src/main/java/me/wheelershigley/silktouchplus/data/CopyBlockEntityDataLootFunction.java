package me.wheelershigley.silktouchplus.data;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.wheelershigley.silktouchplus.SilkTouchPlus;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.*;
import net.minecraft.loot.provider.nbt.LootNbtProvider;
import net.minecraft.loot.provider.nbt.LootNbtProviderTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.StringIdentifiable;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.List;
import java.util.function.Supplier;

public class CopyBlockEntityDataLootFunction extends ConditionalLootFunction  {
    public static final MapCodec<CopyBlockEntityDataLootFunction> CODEC = RecordCodecBuilder.mapCodec(
        (instance) -> {
            return addConditionsField(instance)
                .and(
                    instance.group(
                        LootNbtProviderTypes.CODEC.fieldOf("source").forGetter(
                            (function) -> {
                                return function.source;
                            }
                        ),
                        Operation.CODEC.listOf().fieldOf("ops").forGetter(
                            (function) -> {
                                return function.operations;
                            }
                        )
                    )
                )
                .apply(instance, CopyBlockEntityDataLootFunction::new)
            ;
        }
    );
    private final LootNbtProvider source;
    private final List<Operation> operations;

    protected CopyBlockEntityDataLootFunction(List<LootCondition> conditions, LootNbtProvider source, List<Operation> operations) {
        super(conditions);
        this.source = source;
        this.operations = List.copyOf(operations);
    }

//    private static final LootFunctionType<CopyBlockEntityDataLootFunction> BLOCK_ENTITY = Registry.register(
//        Registries.LOOT_FUNCTION_TYPE,
//        Identifier.ofVanilla("block_entity_data"),
//        new LootFunctionType<>(CopyBlockEntityDataLootFunction.CODEC)
//    );

    @Override
    public LootFunctionType<CopyNbtLootFunction> getType() {
        return LootFunctionTypes.COPY_CUSTOM_DATA;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        NbtElement nbtElement = this.source.getNbt(context);
        if (nbtElement == null) {
            return stack;
        }

        MutableObject<NbtCompound> mutableObject = new MutableObject<>();
        Supplier<NbtElement> supplier = () -> {
            if(mutableObject.getValue() == null) {
                mutableObject.setValue(
                    (
                        stack.getOrDefault(
                            DataComponentTypes.BLOCK_ENTITY_DATA,
                            NbtComponent.DEFAULT
                        )
                    ).copyNbt()
                );
            }

            return (NbtElement)mutableObject.getValue();
        };
        this.operations.forEach(
            (operation) -> {
                operation.execute(supplier, nbtElement);
            }
        );
        NbtCompound nbtCompound = (NbtCompound)mutableObject.getValue();
        if (nbtCompound != null) {
            NbtComponent.set(
                DataComponentTypes.BLOCK_ENTITY_DATA,
                stack,
                nbtCompound
            );
        }

        return stack;
    }

    public static class Builder extends ConditionalLootFunction.Builder<Builder> {
        private final LootNbtProvider source;
        private final List<Operation> operations = Lists.newArrayList();

        public Builder(LootNbtProvider source) {
            this.source = source;
        }

        public Builder withOperation(String source, String target, Operator operator) {
            try {
                this.operations.add(
                    new Operation(
                        NbtPathArgumentType.NbtPath.parse(source),
                        NbtPathArgumentType.NbtPath.parse(target),
                        operator
                    )
                );
                return this;
            } catch (CommandSyntaxException var5) {
                throw new IllegalArgumentException(var5);
            }
        }

        public Builder withOperation(String source, String target) {
            return this.withOperation(source, target, Operator.REPLACE);
        }

        protected Builder getThisBuilder() {
            return this;
        }

        public LootFunction build() {
            return new CopyBlockEntityDataLootFunction(this.getConditions(), this.source, this.operations);
        }
    }

    public static record Operation(
        NbtPathArgumentType.NbtPath parsedSourcePath,
        NbtPathArgumentType.NbtPath parsedTargetPath,
        Operator operator
    ) {
        public static final Codec<Operation> CODEC = RecordCodecBuilder.create(
            (instance) -> {
                return instance.group(
                    NbtPathArgumentType.NbtPath.CODEC.fieldOf("source").forGetter(Operation::parsedSourcePath),
                    NbtPathArgumentType.NbtPath.CODEC.fieldOf("target").forGetter(Operation::parsedTargetPath),
                    Operator.CODEC.fieldOf("op").forGetter(Operation::operator)
                )
                .apply(instance, Operation::new);
            }
        );

        public Operation(
            NbtPathArgumentType.NbtPath parsedSourcePath,
            NbtPathArgumentType.NbtPath parsedTargetPath,
            Operator operator
        ) {
            this.parsedSourcePath = parsedSourcePath;
            this.parsedTargetPath = parsedTargetPath;
            this.operator = operator;
        }

        public void execute(Supplier<NbtElement> itemNbtGetter, NbtElement sourceEntityNbt) {
            try {
                List<NbtElement> list = this.parsedSourcePath.get(sourceEntityNbt);
                if (!list.isEmpty()) {
                    this.operator.merge(
                        itemNbtGetter.get(),
                        this.parsedTargetPath,
                        list
                    );
                }
            } catch (CommandSyntaxException var4) {
                SilkTouchPlus.LOGGER.error("Error in BlockEntity LootFunction copy-execution.");
            }

        }

        public NbtPathArgumentType.NbtPath parsedSourcePath() {
            return this.parsedSourcePath;
        }

        public NbtPathArgumentType.NbtPath parsedTargetPath() {
            return this.parsedTargetPath;
        }

        public Operator operator() {
            return this.operator;
        }
    }

    public static enum Operator implements StringIdentifiable {
        REPLACE("replace") {
            public void merge(NbtElement itemNbt, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceNbts) throws CommandSyntaxException {
                targetPath.put(
                    itemNbt,
                    (NbtElement)Iterables.getLast(sourceNbts)
                );
            }
        },
        APPEND("append") {
            public void merge(NbtElement itemNbt, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceNbts) throws CommandSyntaxException {
                List<NbtElement> list = targetPath.getOrInit(itemNbt, NbtList::new);
                list.forEach(
                    (foundNbt) -> {
                        if(foundNbt instanceof NbtList) {
                            sourceNbts.forEach(
                                (sourceNbt) -> {
                                    ( (NbtList)foundNbt ).add( sourceNbt.copy() );
                                }
                            );
                        }
                    }
                );
            }
        },
        MERGE("merge") {
            public void merge(NbtElement itemNbt, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceNbts) throws CommandSyntaxException {
                List<NbtElement> list = targetPath.getOrInit(itemNbt, NbtCompound::new);
                list.forEach(
                    (foundNbt) -> {
                        if(foundNbt instanceof NbtCompound) {
                            sourceNbts.forEach(
                                (sourceNbt) -> {
                                    if(sourceNbt instanceof NbtCompound) {
                                        ( (NbtCompound)foundNbt ).copyFrom( (NbtCompound)sourceNbt );
                                    }
                                }
                            );
                        }
                    }
                );
            }
        };

        public static final Codec<Operator> CODEC = StringIdentifiable.createCodec(Operator::values);
        private final String name;

        public abstract void merge(NbtElement itemNbt, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceNbts) throws CommandSyntaxException;

        Operator(final String name) {
            this.name = name;
        }

        public String asString() {
            return this.name;
        }
    }
}
