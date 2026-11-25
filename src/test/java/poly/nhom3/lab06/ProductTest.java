package poly.nhom3.lab06;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;
import poly.nhom3.support.BaseTest;
import poly.nhom3.support.Evidence;
import poly.nhom3.support.PhanCong;

import java.util.List;

/**
 * Lab 06 – Bài 3: test trang sản phẩm (group product).
 */
@PhanCong(bai = "Lab 06 – Bài 3: Groups & Parallel", nguoiThucHien = "Nguyễn Thái Minh Tín", mssv = "PS48989")
public class ProductTest extends BaseTest {

    private List<WebElement> openInventory() {
        LoginTest.loginAs(this, "standard_user");
        return waitFor().until(ExpectedConditions.numberOfElementsToBe(By.className("inventory_item"), 6));
    }

    @Test(groups = {"smoke", "regression", "product"}, description = "Trang sản phẩm có đủ 6 sản phẩm")
    public void coDu6SanPham() {
        Assert.assertEquals(openInventory().size(), 6);
        if ("tuan_tu".equals(System.getProperty("runmode"))) {
            Evidence.checkpoint("lab06_bai3_tuan_tu", "Chạy tuần tự: chỉ 1 trình duyệt mở tại một thời điểm");
        }
    }

    @Test(groups = {"regression", "product"}, description = "Sắp xếp giá thấp -> cao")
    public void sapXepGiaTangDan() {
        openInventory();
        new Select(driver().findElement(By.className("product_sort_container"))).selectByValue("lohi");
        List<Double> prices = driver().findElements(By.className("inventory_item_price")).stream()
                .map(e -> Double.parseDouble(e.getText().replace("$", ""))).toList();
        Assert.assertEquals(prices, prices.stream().sorted().toList(), "Giá phải tăng dần");
    }

    @Test(groups = {"regression", "product"}, description = "Mở trang chi tiết sản phẩm")
    public void xemChiTietSanPham() {
        openInventory();
        driver().findElement(By.linkText("Sauce Labs Backpack")).click();
        String name = waitFor().until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='inventory-item-name']"))).getText();
        Assert.assertEquals(name, "Sauce Labs Backpack");
    }

    @Test(groups = {"regression", "product"}, description = "Nút Add to cart đổi thành Remove")
    public void themVaoGioDoiNutRemove() {
        openInventory();
        driver().findElement(By.id("add-to-cart-sauce-labs-bike-light")).click();
        Assert.assertTrue(driver().findElement(By.id("remove-sauce-labs-bike-light")).isDisplayed());
    }
}
