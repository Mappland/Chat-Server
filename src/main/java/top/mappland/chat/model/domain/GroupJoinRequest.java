package top.mappland.chat.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.sql.Timestamp;

/**
 * 聊天域加入请求实体类
 */
@Data
public class GroupJoinRequest {
    private Long id;
    private Long groupId;
    private Long uid;
    private String status;
    @TableField(value = "request_time")
    private Timestamp requestTime;
    @TableField(value = "approve_time")
    private Timestamp approveTime;
    private Long approverId;
}
