package poly.nhom3.lab07;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import poly.nhom3.support.DriverFactory;
import poly.nhom3.support.Evidence;
import poly.nhom3.support.ExceptionLogger;
import poly.nhom3.support.PhanCong;

import java.time.Duration;

/**
 * Lab 07 – Bài 2: cố tình tạo 5 exception phổ biến của Selenium, bắt bằng assertThrows / try-catch,
 * in thông báo thân thiện qua ExceptionLogger và phục hồi nếu có thể.
 */
@PhanCong(bai = "Lab 07 – Bài 2: Exception", nguoiThucHien = "Nguyễn Anh Duy", mssv = "PS48450")
public class ExceptionHandlingTest {

    private static final String BASE = "https://the-internet.herokuapp.com";
    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = DriverFactory.createChrome(DriverFactory::keepAlertOpen);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(priority = 1, description = "NoSuchElementException – tìm element không tồn tại")
    public void noSuchElement() {
        driver.get(BASE + "/login");
        By wrong = By.id("user-name");
        NoSuchElementException e = Assert.expectThrows(NoSuchElementException.class, () -> driver.findElement(wrong));
        ExceptionLogger.log("Tìm ô username bằng " + wrong, e);

        String value = ExceptionLogger.attempt("Nhập username bằng locator sai",
                () -> { driver.findElement(wrong).sendKeys("tomsmith"); return "đã nhập"; },
                () -> { // phục hồi: thử locator dự phòng
                    WebElement el = driver.findElement(By.id("username"));
                    el.sendKeys("tomsmith");
                    return el.getDomProperty("value");
                });
        Assert.assertEquals(value, "tomsmith");
    }

    @Test(priority = 2, description = "TimeoutException – chờ element quá thời gian")
    public void timeout() {
        driver.get(BASE + "/dynamic_loading/2");
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
        TimeoutException e = Assert.expectThrows(TimeoutException.class,
                () -> shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#finish h4"))));
        ExceptionLogger.log("Chờ chữ Hello World! trong 2 giây (chưa bấm Start)", e);

        String text = ExceptionLogger.attempt("Bấm Start rồi chờ 2 giây",
                () -> { driver.findElement(By.cssSelector("#start button")).click();
                        return shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#finish h4"))).getText(); },
                () -> new WebDriverWait(driver, Duration.ofSeconds(15)) // phục hồi: tăng thời gian chờ
                        .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#finish h4"))).getText());
        Assert.assertEquals(text, "Hello World!");
    }

    @Test(priority = 3, description = "ElementNotInteractableException – click element đang bị ẩn")
    public void elementNotInteractable() {
        driver.get(BASE + "/dynamic_loading/1");
        WebElement hidden = driver.findElement(By.cssSelector("#finish h4"));
        ElementNotInteractableException e = Assert.expectThrows(ElementNotInteractableException.class, hidden::click);
        ExceptionLogger.log("Click chữ Hello World! khi đang ẩn", e);

        String text = ExceptionLogger.attempt("Click lại khi vẫn chưa bấm Start",
                () -> { hidden.click(); return hidden.getText(); },
                () -> { // phục hồi: bấm Start rồi chờ phần tử hiển thị
                    driver.findElement(By.cssSelector("#start button")).click();
                    return new WebDriverWait(driver, Duration.ofSeconds(15))
                            .until(ExpectedConditions.visibilityOf(hidden)).getText();
                });
        Assert.assertEquals(text, "Hello World!");
    }

    @Test(priority = 4, description = "StaleElementReferenceException – element đã bị xoá khỏi DOM")
    public void staleElement() {
        driver.get(BASE + "/add_remove_elements/");
        driver.findElement(By.cssSelector("button[onclick='addElement()']")).click();
        driver.findElement(By.cssSelector("button[onclick='addElement()']")).click();
        WebElement firstDelete = driver.findElement(By.cssSelector("#elements button"));
        firstDelete.click(); // nút này bị xoá khỏi DOM
        StaleElementReferenceException e = Assert.expectThrows(StaleElementReferenceException.class, firstDelete::click);
        ExceptionLogger.log("Click lại nút Delete đã bị xoá", e);

        Integer left = ExceptionLogger.attempt("Click nút Delete bằng tham chiếu cũ",
                () -> { firstDelete.click(); return driver.findElements(By.cssSelector("#elements button")).size(); },
                () -> { // phục hồi: tìm lại phần tử mới trong DOM rồi click
                    driver.findElement(By.cssSelector("#elements button")).click();
                    return driver.findElements(By.cssSelector("#elements button")).size();
                });
        Assert.assertEquals(left.intValue(), 0);
    }

    @Test(priority = 5, description = "UnhandledAlertException – alert bất ngờ chặn thao tác")
    public void unhandledAlert() {
        driver.get(BASE + "/javascript_alerts");
        driver.findElement(By.cssSelector("button[onclick='jsAlert()']")).click();
        Evidence.checkpoint("lab07_bai2_alert", "Alert bất ngờ đang mở trên trang");
        UnhandledAlertException e = Assert.expectThrows(UnhandledAlertException.class,
                () -> driver.findElement(By.id("result")).getText());
        ExceptionLogger.log("Đọc kết quả khi alert đang mở", e);

        String result;
        try {
            Alert alert = driver.switchTo().alert();     // phục hồi: xử lý alert trước
            System.out.println("   ↻ Phục hồi: alert có nội dung '" + alert.getText() + "' -> accept()");
            alert.accept();
        } catch (NoAlertPresentException ignored) {
            System.out.println("   ↻ Alert đã tự đóng, không cần xử lý");
        }
        result = driver.findElement(By.id("result")).getText();
        System.out.println("   ✔ Sau khi xử lý alert, kết quả trên trang: " + result);
        Assert.assertEquals(result, "You successfully clicked an alert");
    }
}
