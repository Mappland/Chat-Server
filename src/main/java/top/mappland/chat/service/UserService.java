package top.mappland.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.mappland.chat.model.domain.User;
import top.mappland.chat.model.domain.UserGroup;

import java.util.List;

public interface UserService extends IService<User> {
    void recordLoginHistory(Long uid, String loginIp);
    List<UserGroup> getUserGroups(Long uid);
}
