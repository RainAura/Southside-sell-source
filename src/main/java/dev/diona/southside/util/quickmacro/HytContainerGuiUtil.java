package dev.diona.southside.util.quickmacro;

import dev.diona.southside.util.quickmacro.metadatas.GermItem;
import dev.diona.southside.util.quickmacro.metadatas.GermMetadata;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class HytContainerGuiUtil {
    public static ItemStack getItemStack(Item item, String name, String[] lore) {
        ItemStack itemStack = new ItemStack(item);

        NBTTagCompound displayTag = new NBTTagCompound();
        displayTag.setTag("Name", new NBTTagString(name.trim()));

        NBTTagList loreList = new NBTTagList();
        for (String s : lore) {
            loreList.appendTag(new NBTTagString(s));
        }
        displayTag.setTag("Lore", loreList);

        itemStack.setTagInfo("display", displayTag);
        return itemStack;
    }

    public static GermItem getItemStack(Item item, GermMetadata metadata, String name, String[] lore) {
        GermItem itemStack = new GermItem(item, metadata);

        NBTTagCompound displayTag = new NBTTagCompound();
        displayTag.setTag("Name", new NBTTagString(name.trim()));

        NBTTagList loreList = new NBTTagList();
        for (String s : lore) {
            loreList.appendTag(new NBTTagString(s));
        }
        displayTag.setTag("Lore", loreList);

        itemStack.setTagInfo("display", displayTag);
        return itemStack;
    }

    public static ItemStack getItemStack(Item item, String name) {
        ItemStack itemStack = new ItemStack(item);

        NBTTagCompound displayTag = new NBTTagCompound();
        displayTag.setTag("Name", new NBTTagString(name.trim()));

        itemStack.setTagInfo("display", displayTag);
        return itemStack;
    }

    public static GermItem getItemStack(Item item, GermMetadata metadata, String name) {
        GermItem itemStack = new GermItem(item, metadata);

        NBTTagCompound displayTag = new NBTTagCompound();
        displayTag.setTag("Name", new NBTTagString(name.trim()));

        itemStack.setTagInfo("display", displayTag);
        return itemStack;
    }
}
