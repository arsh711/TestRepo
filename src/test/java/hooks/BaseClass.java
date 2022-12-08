package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import utils.EmailReport;
import utils.PageUtils;
import utils.PropertyUtils;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;


public class BaseClass {
    public static WebDriver driver;
    public static String url= "https://"+PropertyUtils.getProperty("saucelab.username")+":"+PropertyUtils.getProperty("saucelab.secret.key")+PropertyUtils.getProperty("saucelab.link");

    @Before
    public void setupBrowser(){
        String browserName = PropertyUtils.getProperty("browser");
        Capabilities caps;
        Map<String, Object> prefs = new HashMap<String, Object>();
        switch (browserName.toLowerCase()){
            case "chrome":
                caps = new ChromeOptions();
                ((ChromeOptions)caps).setHeadless(true);
                prefs.put("download.default_directory", System.getProperty("user.dir")+PropertyUtils.getProperty("default.download.location"));
                ((ChromeOptions) caps).setExperimentalOption("prefs",prefs);
                break;
            case "firefox":
                caps = new FirefoxOptions();
                ((FirefoxOptions) caps).setCapability("browser.download.folderList", 2);
                ((FirefoxOptions) caps).setCapability("browser.download.dir",System.getProperty("user.dir")+PropertyUtils.getProperty("default.download.location"));
                ((FirefoxOptions) caps).setCapability("browser.helperApps.neverAsk.saveToDisk","application/vnd.microsoft.portable-executable");
                break;
            case "edge":
                caps = new EdgeOptions();
                prefs.put("download.default_directory",  System.getProperty("user.dir")+PropertyUtils.getProperty("default.download.location"));
                ((EdgeOptions)caps).setCapability("prefs",prefs);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + browserName.toLowerCase());
        }

        if (PropertyUtils.getProperty("remote").toLowerCase().equals("false")) {

            switch (browserName.toLowerCase()) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    driver = new ChromeDriver(caps);
                    break;
                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    driver = new FirefoxDriver(caps);
                    break;
                case "edge":
                    WebDriverManager.edgedriver().setup();
                    driver = new EdgeDriver(caps);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + browserName.toLowerCase()+" Supported browsers : chrome, firefox, edge");
            }
        }else{
            switch (browserName.toLowerCase()) {
                case "chrome":
                    ((ChromeOptions) caps).setCapability("platform", PropertyUtils.getProperty("platform"));
                    ((ChromeOptions) caps).setCapability("version", PropertyUtils.getProperty("browser.version"));
                    ((ChromeOptions) caps).setCapability("name", PropertyUtils.getProperty("test.name"));
                break;
                case "firefox":
                    ((FirefoxOptions) caps).setCapability("platform",PropertyUtils.getProperty("platform"));
                    ((FirefoxOptions) caps).setCapability("version",PropertyUtils.getProperty("browser.version"));
                    ((FirefoxOptions) caps).setCapability("name",PropertyUtils.getProperty("test.name"));
                    break;
                case "edge":
                    ((EdgeOptions)caps).setCapability("platform",PropertyUtils.getProperty("platform"));
                    ((EdgeOptions)caps).setCapability("version",PropertyUtils.getProperty("browser.version"));
                    ((EdgeOptions)caps).setCapability("name",PropertyUtils.getProperty("test.name"));
                    break;
            }

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
            FileUtils.copyFile(scr, new File(PropertyUtils.getProperty("default.screenshot.location")+ "/" + fileName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @After()
    public void tearDown(Scenario scenario){
        if (scenario.isFailed()) {
            saveScreenShot(driver,scenario);
//            EmailReport.sendMail();
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
