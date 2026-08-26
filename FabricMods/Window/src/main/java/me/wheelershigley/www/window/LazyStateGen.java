package me.wheelershigley.www.window;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static me.wheelershigley.www.window.Window.MOD_ID;
import static me.wheelershigley.www.window.Window.getWindowIdentifier;

public class LazyStateGen {
    private static final List<String> COLORS = List.of(
    "white", "light_gray", "gray", "black",
        "brown", "red", "orange", "yellow",
        "lime", "green", "cyan", "light_blue",
        "blue", "purple", "magenta", "pink"
    );

    public static void generate() {
        Path resourceRoot = FabricLoader.getInstance()
            .getGameDir()
            .resolve("../src/main/resources/")
        ;

        for(String color : COLORS) {
            Identifier id = getWindowIdentifier(color + "_portal");

            try {
                // {color}_portal.json (blockstates)
                Path blockStatePath = resourceRoot
                    .resolve("assets")
                    .resolve(MOD_ID)
                    .resolve("blockstates")
                    .resolve(color + "_portal.json")
                ;
                Files.createDirectories( blockStatePath.getParent() );
                Files.writeString(blockStatePath, getBlockState(color), StandardCharsets.UTF_8);

                // {color}_portal_{axis}.json (blockmodels)
                for( Direction.Axis axis : Direction.Axis.values() ) {
                    Path blockModelPath = resourceRoot
                        .resolve("assets")
                        .resolve(MOD_ID)
                        .resolve("models").resolve("block")
                        .resolve(color + "_portal_" + axis.toString().toLowerCase() + ".json")
                    ;
                    Files.createDirectories( blockModelPath.getParent() );
                    Files.writeString(blockModelPath, getBlockModel(color, axis), StandardCharsets.UTF_8);
                }

                // {color}_portal.png.mcmeta (texture-animations)
                for( DyeColor _color : DyeColor.values() ) {
                    Path blockModelPath = resourceRoot
                        .resolve("assets")
                        .resolve(MOD_ID)
                        .resolve("textures").resolve("block")
                        .resolve(_color.getSerializedName() + "_portal.png.mcmeta")
                    ;
                    Files.createDirectories( blockModelPath.getParent() );
                    Files.writeString(blockModelPath, MCMETA, StandardCharsets.UTF_8);
                }
            } catch(IOException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    private static String getBlockState(String color) {
        if( !color.isEmpty() ) {
            color += '_';
        }
        return
            "{\n" +
            "  \"variants\": {\n" +
            "    \"axis=x\": {\n" +
            "      \"model\": \"window:block/" + color + "portal_x\"\n" +
            "    },\n" +
            "    \"axis=y\": {\n" +
            "      \"model\": \"window:block/" + color + "portal_y\"\n" +
            "    },\n" +
            "    \"axis=z\": {\n" +
            "      \"model\": \"window:block/" + color + "portal_z\"\n" +
            "    }\n" +
            "  }\n" +
            "}\n"
        ;
    }
    private static String getBlockModel(String color, Direction.Axis axis) {
        final String from, to; {
            from = switch(axis) {
                case X -> "6, 0, 0";
                case Y -> "0, 6, 0";
                case Z -> "0, 0, 6";
            };
            to = switch(axis) {
                case X -> "10, 16, 16";
                case Y -> "16, 10, 16";
                case Z -> "16, 16, 10";
            };
        }

        String firstDirection = "", secondDirection = ""; {
            switch(axis) {
                case X:
                    firstDirection  = "east";
                    secondDirection = "west";
                    break;
                case Y:
                    firstDirection  = "up";
                    secondDirection = "down";
                    break;
                case Z:
                    firstDirection  = "north";
                    secondDirection = "south";
                    break;
            }
        }

        return
            "{\n" +
            "  \"textures\": {\n" +
            "    \"portal\": \"window:block/" + color + "_portal\",\n" +
            "    \"particle\": \"#portal\"\n" +
            "  },\n" +
            "  \"elements\": [\n" +
            "    {\n" +
            "      \"from\": [" + from + "],\n" +
            "      \"to\": [" + to + "],\n" +
            "      \"faces\": {\n" +
            "        \"" + firstDirection + "\": {\n" +
            "          \"texture\": \"#portal\"\n" +
            "        },\n" +
            "        \"" + secondDirection + "\": {\n" +
            "          \"texture\": \"#portal\"\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}"
        ;
    }
    private static final String MCMETA =
        "{\n" +
        "  \"animation\": {\n" +
        "    \"frametime\": 2\n" +
        "  }\n" +
        "}\n"
    ;
}
