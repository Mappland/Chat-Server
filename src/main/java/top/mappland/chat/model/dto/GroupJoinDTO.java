package top.mappland.chat.model.dto;

import lombok.Data;

/**
 * 聊天域加入信息
 */
@Data
public class GroupJoinDTO {
    private Long groupId;
    private Long userId;
    private String role;
}
