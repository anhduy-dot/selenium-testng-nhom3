# selenium-testng – Nhóm 03 (SOF3091 – Kiểm thử nâng cao cùng AI)

[![Selenium CI](https://github.com/anhduy-dot/selenium-testng-nhom3/actions/workflows/selenium-ci.yml/badge.svg)](https://github.com/anhduy-dot/selenium-testng-nhom3/actions/workflows/selenium-ci.yml)
[![Selenium Matrix](https://github.com/anhduy-dot/selenium-testng-nhom3/actions/workflows/selenium-matrix.yml/badge.svg)](https://github.com/anhduy-dot/selenium-testng-nhom3/actions/workflows/selenium-matrix.yml)
[![Selenium CI/CD](https://github.com/anhduy-dot/selenium-testng-nhom3/actions/workflows/selenium-cicd.yml/badge.svg)](https://github.com/anhduy-dot/selenium-testng-nhom3/actions/workflows/selenium-cicd.yml)

Project Selenium WebDriver + TestNG + Maven dùng chung cho Lab 05 – Lab 08, lớp SD21302.
Trang kiểm thử: [saucedemo.com](https://www.saucedemo.com) và [the-internet.herokuapp.com](https://the-internet.herokuapp.com).

## Thành viên và phân công

| Lab | Bài 1 | Bài 2 | Bài 3 | Bài 4 | Tổng hợp báo cáo | Kịch bản + chạy lại |
|---|---|---|---|---|---|---|
| 05 | Tín | Hải | Khoa | Duy | Trường | Triết |
| 06 | Trường | Triết | Tín | Hải | Khoa | Duy |
| 07 | Khoa | Duy | Trường | Triết | Tín | Hải |
| 08 | Hải | Duy | Khoa | Tín | Triết | Trường |

Lương Công Trường (PS48775) · Nguyễn Thái Minh Tín (PS48989) · Lê Đăng Hải (PS48830) ·
Nguyễn Đăng Khoa (PS48883) · Nguyễn Anh Duy (PS48450) · Phạm Minh Triết (PS48509)

Mỗi class test có annotation `@PhanCong` ghi người phụ trách; tên này được in ra console và hiện ở mục *Author* của Extent Report.

## Cấu trúc

```
src/test/java/poly/nhom3/
├── support/   DriverFactory (Chrome/Firefox/Edge), BaseTest (ThreadLocal), Evidence, CsvDataReader,
│              ExceptionLogger, ConsoleLogListener, ExtentReportListener, RetryAnalyzer/RetryTransformer
├── pages/     LoginPage, InventoryPage, CartPage, CheckoutPage (Page Object)
├── lab05/     Locator, XPath/CSS, TestNG + testng.xml, Cross-browser
├── lab06/     @DataProvider, CSV, Groups + Parallel
└── lab07/     Debug, Exception, AI viết & tối ưu checkout, test 10 bước
suites/        các file testng.xml theo từng bài
.github/workflows/  selenium-ci.yml (Bài 8.2), selenium-matrix.yml (Bài 8.3), selenium-cicd.yml (Bài 8.4)
```

## Cách chạy

```bash
./mvnw test                                        # toàn bộ testng.xml (Chrome)
./mvnw test -Dbrowser=firefox                      # đổi trình duyệt
./mvnw test -Dsuite=suites/lab06-smoke.xml         # chỉ group smoke
./mvnw test -Dsuite=suites/lab06-parallel-methods.xml
./mvnw test -Dheadless=true -Dretry=1              # chế độ CI
```

Windows dùng `mvnw.cmd` thay cho `./mvnw`. Report: `target/surefire-reports/emailable-report.html` và `target/extent-report/ExtentReport.html`.
