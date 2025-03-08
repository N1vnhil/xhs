package org.n1vnhil.xhsauth;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhsauth.domain.dataobject.UserDO;
import org.n1vnhil.xhsauth.domain.mapper.UserDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@Slf4j
@SpringBootTest
class XhsAuthApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private UserDOMapper userDOMapper;

    @Test
    void testInsert() {
        UserDO userDO = UserDO.builder()
                .username("Alice")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        userDOMapper.insert(userDO);
    }

    @Test
    void testSelect() {
        // 查询主键 ID 为 4 的记录
        UserDO userDO = userDOMapper.selectByPrimaryKey(4L);
        log.info("User: {}", JsonUtils.toJsonString(userDO));
    }

}
