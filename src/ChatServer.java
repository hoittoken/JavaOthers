import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.*;

public class ChatServer {
    private static final Logger LOGGER = Logger.getLogger(ChatServer.class.getName()); // Логгер для записи событий
    private ArrayList<Client> clients = new ArrayList<>(); // Список подключившихся клиентов
    private ServerSocket serverSocket; // Сокет сервера
    private String clientName; // Имя подключившегося клиента

    public ChatServer() {
        try {
            // Настройка логгера
            setupLogger();
            serverSocket = new ServerSocket(1234);
            LOGGER.info("Сервер запущен и ожидает подключения на порту 1234.");
        } catch (IOException e) {
            LOGGER.severe("Ошибка при создании серверного сокета: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void setupLogger() throws IOException {
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
        FileHandler fileHandler = new FileHandler("server.log", true); // true - добавление в существующий файл
        fileHandler.setFormatter(formatter);
        LOGGER.addHandler(fileHandler);
    }

    void sendAll(String message) {
        for (Client client : clients) {
            client.receive(message);
        }
        LOGGER.info("Сообщение отправлено всем клиентам: " + message);
    }

    public void run() {
        while (true) {
            LOGGER.info("Ожидание подключения нового клиента...");
            try {
                Socket socket = serverSocket.accept();
                Client client = new Client(socket, this);
                clients.add(client);
                clientName = client.toString();
                LOGGER.info("Клиент подключен: " + clientName);
            } catch (IOException e) {
                LOGGER.severe("Ошибка при подключении клиента: " + e.getMessage());
                break;
            }
        }
    }

    public static void main(String[] args) {
        try {
            new ChatServer().run();
        } catch (Exception e) {
            LOGGER.severe("Критическая ошибка сервера: " + e.getMessage());
        }
    }
}