import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;

import me.djtheredstoner.devauth.forge.legacy.ForgeLegacyBootstrap;
import net.minecraft.client.main.Main;

public class Start
{
    public static void main(String[] args)
    {
//        Main.main(concat(new String[] {"--version", "mcp", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.12", "--userProperties", "{}"}, args));
        Main.main(ForgeLegacyBootstrap.processArguments(concat(new String[] {"--version", "mcp", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.12", "--userProperties", "{}", "--width", "640", "--height", "360"}, args)));
    }

    public static <T> T[] concat(T[] first, T[] second)
    {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
