//package com.guan.community;
//
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.core.KafkaTemplate;
//
//@SpringBootTest
//public class KafkaTest {
//
//    @Test
//    public void test() {
//
//    }
//}
//
//class KafkaProducer{
//
//    @Autowired
//    private KafkaTemplate kafkaTemplate;
//
//    public void sendMessage(String topic, String content) {
//        kafkaTemplate.send(topic, content);
//    }
//
//}
//
//class KafkaConsumer{
//
//    @KafkaListener(topics = {"test"})
//
//
//
//
//}