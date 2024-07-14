package top.mappland.chat.service;

import top.mappland.chat.model.dto.*;
import top.mappland.chat.model.domain.GroupJoinRequest;
import top.mappland.chat.model.vo.Response;

import java.util.List;

/**
 * 聊天域服务接口
 */
public interface GroupService {

    Response<String> requestJoinGroup(GroupJoinDTO groupJoinDTO);

    Response<String> approveJoinRequest(GroupJoinApproveDTO groupJoinApproveDTO);

    Response<String> changeMemberRole(ChangeRoleDTO changeRoleDTO, String token);

    Response<String> inviteUserToGroup(Long groupId, Long userId, String role, String token);

    Response<String> removeUserFromGroup(Long groupId, Long userId, String token);

    Response<String> deleteGroup(Long groupId, String token);

    Response<String> updateGroupName(Long groupId, String groupName, String token);

    Response<String> updateGroupAvatar(Long groupId, String avatar, String token);


    Response<String> validateGroupAndUser(Long groupId, Long userId);

    <T> Response<T>  getPendingJoinRequests(GroupManagerDTO groupManagerDTO);

    String getUsernameByUid(Long uid);

    void sendMessageToGroup(GroupMessageDTO groupMessageDTO);

    void sendMessageToMember(String memberId, GroupMessageDTO groupMessageDTO) throws Exception;
}