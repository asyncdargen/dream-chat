package ua.dream.chat.window.elements;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.val;
import ua.dream.chat.model.DateMessage;
import ua.dream.chat.model.EncodedImage;
import ua.dream.chat.model.Message;
import ua.dream.chat.util.References;
import ua.dream.chat.util.ResourceUtil;

import java.io.IOException;
import java.net.URL;

public class MessageView {

    public static final URL FXML_RESOURCE = ResourceUtil.getFxmlResourceURL("main/MessageView.fxml");

    @Getter
    private final Message message;

    @Getter
    @FXML
    private HBox root;
    @FXML
    private Label messageLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private ImageView attachmentImageView;

    public MessageView(Message message) {
        this.message = message;
        try {
            FXMLLoader loader = new FXMLLoader(FXML_RESOURCE);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        val text = message.getMessage();
        if (text.startsWith("base64;image:")) {
            val imageRaw = new EncodedImage(text.substring("base64;image:".length()));
            val image = new Image(imageRaw.openInputStream());
            attachmentImageView.setImage(image);
            attachmentImageView.setFitWidth(256);
            attachmentImageView.setFitHeight(256);
            attachmentImageView.setVisible(true);
        } else messageLabel.setText(text);
        dateLabel.setText(message instanceof DateMessage ? "" : References.TIME_SIMPLE_FORMAT.format(message.getTimestamp()));
    }

}
