package top.mappland.chat.web.controller;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.mappland.chat.model.domain.GroupJoinRequest;
import top.mappland.chat.model.domain.Group;
import top.mappland.chat.model.dto.GroupJoinApproveDTO;
import top.mappland.chat.model.dto.GroupRegisterDTO;
import top.mappland.chat.model.dto.GroupJoinDTO;
import top.mappland.chat.model.dto.ChangeRoleDTO;
import top.mappland.chat.model.vo.Response;
import top.mappland.chat.service.GroupCreateService;
import top.mappland.chat.service.GroupService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 聊天域控制器
 */
@RestController
@RequestMapping("/group")
public class GroupController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupCreateService groupCreateService;

    @PostMapping("/create")
    public Response<String> createGroup(@RequestBody GroupRegisterDTO groupRegisterDTO) {
        Group group = new Group();
        group.setGroup_name(groupRegisterDTO.getGroupName());
        group.setOwner_id(groupRegisterDTO.getOwnerId());
        group.setCreated_at(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        groupCreateService.save(group);
        logger.info("User registered successfully: {}", group.getGroupId());
        return Response.success("群组创建成功！id如下", String.valueOf(group.getGroupId()));
    }

    @PostMapping("/approveJoin")
    public Response<String> approveJoinRequest(@RequestBody GroupJoinApproveDTO groupJoinApproveDTO, @RequestHeader("Authorization") String token) {
//    public Response<String> approveJoinRequest(@RequestBody Long requestId, @RequestBody Long groupId, @RequestBody Boolean approve, @RequestHeader("Authorization") String token) {
        Long requestId = groupJoinApproveDTO.getRequestId();
        Long groupId = groupJoinApproveDTO.getGroupId();
        Long uid = groupJoinApproveDTO.getUid();
        Long requestUid = groupJoinApproveDTO.getRequestUid();
        boolean approve = groupJoinApproveDTO.isApprove();
        return groupService.approveJoinRequest(uid, requestId, groupId, approve, requestUid);
    }


    @PostMapping("/join")
    public Response<String> requestJoinGroup(@RequestBody GroupJoinDTO groupJoinDTO, @RequestHeader("Authorization") String token) {
        return groupService.requestJoinGroup(groupJoinDTO, token);
    }

    @PostMapping("/changeRole")
    public Response<String> changeMemberRole(@RequestBody ChangeRoleDTO changeRoleDTO, @RequestHeader("Authorization") String token) {
        return groupService.changeMemberRole(changeRoleDTO, token);
    }

    @PostMapping("/invite")
    public Response<String> inviteUserToGroup(@RequestParam Long groupId, @RequestParam Long userId, @RequestParam String role, @RequestHeader("Authorization") String token) {
        return groupService.inviteUserToGroup(groupId, userId, role, token);
    }

    @PostMapping("/remove")
    public Response<String> removeUserFromGroup(@RequestParam Long groupId, @RequestParam Long userId, @RequestHeader("Authorization") String token) {
        return groupService.removeUserFromGroup(groupId, userId, token);
    }

    @DeleteMapping("/delete")
    public Response<String> deleteGroup(@RequestParam Long groupId, @RequestHeader("Authorization") String token) {
        return groupService.deleteGroup(groupId, token);
    }

    @PostMapping("/updateName")
    public Response<String> updateGroupName(@RequestParam Long groupId, @RequestParam String groupName, @RequestHeader("Authorization") String token) {
        return groupService.updateGroupName(groupId, groupName, token);
    }

    @PostMapping("/updateAvatar")
    public Response<String> updateGroupAvatar(@RequestParam Long groupId, @RequestParam String avatar, @RequestHeader("Authorization") String token) {
        return groupService.updateGroupAvatar(groupId, avatar, token);
    }

    @GetMapping("/pendingJoinRequests")
    public Response<List<GroupJoinRequest>> getPendingJoinRequests(@RequestHeader("Authorization") String token) {
        return groupService.getPendingJoinRequests(token);
    }

    @PostMapping("/sendMessage")
    public Response<String> sendMessage(@RequestParam Long groupId, @RequestParam Long userId, @RequestParam String messageType, @RequestParam String messageContent, @RequestParam String filePath) {
        return groupService.addMessage(groupId, userId, messageType, messageContent, filePath);
    }
}
