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
import poly.nhom3.support.Evidence;
import poly.nhom3.support.PhanCong;

import java.time.Duration;

/**
 * Lab 05 – Bài 3: test chức năng login trên https://www.saucedemo.com bằng TestNG (không dùng JUnit).
 * Tên method đặt theo mã test case + mô tả tiếng Việt; thứ tự chạy cố định bằng priority.
 */
@PhanCong(bai = "Lab 05 – Bài 3: TestNG + suite", nguoiThucHien = "Nguyễn Đăng Khoa", mssv = "PS48883")
public class LoginTestNG {

    private static final String URL = "https://www.saucedemo.com/";
    private static final By USERNAME = By.id("user-name");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By ERROR = By.cssSelector("h3[data-test='error']");
    private static final By PAGE_TITLE = By.cssSelector("span.title");

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void moTrinhDuyet() {
        driver = DriverFactory.create();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(URL);
        System.out.println("@BeforeMethod: mở Chrome mới và vào trang login");
    }

    @AfterMethod(alwaysRun = true)
    public void dongTrinhDuyet() {
        if (driver != null) {
            driver.quit();
        }
        System.out.println("@AfterMethod: đã đóng trình duyệt");
    }

    @Test(priority = 1, description = "TC01 – Login thành công với standard_user / secret_sauce")
    public void tc01_loginThanhCong() {
        login("standard_user", "secret_sauce");
        wait.until(ExpectedConditions.urlContains("inventory.html"));
        // URL đổi trước khi React vẽ xong trang Products -> phải chờ tiêu đề hiển thị rồi mới đọc
        String title = wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_TITLE)).getText();
        System.out.println("URL sau login: " + driver.getCurrentUrl() + " | Tiêu đề: " + title);
        Assert.assertTrue(driver.getCurrentUrl().endsWith("/inventory.html"), "Phải chuyển sang trang inventory");
        Assert.assertEquals(title, "Products", "Tiêu đề trang sản phẩm");
        Evidence.checkpoint("lab05_bai3_tc01", "Login thành công, đang ở trang Products");
    }

    @Test(priority = 2, description = "TC02 – Login sai username")
    public void tc02_loginSaiUsername() {
        login("sai_user", "secret_sauce");
        String error = waitError();
        Assert.assertEquals(error, "Epic sadface: Username and password do not match any user in this service");
        Assert.assertTrue(driver.getCurrentUrl().equals(URL), "Vẫn phải ở trang login");
    }

    @Test(priority = 3, description = "TC03 – Login sai password")
    public void tc03_loginSaiPassword() {
        login("standard_user", "sai_password");
        String error = waitError();
        Assert.assertEquals(error, "Epic sadface: Username and password do not match any user in this service");
        Evidence.checkpoint("lab05_bai3_tc03", "Sai password: hiện thông báo lỗi màu đỏ");
    }

    @Test(priority = 4, description = "TC04 (nhóm thêm) – Tài khoản bị khoá locked_out_user")
    public void tc04_loginTaiKhoanBiKhoa() {
        login("locked_out_user", "secret_sauce");
        Assert.assertEquals(waitError(), "Epic sadface: Sorry, this user has been locked out.");
    }

    private void login(String username, String password) {
        driver.findElement(USERNAME).sendKeys(username);
        driver.findElement(PASSWORD).sendKeys(password);
        driver.findElement(LOGIN_BUTTON).click();
        System.out.println("Đăng nhập với: '" + username + "' / '" + password + "'");
    }

    private String waitError() {
        String error = wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR)).getText();
        System.out.println("Thông báo lỗi: " + error);
        return error;
    }
}
