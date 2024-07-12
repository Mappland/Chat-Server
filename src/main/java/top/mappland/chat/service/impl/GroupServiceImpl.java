package top.mappland.chat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.mappland.chat.mapper.GroupMapper;
import top.mappland.chat.model.dto.GroupRegisterDTO;
import top.mappland.chat.model.dto.GroupJoinDTO;
import top.mappland.chat.model.dto.ChangeRoleDTO;
import top.mappland.chat.model.vo.Response;
import top.mappland.chat.service.GroupService;

/**
 * 聊天域服务实现类
 */
@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupMapper groupMapper;

    @Override
    @Transactional
    public Response<String> createGroup(GroupRegisterDTO groupRegisterDTO) {
        // 调用存储过程创建聊天域和相关表
        groupMapper.createChatGroup(groupRegisterDTO.getGroupName(), groupRegisterDTO.getOwnerId());
        return Response.success("聊天域创建成功", null);
    }

    @Override
    @Transactional
    public Response<String> joinGroup(GroupJoinDTO groupJoinDTO) {
        groupMapper.addGroupMember(groupJoinDTO.getGroupId(), groupJoinDTO.getUserId(), groupJoinDTO.getRole());
        return Response.success("加入聊天域成功", null);
    }

    @Override
    @Transactional
    public Response<String> changeMemberRole(ChangeRoleDTO changeRoleDTO) {
        groupMapper.updateMemberRole(changeRoleDTO.getGroupId(), changeRoleDTO.getUserId(), changeRoleDTO.getNewRole());
        return Response.success("成员权限变更成功", null);
    }
}
