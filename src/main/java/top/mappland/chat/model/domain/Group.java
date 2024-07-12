package top.mappland.chat.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("chat_group.all_group")
public class Group {
    // 主键id
    @TableId(value = "groupId", type = IdType.AUTO)
    private Long groupId;
    private String group_name;
    private Long owner_id;
    private String avatar;
    private String created_at;
}
