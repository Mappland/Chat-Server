package top.mappland.chat.model.dto;

import lombok.Data;

@Data
public class GroupManagerDTO {
    private Long groupId;
    private Long uid;
    private String jwt;
}
