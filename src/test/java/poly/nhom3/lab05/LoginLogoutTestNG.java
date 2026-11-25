package poly.nhom3.lab05;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import poly.nhom3.support.DriverFactory;
import poly.nhom3.support.PhanCong;

import java.time.Duration;

/**
 * Lab 05 – Bài 3: class LoginLogoutTest (JUnit 5, Lab 04) được AI chuyển sang TestNG.
 * Đổi: @BeforeEach -> @BeforeMethod, @AfterEach -> @AfterMethod, @DisplayName -> description,
 * Assertions.assertEquals(expected, actual, msg) -> Assert.assertEquals(actual, expected, msg).
 */
@PhanCong(bai = "Lab 05 – Bài 3: TestNG + suite", nguoiThucHien = "Nguyễn Đăng Khoa", mssv = "PS48883")
public class LoginLogoutTestNG {

    private static final String LOGIN_URL = "https://the-internet.herokuapp.com/login";
    private static final By USERNAME_INPUT = By.id("username");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By LOGIN_BUTTON = By.cssSelector("button[type='submit']");
    private static final By FLASH_MESSAGE = By.id("flash");
    private static final By LOGOUT_BUTTON = By.cssSelector("a[href='/logout']");

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        driver = DriverFactory.create();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(priority = 5, description = "TC05 – Đăng nhập the-internet rồi đăng xuất quay về trang login (từ Lab 04)")
    public void tc05_loginThenLogout() {
        driver.get(LOGIN_URL);
        driver.findElement(USERNAME_INPUT).sendKeys("tomsmith");
        driver.findElement(PASSWORD_INPUT).sendKeys("SuperSecretPassword!");
        driver.findElement(LOGIN_BUTTON).click();

        String message = wait.until(ExpectedConditions.visibilityOfElementLocated(FLASH_MESSAGE)).getText();
        Assert.assertTrue(message.contains("You logged into a secure area!"), "Thông báo đăng nhập thành công");
        wait.until(ExpectedConditions.urlContains("/secure"));

        wait.until(ExpectedConditions.elementToBeClickable(LOGOUT_BUTTON)).click();
        wait.until(ExpectedConditions.urlToBe(LOGIN_URL));
        Assert.assertEquals(driver.getCurrentUrl(), LOGIN_URL, "Sau logout phải quay về trang login");
        System.out.println("Login/Logout trên the-internet OK, URL cuối: " + driver.getCurrentUrl());
    }
}
