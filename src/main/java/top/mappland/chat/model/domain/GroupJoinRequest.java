package top.mappland.chat.model.domain;

import lombok.Data;
import java.sql.Timestamp;

/**
 * 聊天域加入请求实体类
 */
@Data
public class GroupJoinRequest {
    private Long id;
    private Long groupId;
    private Long userId;
    private String status;
    private Timestamp requestTime;
    private Timestamp approveTime;
    private Long approverId;
}
