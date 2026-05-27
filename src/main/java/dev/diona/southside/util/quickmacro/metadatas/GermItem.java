package dev.diona.southside.util.quickmacro.metadatas;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GermItem extends ItemStack {
    public GermMetadata metadata;
    public GermItem(Item itemIn, GermMetadata metadata) {
        super(itemIn);
        this.metadata = metadata;
    }
}
