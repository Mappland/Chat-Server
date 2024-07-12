package top.mappland.chat.service.impl;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.mappland.chat.config.DataSourceKey;
import top.mappland.chat.config.UseDataSource;
import top.mappland.chat.mapper.GroupMapper;
import top.mappland.chat.model.domain.GroupJoinRequest;
import top.mappland.chat.model.dto.GroupRegisterDTO;
import top.mappland.chat.model.dto.GroupJoinDTO;
import top.mappland.chat.model.dto.ChangeRoleDTO;
import top.mappland.chat.model.vo.Response;
import top.mappland.chat.service.GroupService;
import top.mappland.chat.util.JwtUtils;
import top.mappland.chat.web.controller.UserController;

import java.util.Collections;
import java.util.List;

/**
 * 聊天域服务实现类
 */
@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupMapper groupMapper;


//    @Override
//    @Transactional
//    public Response<String> createGroup(GroupRegisterDTO groupRegisterDTO) {
//        Long[] groupId = new Long[1];
//        groupMapper.createChatGroup(groupRegisterDTO.getGroupName(), groupRegisterDTO.getOwnerId(), groupId);
//        groupMapper.insertAdminGroupRecord(groupRegisterDTO.getOwnerId(), groupId[0], "OWNER");
//        return Response.success("聊天域创建成功，群组ID: " + groupId[0], null);
//    }

    @Override
    @Transactional
    public Response<String> requestJoinGroup(GroupJoinDTO groupJoinDTO, String token) {
        Claims claims = JwtUtils.parseToken(token);
        Long userId = claims.get("uid", Long.class);

        if (groupMapper.isUserInGroup(groupJoinDTO.getGroupId(), userId) > 0) {
            return Response.error(409, "用户已在群组中");
        }

        if (groupMapper.getPendingJoinRequest(groupJoinDTO.getGroupId(), userId) != null) {
            return Response.error(409, "加入请求已存在");
        }

        groupMapper.addJoinRequest(groupJoinDTO.getGroupId(), userId);
        return Response.success("加入请求已提交，等待审批", null);
    }

    @Override
    @Transactional
    public Response<String> approveJoinRequest(Long requestId, String token) {
        Claims claims = JwtUtils.parseToken(token);
        Long approverId = claims.get("uid", Long.class);

        GroupJoinRequest joinRequest = groupMapper.getPendingJoinRequestById(requestId);
        if (joinRequest == null) {
            return Response.error(404, "加入请求不存在");
        }

        String role = groupMapper.getUserRole(joinRequest.getGroupId(), approverId);
        if (!"OWNER".equals(role) && !"ADMIN".equals(role)) {
            return Response.error(403, "无权审批加入请求");
        }

        groupMapper.updateJoinRequestStatus(requestId, "APPROVED", approverId);
        groupMapper.addGroupMember(joinRequest.getGroupId(), joinRequest.getUserId(), "MEMBER");
        return Response.success("加入请求已批准", null);
    }

    @Override
    @Transactional
    public Response<String> changeMemberRole(ChangeRoleDTO changeRoleDTO, String token) {
        Claims claims = JwtUtils.parseToken(token);
        Long approverId = claims.get("uid", Long.class);

        String role = groupMapper.getUserRole(changeRoleDTO.getGroupId(), approverId);
        if (!"OWNER".equals(role) && !"ADMIN".equals(role)) {
            return Response.error(403, "无权更改成员权限");
        }

        groupMapper.updateMemberRole(changeRoleDTO.getGroupId(), changeRoleDTO.getUserId(), changeRoleDTO.getNewRole());

        if ("ADMIN".equals(changeRoleDTO.getNewRole()) || "OWNER".equals(changeRoleDTO.getNewRole())) {
            groupMapper.insertAdminGroupRecord(changeRoleDTO.getUserId(), changeRoleDTO.getGroupId(), changeRoleDTO.getNewRole());
        } else {
            groupMapper.deleteAdminGroupRecord(changeRoleDTO.getUserId(), changeRoleDTO.getGroupId());
        }

        return Response.success("成员权限变更成功", null);
    }

    @Override
    @Transactional
    public Response<String> inviteUserToGroup(Long groupId, Long userId, String role, String token) {
        Claims claims = JwtUtils.parseToken(token);
        Long inviterId = claims.get("uid", Long.class);

        String inviterRole = groupMapper.getUserRole(groupId, inviterId);
        if (!"OWNER".equals(inviterRole) && !"ADMIN".equals(inviterRole)) {
            return Response.error(403, "无权邀请用户");
        }

        if (groupMapper.isUserInGroup(groupId, userId) > 0) {
            return Response.error(409, "用户已在群组中");
        }

        groupMapper.addGroupMember(groupId, userId, role);
        return Response.success("用户邀请成功", null);
    }

    @Override
    @Transactional
    public Response<String> removeUserFromGroup(Long groupId, Long userId, String token) {
        Claims claims = JwtUtils.parseToken(token);
        Long removerId = claims.get("uid", Long.class);

        String role = groupMapper.getUserRole(groupId, removerId);
        if (!"OWNER".equals(role) && !"ADMIN".equals(role)) {
            return Response.error(403, "无权移除用户");
        }

        groupMapper.removeGroupMember(groupId, userId);
        return Response.success("用户移除成功", null);
    }

    @Override
    @Transactional
    public Response<String> deleteGroup(Long groupId, String token) {
        Claims claims = JwtUtils.parseToken(token);
        Long userId = claims.get("uid", Long.class);

        String role = groupMapper.getUserRole(groupId, userId);
        if (!"OWNER".equals(role)) {
            return Response.error(403, "无权删除群组");
        }

        groupMapper.deleteChatGroup(groupId);
        groupMapper.deleteAdminGroupRecord(userId, groupId);
        return Response.success("聊天域删除成功", null);
    }

    @Override
    @Transactional
    public Response<String> updateGroupName(Long groupId, String groupName, String token) {
        Claims claims = JwtUtils.parseToken(token);
        Long userId = claims.get("uid", Long.class);

        String role = groupMapper.getUserRole(groupId, userId);
        if (!"OWNER".equals(role) && !"ADMIN".equals(role)) {
            return Response.error(403, "无权更改群组名称");
        }

        groupMapper.updateGroupName(groupId, groupName);
        return Response.success("群组名称更新成功", null);
    }

    @Override
    @Transactional
    public Response<String> updateGroupAvatar(Long groupId, String avatar, String token) {
        Claims claims = JwtUtils.parseToken(token);
        Long userId = claims.get("uid", Long.class);

        String role = groupMapper.getUserRole(groupId, userId);
        if (!"OWNER".equals(role) && !"ADMIN".equals(role)) {
            return Response.error(403, "无权更改群组头像");
        }

        groupMapper.updateGroupAvatar(groupId, avatar);
        return Response.success("群组头像更新成功", null);
    }

    @Override
    @Transactional
    public Response<String> addMessage(Long groupId, Long userId, String messageType, String messageContent, String filePath) {
        groupMapper.addMessage(groupId, userId, messageType, messageContent, filePath);
        return Response.success("消息发送成功", null);
    }

    @Override
    @Transactional
    @UseDataSource(DataSourceKey.CHAT_USER)
    public Response<List<GroupJoinRequest>> getPendingJoinRequests(String token) {
        Claims claims = JwtUtils.parseToken(token);
        Long uid = claims.get("uid", Long.class);

        // 获取用户管理的群组ID列表
        List<Long> adminGroups = groupMapper.getAdminGroups(uid);

        // 如果没有管理的群组，直接返回空列表
        if (adminGroups.isEmpty()) {
            return Response.success(Collections.emptyList());
        }

        return getPendingJoinRequestsByAdminGroups(adminGroups);
    }

    @Transactional
    @UseDataSource(DataSourceKey.CHAT_GROUP)
    protected Response<List<GroupJoinRequest>> getPendingJoinRequestsByAdminGroups(List<Long> adminGroups) {
        // 获取待处理的加入请求
        List<GroupJoinRequest> joinRequests = groupMapper.getPendingJoinRequests(adminGroups);
        return Response.success(joinRequests);
    }

    @Override
    public Response<String> validateGroupAndUser(Long groupId, Long userId) {
        if (groupMapper.isGroupExist(groupId) == 0) {
            return Response.error(404, "群组不存在");
        }

        if (groupMapper.isUserInGroup(groupId, userId) > 0) {
            return Response.error(409, "用户已在群组中");
        }

        return null;
    }
}