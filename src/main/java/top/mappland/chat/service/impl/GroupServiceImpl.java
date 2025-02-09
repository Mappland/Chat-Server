package top.mappland.chat.service.impl;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.mappland.chat.config.DataSourceKey;
import top.mappland.chat.config.UseDataSource;
import top.mappland.chat.handler.ChatWebSocketHandler;
import top.mappland.chat.mapper.GroupMapper;
import top.mappland.chat.model.domain.GroupJoinRequest;
import top.mappland.chat.model.dto.*;
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
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private ChatWebSocketHandler chatWebSocketHandler;

    @Override
    @Transactional
    public Response<String> requestJoinGroup(GroupJoinDTO groupJoinDTO) {
        // 验证JWT
        Response<String> jwtValidationResponse = JwtUtils.validateJwt(groupJoinDTO.getJwt(), groupJoinDTO.getUserId());
        if (jwtValidationResponse.getCode() != 200) {
            return jwtValidationResponse;
        }

        Long userId = groupJoinDTO.getUserId();
        if (groupMapper.isGroupExist(groupJoinDTO.getGroupId()) < 1) {
            return Response.error(409, "群组不存在");
        }
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
    public Response<String> approveJoinRequest(GroupJoinApproveDTO groupJoinApproveDTO) {
        Long uid = groupJoinApproveDTO.getUid();
        String jwt = groupJoinApproveDTO.getJwt();
        Long requestId = groupJoinApproveDTO.getRequestId();
        Long groupId = groupJoinApproveDTO.getGroupId();

        // 验证JWT
        Response<String> jwtValidationResponse = JwtUtils.validateJwt(jwt, uid);
        if (jwtValidationResponse.getCode() != 200) {
            return jwtValidationResponse;
        }


        GroupJoinRequest joinRequest = groupMapper.getPendingJoinRequestById(groupId, requestId);
        if (joinRequest == null) {
            return Response.error(404, "加入请求不存在");
        }

        String role = groupMapper.getUserRole(groupId, uid);
        logger.info(role);
        if (!"OWNER".equals(role) && !"ADMIN".equals(role)) {
            return Response.error(403, "无权审批加入请求");
        }

        String status = groupJoinApproveDTO.isApprove() ? "APPROVED" : "REJECTED";
        groupMapper.updateJoinRequestStatus(groupId, requestId, status, uid);

        if (groupJoinApproveDTO.isApprove()) {
            logger.info(groupJoinApproveDTO.toString());
            groupMapper.addGroupMember(groupJoinApproveDTO.getGroupId(), groupJoinApproveDTO.getRequestUid());
            groupMapper.insertUserGroupItem(groupJoinApproveDTO.getGroupId(), "MEMBER", groupJoinApproveDTO.getRequestUid());
        }

        String message = groupJoinApproveDTO.isApprove() ? "加入请求已批准" : "加入请求已拒绝";
        return Response.success(message, null);
    }


    // todo 改变已经存在的用户的权限
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

    // todo 邀请用户进入群组
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

        groupMapper.addGroupMember(groupId, userId);
        return Response.success("用户邀请成功", null);
    }

    // todo 把用户从群组中删除
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

    // todo 删除群组
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

    // todo 更爱群组名称
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

    // todo 更改群组头像
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


    /**
     * 获取用户管理的群组的groupId
     * @param groupManagerDTO: 从客户端获取的类
     * @return
     */
    @Override
    @Transactional
    @UseDataSource(DataSourceKey.CHAT_USER)
    public <T> Response<T> getPendingJoinRequests(GroupManagerDTO groupManagerDTO) {
        Response<T> jwtValidationResponse = JwtUtils.validateJwt(groupManagerDTO.getJwt(), groupManagerDTO.getUid());
        if (jwtValidationResponse.getCode() != 200) {
            return jwtValidationResponse;
        }

        Long uid = groupManagerDTO.getUid();
        List<Long> adminGroups = groupMapper.getAdminGroups(uid);

        if (adminGroups.isEmpty()) {
            return (Response<T>) Response.success(Collections.emptyList());
        }

        return (Response<T>) getPendingJoinRequestsByAdminGroups(adminGroups);
    }

    @Transactional
    @UseDataSource(DataSourceKey.CHAT_GROUP)
    protected Response<List<GroupJoinRequest>> getPendingJoinRequestsByAdminGroups(List<Long> adminGroups) {
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

    @Override
    public void sendMessageToGroup(GroupMessageDTO groupMessageDTO) {
        String groupId = groupMessageDTO.getGroupId();
        List<String> members = groupMapper.findGroupMembers(groupId);

        for (String member : members) {
            try {
                sendMessageToMember(member, groupMessageDTO);
            } catch (Exception e) {
                logger.error("Failed to send message to member: " + member, e);
            }
        }
    }

    @Override
    public void sendMessageToMember(String memberId, GroupMessageDTO groupMessageDTO) throws Exception {
        chatWebSocketHandler.sendMessageToMember(groupMessageDTO);
    }

    @Override
    public String getUsernameByUid(Long uid) {
        return groupMapper.getUsernameByUid(uid);
    }
}