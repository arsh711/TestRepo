package hooks;


import com.sun.javafx.runtime.async.AbstractRemoteResource;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import utils.PropertyUtils;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;


public class BaseClass {
    public static WebDriver driver;
    public static String url= "https://"+PropertyUtils.getProperty("saucelab.username")+":"+PropertyUtils.getProperty("saucelab.secret.key")+PropertyUtils.getProperty("saucelab.link");

    @Before
    public void setupBrowser(){
        String browserName = PropertyUtils.getProperty("browser");
        if (PropertyUtils.getProperty("remote").toLowerCase().equals("false")) {
            switch (browserName.toLowerCase()) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    driver = new ChromeDriver();
                    break;
                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    driver = new FirefoxDriver();
                    break;
                case "edge":
                    WebDriverManager.edgedriver().setup();
                    driver = new EdgeDriver();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + browserName.toLowerCase()+" Supported browsers : chrome, firefox, edge");
            }
        }else{
            DesiredCapabilities caps;
            switch (browserName.toLowerCase()){
                case "chrome":
                    caps = DesiredCapabilities.chrome();
                    break;
                case "firefox":
                    caps = DesiredCapabilities.firefox();
                    break;
                case "edge":
                    caps = DesiredCapabilities.edge();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + browserName.toLowerCase());
            }
            caps.setCapability("platform",PropertyUtils.getProperty("platform"));
            caps.setCapability("version",PropertyUtils.getProperty("browser.version"));
            caps.setCapability("name",PropertyUtils.getProperty("test.name"));

            try {
                driver = new RemoteWebDriver(new URL(url),caps);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    public static WebDriver getDriver(){
        return driver;
    }

    public static String getScenarioName(Scenario scenario){
        return scenario.getName().replaceAll(" ","_");
    }

    @Attachment( value = "Failed Test ScreenShot", type = "image/png")
    public void saveScreenShot(WebDriver driver, Scenario scenario){
        TakesScreenshot screenshot = (TakesScreenshot)driver;
        String fileName = getScenarioName(scenario);
        File scr=screenshot.getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scr, new File("target/screenshots/" + fileName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @After()
    public void tearDown(Scenario scenario){
        if (scenario.isFailed()) {
            saveScreenShot(driver,scenario);
            try {
                FileInputStream inputStream = new FileInputStream("target/screenshots/" + getScenarioName(scenario) + ".png");
                Allure.addAttachment("Failed Test Screenshot",inputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Allure.addAttachment("Failed Scenario Name",getScenarioName(scenario));
        }
        driver.quit();
    }




}
