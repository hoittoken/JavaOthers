import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

public class Client implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName()); // Логгер для записи событий
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // Для реализации таймера активности
    Socket socket;
    Scanner in;
    PrintStream out;
    ChatServer chatServer;
    String userName; // Имя указанное пользователем при подключении к чат серверу
    Boolean isActive = true; // Выключатель по неактивности пользователя

    public Client(Socket socket, ChatServer chatServer) {
        this.socket = socket;
        this.chatServer = chatServer;

        // Настройка логгера
        setupLogger();

        // Запускаем поток
        new Thread(this).start();
    }

    // Метод для настройки логгера
    private void setupLogger() {
        // Удаляем стандартный консольный обработчик
        LOGGER.setUseParentHandlers(false);

        // Создаем форматтер для логов
        Formatter formatter = new SimpleFormatter() {
            @Override
            public synchronized String format(LogRecord record) {
                return String.format("[%1$tF %1$tT] [%2$-7s] %3$s%n",
                        record.getMillis(),
                        record.getLevel().getName(),
                        record.getMessage());
            }
        };

        // Создаем консольный обработчик
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        LOGGER.addHandler(consoleHandler);

        // Создаем файловый обработчик для записи логов в файл
        try {
            FileHandler fileHandler = new FileHandler("client.log", true); // true - добавление в существующий файл
            fileHandler.setFormatter(formatter);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            LOGGER.severe("Ошибка при создании файлового обработчика: " + e.getMessage());
        }
    }

    // Получение сообщений от сервера
    void receive(String message) {
        out.println(message);
        LOGGER.info("Получено сообщение от сервера: " + message);
    }

    @Override
    public void run() {
        try (InputStream is = socket.getInputStream();
             OutputStream os = socket.getOutputStream();
             Scanner scanner = new Scanner(is);
             PrintStream printStream = new PrintStream(os)) {

            this.in = scanner;
            this.out = printStream;

            // Приветственное сообщение
            out.println("Welcome to chat");
            LOGGER.info("Отправлено приветственное сообщение клиенту.");

            out.println("Enter your name, please:");
            String userName = in.nextLine().trim();
            if (userName.isEmpty()) {
                out.println("Имя пользователя не может быть пустым. Подключение закрыто.");
                LOGGER.warning("Клиент ввел пустое имя. Подключение закрыто.");
                return;
            }
            this.userName = userName;
            out.println("Nice to meet you " + userName + ". Let's chat!");
            LOGGER.info("Клиент " + userName + " успешно подключен.");

            // Запускаем таймер активности
            long INACTIVITY_TIMEOUT = 300; // Время отсутствия активности в секундах
            scheduler.schedule(() -> disconnect(userName), INACTIVITY_TIMEOUT, TimeUnit.SECONDS);
            LOGGER.info("Таймер активности запущен для клиента " + userName);

            // Получение сообщения от пользователя
            String input;
            while ((input = in.nextLine()) != null && isActive) {
                if ("/exit".equalsIgnoreCase(input.trim())) {
                    out.println("Выход из чата...");
                    LOGGER.info("Клиент " + userName + " завершил соединение командой /exit.");
                    break;
                }
                chatServer.sendAll(userName + ": " + input);
                LOGGER.info("Клиент " + userName + " отправил сообщение: " + input);
                resetActivityTimer(userName); // Сбрасываем таймер активности
            }

        } catch (IOException e) {
            LOGGER.severe("Ошибка ввода/вывода: " + e.getMessage());
        } finally {
            scheduler.shutdown();
            try {
                socket.close();
                LOGGER.info("Сокет клиента " + userName + " закрыт.");
            } catch (IOException e) {
                LOGGER.warning("Ошибка при закрытии сокета клиента: " + e.getMessage());
            }
        }
    }

    // Сброс таймера активности
    private void resetActivityTimer(String userName) {
        scheduler.schedule(() -> disconnect(userName), 300, TimeUnit.SECONDS);
        LOGGER.info("Таймер активности сброшен для клиента " + userName);
    }

    // Отключение клиента
    private void disconnect(String userName) {
        isActive = false;
        chatServer.sendAll(userName + " has been disconnected due to inactivity.");
        LOGGER.info("Клиент " + userName + " отключен за неактивность.");
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.warning("Ошибка при закрытии сокета клиента: " + e.getMessage());
        }
    }
}