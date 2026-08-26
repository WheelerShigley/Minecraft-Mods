package me.wheelershigley.www.window;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

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

        for (String color : COLORS) {
            Identifier id = getWindowIdentifier(color + "_portal");

            try {
                Path path = resourceRoot
                    .resolve("assets")
                    .resolve(MOD_ID)
                    .resolve("blockstates")
                    .resolve(color + "_portal.json")
                ;

                Files.createDirectories(path.getParent());
                Files.writeString(path, BLOCKSTATE, StandardCharsets.UTF_8);
            } catch(IOException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    private static final String BLOCKSTATE =
        "{\n" +
        "  \"variants\": {\n" +
        "    \"axis=x\": {\n" +
        "      \"model\": \"window:block/portal_x\"\n" +
        "    },\n" +
        "    \"axis=y\": {\n" +
        "      \"model\": \"window:block/portal_y\"\n" +
        "    },\n" +
        "    \"axis=z\": {\n" +
        "      \"model\": \"window:block/portal_z\"\n" +
        "    }\n" +
        "  }\n" +
        "}\n"
    ;
}
