package poly.nhom3.lab07;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import poly.nhom3.support.BaseTest;
import poly.nhom3.support.PhanCong;

/**
 * Lab 07 – Bài 3: BẢN ĐẦU TIÊN do AI sinh từ mô tả tiếng Việt (giữ lại để so sánh với CheckoutFlowTest).
 * Nhược điểm: 1 method dài, Thread.sleep cố định, locator id/class rải rác, không tái sử dụng được.
 */
@PhanCong(bai = "Lab 07 – Bài 3: AI viết & tối ưu script", nguoiThucHien = "Lương Công Trường", mssv = "PS48775")
public class CheckoutFlowAiDraftTest extends BaseTest {

    @Test(description = "Bản AI sinh lần 1 – chạy được nhưng chậm và khó bảo trì")
    public void checkoutBanDauCuaAi() throws InterruptedException {
        driver().get("https://www.saucedemo.com/");
        driver().findElement(By.id("user-name")).sendKeys("standard_user");
        driver().findElement(By.id("password")).sendKeys("secret_sauce");
        driver().findElement(By.id("login-button")).click();
        Thread.sleep(2000);
        driver().findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        driver().findElement(By.id("add-to-cart-sauce-labs-bike-light")).click();
        Thread.sleep(1000);
        Assert.assertEquals(driver().findElement(By.className("shopping_cart_badge")).getText(), "2");
        driver().findElement(By.className("shopping_cart_link")).click();
        Thread.sleep(2000);
        driver().findElement(By.id("checkout")).click();
        Thread.sleep(2000);
        driver().findElement(By.id("first-name")).sendKeys("Duy");
        driver().findElement(By.id("last-name")).sendKeys("Nguyen");
        driver().findElement(By.id("postal-code")).sendKeys("700000");
        driver().findElement(By.id("continue")).click();
        Thread.sleep(2000);
        String total = driver().findElement(By.className("summary_total_label")).getText();
        System.out.println("Tổng tiền (bản AI đầu tiên): " + total);
        Assert.assertEquals(total, "Total: $43.18");
        driver().findElement(By.id("finish")).click();
        Thread.sleep(2000);
        Assert.assertEquals(driver().findElement(By.className("complete-header")).getText(), "Thank you for your order!");
    }
}
