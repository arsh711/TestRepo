package runner;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)

@CucumberOptions( features = "src/test/resources/FrameworkFeature/",
        glue = {""},
        tags = "  @InvalidLogin",
        plugin = {"io.qameta.allure.cucumber6jvm.AllureCucumber6Jvm","pretty","html:target/cucumber-reports.html"},
        publish = true

)
public class RunCucumberTest {

}
