package top.mappland.chat.model.dto;

import lombok.Data;

@Data
public class UserLoginDTO {
    private Long uid;
    private String username;
    private String password;
}
