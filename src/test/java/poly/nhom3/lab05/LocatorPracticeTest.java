package poly.nhom3.lab05;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import poly.nhom3.support.DriverFactory;
import poly.nhom3.support.Evidence;
import poly.nhom3.support.PhanCong;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lab 05 – Bài 1: tìm cùng một element bằng nhiều loại locator trên https://www.saucedemo.com
 */
@PhanCong(bai = "Lab 05 – Bài 1: Locator", nguoiThucHien = "Nguyễn Thái Minh Tín", mssv = "PS48989")
public class LocatorPracticeTest {

    private static final String URL = "https://www.saucedemo.com/";
    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = DriverFactory.create();
        driver.get(URL);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(description = "Tìm ô Username bằng 6 loại locator, ô Password bằng 5 cách")
    public void findUsernameAndPasswordByManyLocators() {
        Map<String, By> username = new LinkedHashMap<>();
        username.put("id", By.id("user-name"));
        username.put("name", By.name("user-name"));
        username.put("className", By.className("form_input"));
        username.put("tagName", By.tagName("input"));
        username.put("cssSelector", By.cssSelector("input[data-test='username']"));
        username.put("xpath", By.xpath("//input[@placeholder='Username']"));

        WebElement expected = driver.findElement(By.id("user-name"));
        System.out.println("=== Ô USERNAME ===");
        for (Map.Entry<String, By> e : username.entrySet()) {
            WebElement el = driver.findElement(e.getValue());
            int matches = driver.findElements(e.getValue()).size();
            printElement(e.getKey(), e.getValue(), el, matches);
            Assert.assertEquals(el, expected, "Locator " + e.getKey() + " phải trỏ đúng ô Username");
        }

        Map<String, By> password = new LinkedHashMap<>();
        password.put("id", By.id("password"));
        password.put("name", By.name("password"));
        password.put("cssSelector", By.cssSelector("input[type='password']"));
        password.put("cssSelector(data-test)", By.cssSelector("[data-test='password']"));
        password.put("xpath", By.xpath("//input[@placeholder='Password']"));

        WebElement expectedPassword = driver.findElement(By.id("password"));
        System.out.println("=== Ô PASSWORD ===");
        for (Map.Entry<String, By> e : password.entrySet()) {
            WebElement el = driver.findElement(e.getValue());
            printElement(e.getKey(), e.getValue(), el, driver.findElements(e.getValue()).size());
            Assert.assertEquals(el, expectedPassword, "Locator " + e.getKey() + " phải trỏ đúng ô Password");
        }

        Evidence.highlight(driver, expected);
        Evidence.highlight(driver, expectedPassword);
        Evidence.checkpoint("lab05_bai1_locator", "Đã tìm ô Username/Password bằng nhiều locator (viền đỏ)");
    }

    @Test(description = "Tìm element không tồn tại để quan sát NoSuchElementException",
            expectedExceptions = NoSuchElementException.class)
    public void findMissingElementThrowsNoSuchElement() {
        long start = System.currentTimeMillis();
        try {
            driver.findElement(By.id("khong-ton-tai"));
        } catch (NoSuchElementException e) {
            System.out.println("Bắt được NoSuchElementException sau " + (System.currentTimeMillis() - start) + " ms");
            System.out.println("Thông báo gốc: " + e.getMessage().lines().findFirst().orElse(""));
            List<WebElement> none = driver.findElements(By.id("khong-ton-tai"));
            System.out.println("Cùng locator nhưng dùng findElements -> list rỗng, size = " + none.size());
            throw e; // ném lại để TestNG xác nhận đúng loại exception mong đợi
        }
    }

    private static void printElement(String type, By by, WebElement el, int matches) {
        System.out.printf("%-24s | %-45s | tag=%s, id=%s, name=%s, class=%s, placeholder=%s, data-test=%s | khớp %d phần tử%n",
                type, by, el.getTagName(), el.getDomAttribute("id"), el.getDomAttribute("name"),
                el.getDomAttribute("class"), el.getDomAttribute("placeholder"), el.getDomAttribute("data-test"), matches);
    }
}
