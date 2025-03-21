## Task № 13.3.3
### Простой чат сервер с параллельной обработкой подключенных клиентов.

Минимальная функциональность успешно выполненного задания: 
1. К серверу может присоединиться несколько клиентов одновременно. 
2. Всё, что пишется на любом из клиентов, передается в окна других клиентов после нажатия на Enter. 
3. При отсоединении любого клиента программа должна продолжать работать корректно. Уже присоединенные клиенты продолжают общаться между собой, и к ним могут присоединиться новые.

Обзор классов:
 - **ChatServer** - чат сервер. Поднимается на локальной машине 127.0.0.1 порт 1234. Умеет рассылать всем подключенным пользователям сообщения. Умеет логировать изменения состояний. Логи хранит в server.log
 - **Client** - клиент создаваемый на сервере. Умеет будучи подключенным к серверу получить имя пользователя и отправлять сообщения от этого имени. Умеет отправлять и получать сообщения на/с сервера. Умеете отключать неактивных пользователей (по умолчанию отсечка - 5 минут без написания сообщений). Также умеет логировать изменения состояний. Логи хранит в client.log. Можно было определить весь перечисленный функционал внутри класса ChatServer, но нафига?)
 - **SimpleNetClient** - клиент для подключения к чат серверу. Имеет простейшее окно с полем для чтения и полем для отправки сообщения. Отправка сообщения осуществляется нажатием кнопки "Отправить" или по нажатию клавиши Enter. Имеет буфер для накопления сообщения перед его отправкой на сервер. Это сделано для реализации возможности редактирования сообщения перед отправкой (для работы кнопки backspace).

Порядок работы:
1. Запускаем сервер ChatServer.java
2. Запускаем необходимое количество клиентов SimpleNetClient.java
3. Для прекращения работы клиента выключаем его закрытием окна.
4. Останавливаем сервер.