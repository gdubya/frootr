package bv.frootr.service;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttService.class);

    private final FrootService frootService;

    public MqttService(final String serverURI, final String mqttTopics, final FrootService frootService) {
        this.frootService = frootService;
        connectAndSubscribe(serverURI, mqttTopics);
    }

    /**
     * Initialise the MQTT client, subscribe to the required topics, and register callbacks
     * @param serverURI The full URI for connecting to the MQTT broker (e.g. tcp://mqtt.server:1883)
     * @param mqttTopics The topics to which to subscribe.
     */
    private void connectAndSubscribe(String serverURI, String... mqttTopics) {
        try {
            MqttClient mqttClient = new MqttClient(serverURI, MqttClient.generateClientId(), new MemoryPersistence());
            mqttClient.connect();
            mqttClient.subscribe(mqttTopics);
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    LOGGER.warn("Connection lost!", cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String messageBody = new String(message.getPayload());
                    LOGGER.info("Message arrived on {}: {}", topic, messageBody);
                    String[] parts = topic.split("/");
                    if (parts.length > 2) {
                        String fruit = parts[2];
                        Integer count = Integer.valueOf(messageBody);
                        frootService.updateBowl(fruit, count);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    LOGGER.debug("Message delivery completed");
                }
            });
        } catch (MqttException me) {
            throw new IllegalStateException("Could not create MqttClient", me);
        }
    }
}
