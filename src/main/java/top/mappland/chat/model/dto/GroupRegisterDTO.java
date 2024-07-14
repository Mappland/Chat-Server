package top.mappland.chat.model.dto;

import lombok.Data;

/**
 * 聊天域注册信息
 */
@Data
public class GroupRegisterDTO {
    private String groupName;
    private Long[] groupId;
    private String jwt;
    private Long ownerId;
}
