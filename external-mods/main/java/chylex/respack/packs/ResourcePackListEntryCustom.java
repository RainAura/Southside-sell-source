package chylex.respack.packs;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.ResourcePackListEntryFound;

public abstract class ResourcePackListEntryCustom extends ResourcePackListEntryFound{
	public ResourcePackListEntryCustom(GuiScreenResourcePacks ownerScreen){
		super(ownerScreen, null);
	}
	
	@Override
	public abstract void bindResourcePackIcon();
	
	@Override
	public abstract String getResourcePackName();
	
	@Override
	public abstract String getResourcePackDescription();
	
	@Override
	public boolean showHoverOverlay(){
		return super.showHoverOverlay();
	}
}
