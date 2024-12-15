package me.wheelershigley.unlimited_anvil;

public class HelperFunctions {
    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)"+regex+"(?!.*?"+regex+")", replacement);
    }

    public static <T> boolean contains(T[] set, T element) {
        if(set == null) { return false; }

        for(T subset : set) {
            if(subset.equals(element) ) {
                return true;
            }
        }
        return false;
    }
}
