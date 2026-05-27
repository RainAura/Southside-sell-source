package dev.diona.southside.module.modules.client;

import dev.diona.southside.event.events.WorldEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.annotations.DefaultEnabled;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import me.bush.eventbus.annotation.EventListener;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import javax.swing.*;
@DefaultEnabled
public class MemoryMitigation extends Module {
    public MemoryMitigation(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    public final Slider rateValue = new Slider("Max RAM rate", 0.7, 0.3, 0.8, 0.1);

    @EventListener
    public void onWorld(WorldEvent event) {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem os = systemInfo.getOperatingSystem();

        // 获取系统内存信息
        GlobalMemory globalMemory = systemInfo.getHardware().getMemory();
        long totalMemory = globalMemory.getTotal();
        long availableMemory = globalMemory.getAvailable();

        // 获取进程内存信息
        int processId = os.getProcessId();
        OSProcess process = os.getProcess(processId);
        long residentSetSize = process.getResidentSetSize(); // 获取内存使用量

        System.out.println("Resident Set Size (Memory Usage): " + FormatUtil.formatBytes(residentSetSize));
        if (residentSetSize > (long) (totalMemory * rateValue.getValue().doubleValue())) {
            int result = JOptionPane.showConfirmDialog(null, "SouthSide 检测到了内存泄漏，烦请您重启客户端\n" +
                    "（此问题我们已经知晓，但是还需要很多时间查找原因）\n" +
//                    "截图该信息：" + heapMemoryUsage.getCommitted() + " " + nonHeapMemoryUsage.getCommitted() + " " + totalPhysicalMemory + "\n" +
                    "点击 '是' 将为你关闭客户端", "SouthSide 防沉迷系统", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }
}
