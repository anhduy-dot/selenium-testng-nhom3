package poly.nhom3.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/** Page Object sơ khai cho trang login saucedemo (Lab 07 Bài 3 – AI đề xuất tách ra). */
public class LoginPage {

    public static final String URL = "https://www.saucedemo.com/";
    // Locator dùng data-test: thuộc tính dành riêng cho test nên ít đổi khi giao diện thay đổi
    private static final By USERNAME = By.cssSelector("[data-test='username']");
    private static final By PASSWORD = By.cssSelector("[data-test='password']");
    private static final By LOGIN = By.cssSelector("[data-test='login-button']");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public LoginPage open() {
        driver.get(URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME));
        return this;
    }

    public InventoryPage loginAs(String username, String password) {
        driver.findElement(USERNAME).sendKeys(username);
        driver.findElement(PASSWORD).sendKeys(password);
        driver.findElement(LOGIN).click();
        return new InventoryPage(driver).waitLoaded();
    }
}
