package poly.nhom3.lab07;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import poly.nhom3.pages.CartPage;
import poly.nhom3.pages.CheckoutPage;
import poly.nhom3.pages.InventoryPage;
import poly.nhom3.pages.LoginPage;
import poly.nhom3.support.BaseTest;
import poly.nhom3.support.Evidence;
import poly.nhom3.support.ExceptionLogger;
import poly.nhom3.support.PhanCong;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Lab 07 – Bài 4: test case 10 bước có Explicit Wait, xử lý 2 loại exception, data-driven 3 bộ dữ liệu, log rõ ràng.
 */
@PhanCong(bai = "Lab 07 – Bài 4: Debug + Exception + AI", nguoiThucHien = "Phạm Minh Triết", mssv = "PS48509")
public class ComplexPurchaseFlowTest extends BaseTest {

    private static final String BUG = System.getProperty("bug", "");
    private static final By PROMO_BANNER = By.id("promo-banner"); // saucedemo không có banner -> tình huống NoSuchElement
    private static final Map<String, Double> PRICE = Map.of(
            "Sauce Labs Backpack", 29.99, "Sauce Labs Bike Light", 9.99, "Sauce Labs Bolt T-Shirt", 15.99,
            "Sauce Labs Fleece Jacket", 49.99, "Sauce Labs Onesie", 7.99, "Test.allTheThings() T-Shirt (Red)", 15.99);

    @DataProvider(name = "orders")
    public Object[][] orders() {
        return new Object[][]{
                {"standard_user", List.of("Sauce Labs Backpack", "Sauce Labs Onesie"), "Anh", "Duy", "700000"},
                {"standard_user", List.of("Sauce Labs Fleece Jacket", "Sauce Labs Bolt T-Shirt", "Sauce Labs Bike Light"), "Minh", "Triet", "100000"},
                {"performance_glitch_user", List.of("Test.allTheThings() T-Shirt (Red)"), "Cong", "Truong", "550000"},
        };
    }

    @Test(dataProvider = "orders", description = "Mua hàng đầy đủ 10 bước")
    public void muaHang10Buoc(String user, List<String> products, String first, String last, String zip) {
        info("Bộ dữ liệu: user=" + user + ", sản phẩm=" + products + ", khách=" + first + " " + last);

        LoginPage login = new LoginPage(driver()).open();
        log(1, "Mở trang login: " + driver().getCurrentUrl());

        InventoryPage inventory;
        if (BUG.equals("nowait")) {   // CỐ TÌNH PHÁ: bỏ Explicit Wait sau khi login
            driver().findElement(By.cssSelector("[data-test='username']")).sendKeys(user);
            driver().findElement(By.cssSelector("[data-test='password']")).sendKeys("secret_sauce");
            driver().findElement(By.cssSelector("[data-test='login-button']")).click();
            inventory = new InventoryPage(driver());
        } else {
            inventory = login.loginAs(user, "secret_sauce");   // performance_glitch_user chậm ~5s -> có wait
        }
        log(2, "Đăng nhập xong, tiêu đề trang: " + inventory.title());

        boolean hasBanner = ExceptionLogger.attempt("Tìm banner khuyến mãi (không bắt buộc)",   // exception 1
                () -> driver().findElement(PROMO_BANNER).isDisplayed(), () -> false);
        info("Có banner khuyến mãi? " + hasBanner + " -> bỏ qua, tiếp tục");

        List<Double> prices = inventory.sortBy("lohi").prices();
        log(3, "Sắp xếp giá thấp -> cao: " + prices);
        Assert.assertEquals(prices, prices.stream().sorted().toList(), "Giá phải tăng dần");

        products.forEach(inventory::addToCart);
        log(4, "Đã bấm Add to cart cho " + products.size() + " sản phẩm");

        String badge = waitBadge(String.valueOf(products.size()));        // exception 2 (TimeoutException)
        log(5, "Badge giỏ hàng = " + badge);
        Assert.assertEquals(badge, String.valueOf(products.size()));

        CartPage cart = inventory.openCart();
        log(6, "Trong giỏ: " + cart.itemNames());
        Assert.assertEqualsNoOrder(cart.itemNames().toArray(), products.toArray());

        CheckoutPage checkout = cart.checkout();
        log(7, "Mở form thông tin khách hàng");

        checkout.fillInformation(first, last, zip);
        log(8, "Đã nhập " + first + " " + last + " / " + zip + " -> trang Overview");

        double expectedSubtotal = products.stream().mapToDouble(PRICE::get).sum();
        log(9, String.format("Item total = %.2f (mong đợi %.2f), Tax = %.2f, Total = %.2f",
                checkout.subtotal(), expectedSubtotal, checkout.tax(), checkout.total()));
        Assert.assertEquals(checkout.subtotal(), expectedSubtotal, 0.001, "Tổng tiền hàng");
        Assert.assertEquals(checkout.tax(), Math.round(expectedSubtotal * 8) / 100.0, 0.011, "Thuế 8%");
        Assert.assertEquals(checkout.total(), checkout.subtotal() + checkout.tax(), 0.011, "Tổng thanh toán");
        if (products.size() == 3) {
            Evidence.checkpoint("lab07_bai4_overview", "Bước 9: trang Overview của bộ dữ liệu 2");
        }

        String header = checkout.finish();
        log(10, "Bấm Finish: " + header);
        Assert.assertEquals(header, "Thank you for your order!");
    }

    /** Chờ badge giỏ hàng; nếu hết giờ (TimeoutException) thì log rồi đọc lại bằng findElements. */
    private String waitBadge(String expected) {
        // CỐ TÌNH PHÁ (-Dbug=locator): đổi locator badge thành data-test không tồn tại
        By badge = BUG.equals("locator") ? By.cssSelector("[data-test='cart-badge']") : By.cssSelector("[data-test='shopping-cart-badge']");
        try {
            return new WebDriverWait(driver(), Duration.ofSeconds(5))
                    .until(ExpectedConditions.textToBe(badge, expected)) ? driver().findElement(badge).getText() : "";
        } catch (TimeoutException | NoSuchElementException e) {
            ExceptionLogger.log("Chờ badge giỏ hàng = " + expected, e);
            return driver().findElements(badge).stream().map(el -> el.getText()).findFirst().orElse("0");
        }
    }

    private static void log(int step, String message) {
        String line = "[Bước " + step + "/10] " + message;
        System.out.println(line);
        Reporter.log(line);
    }

    private static void info(String message) {
        System.out.println("[Info] " + message);
        Reporter.log("[Info] " + message);
    }
}
