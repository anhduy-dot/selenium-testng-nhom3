package poly.nhom3.lab06;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import poly.nhom3.support.BaseTest;
import poly.nhom3.support.PhanCong;

/**
 * Lab 06 – Bài 3: test login có gán group (smoke, regression, login) để chạy chọn lọc và chạy song song.
 */
@PhanCong(bai = "Lab 06 – Bài 3: Groups & Parallel", nguoiThucHien = "Nguyễn Thái Minh Tín", mssv = "PS48989")
public class LoginTest extends BaseTest {

    static void loginAs(BaseTest t, String username) {
        t.driver().get("https://www.saucedemo.com/");
        t.driver().findElement(By.id("user-name")).sendKeys(username);
        t.driver().findElement(By.id("password")).sendKeys("secret_sauce");
        t.driver().findElement(By.id("login-button")).click();
    }

    @Test(groups = {"smoke", "regression", "login"}, description = "Login hợp lệ vào trang Products")
    public void loginHopLe() {
        loginAs(this, "standard_user");
        String title = waitFor().until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.page-title"))).getText();
        Assert.assertEquals(title, "Products");
    }

    @Test(groups = {"regression", "login"}, description = "Tài khoản bị khoá hiển thị lỗi")
    public void loginTaiKhoanBiKhoa() {
        loginAs(this, "locked_out_user");
        String error = waitFor().until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h3[data-test='error']"))).getText();
        Assert.assertTrue(error.contains("locked out"), error);
    }

    @Test(groups = {"regression", "login"}, description = "Bỏ trống username hiển thị lỗi bắt buộc")
    public void loginBoTrongUsername() {
        driver().get("https://www.saucedemo.com/");
        driver().findElement(By.id("login-button")).click();
        String error = waitFor().until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h3[data-test='error']"))).getText();
        Assert.assertEquals(error, "Epic sadface: Username is required");
    }

    @Test(groups = {"smoke", "regression", "login"}, description = "Đăng xuất quay về trang login")
    public void dangXuat() {
        loginAs(this, "standard_user");
        waitFor().until(ExpectedConditions.elementToBeClickable(By.id("react-burger-menu-btn"))).click();
        waitFor().until(ExpectedConditions.elementToBeClickable(By.id("logout_sidebar_link"))).click();
        waitFor().until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));
        Assert.assertEquals(driver().getCurrentUrl(), "https://www.saucedemo.com/");
    }
}
