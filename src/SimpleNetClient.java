import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class SimpleNetClient {
    private JFrame frame; // Главное окно приложения
    private JTextArea chatArea; // Текстовая область для отображения сообщений
    private JTextField messageField; // Поле для ввода сообщений
    private JButton sendButton; // Кнопка для отправки сообщений
    private PrintWriter out; // Поток для отправки данных на сервер
    private BufferedReader in; // Поток для чтения данных от сервера
    private Socket socket; // Сокет для подключения к серверу

    public SimpleNetClient(String serverAddress, int PORT) {
        // Создаем GUI
        createGUI();

        // Подключаемся к серверу
        try {
            socket = new Socket(serverAddress, PORT); // Создаем сокет для подключения к серверу
            out = new PrintWriter(socket.getOutputStream(), true); // Поток для отправки данных на сервер
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Поток для чтения данных от сервера

            // Запускаем поток для чтения сообщений от сервера
            new Thread(this::receiveMessages).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Не удалось подключиться к серверу: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void createGUI() {
        // Создаем главное окно
        frame = new JFrame("Чат-клиент");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Закрытие программы при закрытии окна
        frame.setSize(400, 300); // Устанавливаем размер окна

        // Создаем текстовую область для отображения сообщений
        chatArea = new JTextArea();
        chatArea.setEditable(false); // Делаем текстовую область только для чтения
        JScrollPane scrollPane = new JScrollPane(chatArea); // Добавляем прокрутку
        frame.add(scrollPane, BorderLayout.CENTER); // Размещаем текстовую область в центре окна

        // Создаем панель для ввода сообщений и кнопки отправки
        JPanel inputPanel = new JPanel(); // Панель для ввода
        inputPanel.setLayout(new BorderLayout()); // Используем компоновку BorderLayout

        messageField = new JTextField(); // Поле для ввода сообщений
        inputPanel.add(messageField, BorderLayout.CENTER); // Размещаем поле ввода в центре панели

        sendButton = new JButton("Отправить"); // Кнопка для отправки
        inputPanel.add(sendButton, BorderLayout.EAST); // Размещаем кнопку справа

        frame.add(inputPanel, BorderLayout.SOUTH); // Размещаем панель ввода внизу окна

        // Добавляем обработчик события для кнопки отправки
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Обработка нажатия Enter в поле ввода
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        frame.setVisible(true); // Делаем окно видимым
    }

    private void sendMessage() {
        String message = messageField.getText().trim(); // Получаем текст из поля ввода
        if (!message.isEmpty()) { // Если сообщение не пустое
            out.println(message); // Отправляем сообщение на сервер
            messageField.setText(""); // Очищаем поле ввода
        }
    }

    private void receiveMessages() {
        try {
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) { // Читаем сообщения от сервера
                chatArea.append(serverMessage + "\n"); // Добавляем сообщение в текстовую область
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Ошибка при получении данных от сервера: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {

        final String serverAddress = "127.0.0.1";
        final int PORT = 1234;
        // Создаем экземпляр клиента
        SwingUtilities.invokeLater(() -> new SimpleNetClient(serverAddress, PORT));
    }
}