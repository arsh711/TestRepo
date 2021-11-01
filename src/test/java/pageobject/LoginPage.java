package pageobject;

import hooks.BaseClass;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utils.PageUtils;


public class LoginPage {

    public LoginPage(){
        PageFactory.initElements(BaseClass.getDriver(),this);
    }

    @FindBy(id = "email")
    WebElement usernameTextField;

    public void enterUsernameInTextField(String username){
        usernameTextField.sendKeys(username);
    }

    @FindBy(id = "password")
    WebElement passwordTextField;

    public void enterPasswordInTextField(String password){
        passwordTextField.sendKeys(password);
    }

    @FindBy(css = "button[type='submit']")
    WebElement loginBtn;

    public void clickOnLoginBtn(){
        PageUtils.scrollToElement(loginBtn);
        loginBtn.click();
    }

    @FindBy(css = ".page-heading h3")
    WebElement dashboardHeadingText;

    public String getDashboardHeadingText(){
        return dashboardHeadingText.getText();
    }

    @FindBy(css = "label[for='email']")
    WebElement emailErrorMessages;

    public String getEmailErrorMessage(){
        return emailErrorMessages.getText();
    }

    @FindBy(css = "label[for='password']")
    WebElement passwordErrorMessages;

    public String getPasswordErrorMessage(){
        return passwordErrorMessages.getText();
    }

    @FindBy(css = "div[class='toastify on error toastify-center toastify-top']")
    WebElement invalidErrorMessages;

    public String getInvalidErrorMessage(){
        PageUtils.waitUntilVisible(invalidErrorMessages,10);
        return invalidErrorMessages.getText();
    }
}
