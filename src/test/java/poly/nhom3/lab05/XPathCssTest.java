package poly.nhom3.lab05;

import org.openqa.selenium.By;
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
 * Lab 05 – Bài 2: XPath và CSS Selector nâng cao trên https://the-internet.herokuapp.com/add_remove_elements/
 */
@PhanCong(bai = "Lab 05 – Bài 2: XPath & CSS", nguoiThucHien = "Lê Đăng Hải", mssv = "PS48830")
public class XPathCssTest {

    private static final String URL = "https://the-internet.herokuapp.com/add_remove_elements/";

    // Locator được chọn dùng chính thức (bền nhất theo đánh giá của nhóm)
    static final By ADD_BUTTON = By.cssSelector("button[onclick='addElement()']");
    static final By DELETE_BUTTONS = By.cssSelector("#elements > button.added-manually");

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

    @Test(priority = 1, description = "So sánh các phiên bản XPath/CSS cho nút Add Element và nút Delete")
    public void compareLocatorVersions() {
        Map<String, By> addVersions = new LinkedHashMap<>();
        addVersions.put("AI-1 XPath tuyệt đối", By.xpath("/html/body/div[2]/div/div/button"));
        addVersions.put("AI-2 XPath theo text", By.xpath("//button[text()='Add Element']"));
        addVersions.put("AI-3 XPath theo onclick", By.xpath("//button[@onclick='addElement()']"));
        addVersions.put("AI-4 XPath theo cấu trúc", By.xpath("//div[@class='example']/button"));
        addVersions.put("AI-5 CSS theo onclick (CHỌN)", ADD_BUTTON);
        addVersions.put("Nhóm tự viết: neo theo tiêu đề h3",
                By.xpath("//h3[normalize-space()='Add/Remove Elements']/following-sibling::div[@class='example']/button"));

        WebElement chosen = driver.findElement(ADD_BUTTON);
        System.out.println("=== NÚT ADD ELEMENT ===");
        for (Map.Entry<String, By> e : addVersions.entrySet()) {
            List<WebElement> found = driver.findElements(e.getValue());
            System.out.printf("%-38s | %-95s | khớp %d | cùng element đã chọn: %s%n",
                    e.getKey(), e.getValue(), found.size(), !found.isEmpty() && found.get(0).equals(chosen));
            Assert.assertEquals(found.size(), 1, e.getKey() + " phải khớp đúng 1 nút");
        }

        for (int i = 0; i < 3; i++) {
            chosen.click();
        }

        Map<String, By> deleteVersions = new LinkedHashMap<>();
        deleteVersions.put("Tất cả nút Delete (CSS)", DELETE_BUTTONS);
        deleteVersions.put("Tất cả nút Delete (XPath)", By.xpath("//div[@id='elements']/button[@class='added-manually']"));
        deleteVersions.put("Theo text chứa 'Delete'", By.xpath("//button[contains(normalize-space(.),'Delete')]"));
        deleteVersions.put("Theo index: nút Delete thứ 2", By.xpath("(//div[@id='elements']/button)[2]"));
        deleteVersions.put("following-sibling từ nút Add", By.xpath("//button[text()='Add Element']/following-sibling::div[@id='elements']/button"));
        deleteVersions.put("parent:: của nút Delete", By.xpath("//button[@class='added-manually']/parent::div"));
        deleteVersions.put("CSS :last-of-type (nhóm tự viết)", By.cssSelector("div.example #elements button:last-of-type"));

        System.out.println("=== SAU KHI ADD 3 LẦN ===");
        for (Map.Entry<String, By> e : deleteVersions.entrySet()) {
            List<WebElement> found = driver.findElements(e.getValue());
            String first = found.isEmpty() ? "-" : found.get(0).getTagName() + "#" + found.get(0).getDomAttribute("id")
                    + " '" + found.get(0).getText().lines().findFirst().orElse("") + "'";
            System.out.printf("%-34s | %-90s | khớp %d | phần tử đầu: %s%n", e.getKey(), e.getValue(), found.size(), first);
        }
        Assert.assertEquals(driver.findElements(DELETE_BUTTONS).size(), 3);
        Assert.assertEquals(driver.findElement(By.xpath("//button[@class='added-manually']/parent::div")).getDomAttribute("id"), "elements");

        driver.findElements(DELETE_BUTTONS).forEach(b -> Evidence.highlight(driver, b));
        Evidence.highlight(driver, chosen);
        Evidence.checkpoint("lab05_bai2_xpath_css", "Đã Add 3 lần, các nút Delete được tô viền đỏ");
    }

    @Test(priority = 2, description = "Click Add -> số nút Delete tăng; click Delete -> số nút giảm")
    public void addThenDeleteChangesCount() {
        WebElement add = driver.findElement(ADD_BUTTON);
        for (int i = 1; i <= 3; i++) {
            add.click();
            int count = driver.findElements(DELETE_BUTTONS).size();
            System.out.println("Sau lần Add thứ " + i + ": " + count + " nút Delete");
            Assert.assertEquals(count, i);
        }
        driver.findElement(By.xpath("(//div[@id='elements']/button)[2]")).click();
        int afterOne = driver.findElements(DELETE_BUTTONS).size();
        System.out.println("Sau khi click 1 nút Delete: " + afterOne + " nút");
        Assert.assertEquals(afterOne, 2);

        while (!driver.findElements(DELETE_BUTTONS).isEmpty()) {
            driver.findElements(DELETE_BUTTONS).get(0).click();
        }
        System.out.println("Xoá hết: " + driver.findElements(DELETE_BUTTONS).size() + " nút Delete");
        Assert.assertTrue(driver.findElements(DELETE_BUTTONS).isEmpty());
    }
}
