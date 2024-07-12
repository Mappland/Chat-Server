package top.mappland.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.mappland.chat.model.domain.User;

/**
 * UserMapper 是用于操作用户数据的接口。
 * 它继承了 BaseMapper 接口，可以直接使用 MyBatis-Plus 提供的 CRUD 方法。
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
