package poly.nhom3.lab06;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import poly.nhom3.support.BaseTest;
import poly.nhom3.support.PhanCong;

/**
 * Lab 06 – Bài 3: test giỏ hàng (group product + regression).
 */
@PhanCong(bai = "Lab 06 – Bài 3: Groups & Parallel", nguoiThucHien = "Nguyễn Thái Minh Tín", mssv = "PS48989")
public class CartTest extends BaseTest {

    private void addTwoItems() {
        LoginTest.loginAs(this, "standard_user");
        waitFor().until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-sauce-labs-backpack"))).click();
        driver().findElement(By.id("add-to-cart-sauce-labs-bolt-t-shirt")).click();
        // Sửa lỗi flaky khi chạy song song: chờ badge = 2 (giỏ hàng đã lưu đủ 2 món) rồi mới đi tiếp
        waitFor().until(ExpectedConditions.textToBe(By.className("shopping_cart_badge"), "2"));
    }

    @Test(groups = {"smoke", "regression", "product"}, description = "Thêm 2 sản phẩm -> badge giỏ hàng = 2")
    public void badgeGioHangBang2() {
        addTwoItems();
        Assert.assertEquals(driver().findElement(By.className("shopping_cart_badge")).getText(), "2");
    }

    @Test(groups = {"regression", "product"}, description = "Giỏ hàng liệt kê đúng 2 sản phẩm")
    public void gioHangCoDung2SanPham() {
        addTwoItems();
        driver().findElement(By.className("shopping_cart_link")).click();
        int items = waitFor().until(ExpectedConditions.numberOfElementsToBe(By.className("cart_item"), 2)).size();
        Assert.assertEquals(items, 2);
    }

    @Test(groups = {"regression", "product"}, description = "Xoá sản phẩm khỏi giỏ")
    public void xoaSanPhamKhoiGio() {
        addTwoItems();
        driver().findElement(By.className("shopping_cart_link")).click();
        waitFor().until(ExpectedConditions.numberOfElementsToBe(By.className("cart_item"), 2));
        waitFor().until(ExpectedConditions.elementToBeClickable(By.id("remove-sauce-labs-backpack"))).click();
        int left = waitFor().until(ExpectedConditions.numberOfElementsToBe(By.className("cart_item"), 1)).size();
        Assert.assertEquals(left, 1);
    }
}
