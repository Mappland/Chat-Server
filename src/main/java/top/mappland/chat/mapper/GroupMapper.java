package top.mappland.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.StatementType;
import top.mappland.chat.model.domain.GroupJoinRequest;
import top.mappland.chat.model.domain.User;

import java.util.List;

/**
 * 聊天域 Mapper 接口
 */
@Mapper
public interface GroupMapper extends BaseMapper<User> {

//    @Select("{CALL create_chat_group(#{groupName, mode=IN, jdbcType=VARCHAR}, " +
//            "#{ownerId, mode=IN, jdbcType=BIGINT}, " +
//            "#{groupId, mode=OUT, jdbcType=BIGINT})}")
//    @Options(statementType = StatementType.CALLABLE)
//    void createChatGroup(@Param("groupName") String groupName,
//                         @Param("ownerId") Long ownerId,
//                         @Param("groupId") Long[] groupId);

    /**
     * 添加成员到聊天域
     *
     * @param groupId 聊天域 ID
     * @param userId  用户 ID
     * @param role    角色
     */
    @Update("INSERT INTO chat_group.${groupId}_member (group_id, user_id, role) VALUES (#{groupId}, #{userId}, #{role})")
    void addGroupMember(@Param("groupId") Long groupId, @Param("userId") Long userId, @Param("role") String role);

    /**
     * 更新成员角色
     *
     * @param groupId 聊天域 ID
     * @param userId  用户 ID
     * @param newRole 新角色
     */
    @Update("UPDATE chat_group.${groupId}_member SET role = #{newRole} WHERE group_id = #{groupId} AND user_id = #{userId}")
    void updateMemberRole(@Param("groupId") Long groupId, @Param("userId") Long userId, @Param("newRole") String newRole);

    /**
     * 查询聊天域是否存在
     *
     * @param groupId 聊天域 ID
     * @return 成员数量
     */
    @Select("SELECT COUNT(*) FROM chat_group.all_group WHERE id = #{groupId}")
    int isGroupExist(@Param("groupId") Long groupId);

    /**
     * 查询用户是否在聊天域中
     *
     * @param groupId 聊天域 ID
     * @param userId  用户 ID
     * @return 是否在聊天域中
     */
    @Select("SELECT COUNT(*) FROM chat_group.${groupId}_member WHERE user_id = #{userId}")
    int isUserInGroup(@Param("groupId") Long groupId, @Param("userId") Long userId);


    @Delete("DELETE FROM chat_group.${groupId}_member WHERE user_id = #{userId}")
    void removeGroupMember(@Param("groupId") Long groupId, @Param("userId") Long userId);

    @Delete("DELETE FROM chat_group.all_group WHERE id = #{groupId}")
    void deleteChatGroup(@Param("groupId") Long groupId);

    @Update("UPDATE chat_group.all_group SET group_name = #{groupName} WHERE id = #{groupId}")
    void updateGroupName(@Param("groupId") Long groupId, @Param("groupName") String groupName);

    @Update("UPDATE chat_group.all_group SET avatar = #{avatar} WHERE id = #{groupId}")
    void updateGroupAvatar(@Param("groupId") Long groupId, @Param("avatar") String avatar);

    @Insert("INSERT INTO chat_group.${groupId}_message (group_id, user_id, message_type, message_content, file_path) VALUES (#{groupId}, #{userId}, #{messageType}, #{messageContent}, #{filePath})")
    void addMessage(@Param("groupId") Long groupId, @Param("userId") Long userId, @Param("messageType") String messageType, @Param("messageContent") String messageContent, @Param("filePath") String filePath);

    @Insert("INSERT INTO chat_group.${groupId}_join_request (group_id, user_id) VALUES (#{groupId}, #{userId})")
    void addJoinRequest(@Param("groupId") Long groupId, @Param("userId") Long userId);

    @Select("SELECT * FROM chat_group.${groupId}_join_request WHERE group_id = #{groupId} AND user_id = #{userId} AND status = 'PENDING'")
    GroupJoinRequest getPendingJoinRequest(@Param("groupId") Long groupId, @Param("userId") Long userId);

    @Update("UPDATE chat_group.${groupId}_join_request SET status = #{status}, approve_time = CURRENT_TIMESTAMP, approver_id = #{approverId} WHERE id = #{requestId}")
    void updateJoinRequestStatus(@Param("requestId") Long requestId, @Param("status") String status, @Param("approverId") Long approverId);

    @Select("SELECT * FROM chat_group.${groupId}_join_request WHERE id = #{requestId}")
    GroupJoinRequest getPendingJoinRequestById(@Param("requestId") Long requestId);

    @Select("SELECT role FROM chat_group.${groupId}_member WHERE user_id = #{userId}")
    String getUserRole(@Param("groupId") Long groupId, @Param("userId") Long userId);

    /**
     * 插入管理员群组记录
     *
     * @param uid 用户ID
     * @param groupId 群组ID
     * @param role 角色
     */
    @Insert("INSERT INTO chat_user.${uid}_admin_group (uid, group_id, role) VALUES (#{uid}, #{groupId}, #{role})")
    void insertAdminGroupRecord(@Param("uid") Long uid, @Param("groupId") Long groupId, @Param("role") String role);

    /**
     * 删除管理员群组记录
     *
     * @param uid 用户ID
     * @param groupId 群组ID
     */
    @Delete("DELETE FROM chat_user.${uid}_admin_group WHERE group_id = #{groupId} AND uid = #{uid}")
    void deleteAdminGroupRecord(@Param("uid") Long uid, @Param("groupId") Long groupId);

    /**
     * 获取用户管理的群组ID列表
     *
     * @param uid 用户ID
     * @return 用户管理的群组ID列表
     */
    @Select("SELECT group_id FROM chat_user.${uid}_admin_group WHERE role = 'OWNER' OR role = 'ADMIN'")
    List<Long> getAdminGroups(@Param("uid") Long uid);

    /**
     * 获取待处理的加入请求
     *
     * @param adminGroups 管理员群组ID集合
     * @return 待处理的加入请求列表
     */
    @Select({
            "<script>",
            "<foreach collection='adminGroups' item='group' separator=' UNION ALL '>",
            "  SELECT gjr.id, gjr.group_id, gjr.user_id, gjr.status, gjr.request_time, gjr.approve_time, gjr.approver_id",
            "  FROM chat_group.${group}_join_request gjr",
            "  WHERE gjr.status = 'PENDING'",
            "</foreach>",
            "</script>"
    })
    List<GroupJoinRequest> getPendingJoinRequests(@Param("adminGroups") List<Long> adminGroups);
}