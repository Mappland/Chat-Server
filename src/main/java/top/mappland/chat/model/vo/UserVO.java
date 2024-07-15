package top.mappland.chat.model.vo;

import lombok.Data;

@Data
public class UserVO {
    private Long uid;
    private String username;
    private String email;
    private String gender;
}