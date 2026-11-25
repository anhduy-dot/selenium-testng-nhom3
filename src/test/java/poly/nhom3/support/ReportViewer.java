package poly.nhom3.support;

import org.openqa.selenium.WebDriver;

import java.nio.file.Paths;

/**
 * Mở file report HTML (TestNG / Extent) bằng Chrome ở nửa phải màn hình để chụp minh chứng.
 * Cách chạy: java -cp ... poly.nhom3.support.ReportViewer &lt;file.html&gt; &lt;tên-ảnh&gt; [scrollY]
 */
public final class ReportViewer {

    private ReportViewer() {
    }

    public static void main(String[] args) {
        boolean web = args[0].startsWith("http");
        String url = web ? args[0] : Paths.get(args[0]).toAbsolutePath().toUri().toString();
        String shot = args.length > 1 ? args[1] : "report";
        WebDriver driver = DriverFactory.create("chrome");
        try {
            driver.get(url);
            Evidence.pause(web ? 3000 : 1500);
            for (int i = 2; i < args.length; i++) {   // tham số thêm: "js:<lệnh>" hoặc số pixel cần cuộn
                String script = args[i].startsWith("js:") ? args[i].substring(3) : "window.scrollTo(0," + args[i] + ")";
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(script);
                Evidence.pause(800);
            }
            System.out.println("Đang mở: " + url);
            Evidence.checkpoint(shot, "Mở " + url);
        } finally {
            driver.quit();
        }
    }
}
