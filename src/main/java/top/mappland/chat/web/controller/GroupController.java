package top.mappland.chat.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import top.mappland.chat.model.domain.Group;
import top.mappland.chat.model.domain.GroupJoinRequest;
import top.mappland.chat.model.dto.*;
import top.mappland.chat.model.vo.Response;
import top.mappland.chat.service.GroupCreateService;
import top.mappland.chat.service.GroupService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*", methods = {})
@RestController
@RequestMapping("/group")
public class GroupController {

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupCreateService groupCreateService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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
    public Response<String> approveJoinRequest(@RequestBody GroupJoinApproveDTO groupJoinApproveDTO) {
        return groupService.approveJoinRequest(groupJoinApproveDTO);
    }

    @PostMapping("/join")
    public Response<String> requestJoinGroup(@RequestBody GroupJoinDTO groupJoinDTO) {
        return groupService.requestJoinGroup(groupJoinDTO);
    }

    // 获取群组申请
    @PostMapping("/pendingJoinRequests")
    public Response<List<GroupJoinRequest>> getPendingJoinRequests(@RequestBody GroupManagerDTO groupManagerDTO) {
        return groupService.getPendingJoinRequests(groupManagerDTO);
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

}
