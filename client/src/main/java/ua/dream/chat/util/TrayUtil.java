package ua.dream.chat.util;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.val;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

@UtilityClass
public class TrayUtil {

    public final boolean SUPPORTED = SystemTray.isSupported();
    @Getter
    private TrayIcon icon;

    static {
        if (SUPPORTED) {
            val tray = SystemTray.getSystemTray();
            try {
                val image = ImageIO.read(ResourceUtil.getImageResourceURL("old_icon.png"));
                icon = new TrayIcon(image);
                tray.add(icon);
            } catch (IOException | AWTException e) {
                e.printStackTrace();
            }
        }
    }

    public void notify(String title, String message) {
        if (SUPPORTED)
            icon.displayMessage(title, message, TrayIcon.MessageType.INFO);
    }

}
