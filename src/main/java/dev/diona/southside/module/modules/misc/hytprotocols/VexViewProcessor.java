package dev.diona.southside.module.modules.misc.hytprotocols;

import dev.diona.southside.gui.container.HytPartyCreateGui;
import dev.diona.southside.gui.container.HytPartyInputGui;
import dev.diona.southside.gui.container.HytPartyInviteGui;
import dev.diona.southside.gui.container.HytPartyManageGui;
import dev.diona.southside.module.modules.misc.HytProtocol;
import dev.diona.southside.util.quickmacro.vexview.VexViewReader;
import dev.diona.southside.util.quickmacro.vexview.VexViewSender;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

import static dev.diona.southside.Southside.MC.mc;

public class VexViewProcessor implements HytProtocol.HytProtocolProcessor {
    @Override
    public String getChannel() {
        return "VexView";
    }

    @Override
    public void process(PacketBuffer buf) throws IOException {
        VexViewReader vexViewReader = new VexViewReader(buf);
        if (vexViewReader.invited) {
            // TODO: 接收邀请信息
        } else if (vexViewReader.sign) {
            VexViewSender.clickButton(vexViewReader.getButton("sign").id);
        } else if (vexViewReader.containsButton("手动输入")) {
            VexViewSender.clickButton(vexViewReader.getButton("手动输入").id);
        } else if (vexViewReader.containsButton("提交")) {
            String prefix = "";
            if (VexViewReader.inviting) {
                prefix = "/kh invite ";
                VexViewReader.inviting = false;
            }
            else prefix = "/kh join ";
            mc.displayGuiScreen(new HytPartyInputGui(vexViewReader.getElement(vexViewReader.getButtonIndex("提交") - 1), vexViewReader.getButton("提交"), prefix));
        } else if (vexViewReader.list) {
            mc.displayGuiScreen(new HytPartyInviteGui(vexViewReader.requests));
        } else if (vexViewReader.containsButtons("创建队伍", "申请入队")) {
            mc.displayGuiScreen(new HytPartyCreateGui(vexViewReader.getButton("创建队伍"), vexViewReader.getButton("申请入队")));
        } else if (vexViewReader.containsButtons("申请列表", "申请列表", "踢出队员", "离开队伍", "解散队伍")) {
            if (vexViewReader.containsButton("邀请玩家")) {
                mc.displayGuiScreen(new HytPartyManageGui(vexViewReader.getButton("离开队伍"), vexViewReader.getButton("解散队伍"), vexViewReader.getButton("邀请玩家"), vexViewReader.getButton("申请列表")));
            } else {
                mc.displayGuiScreen(new HytPartyManageGui(vexViewReader.getButton("离开队伍"), vexViewReader.getButton("解散队伍"), null, null));
            }
        } else if (vexViewReader.containsButton("离开队伍")) {
            mc.displayGuiScreen(new HytPartyManageGui(vexViewReader.getButton("离开队伍"), null, null, null));
        }
    }
}
