package ua.dream.chat;

import lombok.Getter;
import lombok.val;
import ua.dream.chat.repository.MessageRepository;
import ua.dream.chat.repository.UserRepository;
import ua.dream.chat.repository.auth.AuthRepository;
import ua.dream.chat.server.DreamChatServer;
import ua.dream.chat.util.Database;
import ua.dream.chat.util.logger.Logger;

import java.util.Scanner;

public class App {

    @Getter
    private static Database database;
    @Getter
    private static UserRepository userRepository;
    @Getter
    private static MessageRepository messageRepository;
    @Getter
    private static AuthRepository authRepository;
    @Getter
    private static DreamChatServer server;

    public static void main(String[] args) {
        Logger.LOGGER.info("Loading DreamChat server...");

        Logger.LOGGER.info("Starting server...");
        server = new DreamChatServer();

        Logger.LOGGER.info("Initialize database...");
        database = new Database("root", "localhost", "dreamchat", "wyk2MGMaQg3g4jye");

        Logger.LOGGER.info("Initialize user repository...");
        userRepository = new UserRepository();

        Logger.LOGGER.info("Initialize user messages repository...");
        messageRepository = new MessageRepository();

        Logger.LOGGER.info("Initialize auth user data repository...");
        authRepository = new AuthRepository();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Logger.LOGGER.info("Stopping server... Saving data...");
            userRepository.saveAll(true);
        }));

        val scanner = new Scanner(System.in);
        while (server.getClient().isActive() && !scanner.nextLine().equalsIgnoreCase("stop"));

        System.exit(0);
    }

}
