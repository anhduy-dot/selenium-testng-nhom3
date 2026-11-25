package poly.nhom3.support;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/** Gắn RetryAnalyzer cho mọi @Test khi chạy với -Dretry=N (N &gt; 0), không phải sửa từng class test. */
public class RetryTransformer implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        if (Integer.getInteger("retry", 0) > 0) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }
}
