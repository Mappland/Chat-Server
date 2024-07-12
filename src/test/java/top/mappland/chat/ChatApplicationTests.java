package top.mappland.chat;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.mappland.chat.mapper.UserMapper;
import top.mappland.chat.model.domain.User;

import java.util.List;

@SpringBootTest
class MybatisPlusDemoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private UserMapper userMapper;

    @Test
    void testMybatisPlus() {
        List<User> users = userMapper.selectList(null);
        System.out.println(users);

    }

//    @Test
//    void testMybatisPlusWrapper(){
//        // 查询名字包含'j', 年龄大于20岁，邮箱不为空的员工信息
//        // SELECT * FROM user WHERE last_name LIKE CONCAT('%', 'j', '%') AND age > 20 AND email IS NOT NULL
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.like("last_name", "j")
//                .gt("age", 20) // 大于用gt
//                .isNotNull("email");
//        employeeMapper.selectList(queryWrapper);
//    }
//
//    @Test
//    void testMybatisPlusWrapperLambda(){
//        // 查询名字包含'j', 年龄大于20岁，邮箱不为空的员工信息, JDK版本大于1.8
//        // SELECT * FROM bl_employee WHERE last_name LIKE CONCAT('%', 'j', '%') AND age > 20 AND email IS NOT NULL
//        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.like(Employee::getLastName, "j")
//                .gt(Employee::getAge, 20)
//                .isNotNull(Employee::getEmail);
//        employeeMapper.selectList(lambdaQueryWrapper);
//    }
//
//    @Test
//    void testMybatisPlusUpdateWrapper(){
//        // 将名字中包含j且年龄大于20岁的员工年龄改为 50，邮箱改为emp@163.com
//        // UPDATE bl_employee SET age = 50, email = 'emp@163.com' WHERE last_name LIKE CONCAT('%', 'j', '%') AND age > 20
//        LambdaUpdateWrapper<Employee> lambdaQueryWrapper = new LambdaUpdateWrapper<>();
//        lambdaQueryWrapper.like(Employee::getLastName, "j")
//                .gt(Employee::getAge, 20)
//                .set(Employee::getAge, 50)
//                .set(Employee::getEmail, "emp@163.com");
//
//        System.out.println(employeeMapper.update(null, lambdaQueryWrapper));
//    }
}
