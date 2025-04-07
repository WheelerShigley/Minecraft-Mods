package me.wheelershigley.charged.config;

import me.wheelershigley.charged.Charged;
import net.minecraft.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

            unparsedPair = new Pair<>(splitPair[0], splitPair[1]);
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
        if( unparsedPair.getRight().matches("^[0-9]*$") ) {
            return new Configuration<Long>(
                unparsedPair.getLeft(),
                Long.parseLong( unparsedPair.getRight() )
            );
        }
        if( unparsedPair.getRight().matches("^[0-9.]*$") ) {
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
                Charged.LOGGER.error("Error creating Configuration File.");
            }
        }

        Configurations measuredConfigurations;
        try {
            measuredConfigurations = loadConfigurations(this.configurationFile, this.configurationFileName);
        } catch(IOException ioException) {
            measuredConfigurations = null;
            Charged.LOGGER.error("Failed to load Configurations");
        }

        //write missing values to file
        //TODO
        Charged.LOGGER.info("Measured configurations as: {" + measuredConfigurations.toString() + "}");

        Charged.LOGGER.info("Loaded configurations as: {" + configurations.toString() + "}");
    }
}
