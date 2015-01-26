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
            driver.wait(3000);
            assertEquals("Frootr :: How do ya like them apples?", driver.getTitle());
            sendInTheFruit();
            driver.wait(10000);
            WebElement fruitBowl = driver.findElement(By.id("fruitBowl"));
            List<WebElement> fruitBowlRows = fruitBowl.findElements(By.tagName("tr"));
            assertEquals(6, fruitBowlRows.size());
            assertFruitInRow(1, "apples", "6");
            assertFruitInRow(2, "grapes", "1");
            assertFruitInRow(3, "kumquats", "2");
            assertFruitInRow(4, "pears", "5");
            assertFruitInRow(5, "pineapples", "17");
        }
    }

    private void assertFruitInRow(int rowNum, String name, String quantity) {
        WebElement fruitBowl = driver.findElement(By.id("fruitBowl"));
        List <WebElement> fruitBowlRows = fruitBowl.findElements(By.tagName("tr"));
        List <WebElement> fruitBowlRowCells = fruitBowlRows.get(rowNum).findElements(By.tagName("td"));
        assertEquals(2, fruitBowlRowCells.size());
        assertEquals(name, fruitBowlRowCells.get(0).getText());
        assertEquals(quantity, fruitBowlRowCells.get(1).getText());
    }

    private void sendInTheFruit() {
        try {
            MqttClient mqttClient = new MqttClient(mqttUri, MqttClient.generateClientId(), new MemoryPersistence());
            mqttClient.connect();
            mqttClient.publish("frootr/123/pineapples", new MqttMessage("17".getBytes()));
            mqttClient.publish("frootr/123/apples", new MqttMessage("6".getBytes()));
            mqttClient.publish("frootr/123/grapes", new MqttMessage("1".getBytes()));
            mqttClient.publish("frootr/123/kumquats", new MqttMessage("2".getBytes()));
            mqttClient.publish("frootr/123/pears", new MqttMessage("5".getBytes()));
            mqttClient.disconnect();
        } catch (MqttException me) {
            throw new IllegalStateException("Could not create MqttClient", me);
        }

    }
}
