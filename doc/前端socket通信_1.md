好的，以下是针对Vue.js的WebSocket通信说明文档，包括建立连接、接收消息、发送消息的详细步骤和示例代码。

WebSocket 通信说明文档 (Vue.js)
1. 创建 Vue 项目
   如果你还没有创建Vue项目，可以使用Vue CLI创建一个新的Vue项目：
   ```shell
   vue create websocket-chat
   ```

2. 安装 Axios (用于获取 JWT)
   如果你需要通过HTTP请求获取JWT，可以使用Axios：
   ```shell
    npm install axios
   ```

3. 在 Vue 项目中使用 WebSocket
   #### 建立连接

   在Vue项目中，可以使用生命周期钩子在组件创建时建立WebSocket连接。

   ```javascript
   <template>
     <div id="app">
       <h1>WebSocket Chat Test</h1>
       <div>
         <label for="message">Message:</label>
         <input type="text" v-model="message" @keyup.enter="sendMessage">
         <button @click="sendMessage">Send</button>
       </div>
       <div>
         <h2>Messages:</h2>
         <ul>
           <li v-for="msg in messages" :key="msg.timestamp">
             [{{ msg.timestamp }}] {{ msg.username }}: {{ msg.content }}
           </li>
         </ul>
       </div>
     </div>
   </template>
   
   <script>
   import axios from 'axios';
   
   export default {
     data() {
       return {
         socket: null,
         message: '',
         messages: [],
         jwt: 'your-jwt-token',  // 替换为实际的JWT
         uid: 'your-user-id',    // 替换为实际的用户ID
         groupId: '123',         // 替换为实际的群组ID
       };
     },
     created() {
       this.connectWebSocket();
     },
     methods: {
       connectWebSocket() {
         this.socket = new WebSocket(`ws://localhost:8080/chat?groupId=${this.groupId}`);
   
         this.socket.onopen = () => {
           console.log('Connected to WebSocket server.');
         };
   
         this.socket.onmessage = (event) => {
           const message = JSON.parse(event.data);
           console.log('Received:', message.content);
           this.messages.push(message);
         };
   
         this.socket.onclose = () => {
           console.log('Disconnected from WebSocket server.');
         };
   
         this.socket.onerror = (error) => {
           console.error('WebSocket error:', error);
         };
       },
       sendMessage() {
         if (!this.message) return;
   
         const messageObj = {
           jwt: this.jwt,
           uid: this.uid,
           groupId: this.groupId,
           message: this.message,
         };
   
         this.socket.send(JSON.stringify(messageObj));
         this.message = '';
       }
     }
   };
   </script>
   
   <style scoped>
   #app {
     font-family: Avenir, Helvetica, Arial, sans-serif;
     text-align: center;
     margin-top: 60px;
   }
   
   input {
     padding: 10px;
     margin-right: 10px;
   }
   
   button {
     padding: 10px;
   }
   </style>
   ```

### 4. 获取 JWT 并连接 WebSocket

在实际应用中，JWT 可能需要通过登录接口获取。以下是一个示例，展示了如何通过登录接口获取 JWT，并在成功后建立 WebSocket 连接。

```javascript
<template>
  <div id="app">
    <h1>WebSocket Chat Test</h1>
    <div v-if="!isConnected">
      <label for="username">Username:</label>
      <input type="text" v-model="username">
      <label for="password">Password:</label>
      <input type="password" v-model="password">
      <button @click="login">Login</button>
    </div>
    <div v-else>
      <div>
        <label for="message">Message:</label>
        <input type="text" v-model="message" @keyup.enter="sendMessage">
        <button @click="sendMessage">Send</button>
      </div>
      <div>
        <h2>Messages:</h2>
        <ul>
          <li v-for="msg in messages" :key="msg.timestamp">
            [{{ msg.timestamp }}] {{ msg.username }}: {{ msg.content }}
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      socket: null,
      message: '',
      messages: [],
      jwt: '',
      uid: '',
      groupId: '123',  // 替换为实际的群组ID
      username: '',
      password: '',
      isConnected: false
    };
  },
  methods: {
    async login() {
      try {
        const response = await axios.post('http://localhost:8080/login', {
          username: this.username,
          password: this.password
        });
        this.jwt = response.data.jwt;
        this.uid = response.data.uid;
        this.isConnected = true;
        this.connectWebSocket();
      } catch (error) {
        console.error('Login failed:', error);
      }
    },
    connectWebSocket() {
      this.socket = new WebSocket(`ws://localhost:8080/chat?groupId=${this.groupId}`);

      this.socket.onopen = () => {
        console.log('Connected to WebSocket server.');
      };

      this.socket.onmessage = (event) => {
        const message = JSON.parse(event.data);
        console.log('Received:', message.content);
        this.messages.push(message);
      };

      this.socket.onclose = () => {
        console.log('Disconnected from WebSocket server.');
      };

      this.socket.onerror = (error) => {
        console.error('WebSocket error:', error);
      };
    },
    sendMessage() {
      if (!this.message) return;

      const messageObj = {
        jwt: this.jwt,
        uid: this.uid,
        groupId: this.groupId,
        message: this.message,
      };

      this.socket.send(JSON.stringify(messageObj));
      this.message = '';
    }
  }
};
</script>

<style scoped>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  text-align: center;
  margin-top: 60px;
}

input {
  padding: 10px;
  margin-right: 10px;
}

button {
  padding: 10px;
}
</style>

```

### 总结

通过以上文档，前端开发人员可以：

1. 在 Vue 项目中建立 WebSocket 连接。
2. 通过 WebSocket 接收和处理从服务器推送的消息。
3. 通过 WebSocket 向服务器发送消息。
4. 通过登录接口获取 JWT 并在登录成功后建立 WebSocket 连接。

这样，前端开发人员就可以使用 Vue.js 与后端的 WebSocket 服务器进行通信，实现实时聊天功能。
