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

CREATE TABLE group_join_request
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_id     BIGINT NOT NULL,
    user_id      BIGINT NOT NULL,
    status       ENUM ('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    request_time TIMESTAMP                                DEFAULT CURRENT_TIMESTAMP,
    approve_time TIMESTAMP,
    approver_id  BIGINT,
    FOREIGN KEY (group_id) REFERENCES all_group (id),
    FOREIGN KEY (user_id) REFERENCES chat_user.user (uid)
);


DELIMITER //

CREATE PROCEDURE create_chat_group(IN groupName VARCHAR(255), IN ownerId BIGINT)
BEGIN
    DECLARE groupId BIGINT;

    -- 插入到 all_group 表
    INSERT INTO chat_group.all_group (group_name, owner_id) VALUES (groupName, ownerId);
    SET groupId = LAST_INSERT_ID();

    -- 插入到 group_member 表
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
