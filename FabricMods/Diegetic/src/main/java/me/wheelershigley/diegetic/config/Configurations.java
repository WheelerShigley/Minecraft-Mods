package me.wheelershigley.diegetic.config;

import me.wheelershigley.diegetic.Diegetic;
import net.minecraft.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Configurations {
    private final File configurationFile;
    private final String configurationFileName;

    private HashMap<String, Configuration<?> > configurations = new HashMap<>();

    public Configurations(File configurationFile, String fileName) {
        this.configurationFile = configurationFile;
        this.configurationFileName = fileName;
    }

    public boolean addConfiguration(Configuration<?> configuration) {
        if( configurations.containsKey( configuration.getName() ) ) {
            return false;
        }

        configurations.put(configuration.getName(), configuration);
        return true;
    }

    public Configuration<?> getConfiguration(String key) {
        return configurations.get(key);
    }

    private void createConfigurationFile() throws IOException {
        //if files are missing, create them
        if( this.configurationFile.exists() && !this.configurationFile.isDirectory() ) { return; }
        this.configurationFile.getParentFile().mkdirs();
        Files.createFile( this.configurationFile.toPath() );

        //when new, write default data to files
        StringBuilder configBuilder = new StringBuilder();
        for(Map.Entry<String, Configuration<?> > configuration : configurations.entrySet() ) {
            configBuilder.append( configuration.getValue().getDefaultConfiguration() ).append("\r\n");
        }
        configBuilder.append("\r\n");

        PrintWriter writer = new PrintWriter(this.configurationFile, StandardCharsets.UTF_8);
        writer.write( configBuilder.toString() );
        writer.close();
    }

    private static Configuration<?> parseConfiguration(String line) {
        Pair<String, String> unparsedPair; {
            String[] splitPair = line.split(":");
            if(splitPair.length != 2) {
                return null;
            }

            unparsedPair = new Pair<>(
                splitPair[0].trim(),
                splitPair[1].trim()
            );
        }

        //determine the data-type of the stored data
        if(
               unparsedPair.getRight().equalsIgnoreCase("true")
            || unparsedPair.getRight().equalsIgnoreCase("false")
        ) {
            return new Configuration<Boolean>(
                unparsedPair.getLeft(),
                Boolean.parseBoolean( unparsedPair.getRight() )
            );
        }
        if( unparsedPair.getRight().matches("^[-+0-9]*$") ) {
            return new Configuration<Long>(
                unparsedPair.getLeft(),
                Long.parseLong( unparsedPair.getRight() )
            );
        }
        if( unparsedPair.getRight().matches("^[-+0-9.]*$") ) {
            return new Configuration<Double>(
                unparsedPair.getLeft(),
                Double.parseDouble( unparsedPair.getRight() )
            );
        }

        return new Configuration<String>(
            unparsedPair.getLeft(),
            unparsedPair.getRight()
        );
    }

    private static Configurations loadConfigurations(File configurationFile, String configurationFileName) throws IOException {
        Configurations configurations = new Configurations(configurationFile, configurationFileName);

        Scanner reader = new Scanner( configurations.configurationFile );
        Configuration<?> currentConfiguration;
        for(int lineNumber = 1; reader.hasNextLine(); lineNumber++ ) {
            currentConfiguration = parseConfiguration( reader.nextLine() );
            if(currentConfiguration != null) {
                configurations.addConfiguration(currentConfiguration);
            }
        }

        return configurations;
    }

    public String toString() {
        StringBuilder configurationsBuilder = new StringBuilder();
        for(Map.Entry<String, Configuration<?> > configuration : configurations.entrySet() ) {
            configurationsBuilder
                .append( configuration.getValue().getName()             )
                .append(": ")
                .append( configuration.getValue().getValue() )
                .append("; ")
            ;
        }
        return configurationsBuilder.toString();
    }

    public void reload() {
        if( ! this.configurationFile.exists() ) {
            try {
                createConfigurationFile();
            } catch(IOException ioException) {
                Diegetic.LOGGER.error("Error creating configuration file.");
            }
        }

        Configurations measuredConfigurations;
        try {
            measuredConfigurations = loadConfigurations(this.configurationFile, this.configurationFileName);
        } catch(IOException ioException) {
            measuredConfigurations = null;
            Diegetic.LOGGER.error("Failed to load configurations.");
        }

        //write missing values to file and set custom values
        ArrayList<  Pair< String, Configuration<?> >  > missingConfigurations = new ArrayList<>();
        boolean add_current_configuration;
        for(Map.Entry< String, Configuration<?> > configuration : this.configurations.entrySet() ) {
            add_current_configuration = false;
            if(measuredConfigurations == null) {
                add_current_configuration = true;
            } else {
                if( measuredConfigurations.configurations.containsKey(configuration.getKey() ) ) {
                    Diegetic.configurations
                        .getConfiguration( configuration.getKey() )
                        .setValue( measuredConfigurations.getConfiguration( configuration.getKey() ).getValue() )
                    ;
                } else {
                    add_current_configuration = true;
                }
            }

            if(
                measuredConfigurations != null
                && measuredConfigurations.configurations.containsKey( configuration.getKey() )
                && !measuredConfigurations.configurations.get( configuration.getKey() ).getValue().getClass().equals(
                    this.configurations.get( configuration.getKey() ).getDefaultValue().getClass()
                )
            ) {
                Diegetic.LOGGER.warn(
                    "Type mismatch for configuration \"" +
                    this.configurations.get( configuration.getKey() ).getName() +
                    "\"; set to default value, \"" +
                    this.configurations.get( configuration.getKey() ).getDefaultValue().toString() +
                    "\"."
                );

                this.configurations.get( configuration.getKey() ).setValue(
                    this.configurations.get( configuration.getKey() ).getDefaultValue()
                );
            }

            if(add_current_configuration) {
                missingConfigurations.add(
                    new Pair<>( configuration.getKey(), configuration.getValue() )
                );
            }
        }

        if( configurationFile.exists() ) {
            //read current file
            Scanner reader;
            try {
                reader = new Scanner(this.configurationFile);
            } catch (IOException ioException) {
                Diegetic.LOGGER.error("Error reading configuration file.");
                return;
            }
            StringBuilder fileContentBuilder = new StringBuilder();
            try {
                fileContentBuilder
                    .append(
                        Files.readString(  Paths.get( configurationFile.getAbsolutePath() )  )
                    )
                    .append("\r\n")
                ;
            } catch(IOException ioException) {
                Diegetic.LOGGER.error("Error reading configuration file.");
                return;
            }
            for(Pair< String, Configuration<?> > missingConfiguration : missingConfigurations) {
                fileContentBuilder.append( missingConfiguration.getRight().getDefaultConfiguration() ).append("\r\n");
            }
            reader.close();

            //write missing configurations (at the end of the file)
            PrintWriter writer;
            try {
                writer = new PrintWriter(this.configurationFile, StandardCharsets.UTF_8);
            } catch (IOException ioException) {
                Diegetic.LOGGER.error("Error writing configuration file.");
                return;
            }
            writer.write( fileContentBuilder.toString() );
            writer.close();

        } else {
            try {
                createConfigurationFile();
            } catch (IOException ioException) {
                Diegetic.LOGGER.error("Error creating configuration file.");
            }
        }
    }
}
