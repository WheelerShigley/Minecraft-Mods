package me.wheelershigley.itemlogger.config;

import me.wheelershigley.itemlogger.client.ItemLoggerClient;

import static me.wheelershigley.itemlogger.client.ItemLoggerClient.configs;

public class ConfigHelpers {
    public static ItemLoggerClient.Mode getDefaultMode() {
        ItemLoggerClient.Mode output = ItemLoggerClient.Mode.OFF;
        switch( configs.getConfig("DefaultMode") ) {
            case "log" ->        output = ItemLoggerClient.Mode.LOG;
            ///case "database" -> output =  Mode.DATABASE;
            //default ->           mode = Mode.OFF;
        }
        return output;
    }
}
