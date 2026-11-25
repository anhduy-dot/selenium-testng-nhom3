package poly.nhom3.support;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In log dễ đọc cho từng test: người phụ trách, luồng đang chạy, PASS/FAIL/SKIP và thời gian.
 * Được nạp tự động qua META-INF/services nên chạy bằng -Dtest hay file testng.xml đều có log.
 */
public class ConsoleLogListener implements ITestListener, ISuiteListener {

    private static final Set<Class<?>> ANNOUNCED = ConcurrentHashMap.newKeySet();

    @Override
    public void onStart(ISuite suite) {
        System.out.println("\n================ SUITE: " + suite.getName() + " ================");
    }

    @Override
    public void onTestStart(ITestResult result) {
        Class<?> testClass = result.getTestClass().getRealClass();
        PhanCong pc = testClass.getAnnotation(PhanCong.class);
        if (pc != null && ANNOUNCED.add(testClass)) {
            System.out.println("----- " + pc.bai() + " | Người thực hiện: " + pc.nguoiThucHien()
                    + " (" + pc.mssv() + ") | class " + testClass.getSimpleName() + " -----");
        }
        System.out.println("▶ [" + Thread.currentThread().getName() + "] " + name(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("✔ PASS  " + name(result) + " (" + millis(result) + " ms)");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Throwable t = result.getThrowable();
        String msg = t == null ? "" : t.getClass().getSimpleName() + ": " + firstLine(t.getMessage());
        System.out.println("✘ FAIL  " + name(result) + " (" + millis(result) + " ms) -> " + msg);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("➜ SKIP  " + name(result));
    }

    static String name(ITestResult result) {
        String base = result.getTestClass().getRealClass().getSimpleName() + "." + result.getMethod().getMethodName();
        Object[] params = result.getParameters();
        if (params == null || params.length == 0) {
            return base;
        }
        return base + Arrays.stream(params).map(p -> String.valueOf(p).length() > 40
                ? String.valueOf(p).substring(0, 37) + "..." : String.valueOf(p))
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private static long millis(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }

    static String firstLine(String s) {
        if (s == null) {
            return "";
        }
        int nl = s.indexOf('\n');
        return nl < 0 ? s : s.substring(0, nl);
    }
}
