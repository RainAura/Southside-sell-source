package net.minecraft.client;

import dev.diona.southside.module.modules.misc.HytProtocol;

public class ClientBrandRetriever
{
    public static String getClientModName()
    {
        // TODO: 手动切换
        if (HytProtocol.isInstanceEnabled()) {
            return "fml,forge";
        }
        return "vanilla";
    }
}
