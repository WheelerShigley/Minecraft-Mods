package me.wheelershigley.www.dump_renamer.mixins.notenoughitems;


import codechicken.nei.guihook.GuiContainerManager;
import me.wheelershigley.www.dump_renamer.MyMod;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static codechicken.nei.config.GuiItemIconDumper.cleanFileName;

@Mixin(
    targets = "codechicken.nei.config.GuiItemIconDumper",
    remap = false,
    priority = 800
)
public class PNGRenaming extends GuiScreen  {

    /**
     * @author Wheeler-Shigley
     * @reason file renames
     */
    @Overwrite
    public void exportImage(File dir, BufferedImage img, ItemStack stack) throws IOException {
        Item item = stack.getItem();
        if(item == null) {
            return;
        }

        String damagedIdentifier =
            Item.itemRegistry.getIDForObject(item)
            +','+
            Integer.toString( item.getDamage(stack) )
        ;
        String readableName = EnumChatFormatting.getTextWithoutFormattingCodes(
            GuiContainerManager.itemDisplayNameShort(stack)
        );

        String name = cleanFileName(damagedIdentifier +" → ''"+ readableName +"''");
        MyMod.info("Dumping image of "+ damagedIdentifier +".");

        File file = new File(dir, name + ".png");
        ImageIO.write(img, "png", file);
    }

}
