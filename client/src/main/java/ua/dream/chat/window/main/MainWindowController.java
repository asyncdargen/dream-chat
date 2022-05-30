package ua.dream.chat.window.main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.val;
import lombok.var;
import org.fxmisc.flowless.Cell;
import org.fxmisc.flowless.VirtualFlow;
import ua.dream.chat.App;
import ua.dream.chat.model.DateMessage;
import ua.dream.chat.model.Message;
import ua.dream.chat.packet.message.PacketMessageCreate;
import ua.dream.chat.util.References;
import ua.dream.chat.window.WindowController;
import ua.dream.chat.window.elements.ContactView;
import ua.dream.chat.window.elements.MessageView;
import ua.dream.chat.window.elements.PrettyListView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class MainWindowController extends WindowController {

    @FXML
    private StackPane root;
    @FXML
    private Pane blockingPane;

    @FXML
    private VBox contactsVBox;
    private ListView<Long> chatsListView = new PrettyListView<>();

    @FXML
    private VBox messagesVBox;
    private VirtualFlow<Message, Cell<Message, ?>> messagesFlow;
    private ObservableList<Message> messages = FXCollections.observableArrayList();
    private TextField inputMessageField;

    @FXML
    public void initialize() {
        root.setOnMouseClicked(event -> {
            if (isBlocked()) {
                if (!App.SETTINGS_WINDOW.getController().closeDialogs())
                    App.SETTINGS_WINDOW.getController().close();
                else {
                    App.SEARCH_PROFILE.getController().close();
                    unblockWindow();
                }
            }
        });

        chatsListView.setCellFactory(view -> new ListCell<Long>() {
            @Override
            protected void updateItem(Long id, boolean empty) {
                super.updateItem(id, empty);

                if (id == null || id < 0 || empty) return;

                val element = new ContactView(id);
                setGraphic(element.getRoot());
            }
        });

        chatsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldChat, newChat) -> refreshMessages());
        contactsVBox.getChildren().add(chatsListView);
//        VBox.setVgrow(chatsListView, Priority.ALWAYS);

        inputMessageField = new TextField();
        inputMessageField.setPromptText("Write a message...");
        inputMessageField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER
                    && chatsListView.getSelectionModel().getSelectedItem() != null
                    && inputMessageField.getText().length() != 0) {
                var message = inputMessageField.getText();
                inputMessageField.clear();

                if (message.startsWith("image:")) {
                    try {
                        val imagePath = message.substring("image:".length());
                        val imageFile = new File(imagePath);

                        val outputStream = new ByteArrayOutputStream();
                        FileInputStream inputStream = null;

                        inputStream = new FileInputStream(imageFile);

                        int c;
                        byte[] bytes = new byte[2048];

                        while ((c = inputStream.read(bytes)) != -1)
                            outputStream.write(bytes, 0, c);

                        val imageRaw = Base64.getEncoder().encodeToString(outputStream.toByteArray());
                        message = "base64;image:" + imageRaw;
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }

                App.getClient().write(new PacketMessageCreate(
                        chatsListView.getSelectionModel().getSelectedItem(), System.currentTimeMillis(), message
                ));
            }
        });
    }

    @Override
    public void handleOpen() {
        refreshContacts();
    }

    @FXML
    public void openSettings() {
        blockWindow();

        App.SETTINGS_WINDOW.showAsModalStage();
    }

    @FXML
    public void addProfile() {
        blockWindow();

        App.SEARCH_PROFILE.showOnTopStage();
    }

    public void refreshMessages() {
        messages.clear();
        Long currentUserChat = chatsListView.getSelectionModel().getSelectedItem();
        if (currentUserChat != null && currentUserChat != -1)
            messages.addAll(App.getMessageRepository().getMessages(currentUserChat));

        markDates();

        messagesFlow = VirtualFlow.createVertical(messages, message -> {
            MessageView messageData = new MessageView(message);

            val box = messageData.getRoot();

            box.setAlignment(
                    message instanceof DateMessage
                            ? Pos.CENTER
                            : message.getFromUserId() == App.getUserRepository().getSelfId()
                            ? Pos.CENTER_RIGHT
                            : Pos.CENTER_LEFT
            );

            if (message.getFromUserId() == App.getUserRepository().getSelfId())
                box.getChildren().get(0).getStyleClass().add("my-message");

            return Cell.wrapNode(box);
        }, VirtualFlow.Gravity.REAR);

        messagesVBox.getChildren().clear();
        messagesVBox.getChildren().add(messagesFlow);
        messagesVBox.getChildren().add(inputMessageField);
        VBox.setVgrow(messagesFlow, Priority.ALWAYS);

        messagesFlow.show(messages.size() - 1);
    }

    public void refreshContacts() {
        val selected = chatsListView.getSelectionModel().getSelectedIndex();
        chatsListView.getItems().clear();
        chatsListView.getItems().addAll(App.getUserRepository().getSelfUser().getChatsIds());
        chatsListView.getSelectionModel().select(selected);

        refreshMessages();
    }

    public void markDates() {
        Calendar calendar = Calendar.getInstance();
        int prevDay = -1;
        int prevMonth = -1;
        int day;
        int month;
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);

            calendar.setTime(new Date(message.getTimestamp()));
            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);

            if (prevDay < 0 || prevDay != day ||
                    prevMonth < 0 || prevMonth != month) {
                Message dateMessage = new DateMessage(References.DATE_FORMAT.format(message.getTimestamp()));
                messages.add(i, dateMessage);
            }

            prevDay = day;
            prevMonth = month;
        }
    }

    public void blockWindow() {
        blockingPane.setVisible(true);
    }

    public void unblockWindow() {
        blockingPane.setVisible(false);
    }

    public boolean isBlocked() {
        return blockingPane.isVisible();
    }

}
