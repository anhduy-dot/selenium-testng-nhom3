package poly.nhom3.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/** Page Object cho trang giỏ hàng. */
public class CartPage {

    private static final By ITEM_NAMES = By.cssSelector("[data-test='inventory-item-name']");
    private static final By CHECKOUT = By.cssSelector("[data-test='checkout']");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public CartPage waitLoaded() {
        wait.until(ExpectedConditions.urlContains("cart.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(CHECKOUT));
        return this;
    }

    public List<String> itemNames() {
        return driver.findElements(ITEM_NAMES).stream().map(e -> e.getText()).toList();
    }

    public CheckoutPage checkout() {
        driver.findElement(CHECKOUT).click();
        return new CheckoutPage(driver).waitStepOne();
    }
}
