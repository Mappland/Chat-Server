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


DELIMITER //

CREATE PROCEDURE CreateUserTables(IN user_uid BIGINT)
BEGIN
    DECLARE friend_table_name VARCHAR(255);
    DECLARE group_table_name VARCHAR(255);
    DECLARE login_history_table_name VARCHAR(255);
    declare admin_group_name VARCHAR(255);

    SET friend_table_name = CONCAT(user_uid, '_friend');
    SET group_table_name = CONCAT(user_uid, '_group');
    SET login_history_table_name = CONCAT(user_uid, '_login_history');
    SET admin_group_name = CONCAT(user_uid, '_admin_group');

    SET @friend_table_sql = CONCAT('CREATE TABLE ', friend_table_name, ' (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        friend_uid BIGINT NOT NULL,
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    )');

    SET @group_table_sql = CONCAT('CREATE TABLE ', group_table_name, ' (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        group_id BIGINT NOT NULL,
        role ENUM(''OWNER'', ''ADMIN'', ''MEMBER'') DEFAULT ''MEMBER'',
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    )');

    SET @login_history_table_sql = CONCAT('CREATE TABLE ', login_history_table_name, ' (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        login_ip VARCHAR(255) NOT NULL,
        login_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    )');

    SET @admin_group = CONCAT('CREATE TABLE ', admin_group_name, ' (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        uid BIGINT NOT NULL,
        group_id BIGINT NOT NULL,
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        role ENUM(''OWNER'', ''ADMIN'')
    )');


    PREPARE stmt1 FROM @friend_table_sql;
    EXECUTE stmt1;
    DEALLOCATE PREPARE stmt1;

    PREPARE stmt2 FROM @group_table_sql;
    EXECUTE stmt2;
    DEALLOCATE PREPARE stmt2;

    PREPARE stmt3 FROM @login_history_table_sql;
    EXECUTE stmt3;
    DEALLOCATE PREPARE stmt3;

    PREPARE stmt4 FROM @admin_group;
    EXECUTE stmt4;
    DEALLOCATE PREPARE stmt4;
END //

DELIMITER ;
