package poly.nhom3.support;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Factory tạo WebDriver cho nhiều trình duyệt (Lab 05 Bài 4 – sinh bằng AI, nhóm chỉnh lại).
 * <ul>
 *   <li>Driver do Selenium Manager (có sẵn trong Selenium 4) tự tải; nếu máy chưa có Firefox,
 *       Selenium Manager tải luôn trình duyệt vào ~/.cache/selenium.</li>
 *   <li>-Dbrowser=chrome|firefox|edge chọn trình duyệt, mặc định chrome.</li>
 *   <li>-Dheadless=true hoặc biến môi trường CI=true (GitHub Actions) thì chạy không giao diện.</li>
 *   <li>-Dtile=true xếp các cửa sổ thành lưới 2x2 khi chạy song song (Lab 06).</li>
 * </ul>
 */
public final class DriverFactory {

    private static final AtomicInteger OPENED = new AtomicInteger();

    private DriverFactory() {
    }

    public static String defaultBrowser() {
        return System.getProperty("browser", "chrome");
    }

    public static boolean isHeadless() {
        String prop = System.getProperty("headless");
        if (prop != null && !prop.isBlank()) {
            return Boolean.parseBoolean(prop);
        }
        return "true".equalsIgnoreCase(System.getenv("CI"));
    }

    public static WebDriver create() {
        return create(defaultBrowser());
    }

    public static WebDriver create(String browser) {
        String name = (browser == null || browser.isBlank()) ? defaultBrowser() : browser.trim().toLowerCase();
        WebDriver driver = switch (name) {
            case "chrome" -> new ChromeDriver(chromeOptions());
            case "firefox" -> new FirefoxDriver(geckoService(), firefoxOptions());
            case "edge" -> new EdgeDriver(edgeOptions());
            default -> throw new IllegalArgumentException(
                    "Trình duyệt chưa hỗ trợ: '" + browser + "' (chỉ có chrome, firefox, edge)");
        };
        placeWindow(driver);
        return driver;
    }

    /** Tạo Chrome kèm tuỳ chỉnh riêng cho một bài (ví dụ cách xử lý alert ở Lab 07). */
    public static WebDriver createChrome(Consumer<ChromeOptions> customizer) {
        ChromeOptions options = chromeOptions();
        customizer.accept(options);
        WebDriver driver = new ChromeDriver(options);
        placeWindow(driver);
        return driver;
    }

    public static ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        // Tắt popup "Lưu mật khẩu" / "mật khẩu bị lộ" (popup này che trang saucedemo sau khi login)
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("--disable-features=PasswordLeakDetection",
                "--no-first-run", "--no-default-browser-check", "--disable-search-engine-choice-screen");
        if (isHeadless()) {
            options.addArguments("--headless=new", "--window-size=1366,900",
                    "--no-sandbox", "--disable-dev-shm-usage");
        }
        return options;
    }

    public static FirefoxOptions firefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        options.addPreference("signon.rememberSignons", false);
        if (isHeadless()) {
            options.addArguments("-headless");
        }
        return options;
    }

    /**
     * macOS 27 chặn Firefox đọc ~/Library/Application Support/Firefox khi được mở trực tiếp từ file chạy
     * (Mozilla bug 2078511) -> Firefox thoát ngay với "Could not find profile folder".
     * Cách xử lý: trỏ MOZ_APP_DATA / MOZ_LOCAL_APP_DATA sang thư mục tạm riêng cho mỗi phiên.
     */
    private static GeckoDriverService geckoService() {
        GeckoDriverService.Builder builder = new GeckoDriverService.Builder();
        if (System.getProperty("os.name", "").toLowerCase().contains("mac")) {
            try {
                Path base = Files.createTempDirectory("nhom3-firefox-");
                Path app = Files.createDirectories(base.resolve("app"));
                Path local = Files.createDirectories(base.resolve("local"));
                builder.withEnvironment(Map.of("MOZ_APP_DATA", app.toString(), "MOZ_LOCAL_APP_DATA", local.toString()));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return builder.build();
    }

    public static EdgeOptions edgeOptions() {
        EdgeOptions options = new EdgeOptions();
        if (isHeadless()) {
            options.addArguments("--headless=new", "--window-size=1366,900");
        }
        return options;
    }

    /** Dùng trong Lab 07: để alert mở nguyên khi lệnh tiếp theo bị chặn (ném UnhandledAlertException). */
    public static void keepAlertOpen(ChromeOptions options) {
        options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.IGNORE);
    }

    /** Đặt cửa sổ ở nửa phải màn hình để ảnh minh chứng thấy cả console (trái) và trình duyệt (phải). */
    private static void placeWindow(WebDriver driver) {
        if (isHeadless()) {
            return;
        }
        int slot = OPENED.getAndIncrement();
        WebDriver.Window window = driver.manage().window();
        if (Boolean.getBoolean("tile")) {
            int i = slot % 4;
            window.setPosition(new Point(760 + (i % 2) * 520, 25 + (i / 2) * 572));
            window.setSize(new Dimension(520, 572));
        } else {
            window.setPosition(new Point(760, 25));
            window.setSize(new Dimension(1040, 1144));
        }
    }
}
