package poly.nhom3.lab07;

import org.testng.Assert;
import org.testng.annotations.Test;
import poly.nhom3.pages.CartPage;
import poly.nhom3.pages.CheckoutPage;
import poly.nhom3.pages.InventoryPage;
import poly.nhom3.pages.LoginPage;
import poly.nhom3.support.BaseTest;
import poly.nhom3.support.Evidence;
import poly.nhom3.support.PhanCong;

import java.util.List;

/**
 * Lab 07 – Bài 3: quy trình Login -> thêm sản phẩm vào giỏ -> Checkout.
 * Bản đầu do AI sinh từ mô tả tiếng Việt (1 method dài, dùng Thread.sleep),
 * sau đó AI tối ưu: tách Page Object, thay sleep bằng Explicit Wait, locator theo data-test.
 */
@PhanCong(bai = "Lab 07 – Bài 3: AI viết & tối ưu script", nguoiThucHien = "Lương Công Trường", mssv = "PS48775")
public class CheckoutFlowTest extends BaseTest {

    @Test(description = "Login -> thêm 2 sản phẩm -> checkout thành công")
    public void checkoutHaiSanPham() {
        InventoryPage inventory = new LoginPage(driver()).open().loginAs("standard_user", "secret_sauce");
        Assert.assertEquals(inventory.title(), "Products");

        inventory.addToCart("Sauce Labs Backpack").addToCart("Sauce Labs Bike Light");
        Assert.assertEquals(inventory.cartBadge(), "2");

        CartPage cart = inventory.openCart();
        Assert.assertEquals(cart.itemNames(), List.of("Sauce Labs Backpack", "Sauce Labs Bike Light"));

        CheckoutPage checkout = cart.checkout().fillInformation("Duy", "Nguyen", "700000");
        System.out.printf("Item total = %.2f | Tax = %.2f | Total = %.2f%n", checkout.subtotal(), checkout.tax(), checkout.total());
        Assert.assertEquals(checkout.subtotal(), 29.99 + 9.99, 0.001);
        Assert.assertEquals(checkout.total(), checkout.subtotal() + checkout.tax(), 0.001);
        Evidence.checkpoint("lab07_bai3_checkout", "Trang Overview: tổng tiền trước khi Finish");

        Assert.assertEquals(checkout.finish(), "Thank you for your order!");
    }
}
