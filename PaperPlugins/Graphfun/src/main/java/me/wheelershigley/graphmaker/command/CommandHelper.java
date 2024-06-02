package me.wheelershigley.graphmaker.command;

import me.wheelershigley.graphmaker.GraphingPlugin;

public class CommandHelper {

    public final static String PLUGIN_NAME_PREFIX = "§r§9[§2"+ GraphingPlugin.instance.getName()+"§9]§7:§r ";

    public static boolean startsWith(String origin, String includesAtStart) {
        final char[] quantized_origin = origin.toCharArray();
        final char[] quantized_query = includesAtStart.toCharArray();

        //unnecessary check, speeds up some testcases
        if(quantized_origin.length < quantized_query.length) { return false; }

        for(int i = 0; i < quantized_query.length; i++) {
            if( quantized_origin[i] != quantized_query[i] ) { return false; }
        }
        return true;
    }
}
