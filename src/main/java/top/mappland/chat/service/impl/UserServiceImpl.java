package top.mappland.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import top.mappland.chat.mapper.UserMapper;
import top.mappland.chat.model.domain.User;
import top.mappland.chat.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean save(User user) {
        boolean isSaved = super.save(user);
        if (isSaved) {
            createDynamicTables(user.getUid());
            user.setUid(user.getUid());
        }
        return isSaved;
    }

    private void createDynamicTables(Long uid) {
        jdbcTemplate.update("CALL chat_user.CreateUserTables(?)", uid);
    }

    @Override
    public void recordLoginHistory(Long uid, String loginIp) {
        String tableName = uid + "_login_history";
        String sql = "INSERT INTO " + tableName + " (login_ip, login_time) VALUES (?, CURRENT_TIMESTAMP)";
        jdbcTemplate.update(sql, loginIp);
    }
}
