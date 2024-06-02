package me.wheelershigley.graphmaker;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.wheelershigley.graphmaker.command.CommandHelper.PLUGIN_NAME_PREFIX;

public class Creator {
    private static String TAB = "  ";

    private static String titleize(String title) {
        StringBuilder title_builder = new StringBuilder();

        boolean capitalize_next = true;
        for(char current : title.toCharArray() ) {
            if(current == ' ' || current == '_') {
                title_builder.append("\\n");
                capitalize_next = true;
                continue;
            }

            if(capitalize_next) {
                title_builder.append( Character.toUpperCase(current) );
            } else {
                title_builder.append( Character.toLowerCase(current) );
            }

            if(current == '-' || current == '\n') {
                capitalize_next = true;
                continue;
            }

            capitalize_next = false;
        }

        return title_builder.toString();
    }
    private static List<String> dive(SlimefunItem item) {
        String item_name = PlainTextComponentSerializer.plainText().serialize( item.getItem().displayName() ); item_name = item_name.substring(1,item_name.length()-1);

        ItemStack[] parts; {
            SlimefunItem sf_item = SlimefunItem.getById( item_name.toUpperCase().replace(' ','_') );
            if(sf_item == null) { return null; }
            parts = sf_item.getRecipe();
        }

        List<String> returnable = new ArrayList<>();

        HashMap<String,Integer> parts_counts = new HashMap<>();
        String name, id;
        for(ItemStack part : parts) {
            if(part == null) { continue; }

            name = PlainTextComponentSerializer.plainText().serialize( part.displayName() ); name = name.substring(1,name.length()-1);
            id = GraphingPlugin.name_id_association.get(name);
            if(id == null) { //non-slimefun item
                id = name.toUpperCase().replace(' ','_');
            }

            if( parts_counts.containsKey(id) ) {
                parts_counts.put(id, parts_counts.get(id)+item.getItem().getAmount() );
            } else {
                parts_counts.put(id, item.getItem().getAmount() );
            }
        }

        name = GraphingPlugin.name_id_association.get(item_name);
        for(Map.Entry<String,Integer> part : parts_counts.entrySet() ) {
            returnable.add( part.getKey()+"->"+name +"[label="+part.getValue()+"]");
        }

        return returnable;
    }
    public static void makeGraph(SlimefunItem item) throws IOException {
        String id = item.getId();

        //ensure folder exists
        Path data_folder = Paths.get( GraphingPlugin.instance.getDataFolder().getAbsolutePath() );
        if(Files.notExists(data_folder) ) {
            //create folder
            new File( String.valueOf(data_folder) ).mkdir();
        }

        //check if file already exists
        data_folder = Paths.get( String.valueOf(data_folder)+ "/"+item.getId()+".dot" );
        File digraph;
        if(Files.notExists(data_folder) ) {
            //create file
            digraph = new File( String.valueOf(data_folder) );
            try {
                digraph.createNewFile();
            } catch(IOException io_exception) {
                GraphingPlugin.instance.getLogger().warning(PLUGIN_NAME_PREFIX+" §cUnable to create director, \"§4§o"+digraph.getAbsolutePath()+"§r§4\".");
                return;
            }
        }

        //construct String for file
        StringBuilder graph_code = new StringBuilder(); {
            graph_code.append("digraph ").append( item.getId() ).append(" {\n").append(TAB).append(id).append("\n");

                List<String> unique_ids = new ArrayList<>();

                List<String> materials = dive(item);
                if(materials != null) {
                    final String DOUBLE_TAB = TAB+TAB;
                    StringBuilder current_id = new StringBuilder(); char previous = ' '; char[] quantized_element;
                    for (String element : materials) {
                        graph_code.append(DOUBLE_TAB).append(element).append("\n");

                        quantized_element = element.toCharArray();
                        for(char current : quantized_element) {
                            if(previous == '-' && current == '>') {
                                current_id.setLength( current_id.length()-1 );
                                break;
                            }

                            current_id.append(current);

                            previous = current;
                        }

                        if( !unique_ids.contains( current_id.toString() ) ) {
                            unique_ids.add( current_id.toString() );
                            current_id.setLength(0);
                        }
                    }
                }

            graph_code.append("\n");

                graph_code.append(TAB).append(id).append("[label=\"").append( titleize(id) ).append("\"]\n");
                for(String current : unique_ids) {
                    graph_code.append(TAB).append(current).append("[label=\"").append( titleize(current) ).append("\"]\n");
                }

            graph_code.append("}");
        }

        //set file filled with constructed data
        List<String> lines = new ArrayList<>(); {
            StringBuilder current_line = new StringBuilder();
            for(char current : graph_code.toString().toCharArray() ) {
                if(current == '\n') {
                    lines.add( current_line.toString() );
                    current_line.setLength(0);
                } else {
                    current_line.append(current);
                }
            }
            if(0 < current_line.length() ) {
                lines.add( current_line.toString() );
            }
        }
        Files.write(data_folder, lines, StandardCharsets.UTF_8);
    }

}
