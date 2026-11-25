package poly.nhom3.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ghi người phụ trách lên từng class test.
 * ConsoleLogListener in ra console khi class bắt đầu chạy, ExtentReportListener đưa vào mục Author
 * của Extent Report – dùng làm minh chứng phân công công việc của nhóm.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PhanCong {
    String bai();

    String nguoiThucHien();

    String mssv();
}
