package com.project.springbootinit.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiManagerTest {

    @Resource
    private AiManager aiManager;

    @Test
    void doChat() {
        String answer = aiManager.doChat("分析需求:\n" + "分析用户的增长情况\n" + "原始数据:\n" +
                        "日期，用户数\n" +
                        "1号，10" +
                        "2号，20" +
                        "3号，30"
                        , 1709156902984093697L);

        System.out.println(answer);
    }
}