package me.solacekairos.experiencebottler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class xpb implements TabExecutor {

    private TextComponent constructColorfullName(char[] word, short[][] values) {
        if(word.length < 1 || values.length < 1) { return null; }
        TextComponent name = Component.text(0);

        int shorter; if(word.length < values.length) { shorter = word.length; } else { shorter = values.length; }
        for(int i = 0; i < shorter; i++ ) {
            name.append(  Component.text( word[i] , TextColor.color(values[i][0], values[i][1], values[i][2]) )  );
        }

        return name;
    }
    TextComponent name = constructColorfullName(
            new char[]    {'E','x','p','e','r','i','e','n','c','e',' ','B','o','t','t','l','e'},
            new short[][] { {113, 109, 15 }, {171, 106, 15 }, {229, 103, 16 }, {246, 145, 23 }, {246, 201, 32 },
                            {246, 217, 34 }, {245, 153, 24 }, {232, 103, 16 }, {181, 105, 15 }, {128, 108, 15 },
                            {0  , 0  , 0  }, {10 , 111, 3  }, {3  , 221, 42 }, {0  , 153, 32 }, {1  , 159, 33 },
                            {3  , 221, 42 }, {10 , 104, 1  } }
    );

    /*
    TextComponent nameA = Component.text('E', TextColor.color(113, 109, 15 ) )
            .append(     Component.text('x', TextColor.color(171, 106, 15 ) ) )
            .append(     Component.text('p', TextColor.color(229, 103, 16 ) ) )
            .append(     Component.text('e', TextColor.color(246, 145, 23 ) ) )
            .append(     Component.text('r', TextColor.color(246, 201, 32 ) ) )
            .append(     Component.text('i', TextColor.color(246, 217, 34 ) ) )
            .append(     Component.text('e', TextColor.color(245, 153, 24 ) ) )
            .append(     Component.text('n', TextColor.color(232, 103, 16 ) ) )
            .append(     Component.text('c', TextColor.color(181, 105, 15 ) ) )
            .append(     Component.text('e', TextColor.color(128, 108, 15 ) ) )
            .append(     Component.text(' ', TextColor.color(0  , 0  , 0  ) ) )
            .append(     Component.text('B', TextColor.color(10 , 111, 3  ) ) )
            .append(     Component.text('o', TextColor.color(3  , 228, 45 ) ) )
            .append(     Component.text('t', TextColor.color(0  , 153, 32 ) ) )
            .append(     Component.text('t', TextColor.color(1  , 159, 33 ) ) )
            .append(     Component.text('l', TextColor.color(3  , 221, 42 ) ) )
            .append(     Component.text('e', TextColor.color(10 , 104, 1  ) ) );
     */

    private float pointsInLevel(int level, int count) { return (float)(2*level - count + 6)*count; }
    private float levelToPoints(float level) { return (level*level + 6*level); }
    private double pointsToLevel(float points) { return Math.sqrt( (double)(points + 9) ) - 3.0; }

    private void bottle(Player person, float amount) {
        System.out.println();
        System.out.println( person.getLevel() +" + "+ person.getExp()  +"% - "+ amount );
        System.out.println();
        //problem somewhere?
        int level = person.getLevel();
        float original = levelToPoints( level ) + person.getExp() * pointsInLevel( level+1, 1);

        if(amount < 0.0f) { return; }
        if(original < amount) { amount = original; }

        //take exp
        //person.setLevel( level );
        //person.setExp( percentage );

        //give item
        ItemStack bottle = new ItemStack(Material.EXPERIENCE_BOTTLE);
        {
            ItemMeta metaBottle = bottle.getItemMeta();
            metaBottle.displayName(name);

            List<String> lore = new ArrayList<>();
            lore.add( (amount + "\u24BA") );
            metaBottle.setLore(lore);
            bottle.setItemMeta(metaBottle);

            metaBottle.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        person.getInventory().addItem( bottle );

        //System.out.println( person.getName() +" "+ amount );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) { return false; }

        Player person = ( (Player)sender ); int level = person.getLevel();
        float amount = levelToPoints( level ) + person.getExp() * pointsInLevel( level+1, 1 );
        System.out.println("total = "+amount);

        if(args.length == 1) {
            if( args[0].equalsIgnoreCase("bottle") || args[0].equalsIgnoreCase("b") ) {
                bottle( (Player)sender,  amount ); //defaults to all
                return true;
            }
            if( args[0].equalsIgnoreCase("bottlekeep") || args[0].equalsIgnoreCase("bk") ) {
                if(1080.0f <= amount) { amount -= 1080.0f; } //defaults to level 30
                bottle( (Player)sender, amount );
                return true;
            }
            return false;
        }

        boolean isLevel = (  args[1].charAt( args[1].length()-1 ) == 'L'  );
        if( args[0].equalsIgnoreCase("bottle") || args[0].equalsIgnoreCase("b") ) {
            if( isLevel ) {
                System.out.println(args[1].substring( 0, args[1].length()-1 ));
                int level_request = person.getLevel() - Integer.parseInt(  args[1].substring( 0, args[1].length()-1 )  );
                if(level_request < 0) {
                    person.setLevel(0); person.setExp(0.0f);
                    bottle( person, amount);
                } else {
                    person.setLevel(level_request);
                    amount -= levelToPoints(level_request) + person.getExp()*pointsInLevel(level_request+1, 1);
                    bottle( person, amount );
                }
                System.out.println( "bL: Level = " + level_request );
                return true;
            } else {
                amount = Float.parseFloat( args[1] );
                System.out.println( "b" );
            }
        }
        if( args[0].equalsIgnoreCase("bottlekeep") || args[0].equalsIgnoreCase("bk") ) {
            if( isLevel ) {
                amount -= levelToPoints(   Integer.parseInt(  args[1].substring(0, args[1].length()-1 )  )   );
                System.out.println( "bkL" );
            } else {
                amount -= Float.parseFloat( args[1] );
                System.out.println( "bk" );
            }
        }

        System.out.println(amount);
        bottle( (Player)sender, amount );
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length == 1) {
            List<String> list = new ArrayList<>();
            list.add("bottle"); list.add("bottlekeep");
            list.add("b");      list.add("bk");
            return list;
        }

        if(args.length == 2 && sender instanceof Player) {
            List<String> fill = new ArrayList<>();

            if( args[0].equalsIgnoreCase("bottle") || args[0].equalsIgnoreCase("b") ) {
                fill.add( String.valueOf( ( (Player)sender ).getExp() + levelToPoints( ( (Player)sender ).getLevel() ) ) );
                fill.add( ( (Player)sender ).getLevel() +"L" );
            }
            if( args[0].equalsIgnoreCase("bottlekeep") || args[0].equalsIgnoreCase("bk") ) {
                fill.add("30L");
                fill.add("1395");
            }

            return fill;
        }


        return null;
    }
}
