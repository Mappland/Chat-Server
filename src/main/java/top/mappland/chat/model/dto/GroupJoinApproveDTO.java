package top.mappland.chat.model.dto;

import lombok.Data;

/**
 * 聊天域处理加入请求
 */
@Data
public class GroupJoinApproveDTO {
    private Long groupId;
    private Long requestId;
    private String jwt;
    private Long uid;
    private Long requestUid;
    private boolean approve;
}
