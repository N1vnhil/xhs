package org.n1vnhil.xhsauth;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Slf4j
@SpringBootTest
public class ThreadPoolExecutorTests {

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 测试线程池
     */
    @Test
    void testSubmit() {
        threadPoolTaskExecutor.submit(() -> log.info("Thread 1"));
        threadPoolTaskExecutor.submit(() -> log.info("Thread 2"));
    }

}
