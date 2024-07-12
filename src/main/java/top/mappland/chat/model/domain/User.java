package top.mappland.chat.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User 是表示用户的实体类。
 * 它使用了 Lombok 注解生成构造方法和 getter/setter 方法。
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("user")
public class User {

    // 主键id
    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid;
    // 姓名
    private String username;
    // 密码
    private String password;
    // 邮箱
    private String email;
    // 性别
    private String gender;
    // 创建时间
    private String created_at;

}
