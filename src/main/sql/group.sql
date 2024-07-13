CREATE TABLE all_group
(
    groupId    BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_name VARCHAR(255) NOT NULL,
    owner_id   BIGINT       NOT NULL,
    avatar     VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE all_group
    AUTO_INCREMENT = 100000000;

DELIMITER //

CREATE PROCEDURE create_chat_group(IN groupId BIGINT, IN ownerId BIGINT)
BEGIN
    -- 动态创建聊天域的成员表
    SET @createGroupMemberTable = CONCAT('CREATE TABLE chat_group.', groupId, '_member (',
                                         'id BIGINT PRIMARY KEY AUTO_INCREMENT, ',
                                         'group_id BIGINT NOT NULL, ',
                                         'uid BIGINT NOT NULL, ',
                                         'role ENUM(''OWNER'', ''ADMIN'', ''MEMBER'') DEFAULT ''MEMBER'', ',
                                         'joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ',
                                         'FOREIGN KEY (group_id) REFERENCES chat_group.all_group(groupId))');
    PREPARE stmt FROM @createGroupMemberTable;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    -- 动态创建聊天域的消息表
    SET @createGroupMessageTable = CONCAT('CREATE TABLE chat_group.', groupId, '_message (',
                                          'id BIGINT PRIMARY KEY AUTO_INCREMENT, ',
                                          'group_id BIGINT NOT NULL, ',
                                          'uid BIGINT NOT NULL, ',
                                          'message_type ENUM(''TEXT'', ''IMAGE'', ''FILE'') NOT NULL, ',
                                          'message_content TEXT, ',
                                          'file_path VARCHAR(255), ',
                                          'sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ',
                                          'FOREIGN KEY (group_id) REFERENCES chat_group.all_group(groupId), ',
                                          'FOREIGN KEY (uid) REFERENCES chat_user.user(uid))');
    PREPARE stmt FROM @createGroupMessageTable;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    -- 动态创建聊天域的加入请求表
    SET @createGroupJoinRequestTable = CONCAT('CREATE TABLE chat_group.', groupId, '_join_request (',
                                              'id BIGINT PRIMARY KEY AUTO_INCREMENT, ',
                                              'group_id BIGINT NOT NULL, ',
                                              'uid BIGINT NOT NULL, ',
                                              'status ENUM(''PENDING'', ''APPROVED'', ''REJECTED'') DEFAULT ''PENDING'', ',
                                              'request_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ',
                                              'approve_time TIMESTAMP, ',
                                              'approver_id BIGINT, ',
                                              'FOREIGN KEY (group_id) REFERENCES chat_group.all_group(groupId), ',
                                              'FOREIGN KEY (uid) REFERENCES chat_user.user(uid))');
    PREPARE stmt FROM @createGroupJoinRequestTable;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    -- 插入到 group_member 表
    SET @insertGroupMember =
            CONCAT('INSERT INTO chat_group.', groupId, '_member (group_id, uid, role) VALUES (', groupId, ', ',
                   ownerId, ', ''OWNER'')');
    PREPARE stmt FROM @insertGroupMember;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    -- 插入到 chat_user.${ownerId}_admin_group 表
    SET @insertAdminGroup =
            CONCAT('INSERT INTO chat_user.', ownerId, '_admin_group (uid, group_id, role) VALUES (', ownerId, ', ',
                   groupId, ', ', '''', 'OWNER', '''', ')');
    PREPARE stmt FROM @insertAdminGroup;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

END //

DELIMITER ;
