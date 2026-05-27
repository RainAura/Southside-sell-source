package net.minecraftforge.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.fml.common.FMLLog;
import org.lwjglx.opengl.GL11;
import org.lwjglx.opengl.GL20;

import java.nio.ByteBuffer;

public class ForgeHooksClient {
    public static void preDraw(VertexFormatElement.EnumUsage attrType, VertexFormat format, int element, int stride, ByteBuffer buffer)
    {
        VertexFormatElement attr = format.getElement(element);
        int count = attr.getElementCount();
        int constant = attr.getType().getGlConstant();
        buffer.position(format.getOffset(element));
        switch(attrType)
        {
            case POSITION:
                GlStateManager.glVertexPointer(count, constant, stride, buffer);
                GlStateManager.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                break;
            case NORMAL:
                if(count != 3)
                {
                    throw new IllegalArgumentException("Normal attribute should have the size 3: " + attr);
                }
                GlStateManager.glNormalPointer(constant, stride, buffer);
                GlStateManager.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                break;
            case COLOR:
                GlStateManager.glColorPointer(count, constant, stride, buffer);
                GlStateManager.glEnableClientState(GL11.GL_COLOR_ARRAY);
                break;
            case UV:
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + attr.getIndex());
                GlStateManager.glTexCoordPointer(count, constant, stride, buffer);
                GlStateManager.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                break;
            case PADDING:
                break;
            default:
                FMLLog.log.fatal("Unimplemented vanilla attribute upload: {}", attrType.getDisplayName());
        }
    }

    public static void postDraw(VertexFormatElement.EnumUsage attrType, VertexFormat format, int element, int stride, ByteBuffer buffer)
    {
        VertexFormatElement attr = format.getElement(element);
        switch(attrType)
        {
            case POSITION:
                GlStateManager.glDisableClientState(GL11.GL_VERTEX_ARRAY);
                break;
            case NORMAL:
                GlStateManager.glDisableClientState(GL11.GL_NORMAL_ARRAY);
                break;
            case COLOR:
                GlStateManager.glDisableClientState(GL11.GL_COLOR_ARRAY);
                // is this really needed?
                GlStateManager.resetColor();
                break;
            case UV:
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + attr.getIndex());
                GlStateManager.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                break;
            case PADDING:
                break;
            default:
                FMLLog.log.fatal("Unimplemented vanilla attribute upload: {}", attrType.getDisplayName());
        }
    }
}
