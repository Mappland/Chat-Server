package top.mappland.chat.service;

import top.mappland.chat.model.dto.GroupMessageDTO;
import top.mappland.chat.model.domain.GroupJoinRequest;
import top.mappland.chat.model.dto.GroupJoinDTO;
import top.mappland.chat.model.dto.ChangeRoleDTO;
import top.mappland.chat.model.vo.Response;

import java.util.List;

/**
 * 聊天域服务接口
 */
public interface GroupService {

    Response<String> requestJoinGroup(GroupJoinDTO groupJoinDTO, String token);

    Response<String> approveJoinRequest(Long uid, Long requestId, Long groupId, Boolean approve, Long requestUid);

    Response<String> changeMemberRole(ChangeRoleDTO changeRoleDTO, String token);

    Response<String> inviteUserToGroup(Long groupId, Long userId, String role, String token);

    Response<String> removeUserFromGroup(Long groupId, Long userId, String token);

    Response<String> deleteGroup(Long groupId, String token);

    Response<String> updateGroupName(Long groupId, String groupName, String token);

    Response<String> updateGroupAvatar(Long groupId, String avatar, String token);

    Response<String> addMessage(Long groupId, Long userId, String messageType, String messageContent, String filePath);

    Response<String> validateGroupAndUser(Long groupId, Long userId);

    Response<List<GroupJoinRequest>> getPendingJoinRequests(String token);

    String getUsernameByUid(Long uid);

    void sendMessageToGroup(GroupMessageDTO groupMessageDTO);

    void sendMessageToMember(String memberId, GroupMessageDTO groupMessageDTO) throws Exception;
}