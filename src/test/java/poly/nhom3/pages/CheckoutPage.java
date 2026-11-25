package poly.nhom3.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/** Page Object cho 3 bước checkout: nhập thông tin -> xem tổng tiền -> hoàn tất. */
public class CheckoutPage {

    private static final By FIRST_NAME = By.cssSelector("[data-test='firstName']");
    private static final By LAST_NAME = By.cssSelector("[data-test='lastName']");
    private static final By POSTAL_CODE = By.cssSelector("[data-test='postalCode']");
    private static final By CONTINUE = By.cssSelector("[data-test='continue']");
    private static final By SUBTOTAL = By.cssSelector("[data-test='subtotal-label']");
    private static final By TAX = By.cssSelector("[data-test='tax-label']");
    private static final By TOTAL = By.cssSelector("[data-test='total-label']");
    private static final By FINISH = By.cssSelector("[data-test='finish']");
    private static final By COMPLETE_HEADER = By.cssSelector("[data-test='complete-header']");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public CheckoutPage waitStepOne() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(FIRST_NAME));
        return this;
    }

    public CheckoutPage fillInformation(String firstName, String lastName, String postalCode) {
        driver.findElement(FIRST_NAME).sendKeys(firstName);
        driver.findElement(LAST_NAME).sendKeys(lastName);
        driver.findElement(POSTAL_CODE).sendKeys(postalCode);
        driver.findElement(CONTINUE).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(TOTAL));
        return this;
    }

    public double subtotal() {
        return money(SUBTOTAL);
    }

    public double tax() {
        return money(TAX);
    }

    public double total() {
        return money(TOTAL);
    }

    public String finish() {
        driver.findElement(FINISH).click();
        return wait.until(ExpectedConditions.visibilityOfElementLocated(COMPLETE_HEADER)).getText();
    }

    private double money(By label) {
        String text = driver.findElement(label).getText();          // ví dụ "Item total: $39.98"
        return Double.parseDouble(text.substring(text.indexOf('$') + 1));
    }
}
