项目目录如下：

```shell
PS D:\Project\JAVA\Chat> tree /f ./
文件夹 PATH 列表
卷序列号为 B228-4D7F
D:\PROJECT\JAVA\CHAT
│  .gitignore
│  HELP.md
│  mvnw
│  mvnw.cmd
│  pom.xml
│  README.md
│
├─src
│  ├─main
│  │  ├─java
│  │  │  └─top
│  │  │      └─mappland
│  │  │          └─chat
│  │  │              │  ChatApplication.java
│  │  │              │
│  │  │              ├─config
│  │  │              ├─mapper
│  │  │              │      UserMapper.java
│  │  │              │
│  │  │              ├─model
│  │  │              │  ├─domain
│  │  │              │  │      User.java
│  │  │              │  │
│  │  │              │  ├─dto
│  │  │              │  │      UserLoginDTO.java
│  │  │              │  │      UserRegisterDTO.java
│  │  │              │  │
│  │  │              │  └─vo
│  │  │              │          Response.java
│  │  │              │          UserVO.java
│  │  │              │
│  │  │              ├─service
│  │  │              │  │  User_service.java
│  │  │              │  │
│  │  │              │  └─impl
│  │  │              │          User_service_impl.java
│  │  │              │
│  │  │              ├─util
│  │  │              │      JwtUtils.java
│  │  │              │
│  │  │              └─web
│  │  │                  └─controller
│  │  │                          UserController.java
│  │  │
│  │  ├─resources
│  │  │  │  application-dev.yaml
│  │  │  │  application.yaml
│  │  │  │
│  │  │  ├─static
│  │  │  └─templates
│  │  └─sql
│  │          group.sql
│  │          group_auto_create.sql
│  │          user.sql
│  │          
│  └─test
│      ├─http
│      │      login.http
│      │      resigner.http
│      │
│      └─java
│          └─top
│              └─mappland
│                  └─chat
│                          ChatApplicationTests.java
│
└─target

```

UserMapper.java
```java
package top.mappland.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.mappland.chat.model.domain.User;


// 根据EmployeeMapper对应的表填写BaseMapper中的泛型 BaseMapper<User>
@Mapper
public interface UserMapper extends BaseMapper<User>{

}
```

User.java
```java
package top.mappland.chat.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 无参构造
@NoArgsConstructor
// 全参构造
@AllArgsConstructor
@Data
@TableName("user")
public class User {

    // 主键id
    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid;
    // 姓名
    private String username;
    // 密码
    private String password;
    // 邮箱
    private String email;
    // 性别
    private String gender;
    // 创建时间
    private String created_at;

}

```

UserLoginDTO

```java
package top.mappland.chat.model.dto;

import lombok.Data;

@Data
public class UserLoginDTO {
    private String username;
    private String password;
}

```

UserRegisterDTO

```java
package top.mappland.chat.model.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private String username;
    private String password;
    private String email;
    private String gender;
}

```

Response

```java
package top.mappland.chat.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Response<T> success(T data) {
        return new Response<>(200, "Success", data);
    }

    public static <T> Response<T> success(String message, T data) {
        return new Response<>(200, message, data);
    }

    public static <T> Response<T> error(int code, String message) {
        return new Response<>(code, message, null);
    }
}

```

UserVO

```java
package top.mappland.chat.model.vo;

import lombok.Data;

@Data
public class UserVO {
    private Long uid;
    private String username;
    private String email;
    private String gender;
    private String created_at;
}
```

User_service_impl.java

```java
package top.mappland.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.mappland.chat.mapper.UserMapper;
import top.mappland.chat.model.domain.User;
import top.mappland.chat.service.UserService;
import top.mappland.chat.service.User_service;

@Service
public class User_service_impl extends ServiceImpl<UserMapper, User> implements UserService {
}

```

User_service.java

```java
package top.mappland.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.mappland.chat.model.domain.User;

public interface User_service extends IService<User> {
}

```

JwtUtils.java

```java
package top.mappland.chat.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtils {
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512); // 生成密钥

    /**
     * 生成JWT Token
     *
     * @param username 用户名
     * @return 生成的Token
     */
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1天有效期
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * 验证并解析JWT Token
     *
     * @param token Token
     * @return 解析后的Claims
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

```

UserController

```java
package top.mappland.chat.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.mappland.chat.model.domain.User;
import top.mappland.chat.model.dto.UserLoginDTO;
import top.mappland.chat.model.dto.UserRegisterDTO;
import top.mappland.chat.model.vo.Response;
import top.mappland.chat.model.vo.UserVO;
import top.mappland.chat.service.UserService;
import top.mappland.chat.service.User_service;
import top.mappland.chat.util.JwtUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * UserController 处理用户相关操作，如注册、登录和获取用户详情。
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * 注册新用户。
     *
     * @param userRegisterDTO 用户注册数据传输对象，包含用户名、密码、邮箱和性别。
     * @return 包含成功消息的响应对象，或者当用户名或邮箱已存在时返回错误消息。
     */
    @PostMapping("/register")
    public Response<String> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        // 检查用户名是否已存在
        User existingUserByUsername = userService.lambdaQuery().eq(User::getUsername, userRegisterDTO.getUsername()).one();
        if (existingUserByUsername != null) {
            return Response.error(409, "用户名已存在。");
        }

        // 检查邮箱是否已存在
        User existingUserByEmail = userService.lambdaQuery().eq(User::getEmail, userRegisterDTO.getEmail()).one();
        if (existingUserByEmail != null) {
            return Response.error(409, "邮箱已存在。");
        }

        User user = new User();
        user.setUsername(userRegisterDTO.getUsername());
        user.setPassword(DigestUtils.sha256Hex(userRegisterDTO.getPassword())); // 密码加密
        user.setEmail(userRegisterDTO.getEmail());
        user.setGender(userRegisterDTO.getGender());
        user.setCreated_at(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        userService.save(user);
        logger.info("User registered successfully: {}", user.getUsername());
        return Response.success("注册成功！", null);
    }

    /**
     * 用户登录。
     *
     * @param userLoginDTO 用户登录数据传输对象，包含用户名和密码。
     * @return 如果登录成功，返回包含Token的响应对象；如果登录失败，返回错误消息。
     */
    @PostMapping("/login")
    public Response<String> login(@RequestBody UserLoginDTO userLoginDTO) {
        User user = userService.lambdaQuery().eq(User::getUsername, userLoginDTO.getUsername()).one();
        if (user == null) {
            return Response.error(404, "用户不存在。");
        }
        if (!user.getPassword().equals(DigestUtils.sha256Hex(userLoginDTO.getPassword()))) {
            return Response.error(401, "密码错误。");
        }
        // 生成Token
        String token = JwtUtils.generateToken(user.getUsername());
        logger.info("User logged in successfully: {}", user.getUsername());
        return Response.success("登录成功！", token);
    }

    /**
     * 根据用户ID获取用户详情。
     *
     * @param uid 用户ID。
     * @return 如果找到用户，返回包含用户详情的响应对象；如果未找到用户，返回错误消息。
     */
    @GetMapping("/{uid}")
    public Response<UserVO> getUser(@PathVariable Long uid) {
        User user = userService.getById(uid);
        if (user != null) {
            UserVO userVO = new UserVO();
            userVO.setUid(user.getUid());
            userVO.setUsername(user.getUsername());
            userVO.setEmail(user.getEmail());
            userVO.setGender(user.getGender());
            userVO.setCreated_at(user.getCreated_at());
            return Response.success(userVO);
        } else {
            return Response.error(404, "用户未找到。");
        }
    }
}
```

ChatApplication.java

```java
package top.mappland.chat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("top.mappland.chat.mapper")
@SpringBootApplication
public class ChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }

}
```

application.yaml
```yaml
spring:
  application:
    name: Chat-demo-service
  profiles:
    active: dev
server:
  port: 8888
```
application-dev.yaml
```yaml
spring:
  application:
    name: demo
  datasource:
    chat_user:
      url: jdbc:mysql://47.109.199.76:3306/chat_user
      username: test
      password: testtest
      driver-class-name: com.mysql.cj.jdbc.Driver
    chat_group:
      url: jdbc:mysql://localhost:3306/chat_group
      username: test
      password: testtest
      driver-class-name: com.mysql.cj.jdbc.Driver

# 控制台日志输出
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

group.sql

```sql
CREATE DATABASE IF NOT EXISTS chat_group;

USE chat_group;

CREATE TABLE all_group
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_name VARCHAR(255) NOT NULL UNIQUE,
    owner_id   BIGINT       NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE group_member
(
    id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_id  BIGINT NOT NULL,
    user_id   BIGINT NOT NULL,
    role      ENUM ('OWNER', 'ADMIN', 'MEMBER') DEFAULT 'MEMBER',
    joined_at TIMESTAMP                         DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES all_group (id)
);

CREATE TABLE group_message
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_id        BIGINT                         NOT NULL,
    user_id         BIGINT                         NOT NULL,
    message_type    ENUM ('TEXT', 'IMAGE', 'FILE') NOT NULL,
    message_content TEXT,
    file_path       VARCHAR(255),
    sent_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES all_group (id)
);

```

group_auto_create.sql

```sql
DELIMITER //

CREATE PROCEDURE create_chat_group(IN groupName VARCHAR(255), IN ownerId BIGINT)
BEGIN
    DECLARE groupId BIGINT;

    -- 插入到all_group表
    INSERT INTO chat_group.all_group (group_name, owner_id) VALUES (groupName, ownerId);
    SET groupId = LAST_INSERT_ID();

    -- 插入到group_member表
    INSERT INTO chat_group.group_member (group_id, user_id, role) VALUES (groupId, ownerId, 'OWNER');

    -- 动态创建聊天域的表
    SET @createGroupTable = CONCAT('CREATE TABLE `', groupName, '` (',
                                   'id BIGINT PRIMARY KEY AUTO_INCREMENT, ',
                                   'user_id BIGINT NOT NULL, ',
                                   'message_type ENUM(''TEXT'', ''IMAGE'', ''FILE'') NOT NULL, ',
                                   'message_content TEXT, ',
                                   'file_path VARCHAR(255), ',
                                   'sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ',
                                   'FOREIGN KEY (user_id) REFERENCES chat_user.user(uid))');
    PREPARE stmt FROM @createGroupTable;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    -- 动态创建聊天域的消息表
    SET @createGroupMessageTable = CONCAT('CREATE TABLE `', groupName, '_message` (',
                                          'id BIGINT PRIMARY KEY AUTO_INCREMENT, ',
                                          'group_id BIGINT NOT NULL, ',
                                          'user_id BIGINT NOT NULL, ',
                                          'message_type ENUM(''TEXT'', ''IMAGE'', ''FILE'') NOT NULL, ',
                                          'message_content TEXT, ',
                                          'file_path VARCHAR(255), ',
                                          'sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ',
                                          'FOREIGN KEY (group_id) REFERENCES all_group(id), ',
                                          'FOREIGN KEY (user_id) REFERENCES chat_user.user(uid))');
    PREPARE stmt FROM @createGroupMessageTable;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END //

DELIMITER ;

```

user.sql

```sql
CREATE TABLE user
(
    uid        BIGINT PRIMARY KEY AUTO_INCREMENT,
    username   VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    gender     VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE user
    AUTO_INCREMENT = 100000;

```

pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.1</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>top.mappland</groupId>
    <artifactId>Chat</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>Chat</name>
    <description>Chat</description>
    <url/>
    <licenses>
        <license/>
    </licenses>
    <developers>
        <developer/>
    </developers>
    <scm>
        <connection/>
        <developerConnection/>
        <tag/>
        <url/>
    </scm>
    <properties>
        <java.version>22</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>3.5.7</version>
            <type>pom</type>
        </dependency>

        <!-- sha256 -->
        <dependency>
            <groupId>commons-codec</groupId>
            <artifactId>commons-codec</artifactId>
        </dependency>

        <!-- jwt -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.11.5</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>

        <!-- 多个数据源依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```



请根据以上项目目录和项目文件，完成聊天域注册功能的java代码实现，并编写javadoc和适当的注释，使用中文回复

现在数据库情况如下：
server(MYSQL):

- chat_user(数据库)：
    - user(数据表)
- chat_group(数据库):
- chat_group_message(数据库):

根据以下要求完成数据库创建和数据表创建，假设有一个聊天域为(mappland's_group)，数据库结构应该为：

server(MYSQL):

- chat_user(数据库)：
    - user(数据表)
- chat_group(数据库):
    - all_group(数据表)
    - mappland's_group(数据表)
- chat_group_message(数据库):
    - mappland's_group_message(数据表)

要求：
1. 创建聊天域数据库(chat_group），数据库内一个聊天域对应一个聊天表，数据库中存在一个总表(all_group)，总表中有聊天域名称，域主，创建时间，更新时间等必要信息
2. 创建聊天域时自动在chat_group_message(数据库)使用存储过程创建数据表
3. 聊天域的名称唯一
4. 聊天域的创建者为域主
5. 聊天域共有三级权限：域主，管理员，成员
6. 聊天域的域主、管理员可以邀请其他用户加入
7.
8. 聊天域的域主、管理员可以删除用户
9. 聊天域的域主可以删除聊天域
10. 聊天域的域主、管理员可以设置聊天域的名称
11. 聊天域的域主、管理员可以设置聊天域的头像
12. 聊天域的数据表中存储聊天域的成员及权限，进入日期
13. 以适当的方式存储聊天记录内容，聊天记录可能会有以下几种类型：图片，文字，文件