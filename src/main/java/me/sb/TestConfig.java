package me.sb;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.data.Mod;
import dev.diona.southside.module.Category;
import cc.polyfrost.oneconfig.config.data.OptionSize;
import cc.polyfrost.oneconfig.config.data.PageLocation;

public class TestConfig extends Config {
//    public cc.polyfrost.oneconfig.config.options.impl.Switch aSwitch = new cc.polyfrost.oneconfig.config.options.impl.Switch(
//        "A Very Cool Switch",
//            "A Very Cool Description",
//            1,
//            false
//    );

//    public cc.polyfrost.oneconfig.config.options.impl.Page page = new cc.polyfrost.oneconfig.config.options.impl.Page(
//            "An actual page",
//            PageLocation.BOTTOM,
//            "yes very cool",
//            new SBPage()
//    );

//    @HUD(
//            name = "Example HUD"
//    )
//    public SbTextHud hud = new SbTextHud();
    public cc.polyfrost.oneconfig.config.options.impl.HUD hud = new cc.polyfrost.oneconfig.config.options.impl.HUD("Example HUD", new SbTextHud());

//    @Switch(
//            name = "Toggle Switch (1x)",
//            size = OptionSize.SINGLE // optional
//    )
//    public boolean bob = false;        // default value
//
//    @Slider(
//            name = "You slide me right round baby right round",
//            min = 0f, max = 100f,
//            step = 10
//    )
//    public float slideyboi = 50f; // default value
//
//    @Color(
//            name = "Background Color"
//    )
//    public OneColor testColor = new OneColor(26, 35, 143);        // default color
//
//    @Dropdown(
//            name = "Example Dropdown", // Name of the Dropdown
//            options = {"Option 1", "Option 2", "Option 3", "Option 4"} // Options available.
//    )
    public int exampleDropdown = 1; // Default option (in this case "Option 2")
    public TestConfig() {
        super(new Mod("My Mod", Category.Misc), "./sb");
        initialize();
    }
}
