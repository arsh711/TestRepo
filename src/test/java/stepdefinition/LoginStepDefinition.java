package stepdefinition;

import hooks.BaseClass;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import pageobject.LoginPage;
import utils.PropertyUtils;

import java.util.concurrent.TimeUnit;

public class LoginStepDefinition {

    LoginPage loginPage;
    SoftAssert softAssert = new SoftAssert();

    @Given("^User is on login page$")
    public void userIsOnLoginPage() {
        BaseClass.getDriver().get(PropertyUtils.getProperty("test.website.url"));
        BaseClass.getDriver().manage().window().maximize();
        BaseClass.getDriver().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        loginPage = new LoginPage();
//        PageFactory.initElements(getDriver(),loginPage); //pagefactory
    }

    @When("^User fill valid credentials and click on login button$")
    public void userFillValidCredentialsAndClickOnLoginButton() {
        loginPage.enterUsernameInTextField("superadmin@yopmail.com");
        loginPage.enterPasswordInTextField("123456");
        loginPage.clickOnLoginBtn();
    }

    @Then("^Verify user is logged in$")
    public void verifyUserIsLoggedIn() {
        String expectedWelcomeText = "Dashboard arsh";
        System.out.println(loginPage.getDashboardHeadingText());
//        Assert.assertTrue(loginPage.getDashboardHeadingText().contains(expectedWelcomeText));
        softAssert.assertTrue(loginPage.getDashboardHeadingText().contains(expectedWelcomeText));
        softAssert.assertAll("Assertion Failed");
    }


    @When("^User fill username as \"([^\"]*)\" and password as \"([^\"]*)\" and click on login button$")
    public void userFillUsernameAsAndPasswordAsAndClickOnLoginButton(String username , String password) {
        loginPage.enterUsernameInTextField(username);
        loginPage.enterPasswordInTextField(password);
        loginPage.clickOnLoginBtn();
    }

    @Then("^Verify the error message \"([^\"]*)\" for this \"([^\"]*)\"$")
    public void verifyTheErrorMessageForThis(String expectedErrorMessage , String test) {
        switch (test) {
            case "email":
                Assert.assertEquals(loginPage.getEmailErrorMessage(), expectedErrorMessage, "Test Failed");
                break;
            case "password":
                Assert.assertEquals(loginPage.getPasswordErrorMessage(), expectedErrorMessage, "Test Failed");
                break;
            case "invalid":
                Assert.assertEquals(loginPage.getInvalidErrorMessage(), expectedErrorMessage, "Test Failed");
                break;
        }
    }
}
