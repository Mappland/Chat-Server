package top.mappland.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import top.mappland.chat.config.DataSourceKey;
import top.mappland.chat.config.UseDataSource;
import top.mappland.chat.mapper.GroupCreateMapper;
import top.mappland.chat.mapper.GroupMapper;
import top.mappland.chat.model.domain.Group;
import top.mappland.chat.service.GroupCreateService;

@Service
public class GroupCreateServiceImpl extends ServiceImpl<GroupCreateMapper, Group> implements GroupCreateService {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Autowired
    private GroupCreateMapper groupCreateMapper;
    @Autowired
    private GroupMapper groupMapper;

    @UseDataSource(DataSourceKey.CHAT_GROUP)
    @Override
    public boolean save(Group group) {
        boolean isSaved = super.save(group);
        if (isSaved) {
            createDynamicTables(group.getGroupId(), group.getOwner_id());
            group.setGroupId(group.getGroupId());
            groupMapper.insertUserGroupItem(group.getGroupId(), "OWNER", group.getOwner_id());
        }
        return isSaved;
    }

    private void createDynamicTables(Long groupId, Long ownerId) {
        jdbcTemplate.update("CALL chat_group.create_chat_group(?, ?)", groupId, ownerId);
    }
}
