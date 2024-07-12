package top.mappland.chat.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 聊天域 Mapper 接口
 */
@Mapper
public interface GroupMapper {

    /**
     * 调用存储过程创建聊天域和相关表
     *
     * @param groupName 聊天域名称
     * @param ownerId   域主 ID
     */
    @Update("{CALL chat_group.create_chat_group(#{groupName}, #{ownerId})}")
    void createChatGroup(@Param("groupName") String groupName, @Param("ownerId") Long ownerId);

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
}
