package me.wheelershigley.itemlogger.config;

import me.wheelershigley.itemlogger.ItemLogger;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Config {
    private final HashMap<String, String> DefaultConfigs;
    private final HashMap<String, String> CurrentConfigs;
    private File ConfigFile;

    public Config(/*Defaults with empty default-configurations*/) {
        this.DefaultConfigs = new HashMap<>();
        this.CurrentConfigs = new HashMap<>();

        initialize();
    }
    public Config(HashMap<String, String> DefaultConfigs) {
        this.DefaultConfigs = DefaultConfigs;
        this.CurrentConfigs = new HashMap<>();

        initialize();
    }

    public String getConfig(String input) {
        String output = CurrentConfigs.get(input);
        return (output == null) ? "" : output.toLowerCase();
    }

    //must run after initialization
    public boolean reloadConfigurations() {
        if( ConfigFile.exists() /*&& !ConfigFile.isDirectory()*/ ) {
            /*Read config contents*/ {
                Scanner scanner; {
                    try {
                        scanner = new Scanner(ConfigFile);
                    } catch(FileNotFoundException exception) {
                        ItemLogger.LOGGER.error("Config File Not Found but Exists; Possible Access Error.");
                        return false;
                    }
                }

                String line = "";
                String[] values;
                while( scanner.hasNext() ) {
                    line = scanner.nextLine();
                    line = line.split("#")[0];
                    values = line.split(" ");
                    if(2 <= values.length) {
                        CurrentConfigs.put(values[0], values[1]);
                        ItemLogger.LOGGER.info("Loaded config \""+values[0]+"\" as \""+values[1]+"\".");
                    }
                }

                scanner.close();
            }

            /*Ensure CurrentConfig contains every needed configuration*/ {
                StringBuilder MissingConfigsTextBuilder = new StringBuilder();
                for(Map.Entry<String, String> DesiredConfig : DefaultConfigs.entrySet() ) {
                    if(  CurrentConfigs.containsKey( DesiredConfig.getKey() )  ) { continue; }

                    //when a default-config entry is not included, append it to the end of the file.
                    MissingConfigsTextBuilder.append( DesiredConfig.getKey() ).append(' ').append( DesiredConfig.getValue() );
                    CurrentConfigs.put( DesiredConfig.getKey(), DesiredConfig.getValue() );
                }
                if( !MissingConfigsTextBuilder.isEmpty() ) {
                    String contents; {
                        List<String> lines; {
                            try {
                                lines = Files.readAllLines(Paths.get("file"), StandardCharsets.UTF_8);
                            } catch(IOException exception) {
                                ItemLogger.LOGGER.error("Could not read Config-File.");
                                return false;
                            }
                        }

                        StringBuilder ContentBuilder = new StringBuilder();
                        for(String line : lines) {
                            ContentBuilder.append(line).append("\r\n");
                        }
                        contents = ContentBuilder.toString();
                    }
                    PrintWriter writer; {
                        try {
                            writer = new PrintWriter(ConfigFile, StandardCharsets.UTF_8);
                        } catch(IOException exception) {
                            ItemLogger.LOGGER.error("Could not read Config-File.");
                            return false;
                        }
                    }
                    writer.write(contents + MissingConfigsTextBuilder.toString() );
                    writer.close();
                }
            }
        }

        //Create configurations
        if( !ConfigFile.exists() /*|| ConfigFile.isDirectory()*/ ) {
            ItemLogger.LOGGER.info("Config-file did not exist, creating new one.");
            try {
                createConfigFile(ConfigFile);
            } catch(IOException ioexception) {
                ItemLogger.LOGGER.error("Could not generate Config-file.");
                return false;
            }
        }

        return ConfigFile.exists() && !ConfigFile.isDirectory();
    }

    private String getInitialConfigFileContents(HashMap<String, String> Configs) {
        final String HEADER = "# ItemLogger Configurations\r\n\r\n";

        StringBuilder ConfigTextBuilder = new StringBuilder();
        ConfigTextBuilder.append(HEADER);

        for(Map.Entry<String, String> config : Configs.entrySet()) {
            ConfigTextBuilder.append( config.getKey() ).append(' ').append( config.getValue() ).append("\n");
        }

        return ConfigTextBuilder.toString();
    }

    private void createConfigFile(File file) throws IOException {
        //file.getParentFile().mkdirs();
        Files.createFile( file.toPath() );

        PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8);
        writer.write( getInitialConfigFileContents(DefaultConfigs) );
        writer.close();
    }

    //Loads configs to CurrentConfigs or generates config-file with DefaultConfigs
    private boolean initialize() {
        File ConfigFile = new File(
            FabricLoader.getInstance().getConfigDir().toFile(),
            ItemLogger.MOD_ID +".config"
        );
        this.ConfigFile = ConfigFile;

        return reloadConfigurations();
    }
}
