package poly.nhom3.support;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Hỗ trợ lưu minh chứng khi chạy test.
 * <ul>
 *   <li>checkpoint(): in mốc ra console; khi chạy với -Devidence=true thì gửi yêu cầu chụp toàn màn hình
 *       (console bên trái + trình duyệt bên phải) và đợi chụp xong rồi mới chạy tiếp.</li>
 *   <li>saveBrowserScreenshot(): ảnh Selenium tự chụp nội dung trình duyệt (TakesScreenshot).</li>
 * </ul>
 */
public final class Evidence {

    private static final Path REQUEST_DIR = Paths.get("/tmp/nhom3-evidence/req");
    private static final Path SCREENSHOT_DIR = Paths.get("screenshots");

    private Evidence() {
    }

    public static boolean enabled() {
        return Boolean.getBoolean("evidence");
    }

    public static void checkpoint(String name, String description) {
        System.out.println("[MINH-CHUNG] " + name + " | " + description);
        if (!enabled()) {
            return;
        }
        pause(1200); // đợi trang vẽ xong
        try {
            Files.createDirectories(REQUEST_DIR);
            Path done = REQUEST_DIR.resolve(name + ".done");
            Files.deleteIfExists(done);
            Files.writeString(REQUEST_DIR.resolve(name + ".req"), name);
            long deadline = System.currentTimeMillis() + 10_000;
            while (!Files.exists(done) && System.currentTimeMillis() < deadline) {
                pause(200);
            }
            Files.deleteIfExists(done);
        } catch (IOException e) {
            System.out.println("[CẢNH BÁO] Không gửi được yêu cầu chụp màn hình: " + e.getMessage());
        }
    }

    public static Path saveBrowserScreenshot(WebDriver driver, String fileName) {
        Path target = SCREENSHOT_DIR.resolve(fileName + ".png");
        try {
            Files.createDirectories(SCREENSHOT_DIR);
            Path source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath();
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Không lưu được ảnh chụp " + target, e);
        }
        System.out.println("Đã lưu ảnh chụp trình duyệt: " + target);
        return target;
    }

    /** Tô viền đỏ phần tử để ảnh chụp thấy rõ element vừa tìm được. */
    public static void highlight(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].style.outline='3px solid #e53935';arguments[0].style.outlineOffset='2px';", element);
    }

    public static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
