package com.zhy.yuban.service;
import java.util.*;

import com.zhy.yuban.mapper.UserMapper;
import com.zhy.yuban.model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @Test
    void test() {
        User user = new User();
        user.setUsername("testZhy");
        user.setUserAccount("111");
        user.setAvatarUrl("https://pic.code-nav.cn/user_avatar/1827742963026014210/thumbnail/XLs0gjkf7fHfYaYA.jpg");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("456");


        boolean result = userService.save(user);
        System.out.println(user.getId());
        assertTrue(result);
    }

    @Test
    void userMessage() {

        String userAccount = "zhyx";
        String userPassword = "";
        String checkUserPassword = "123456";
        String planetCode = "1";
        long l = userService.userRegister(userAccount, userPassword, checkUserPassword,planetCode);
        assertTrue(l == -1);
        userAccount = "zhy";
        l = userService.userRegister(userAccount, userPassword, checkUserPassword,planetCode);
        assertTrue(l == -1);
        userAccount = "zh  y";
        l = userService.userRegister(userAccount, userPassword, checkUserPassword,planetCode);
        assertTrue(l == -1);
        userAccount = "zhyx";
        userPassword = "12345678";
        checkUserPassword = "123456789";
        l = userService.userRegister(userAccount, userPassword, checkUserPassword,planetCode);
        assertTrue(l == -1);
        userAccount = "111";
        userPassword = "123456789";
        l = userService.userRegister(userAccount, userPassword, checkUserPassword,planetCode);
        assertTrue(l == -1);
        userAccount = "zhangHyhhh";
        planetCode = "1";
        l = userService.userRegister(userAccount, userPassword, checkUserPassword,planetCode);
        planetCode = "2";
        l = userService.userRegister(userAccount, userPassword, checkUserPassword,planetCode);
        assertTrue(l > 0);
        System.out.println(l);
    }

    @Test
    void searchUserByTags() {
        List<String>  list = Arrays.asList("c++");
        List<User> userList = userService.searchUserByTags(list);
        Assert.assertNotNull(userList);
    }

    @Test
    void BatchInsertUser() {
        Random random = new Random();
        final String[] tags1 = {"java", "C++", "python", "go", "javascript", "php", "C#", "Rust", "Kotlin", "TypeScript"};
        final String[] tags2 = {"emo", "躺平", "运动", "旅游", "美食", "音乐", "电影", "读书", "绘画", "摄影"};
        final String[] tags3 = {"大一", "大二", "大三", "大四", "研究生", "博士"};
        final String[] tags4 = {"女", "男"};
        final int INSERT_NUM = 1000000;
        StopWatch stopWatch = new StopWatch();

        // 创建自适应线程池大小
        // jvm 能够获取的线程数
        Integer processNum = Runtime.getRuntime().availableProcessors();
        // 当前能够运行的线程数
        int coolPoolSize = (int) (processNum / (1 - 0.2));
        // 最大运行的线程数
        int maxPoolSize = (int) (processNum / (1 - 0.8));
        // 1 2 空闲线程等待时间， ，等待队列的长度，线程工厂， 拒绝策略
        ExecutorService executorService = new ThreadPoolExecutor(
                coolPoolSize,
                maxPoolSize,
                2L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());

        stopWatch.start();

        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        int j = 0;
        for(int i = 1;i <= 100;i++) {
            List<User> userList = new ArrayList<>();

            while(true) {
                j++;
                User user = new User();
                user.setUsername("testUser");
                user.setUserAccount("testUser");
                user.setAvatarUrl("https://pic.code-nav.cn/post_picture/1738721423335120897/vqaH4qzRmQE5AQZX.jpg");
                int index = random.nextInt(2);
                user.setGender(index);
                user.setUserPassword("34f75057f8e9773bb13ed93eb53b7c85");
                user.setPhone("11234234565");
                user.setEmail("11234234565@163.com");
                String tag1 = tags1[random.nextInt(tags1.length)];
                String tag2 = tags2[random.nextInt(tags2.length)];
                String tag3 = tags3[random.nextInt(tags3.length)];
                String tag4 = tags4[index];
                user.setTags("[\"" + tag4 + "\", \"" + tag3 + "\", \"" + tag1 + "\", \"" + tag2 + "\"]");
                user.setProfile("我是" + tag3 + "的学生，喜欢" + tag2 + "，擅长" + tag1 + "！");
                userList.add(user);
                if(j % 10000 == 0) {
                    break;
                }
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("ThreadName:" + Thread.currentThread().getName());
                userService.saveBatch(userList, 10000);
            }, executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println("运行时间：" + stopWatch.getTotalTimeMillis());
    }

}