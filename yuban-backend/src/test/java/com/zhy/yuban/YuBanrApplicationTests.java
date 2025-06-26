package com.zhy.yuban;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.security.NoSuchAlgorithmException;

@SpringBootTest
class YuBanrApplicationTests {

    @Test
    void passTest() throws NoSuchAlgorithmException {
        String md = DigestUtils.md5DigestAsHex(("zhy" + "123456").getBytes());
        System.out.println(md);
    }

    @Test
    public void testSelect() {
    }


}
