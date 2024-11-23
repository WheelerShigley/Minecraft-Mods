package me.wheelershigley.silktouchplus.helpers;

import net.minecraft.item.Item;

public class ArrayHelpers {
    public static boolean itemListContainsElement(Item[] set, Item key) {
        for(Item element : set) {
            if( key.equals(element) ) {
                return true;
            }
        }
        return false;
    }
    public static boolean stringListContainsElement(String[] set, String key) {
        for(String element : set) {
            if( key.equals(element) ) {
                return true;
            }
        }
        return false;
    }
}
