package top.mappland.chat.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.mappland.chat.model.dto.GroupRegisterDTO;
import top.mappland.chat.model.dto.GroupJoinDTO;
import top.mappland.chat.model.dto.ChangeRoleDTO;
import top.mappland.chat.model.vo.Response;
import top.mappland.chat.service.GroupService;

/**
 * 聊天域控制器
 */
@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    /**
     * 创建聊天域
     *
     * @param groupRegisterDTO 聊天域注册信息
     * @return 创建结果
     */
    @PostMapping("/create")
    public Response<String> createGroup(@RequestBody GroupRegisterDTO groupRegisterDTO) {
        return groupService.createGroup(groupRegisterDTO);
    }

    /**
     * 加入聊天域
     *
     * @param groupJoinDTO 聊天域加入信息
     * @return 加入结果
     */
    @PostMapping("/join")
    public Response<String> joinGroup(@RequestBody GroupJoinDTO groupJoinDTO) {
        return groupService.joinGroup(groupJoinDTO);
    }

    /**
     * 改变成员权限
     *
     * @param changeRoleDTO 改变权限信息
     * @return 改变结果
     */
    @PostMapping("/changeRole")
    public Response<String> changeMemberRole(@RequestBody ChangeRoleDTO changeRoleDTO) {
        return groupService.changeMemberRole(changeRoleDTO);
    }
}
