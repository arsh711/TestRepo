package hooks;


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
import utils.PropertyUtils;
import java.io.*;


public class BaseClass {
    public static WebDriver driver;

    @Before
    public void setupBrowser(){
        String browserName = PropertyUtils.getProperty("browser");
        switch (browserName.toLowerCase()){
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
                System.out.println("\nEnter valid browser from the list : chrome, firefox, edge");
                break;
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
