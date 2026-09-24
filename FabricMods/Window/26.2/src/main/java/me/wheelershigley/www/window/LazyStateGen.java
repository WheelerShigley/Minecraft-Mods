package me.wheelershigley.www.window;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.DyeColor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static me.wheelershigley.www.window.Window.MOD_ID;

public class LazyStateGen {
    private static final String MCMETA =
        "{\n" +
        "  \"animation\": {\n" +
        "    \"frametime\": 2\n" +
        "  }\n" +
        "}\n"
    ;

    public static void generate() {
        Path resourceRoot = FabricLoader.getInstance()
            .getGameDir()
            .resolve("../src/main/resources/")
        ;

        for(DyeColor dyeColor : DyeColor.values() ) {
            String color = dyeColor.getSerializedName();
            try {
                /* {color}_portal_{axis}.json (blockmodels) */ {
                    Path blockModelPath = resourceRoot
                        .resolve("assets")
                        .resolve(MOD_ID)
                        .resolve("models").resolve("block")
                        .resolve(color + "_portal.json")
                    ;
                    Files.createDirectories( blockModelPath.getParent() );
                    Files.writeString(blockModelPath, getModel(color), StandardCharsets.UTF_8);
                }

                /* {color}_portal_{axis}.json (item-models) */ {
                    Path blockModelPath = resourceRoot
                        .resolve("assets")
                        .resolve(MOD_ID)
                        .resolve("models").resolve("item")
                        .resolve(color + "_portal.json")
                    ;
                    Files.createDirectories( blockModelPath.getParent() );
                    Files.writeString(blockModelPath, itemModel(color), StandardCharsets.UTF_8);
                }

                /* {color}_portal_{axis}.json (items) */ {
                    Path blockModelPath = resourceRoot
                        .resolve("assets")
                        .resolve(MOD_ID)
                        .resolve("items")
                        .resolve(color + "_portal.json")
                    ;
                    Files.createDirectories( blockModelPath.getParent() );
                    Files.writeString(blockModelPath, itemModel(color), StandardCharsets.UTF_8);
                }

                /* {color}_portal.png.mcmeta (texture-animations) */ {
                    Path blockModelPath = resourceRoot
                        .resolve("assets")
                        .resolve(MOD_ID)
                        .resolve("textures").resolve("block")
                        .resolve(color + "_portal.png.mcmeta")
                    ;
                    Files.createDirectories( blockModelPath.getParent() );
                    Files.writeString(blockModelPath, MCMETA, StandardCharsets.UTF_8);
                }
            } catch(IOException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    private static String getModel(final String color) {
        return
            "{\n" +
            "  \"textures\": {\n" +
            "    \"portal\": \"window:block/" + color + "_portal\",\n" +
            "    \"particle\": \"#portal\"\n" +
            "  },\n" +
            "  \"elements\": [\n" +
            "    {\n" +
            "      \"from\": [6, 0, 0],\n" +
            "      \"to\": [10, 16, 16],\n" +
            "      \"faces\": {\n" +
            "        \"east\": {\n" +
            "          \"texture\": \"#portal\"\n" +
            "        },\n" +
            "        \"west\": {\n" +
            "          \"texture\": \"#portal\"\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}"
        ;
    }
    private static String itemModel(final String color) {
        return
            "{\n" +
            "  \"model\": {\n" +
            "    \"type\": \"minecraft:model\",\n" +
            "    \"model\": \"window:block/" + color + "_portal\"\n" +
            "  }\n" +
            "}\n"
        ;
    }
}
