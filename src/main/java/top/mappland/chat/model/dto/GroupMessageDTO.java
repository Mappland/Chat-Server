package top.mappland.chat.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupMessageDTO {
    private String jwt;
    private String uid;
    private String username;
    private String groupId;
    private String message;
}
