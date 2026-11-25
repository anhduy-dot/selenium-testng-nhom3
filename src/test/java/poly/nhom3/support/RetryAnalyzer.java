package poly.nhom3.support;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Lab 08 Bài 4 – giảm flaky test trên CI: test fail sẽ được chạy lại tối đa -Dretry=N lần.
 * Mặc định retry=0 (tắt) để khi chạy ở máy, lỗi thật không bị che mất.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int attempts;

    @Override
    public boolean retry(ITestResult result) {
        int max = Integer.getInteger("retry", 0);
        if (attempts < max) {
            attempts++;
            System.out.println("↻ RETRY lần " + attempts + "/" + max + " cho " + ConsoleLogListener.name(result)
                    + " – lỗi: " + (result.getThrowable() == null ? "" : result.getThrowable().getClass().getSimpleName()));
            return true;
        }
        return false;
    }
}
