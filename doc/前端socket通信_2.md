以下是详细的WebSocket通信说明文档，供前端开发人员使用，来正确调用和处理后端的WebSocket接口。

## WebSocket 通信说明文档

### WebSocket 连接

#### 连接 URL

```shell
ws://localhost:8080/chat?groupId=<groupId>
```

#### 连接参数

- `groupId`: 当前用户所在的群组ID。

### 建立连接

前端需要通过WebSocket API连接到后端的WebSocket服务器。

#### 连接示例

```javascript
const groupId = "123";  // 替换为实际的群组ID
const socket = new WebSocket(`ws://localhost:8080/chat?groupId=${groupId}`);

socket.onopen = function() {
    console.log("Connected to WebSocket server.");
};

socket.onclose = function() {
    console.log("Disconnected from WebSocket server.");
};

socket.onerror = function(error) {
    console.error("WebSocket error: " + error);
};

```

### 接收消息

当服务器推送消息到客户端时，客户端可以通过`onmessage`事件接收消息。

#### 消息格式

服务器推送的消息格式如下：

```json
{
  "type": "message",
  "sender": "user123",
  "username": "JohnDoe",
  "groupId": "1",
  "timestamp": "2024-07-13T12:00:00",
  "content": "Hello, Group!"
}
```

### 发送消息

前端可以通过WebSocket连接向服务器发送消息。

#### 发送消息格式

发送的消息格式如下：

```json
{
  "jwt": "your-jwt-token",
  "uid": "your-user-id",
  "groupId": "1",
  "message": "Hello, Group!"
}
```

发送消息示例

```javascript
function sendMessage() {
    const messageInput = document.getElementById("message");
    const message = messageInput.value;

    const messageObj = {
        jwt: "your-jwt-token",  // 替换为实际的JWT
        uid: "your-user-id",    // 替换为实际的用户ID
        groupId: groupId,       // 当前群组ID
        message: message
    };

    // 发送消息到WebSocket服务器
    socket.send(JSON.stringify(messageObj));

    // 清空输入框
    messageInput.value = "";
}
```

### 总结

通过以上文档，前端开发人员可以：

1. 建立WebSocket连接到后端服务器。
2. 通过WebSocket接收和处理从服务器推送的消息。
3. 通过WebSocket向服务器发送消息。

#### 完整示例

以下是完整的HTML文件示例，展示了如何使用上述说明进行WebSocket通信。

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WebSocket Chat Test</title>
</head>

<body>
<h1>WebSocket Chat Test</h1>
<div>
    <label for="message">Message:</label>
    <input type="text" id="message">
    <button onclick="sendMessage()">Send</button>
</div>
<div>
    <h2>Messages:</h2>
    <ul id="messages"></ul>
</div>

<script>
    // 连接到WebSocket服务器
    const socket = new WebSocket("ws://localhost:8888/chat?groupId=100000000");

    socket.onopen = function () {
        console.log("Connected to WebSocket server.");
    };

    socket.onmessage = function (event) {
        const message = JSON.parse(event.data);
        console.log(message);
        console.log("Received: " + message.content);

        // 显示收到的消息
        const messagesList = document.getElementById("messages");
        const newMessage = document.createElement("li");
        newMessage.textContent = `[${message.timestamp}] ${message.uid} -- ${message.username}: ${message.content}`;
        messagesList.appendChild(newMessage);
    };

    socket.onclose = function () {
        console.log("Disconnected from WebSocket server.");
    };

    socket.onerror = function (error) {
        console.error("WebSocket error: " + error);
    };

    function sendMessage() {
        const messageInput = document.getElementById("message");
        const message = messageInput.value;

        const messageObj = {
            jwt:
                "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0ZXN0MSIsInVpZCI6MTAwMDAxLCJpYXQiOjE3MjA4ODY4MzIsImV4cCI6MTcyMDk3MzIzMn0.zf7EBhUFL4DKaprDXMJiGE0x8tSNjXFDj696UDr16Vpb3RSJMGbtQNd4N1dNMPll",
            // 这里需要替换为实际的JWT
            uid: "100001", // 这里需要替换为实际的用户ID
            groupId: "100000000",
            message: message
        };

        // 发送消息到WebSocket服务器
        socket.send(JSON.stringify(messageObj));

        // 清空输入框
        messageInput.value = "";
    }

</script>
</body>

</html>
```



