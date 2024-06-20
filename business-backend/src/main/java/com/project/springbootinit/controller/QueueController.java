package com.project.springbootinit.controller;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("/queue")
@Slf4j
@Profile({"dev", "local"})
public class QueueController {

    @Resource
    ThreadPoolExecutor threadPoolExecutor;

    @GetMapping("/add")
    public void add(String name){
        //  新建一个没有返回值的任务
        CompletableFuture.runAsync(() -> {
            System.out.println("任务执行中"+ name + "执行人" + Thread.currentThread().getName());
            try {
                Thread.sleep(600000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, threadPoolExecutor);
    }

    @GetMapping("/get")
    public String get(){
        HashMap<Object, Object> map = new HashMap<>();
        int size = threadPoolExecutor.getQueue().size();
        map.put("队列长度", size);
        long taskCount = threadPoolExecutor.getTaskCount();
        map.put("任务总数", taskCount);
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        map.put("完成的任务总数", completedTaskCount);
        int activeCount = threadPoolExecutor.getActiveCount();
        map.put("正在活跃的线程数", activeCount);
        return JSONUtil.toJsonStr(map);

    }
}
