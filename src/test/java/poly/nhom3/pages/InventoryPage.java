package poly.nhom3.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/** Page Object cho trang danh sách sản phẩm. */
public class InventoryPage {

    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By SORT = By.cssSelector("[data-test='product-sort-container']");
    private static final By PRICES = By.cssSelector("[data-test='inventory-item-price']");
    private static final By NAMES = By.cssSelector("[data-test='inventory-item-name']");
    private static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");
    private static final By CART_LINK = By.cssSelector("[data-test='shopping-cart-link']");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public InventoryPage waitLoaded() {
        wait.until(ExpectedConditions.textToBe(TITLE, "Products"));
        return this;
    }

    public String title() {
        return driver.findElement(TITLE).getText();
    }

    /** Đổi kiểu sắp xếp: lohi, hilo, az, za. */
    public InventoryPage sortBy(String value) {
        new Select(driver.findElement(SORT)).selectByValue(value);
        return this;
    }

    /**
     * Đọc giá sau khi sắp xếp. React vẽ lại danh sách nên phần tử cũ có thể bị "stale":
     * đọc lại toàn bộ danh sách, tối đa 3 lần (gợi ý của AI khi hỏi về StaleElementReferenceException).
     */
    public List<Double> prices() {
        for (int attempt = 1; ; attempt++) {
            try {
                return driver.findElements(PRICES).stream()
                        .map(e -> Double.parseDouble(e.getText().replace("$", ""))).toList();
            } catch (StaleElementReferenceException e) {
                if (attempt == 3) {
                    throw e;
                }
                System.out.println("   [StaleElement] Danh sách vừa được vẽ lại, đọc lại lần " + (attempt + 1));
            }
        }
    }

    public List<String> names() {
        return driver.findElements(NAMES).stream().map(e -> e.getText()).toList();
    }

    public InventoryPage addToCart(String productName) {
        By button = By.xpath("//div[@data-test='inventory-item-name' and normalize-space()='" + productName
                + "']/ancestor::div[@data-test='inventory-item']//button[starts-with(@data-test,'add-to-cart')]");
        wait.until(ExpectedConditions.elementToBeClickable(button)).click();
        return this;
    }

    public String cartBadge() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(CART_BADGE)).getText();
    }

    public CartPage openCart() {
        driver.findElement(CART_LINK).click();
        return new CartPage(driver).waitLoaded();
    }
}
