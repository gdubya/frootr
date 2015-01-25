package bv.frootr.config;

import bv.frootr.service.MqttService;
import bv.frootr.service.FrootService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    @Value("${frootr.mqtt.uri}")
    private String mqttUri;

    @Value("${frootr.mqtt.topics}")
    private String mqttTopics;

    @Autowired
    private FrootService frootService;

    @Bean
    public MqttService mqttClient() {
        return new MqttService(mqttUri, mqttTopics, frootService);
    }
}
