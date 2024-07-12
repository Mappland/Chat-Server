package top.mappland.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.mappland.chat.mapper.UserMapper;
import top.mappland.chat.model.domain.User;
import top.mappland.chat.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
