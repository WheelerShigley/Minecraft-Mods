package me.wheelershigley.silktouchplus.helpers;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.nbt.NbtElement;

import java.util.List;
import java.util.function.Supplier;

public class CopyNbtLootFunctionInnerClasses {
    public static record OperationRecord(NbtPathArgumentType.NbtPath parsedSourcePath, NbtPathArgumentType.NbtPath parsedTargetPath, CopyNbtLootFunction.Operator operator) {
        public static final Codec<OperationRecord> CODEC = RecordCodecBuilder.create(
            (instance) -> {
                return instance.group(
                    NbtPathArgumentType.NbtPath.CODEC.fieldOf("source").forGetter(OperationRecord::parsedSourcePath),
                    NbtPathArgumentType.NbtPath.CODEC.fieldOf("target").forGetter(OperationRecord::parsedTargetPath),
                    CopyNbtLootFunction.Operator.CODEC.fieldOf("op").forGetter(OperationRecord::operator)
                )
                .apply(instance, OperationRecord::new);
            }
        );

        public OperationRecord(NbtPathArgumentType.NbtPath parsedSourcePath, NbtPathArgumentType.NbtPath parsedTargetPath, CopyNbtLootFunction.Operator operator) {
            this.parsedSourcePath = parsedSourcePath;
            this.parsedTargetPath = parsedTargetPath;
            this.operator = operator;
        }

        public void execute(Supplier<NbtElement> itemNbtGetter, NbtElement sourceEntityNbt) {
            try {
                List<NbtElement> list = this.parsedSourcePath.get(sourceEntityNbt);
                if( !list.isEmpty() ) {
                    this.operator.merge(
                        itemNbtGetter.get(),
                        this.parsedTargetPath,
                        list
                    );
                }
            } catch (CommandSyntaxException var4) {
            }
        }

        public NbtPathArgumentType.NbtPath parsedSourcePath() {
            return this.parsedSourcePath;
        }

        public NbtPathArgumentType.NbtPath parsedTargetPath() {
            return this.parsedTargetPath;
        }

        public CopyNbtLootFunction.Operator operator() {
            return this.operator;
        }
    }
}