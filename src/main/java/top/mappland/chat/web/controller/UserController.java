package top.mappland.chat.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.mappland.chat.model.domain.User;
import top.mappland.chat.model.dto.UserLoginDTO;
import top.mappland.chat.model.dto.UserRegisterDTO;
import top.mappland.chat.model.vo.Response;
import top.mappland.chat.model.vo.UserVO;
import top.mappland.chat.service.UserService;
import top.mappland.chat.util.JwtUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * UserController 处理用户相关操作，如注册、登录和获取用户详情。
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * 注册新用户。
     *
     * @param userRegisterDTO 用户注册数据传输对象，包含用户名、密码、邮箱和性别。
     * @return 包含成功消息的响应对象，或者当用户名或邮箱已存在时返回错误消息。
     */
    @PostMapping("/register")
    public Response<String> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        // 检查用户名是否已存在
        User existingUserByUsername = userService.lambdaQuery().eq(User::getUsername, userRegisterDTO.getUsername()).one();
        if (existingUserByUsername != null) {
            return Response.error(409, "用户名已存在。");
        }

        // 检查邮箱是否已存在
        User existingUserByEmail = userService.lambdaQuery().eq(User::getEmail, userRegisterDTO.getEmail()).one();
        if (existingUserByEmail != null) {
            return Response.error(409, "邮箱已存在。");
        }

        User user = new User();
        user.setUsername(userRegisterDTO.getUsername());
        user.setPassword(DigestUtils.sha256Hex(userRegisterDTO.getPassword())); // 密码加密
        user.setEmail(userRegisterDTO.getEmail());
        user.setGender(userRegisterDTO.getGender());
        user.setCreated_at(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        userService.save(user);
        logger.info("User registered successfully: {}", user.getUid());
        return Response.success("注册成功！", String.valueOf(user.getUid()));
    }

    /**
     * 用户登录。
     *
     * @param userLoginDTO 用户登录数据传输对象，包含用户ID、用户名和密码。
     * @return 如果登录成功，返回包含Token的响应对象；如果登录失败，返回错误消息。
     */
    @PostMapping("/login")
    public Response<String> login(@RequestBody UserLoginDTO userLoginDTO) {
        if (userLoginDTO.getUid() == null && userLoginDTO.getUsername() != null) {
            return Response.error(400, "请使用UID进行登录。");
        }

        User user = userService.getById(userLoginDTO.getUid());
        if (user == null) {
            return Response.error(404, "用户不存在。");
        }
        if (!user.getPassword().equals(DigestUtils.sha256Hex(userLoginDTO.getPassword()))) {
            return Response.error(401, "密码错误。");
        }
        // 生成Token
        String token = JwtUtils.generateToken(user.getUsername(), user.getUid());
        logger.info("User logged in successfully: {}", user.getUsername());

        // 获取客户端IP地址
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String loginIp = attributes.getRequest().getRemoteAddr();

        // 记录登录历史
        userService.recordLoginHistory(user.getUid(), loginIp);

        return Response.success("登录成功！", token);
    }

    /**
     * 根据用户ID获取用户详情。
     *
     * @param uid 用户ID。
     * @return 如果找到用户，返回包含用户详情的响应对象；如果未找到用户，返回错误消息。
     */
    @GetMapping("/{uid}")
    public Response<UserVO> getUser(@PathVariable Long uid) {
        User user = userService.getById(uid);
        if (user != null) {
            UserVO userVO = new UserVO();
            userVO.setUid(user.getUid());
            userVO.setUsername(user.getUsername());
            userVO.setEmail(user.getEmail());
            userVO.setGender(user.getGender());
            userVO.setCreated_at(user.getCreated_at());
            return Response.success(userVO);
        } else {
            return Response.error(404, "用户未找到。");
        }
    }
}
