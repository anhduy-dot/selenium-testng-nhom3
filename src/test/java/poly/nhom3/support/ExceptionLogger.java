package poly.nhom3.support;

import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;

import java.util.function.Supplier;

/**
 * Lab 07 Bài 2 – method tiện ích chung để bắt, dịch và log exception của Selenium cho dễ đọc,
 * kèm hành động phục hồi nếu có.
 */
public final class ExceptionLogger {

    private ExceptionLogger() {
    }

    /** Chạy action; nếu lỗi thì log thân thiện rồi chạy recovery (nếu có). Lỗi không lường trước thì ném tiếp. */
    public static <T> T attempt(String step, Supplier<T> action, Supplier<T> recovery) {
        try {
            return action.get();
        } catch (NoSuchElementException | TimeoutException | ElementNotInteractableException
                 | StaleElementReferenceException | UnhandledAlertException e) {
            log(step, e);
            if (recovery == null) {
                throw e;
            }
            System.out.println("   ↻ Phục hồi: đang thử lại theo cách an toàn hơn...");
            T value = recovery.get();
            System.out.println("   ✔ Phục hồi thành công, kết quả: " + value);
            return value;
        }
    }

    public static void log(String step, Throwable e) {
        System.out.println("┌─ [LỖI] " + step);
        System.out.println("│  Loại      : " + e.getClass().getSimpleName());
        System.out.println("│  Giải thích: " + friendlyMessage(e));
        System.out.println("└─ Gốc      : " + ConsoleLogListener.firstLine(e.getMessage()));
    }

    public static String friendlyMessage(Throwable e) {
        if (e instanceof NoSuchElementException) {
            return "Không tìm thấy phần tử trên trang – kiểm tra lại locator hoặc trang đã tải đúng chưa.";
        }
        if (e instanceof TimeoutException) {
            return "Hết thời gian chờ mà điều kiện vẫn chưa xảy ra – phần tử không xuất hiện hoặc trang quá chậm.";
        }
        if (e instanceof ElementNotInteractableException) {
            return "Phần tử có trong DOM nhưng đang ẩn/bị che nên không click hay nhập được – cần chờ nó hiển thị.";
        }
        if (e instanceof StaleElementReferenceException) {
            return "Phần tử đã bị xoá hoặc vẽ lại trong DOM sau khi tìm – phải tìm lại phần tử mới.";
        }
        if (e instanceof UnhandledAlertException) {
            return "Có hộp thoại alert đang mở chặn mọi thao tác – cần switchTo().alert() để xử lý trước.";
        }
        return "Lỗi khác: " + e.getClass().getSimpleName();
    }
}
