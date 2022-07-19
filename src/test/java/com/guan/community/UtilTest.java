package com.guan.community;


import com.guan.community.util.MailClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@SpringBootTest
public class UtilTest {

    @Autowired
    private MailClient mailClient;

    @Test
    public void mailSenderTest(){
        mailClient.mailSend("3088709672@qq.com","test","Hello Mail");
    }

    @Autowired
    TemplateEngine templateEngine;

    @Test
    public void sendHtmlTest(){
        Context context = new Context( );
        context.setVariable("username","epoch");
        String result = templateEngine.process("/mail/demo",context);
        System.out.println(result);
        mailClient.mailSend("3088709672@qq.com","test",result);


    }
}
