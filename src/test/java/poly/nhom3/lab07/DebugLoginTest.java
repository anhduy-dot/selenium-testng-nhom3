package poly.nhom3.lab07;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import poly.nhom3.support.BaseTest;
import poly.nhom3.support.Evidence;
import poly.nhom3.support.PhanCong;

/**
 * Lab 07 – Bài 1: kỹ thuật debug Selenium.
 * Mặc định chạy bản ĐÃ SỬA. Chạy với -Dbug=locator | notready | url để tái hiện từng lỗi cố tình tạo ra.
 */
@PhanCong(bai = "Lab 07 – Bài 1: Debugging", nguoiThucHien = "Nguyễn Đăng Khoa", mssv = "PS48883")
public class DebugLoginTest extends BaseTest {

    private static final String BUG = System.getProperty("bug", "");

    @Test(description = "Lỗi 1 – sai locator ô username (NoSuchElementException)")
    public void loi1_saiLocator() {
        driver().get("https://www.saucedemo.com/");
        // BUG: id thật là "user-name" (gạch ngang), bản lỗi gõ nhầm thành "username"
        By username = BUG.equals("locator") ? By.id("username") : By.id("user-name");
        debug("URL = " + driver().getCurrentUrl() + " | title = " + driver().getTitle());
        debug("Số phần tử khớp " + username + ": " + driver().findElements(username).size());
        driver().findElements(By.tagName("input")).forEach(i ->
                debug("  input trên trang: id=" + i.getDomAttribute("id") + ", data-test=" + i.getDomAttribute("data-test")));

        if (BUG.equals("locator")) {
            Evidence.checkpoint("lab07_bai1_debug_locator", "Lỗi 1: trang login vẫn hiển thị bình thường");
        }
        WebElement user = driver().findElement(username);   // <- đặt breakpoint ở dòng này khi debug
        user.sendKeys("standard_user");
        driver().findElement(By.id("password")).sendKeys("secret_sauce");
        driver().findElement(By.id("login-button")).click();
        String title = waitFor().until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.title"))).getText();
        debug("Đăng nhập xong, tiêu đề trang: " + title);
        Assert.assertEquals(title, "Products");
    }

    @Test(description = "Lỗi 2 – click khi element chưa sẵn sàng (ElementNotInteractableException)")
    public void loi2_clickKhiChuaSanSang() {
        driver().get("https://the-internet.herokuapp.com/dynamic_loading/1");
        driver().findElement(By.cssSelector("#start button")).click();
        By result = By.cssSelector("#finish h4");
        WebElement hello = driver().findElement(result);   // phần tử có sẵn trong DOM nhưng đang bị ẩn
        debug("Ngay sau khi bấm Start: isDisplayed = " + hello.isDisplayed() + ", text = '" + hello.getText() + "'");
        if (Boolean.getBoolean("debug.sleep")) {
            debug("Thread.sleep 2 giây để quan sát thanh Loading... (chỉ dùng khi debug)");
            Evidence.pause(2000);
        }
        if (BUG.equals("notready")) {
            Evidence.checkpoint("lab07_bai1_debug_notready", "Lỗi 2: đang Loading..., chữ Hello World! còn ẩn");
            hello.click();   // BUG: click ngay khi chữ còn ẩn -> ElementNotInteractableException
        } else {
            waitFor().until(ExpectedConditions.visibilityOfElementLocated(result));   // SỬA: chờ hiển thị
            debug("Sau khi chờ: isDisplayed = " + hello.isDisplayed() + ", text = '" + hello.getText() + "'");
            hello.click();
        }
        Assert.assertEquals(hello.getText(), "Hello World!");
    }

    @Test(description = "Lỗi 3 – sai URL trang login (trang 404, không có form)")
    public void loi3_saiUrl() {
        // BUG: gõ nhầm /logn thay vì /login
        String url = BUG.equals("url") ? "https://the-internet.herokuapp.com/logn" : "https://the-internet.herokuapp.com/login";
        driver().get(url);
        debug("URL thực tế = " + driver().getCurrentUrl() + " | title = '" + driver().getTitle() + "'");
        debug("Thẻ h1/h2 đầu trang: " + driver().findElements(By.cssSelector("h1, h2")).stream()
                .map(WebElement::getText).findFirst().orElse("(không có)"));
        if (BUG.equals("url")) {
            Evidence.checkpoint("lab07_bai1_debug_url", "Lỗi 3: trang Not Found vì gõ sai URL");
        }
        waitFor().until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("tomsmith");
        driver().findElement(By.id("password")).sendKeys("SuperSecretPassword!");
        driver().findElement(By.cssSelector("button[type='submit']")).click();
        String flash = waitFor().until(ExpectedConditions.visibilityOfElementLocated(By.id("flash"))).getText();
        debug("Thông báo: " + flash.lines().findFirst().orElse(""));
        Assert.assertTrue(flash.contains("You logged into a secure area!"));
    }

    private static void debug(String message) {
        System.out.println("[DEBUG] " + message);
    }
}
