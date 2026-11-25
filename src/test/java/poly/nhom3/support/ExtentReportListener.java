package poly.nhom3.support;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.xml.XmlSuite;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Lab 06 Bài 4 – tạo Extent Report (target/extent-report/ExtentReport.html) sau khi chạy xong suite.
 * Dùng IReporter nên chạy song song vẫn an toàn: report được dựng một lần từ kết quả cuối cùng.
 */
public class ExtentReportListener implements IReporter {

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        String dir = System.getProperty("extent.dir", "target/extent-report");
        ExtentSparkReporter spark = new ExtentSparkReporter(Paths.get(dir, "ExtentReport.html").toString());
        spark.config().setTheme(Theme.STANDARD);
        spark.config().setEncoding("utf-8");
        spark.config().setDocumentTitle("Nhóm 03 – Selenium TestNG Report");
        spark.config().setReportName("Nhóm 03 – SOF3091 – " + suiteNames(suites));
        spark.config().setTimeStampFormat("dd/MM/yyyy HH:mm:ss");

        ExtentReports extent = new ExtentReports();
        extent.setReportUsesManualConfiguration(true); // lấy giờ bắt đầu/kết thúc thật của từng test
        extent.attachReporter(spark);
        extent.setSystemInfo("Nhóm", "03 – SD21302");
        extent.setSystemInfo("Trình duyệt", DriverFactory.defaultBrowser() + (DriverFactory.isHeadless() ? " (headless)" : ""));
        extent.setSystemInfo("Hệ điều hành", System.getProperty("os.name") + " " + System.getProperty("os.arch"));
        extent.setSystemInfo("Java", System.getProperty("java.version"));

        List<ITestResult> results = new ArrayList<>();
        for (ISuite suite : suites) {
            for (ISuiteResult sr : suite.getResults().values()) {
                ITestContext ctx = sr.getTestContext();
                results.addAll(ctx.getPassedTests().getAllResults());
                results.addAll(ctx.getFailedTests().getAllResults());
                results.addAll(ctx.getSkippedTests().getAllResults());
            }
        }
        results.sort(Comparator.comparingLong(ITestResult::getStartMillis));

        for (ITestResult r : results) {
            String description = r.getMethod().getDescription();
            ExtentTest test = extent.createTest(ConsoleLogListener.name(r), description == null ? "" : description);
            PhanCong pc = r.getTestClass().getRealClass().getAnnotation(PhanCong.class);
            if (pc != null) {
                test.assignAuthor(nbsp(pc.nguoiThucHien()));
                test.assignCategory(nbsp(pc.bai()));
            }
            String[] groups = r.getMethod().getGroups();
            if (groups.length > 0) {
                test.assignCategory(groups);
            }
            test.getModel().setStartTime(new Date(r.getStartMillis()));
            test.getModel().setEndTime(new Date(r.getEndMillis()));
            for (String line : Reporter.getOutput(r)) {
                test.info(line);
            }
            switch (r.getStatus()) {
                case ITestResult.SUCCESS -> test.pass("PASS sau " + (r.getEndMillis() - r.getStartMillis()) + " ms");
                case ITestResult.FAILURE -> test.fail(r.getThrowable());
                default -> test.skip(r.getThrowable() == null ? "Bị bỏ qua" : r.getThrowable().toString());
            }
        }
        extent.flush();
        System.out.println("Extent Report: " + Paths.get(dir, "ExtentReport.html").toAbsolutePath());
    }

    /** Extent bỏ dấu cách khi hiển thị tên author/category -> thay bằng khoảng trắng không ngắt. */
    private static String nbsp(String s) {
        return s.replace(' ', '\u00A0');
    }

    private static String suiteNames(List<ISuite> suites) {
        return String.join(", ", suites.stream().map(ISuite::getName).toList());
    }
}
