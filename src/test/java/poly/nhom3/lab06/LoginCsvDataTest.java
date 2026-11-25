package poly.nhom3.lab06;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import poly.nhom3.support.CsvDataReader;
import poly.nhom3.support.DriverFactory;
import poly.nhom3.support.Evidence;
import poly.nhom3.support.PhanCong;

import java.time.Duration;

/**
 * Lab 06 – Bài 2: dữ liệu login đọc từ file ngoài src/test/resources/data/login_data.csv
 * (đọc bằng Apache Commons CSV), không hard-code trong code test.
 */
@PhanCong(bai = "Lab 06 – Bài 2: Dữ liệu từ file CSV", nguoiThucHien = "Phạm Minh Triết", mssv = "PS48509")
public class LoginCsvDataTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @DataProvider(name = "csvLoginData")
    public Object[][] csvLoginData() {
        return CsvDataReader.read("data/login_data.csv", "ma", "mo_ta", "username", "password", "ket_qua_mong_doi");
    }

    @BeforeMethod
    public void setUp() {
        driver = DriverFactory.create();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.get(LoginDataDrivenTest.URL);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(dataProvider = "csvLoginData", description = "Login với dữ liệu đọc từ login_data.csv")
    public void loginFromCsv(String id, String description, String username, String password, String expected) {
        LoginDataDrivenTest.log(">>> Đang chạy bộ dữ liệu " + id + " – " + description
                + " | username='" + username + "', password='" + password + "', mong đợi: " + expected);

        driver.findElement(By.id("user-name")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("login-button")).click();

        if (LoginDataDrivenTest.SUCCESS.equals(expected)) {
            wait.until(ExpectedConditions.urlContains("inventory.html"));
            String title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.title"))).getText();
            Assert.assertEquals(title, "Products");
            LoginDataDrivenTest.log("    Kết quả: vào trang " + title);
        } else {
            String error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h3[data-test='error']"))).getText();
            LoginDataDrivenTest.log("    Kết quả: " + error);
            Assert.assertEquals(error, expected);
        }
        if (id.equals("CSV06")) {
            Evidence.checkpoint("lab06_bai2_csv", "Bộ dữ liệu CSV06 đọc từ file CSV đang chạy");
        }
    }
}
