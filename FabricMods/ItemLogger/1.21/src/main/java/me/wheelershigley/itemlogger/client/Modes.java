package me.wheelershigley.itemlogger.client;

public class Modes {
    public static Mode[] modes = new Mode[]{
        Mode.OFF,
        Mode.LOG
    };

    public static String toString(Mode mode) {
        if( mode.equals(Mode.OFF) ) {
            return "off";
        }
        if( mode.equals(Mode.LOG) ) {
            return "log";
        }

        return null;
    }
    public static Mode toMode(String name) {
        if( name.equals("off") ) {
            return Mode.OFF;
        }
        if( name.equals("log") ) {
            return Mode.LOG;
        }

        return null;
    }

}
