package poly.nhom3.support;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Lab 06 Bài 2 – đọc file CSV trong src/test/resources thành Object[][] cho @DataProvider.
 * Dòng đầu là tiêu đề cột; ô trống được giữ nguyên là chuỗi rỗng (để test username/password rỗng).
 */
public final class CsvDataReader {

    private CsvDataReader() {
    }

    public static Object[][] read(String resourcePath, String... columns) {
        InputStream in = CsvDataReader.class.getClassLoader().getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IllegalStateException("Không tìm thấy file dữ liệu: " + resourcePath);
        }
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setCommentMarker('#')
                .get();
        List<Object[]> rows = new ArrayList<>();
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
             CSVParser parser = CSVParser.builder().setReader(reader).setFormat(format).get()) {
            for (CSVRecord record : parser) {
                Object[] row = new Object[columns.length];
                for (int i = 0; i < columns.length; i++) {
                    row[i] = record.get(columns[i]);
                }
                rows.add(row);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Lỗi đọc file " + resourcePath, e);
        }
        System.out.println("Đã đọc " + rows.size() + " bộ dữ liệu từ " + resourcePath);
        return rows.toArray(new Object[0][]);
    }
}
