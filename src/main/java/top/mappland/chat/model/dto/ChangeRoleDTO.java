package top.mappland.chat.model.dto;

import lombok.Data;

/**
 * 改变成员权限信息
 */
@Data
public class ChangeRoleDTO {
    private Long groupId;
    private Long userId;
    private String newRole;
}
