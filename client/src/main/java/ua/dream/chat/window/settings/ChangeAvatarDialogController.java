package ua.dream.chat.window.settings;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import lombok.SneakyThrows;
import lombok.val;
import ua.dream.chat.App;
import ua.dream.chat.model.EncodedImage;
import ua.dream.chat.packet.user.PacketUserUpdateAvatar;
import ua.dream.chat.window.DialogWindow;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;

public class ChangeAvatarDialogController extends DialogWindow<ChangeAvatarDialogController> {

    @FXML
    private Label statusLabel;
    private File resultFile;
    private final FileChooser chooser = new FileChooser();

    {
        chooser.setTitle("Choose DreamChat user avatar");
        chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Avatar image", "png", "jpg"));
    }

    public ChangeAvatarDialogController() {
        super("settings/ChangeAvatarWindow.fxml");
    }

    @Override
    public void handleOpen() {
        statusLabel.setText("Choose image file to upload");
    }

    public void cancel() {
        resultFile = null;

//        App.SETTINGS_WINDOW.getController().handleOpen();
        close();
    }

    public void choose() {
        resultFile = chooser.showOpenDialog(getScene().getWindow());
        if (resultFile != null)
            statusLabel.setText(resultFile.getAbsolutePath());
    }

    public void reset() {
        resultFile = null;
        App.getUserRepository().getSelfUser().setAvatar(null);
        App.getClient().write(new PacketUserUpdateAvatar(null));
        handleOpen();
//        App.SETTINGS_WINDOW.getController().handleOpen();
    }

    @SneakyThrows
    public void save() {
        if (resultFile != null) {
            val outputStream = new ByteArrayOutputStream();
            val inputStream = new FileInputStream(resultFile);

            int c;
            byte[] bytes = new byte[2048];

            while ((c = inputStream.read(bytes)) != -1)
                outputStream.write(bytes, 0, c);

            val avatarRaw = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            val avatar = new EncodedImage(avatarRaw);
            App.getUserRepository().getSelfUser().setAvatar(avatar);
            App.getClient().write(new PacketUserUpdateAvatar(avatar));
        }

        cancel();
    }

}
