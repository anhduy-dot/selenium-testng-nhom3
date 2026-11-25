package poly.nhom3.support;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.time.Duration;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lớp cha cho các test chạy song song (Lab 06 Bài 3, Lab 07).
 * Mỗi luồng giữ WebDriver riêng trong ThreadLocal nên parallel="methods" không bị giành driver của nhau.
 */
public abstract class BaseTest {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final ThreadLocal<WebDriverWait> WAIT = new ThreadLocal<>();

    // Chỉ dùng khi chụp minh chứng chạy song song: N luồng đầu tiên cùng mở trang rồi đợi nhau để chụp 1 ảnh có N trình duyệt
    private static final int BARRIER_SIZE = Integer.getInteger("evidence.barrier", 0);
    private static final AtomicInteger ARRIVED = new AtomicInteger();
    private static final CyclicBarrier BARRIER = BARRIER_SIZE > 1 ? new CyclicBarrier(BARRIER_SIZE, () ->
            Evidence.checkpoint("lab06_bai3_" + System.getProperty("runmode", "run") + "_cac_trinh_duyet",
                    BARRIER_SIZE + " trình duyệt đang chạy song song")) : null;

    public WebDriver driver() {
        return DRIVER.get();
    }

    public WebDriverWait waitFor() {
        return WAIT.get();
    }

    @BeforeMethod(alwaysRun = true)
    @Parameters("browser")
    public void openBrowser(@Optional("") String browser) {
        WebDriver driver = DriverFactory.create(browser);
        DRIVER.set(driver);
        WAIT.set(new WebDriverWait(driver, Duration.ofSeconds(10)));
        if (BARRIER != null && ARRIVED.incrementAndGet() <= BARRIER_SIZE) {
            driver.get("https://www.saucedemo.com/");
            try {
                BARRIER.await(20, TimeUnit.SECONDS);
            } catch (Exception e) {
                System.out.println("[CẢNH BÁO] Không đủ luồng để chụp ảnh song song: " + e);
            }
        }
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
        }
        DRIVER.remove();
        WAIT.remove();
    }
}
