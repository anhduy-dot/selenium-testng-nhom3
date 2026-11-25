package poly.nhom3.lab05;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import poly.nhom3.support.DriverFactory;
import poly.nhom3.support.Evidence;
import poly.nhom3.support.PhanCong;

import java.time.Duration;

/**
 * Lab 05 – Bài 4: cùng một test login chạy trên nhiều trình duyệt.
 * Tên trình duyệt truyền từ testng.xml qua @Parameters("browser").
 * Chạy với -Dbug=locator hoặc -Dbug=url để cố tình tạo lỗi và thực hành debug.
 */
@PhanCong(bai = "Lab 05 – Bài 4: Cross-browser + debug", nguoiThucHien = "Nguyễn Anh Duy", mssv = "PS48450")
public class CrossBrowserLoginTest {

    private static final String URL = "https://www.saucedemo.com/";
    private static final String WRONG_URL = "https://www.saucedemo.com/trang-khong-ton-tai.html";

    private WebDriver driver;
    private String browser;

    @BeforeMethod
    @Parameters("browser")
    public void setUp(@Optional("chrome") String browser) {
        this.browser = browser;
        driver = DriverFactory.create(browser);
        Capabilities caps = ((HasCapabilities) driver).getCapabilities();
        System.out.println("[" + browser + "] Đã mở " + caps.getBrowserName() + " " + caps.getBrowserVersion()
                + " trên " + caps.getPlatformName());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(description = "Login standard_user trên nhiều trình duyệt")
    public void loginOnBrowser() {
        String bug = System.getProperty("bug", "");
        String url = bug.equals("url") ? WRONG_URL : URL;
        By usernameLocator = bug.equals("locator") ? By.id("user_name") : By.id("user-name");

        driver.get(url);
        debug("Đã mở URL: " + driver.getCurrentUrl() + " | title = '" + driver.getTitle() + "'");
        debug("Locator ô username: " + usernameLocator + " -> tìm thấy "
                + driver.findElements(usernameLocator).size() + " phần tử");
        if (Boolean.getBoolean("debug.sleep")) {
            debug("Thread.sleep 3 giây để quan sát trình duyệt (chỉ dùng khi debug)...");
            Evidence.pause(3000);
            Evidence.checkpoint("lab05_bai4_debug_" + (bug.isEmpty() ? "ok" : bug), "Quan sát trình duyệt khi debug");
        }

        driver.findElement(usernameLocator).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("inventory.html"));
        String title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.title"))).getText();
        debug("Sau login: URL = " + driver.getCurrentUrl() + " | tiêu đề = " + title);
        Assert.assertEquals(title, "Products");
        Evidence.checkpoint("lab05_bai4_" + browser, "Login thành công trên " + browser);
    }

    private void debug(String message) {
        System.out.println("[DEBUG][" + browser + "] " + message);
    }
}
