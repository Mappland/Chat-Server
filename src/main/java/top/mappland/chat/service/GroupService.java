package top.mappland.chat.service;

import top.mappland.chat.model.dto.GroupRegisterDTO;
import top.mappland.chat.model.dto.GroupJoinDTO;
import top.mappland.chat.model.dto.ChangeRoleDTO;
import top.mappland.chat.model.vo.Response;

/**
 * 聊天域服务接口
 */
public interface GroupService {

    /**
     * 创建聊天域
     *
     * @param groupRegisterDTO 聊天域注册信息
     * @return 创建结果
     */
    Response<String> createGroup(GroupRegisterDTO groupRegisterDTO);

    /**
     * 加入聊天域
     *
     * @param groupJoinDTO 聊天域加入信息
     * @return 加入结果
     */
    Response<String> joinGroup(GroupJoinDTO groupJoinDTO);

    /**
     * 改变成员权限
     *
     * @param changeRoleDTO 改变权限信息
     * @return 改变结果
     */
    Response<String> changeMemberRole(ChangeRoleDTO changeRoleDTO);
}
