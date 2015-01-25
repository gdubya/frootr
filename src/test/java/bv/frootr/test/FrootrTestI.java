package bv.frootr.test;

import bv.frootr.FrootrApp;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.MalformedURLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FrootrApp.class)
@WebIntegrationTest
public class FrootrTestI {

    private static final String START_URL = "http://localhost:8080/";

    private WebDriver driver;

    @Value("${local.server.port}")
    private String webServerPort;

    @Value("${frootr.mqtt.uri}")
    private String mqttUri;

    @Value("${frootr.mqtt.topics}")
    private String mqttTopics;

    @Before
    public void setup() throws MalformedURLException {
        driver = new FirefoxDriver();
    }

    @After
    public void tearDown() {
        driver.close();
    }

    /**
     * Test the complete data flow by starting the app, send an MQTT message to the broker, then assert that the display
     * is updated via Websockets
     * @throws InterruptedException
     */
    @Test
    public void testMqttAndWebsocketIntegration() throws InterruptedException {
        driver.get("http://localhost:" + webServerPort);
        synchronized (driver) {
            driver.wait(5000);
            assertEquals("Frootr :: How do ya like them apples?", driver.getTitle());
            sendInTheFruit();
            driver.wait(5000);
            WebElement fruitBowl = driver.findElement(By.id("fruitBowl"));
            List<WebElement> fruitBowlRows = fruitBowl.findElements(By.tagName("tr"));
            assertEquals(4, fruitBowlRows.size());
            assertFruitInRow(fruitBowlRows, 0, "apples", "4");
            assertFruitInRow(fruitBowlRows, 0, "grapes", "1");
            assertFruitInRow(fruitBowlRows, 0, "pineapples", "17");
        }
    }

    private void assertFruitInRow(List<WebElement> fruitBowlRows, int row, String name, String quantity) {
        List<WebElement> cellsInRow = fruitBowlRows.get(row).findElements(By.tagName("td"));
        assertEquals(2, cellsInRow.size());
        assertEquals(name, cellsInRow.get(0).getText());
        assertEquals(quantity, cellsInRow.get(1).getText());
    }

    private void sendInTheFruit() {
        try {
            MqttClient mqttClient = new MqttClient(mqttUri, MqttClient.generateClientId(), new MemoryPersistence());
            mqttClient.connect();
            mqttClient.publish("frootr/123/pineapples", new MqttMessage("17".getBytes()));
            mqttClient.publish("frootr/123/apples", new MqttMessage("4".getBytes()));
            mqttClient.publish("frootr/123/grapes", new MqttMessage("1".getBytes()));
            mqttClient.disconnect();
        } catch (MqttException me) {
            throw new IllegalStateException("Could not create MqttClient", me);
        }

    }
}
