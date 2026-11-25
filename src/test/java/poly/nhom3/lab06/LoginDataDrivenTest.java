package poly.nhom3.lab06;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import poly.nhom3.support.DriverFactory;
import poly.nhom3.support.Evidence;
import poly.nhom3.support.PhanCong;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Lab 06 – Bài 1: Data-Driven Test với @DataProvider trên https://www.saucedemo.com
 * 6 bộ dữ liệu bắt buộc + 3 bộ dữ liệu biên do AI đề xuất (nhóm đã chọn lọc).
 * Chạy với -DshowFail=true để thêm 1 bộ "Fail có chủ đích".
 */
@PhanCong(bai = "Lab 06 – Bài 1: @DataProvider", nguoiThucHien = "Lương Công Trường", mssv = "PS48775")
public class LoginDataDrivenTest {

    static final String URL = "https://www.saucedemo.com/";
    static final String SUCCESS = "INVENTORY";
    static final String MSG_NOT_MATCH = "Epic sadface: Username and password do not match any user in this service";

    private WebDriver driver;
    private WebDriverWait wait;

    @DataProvider(name = "loginData")
    public Object[][] loginData() {
        List<Object[]> rows = new ArrayList<>(Arrays.asList(new Object[][]{
                // mã, mô tả, username, password, kết quả mong đợi
                {"DD01", "Login thành công", "standard_user", "secret_sauce", SUCCESS},
                {"DD02", "Sai username", "user_khong_ton_tai", "secret_sauce", MSG_NOT_MATCH},
                {"DD03", "Sai password", "standard_user", "sai_mat_khau", MSG_NOT_MATCH},
                {"DD04", "Username rỗng", "", "secret_sauce", "Epic sadface: Username is required"},
                {"DD05", "Password rỗng", "standard_user", "", "Epic sadface: Password is required"},
                {"DD06", "Tài khoản bị khoá", "locked_out_user", "secret_sauce", "Epic sadface: Sorry, this user has been locked out."},
                // ---- 3 bộ dữ liệu biên AI đề xuất, nhóm giữ lại ----
                {"AI01", "Username viết HOA (phân biệt hoa thường)", "STANDARD_USER", "secret_sauce", MSG_NOT_MATCH},
                {"AI02", "Chuỗi SQL Injection", "' OR '1'='1", "' OR '1'='1", MSG_NOT_MATCH},
                {"AI03", "Username dài 256 ký tự", "a".repeat(256), "secret_sauce", MSG_NOT_MATCH},
        }));
        if (Boolean.getBoolean("showFail")) {
            rows.add(new Object[]{"FAIL", "[Fail có chủ đích] locked_out_user nhưng mong đợi vào được trang sản phẩm",
                    "locked_out_user", "secret_sauce", SUCCESS});
        }
        return rows.toArray(new Object[0][]);
    }

    @BeforeMethod
    public void setUp() {
        driver = DriverFactory.create();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(URL);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(dataProvider = "loginData", description = "Login saucedemo với nhiều bộ dữ liệu")
    public void login(String id, String description, String username, String password, String expected) {
        String shownUser = username.length() > 20 ? username.substring(0, 20) + "...(" + username.length() + " ký tự)" : username;
        log("[" + id + "] " + description + " | username='" + shownUser + "' | password='" + password + "'");

        driver.findElement(By.id("user-name")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("login-button")).click();

        if (SUCCESS.equals(expected)) {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("inventory.html"),
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h3[data-test='error']"))));
            if (Evidence.enabled()) {
                Evidence.checkpoint("lab06_bai1_" + id.toLowerCase(), "Bộ dữ liệu " + id + ": " + description);
            }
            Assert.assertTrue(driver.getCurrentUrl().endsWith("/inventory.html"),
                    "[" + id + "] Mong đợi vào trang sản phẩm nhưng URL là " + driver.getCurrentUrl()
                            + " – thông báo: " + errorTextOrEmpty());
            log("   -> Vào được trang sản phẩm: " + driver.getCurrentUrl());
        } else {
            String error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h3[data-test='error']"))).getText();
            log("   -> Thông báo lỗi: " + error);
            Assert.assertEquals(error, expected, "[" + id + "] Sai thông báo lỗi");
        }
    }

    private String errorTextOrEmpty() {
        return driver.findElements(By.cssSelector("h3[data-test='error']")).stream()
                .map(e -> e.getText()).findFirst().orElse("");
    }

    static void log(String message) {
        System.out.println(message);
        Reporter.log(message);
    }
}
