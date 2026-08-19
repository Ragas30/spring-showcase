package com.spring.review.service;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.spring.review.entity.DepartmentEntity;
import com.spring.review.entity.EmployeeEntity;
import com.spring.review.entity.EmployeeStatus;
import com.spring.review.entity.Gender;
import com.spring.review.entity.PositionEntity;
import com.spring.review.entityView.EmployeeView;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ExportImportService {

    private final EntityManager em;

    private final CriteriaBuilderFactory cbf;

    private final EntityViewManager evm;

    private static final String[] EXCEL_HEADERS = {
            "No",
            "Employee Code",
            "Full Name",
            "Email",
            "Phone",
            "Gender",
            "Birth Date",
            "Hire Date",
            "Status",
            "Department",
            "Position"
    };

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private List<EmployeeView> getAllEmployees() {

        return evm.applySetting(
                        EntityViewSetting.create(
                                EmployeeView.class
                        ),
                        cbf.create(
                                em,
                                EmployeeEntity.class
                        )
                )
                .getResultList();
    }

    private String getCellValue(
            Row row,
            int colIndex
    ) {

        if (row == null) {
            return null;
        }

        Cell cell = row.getCell(colIndex);

        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }

        if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        }

        return null;
    }

    private String generateEmployeeCode() {

        Long seq = em.createQuery(
                "SELECT nextval('emp_code_seq')",
                Long.class
        ).getSingleResult();

        return String.format(
                "EMP%04d",
                seq
        );
    }

    public void exportEmployeesToExcel(
            HttpServletResponse response
    ) throws IOException {

        List<EmployeeView> employees = getAllEmployees();

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Employees");

            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < EXCEL_HEADERS.length; i++) {

                Cell cell = headerRow.createCell(i);
                cell.setCellValue(EXCEL_HEADERS[i]);
            }

            int rowNum = 1;

            for (int idx = 0; idx < employees.size(); idx++) {

                EmployeeView emp = employees.get(idx);
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(idx + 1);
                row.createCell(1).setCellValue(
                        emp.getEmployeeCode() != null
                                ? emp.getEmployeeCode()
                                : ""
                );
                row.createCell(2).setCellValue(
                        emp.getFullName() != null
                                ? emp.getFullName()
                                : ""
                );
                row.createCell(3).setCellValue(
                        emp.getEmail() != null
                                ? emp.getEmail()
                                : ""
                );
                row.createCell(4).setCellValue(
                        emp.getPhoneNumber() != null
                                ? emp.getPhoneNumber()
                                : ""
                );
                row.createCell(5).setCellValue(
                        emp.getGender() != null
                                ? emp.getGender().name()
                                : ""
                );
                row.createCell(6).setCellValue(
                        emp.getBirthDate() != null
                                ? emp.getBirthDate().format(DATE_FORMATTER)
                                : ""
                );
                row.createCell(7).setCellValue(
                        emp.getHireDate() != null
                                ? emp.getHireDate().format(DATE_FORMATTER)
                                : ""
                );
                row.createCell(8).setCellValue(
                        emp.getStatus() != null
                                ? emp.getStatus().name()
                                : ""
                );
                row.createCell(9).setCellValue(
                        emp.getDepartmentName() != null
                                ? emp.getDepartmentName()
                                : ""
                );
                row.createCell(10).setCellValue(
                        emp.getPositionName() != null
                                ? emp.getPositionName()
                                : ""
                );
            }

            for (int i = 0; i < EXCEL_HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            );
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=employees.xlsx"
            );

            workbook.write(response.getOutputStream());

            log.info(
                    "Excel export completed. Total employees: {}",
                    employees.size()
            );
        }
    }

    public List<String> importEmployeesFromExcel(
            InputStream inputStream
    ) {

        List<String> messages = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            int lastRowNum = sheet.getLastRowNum();

            if (lastRowNum < 1) {

                messages.add("Excel file is empty");
                return messages;
            }

            int successCount = 0;
            int errorCount = 0;

            for (int i = 1; i <= lastRowNum; i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                try {

                    String fullName = getCellValue(row, 2);
                    String email = getCellValue(row, 3);
                    String phone = getCellValue(row, 4);
                    String genderStr = getCellValue(row, 5);
                    String birthDateStr = getCellValue(row, 6);
                    String hireDateStr = getCellValue(row, 7);
                    String statusStr = getCellValue(row, 8);
                    String departmentName = getCellValue(row, 9);
                    String positionName = getCellValue(row, 10);

                    if (fullName == null || fullName.isBlank()) {

                        messages.add(
                                "Row " + (i + 1)
                                        + ": Full name is required"
                        );
                        errorCount++;
                        continue;
                    }

                    if (email == null || email.isBlank()) {

                        messages.add(
                                "Row " + (i + 1)
                                        + ": Email is required"
                        );
                        errorCount++;
                        continue;
                    }

                    Gender gender = null;

                    if (genderStr != null
                            && !genderStr.isBlank()) {

                        gender = Gender.valueOf(
                                genderStr.toUpperCase()
                        );
                    }

                    LocalDate birthDate = null;

                    if (birthDateStr != null
                            && !birthDateStr.isBlank()) {

                        birthDate = LocalDate.parse(
                                birthDateStr,
                                DATE_FORMATTER
                        );
                    }

                    LocalDate hireDate = null;

                    if (hireDateStr != null
                            && !hireDateStr.isBlank()) {

                        hireDate = LocalDate.parse(
                                hireDateStr,
                                DATE_FORMATTER
                        );
                    }

                    EmployeeStatus status = null;

                    if (statusStr != null
                            && !statusStr.isBlank()) {

                        status = EmployeeStatus.valueOf(
                                statusStr.toUpperCase()
                        );
                    }

                    DepartmentEntity department = null;

                    if (departmentName != null
                            && !departmentName.isBlank()) {

                        Long deptCount = cbf.create(
                                        em,
                                        Long.class
                                )
                                .from(DepartmentEntity.class)
                                .select("COUNT(id)")
                                .where("name")
                                .eq(departmentName)
                                .getSingleResult();

                        if (deptCount > 0) {

                            department = cbf.create(
                                            em,
                                            DepartmentEntity.class
                                    )
                                    .where("name")
                                    .eq(departmentName)
                                    .getSingleResult();
                        }
                    }

                    PositionEntity position = null;

                    if (positionName != null
                            && !positionName.isBlank()) {

                        Long posCount = cbf.create(
                                        em,
                                        Long.class
                                )
                                .from(PositionEntity.class)
                                .select("COUNT(id)")
                                .where("name")
                                .eq(positionName)
                                .getSingleResult();

                        if (posCount > 0) {

                            position = cbf.create(
                                            em,
                                            PositionEntity.class
                                    )
                                    .where("name")
                                    .eq(positionName)
                                    .getSingleResult();
                        }
                    }

                    EmployeeEntity employee =
                            EmployeeEntity.builder()
                                    .employeeCode(
                                            generateEmployeeCode()
                                    )
                                    .fullName(fullName)
                                    .email(email)
                                    .phoneNumber(phone)
                                    .gender(gender)
                                    .birthDate(birthDate)
                                    .hireDate(hireDate)
                                    .status(
                                            status != null
                                                    ? status
                                                    : EmployeeStatus.ACTIVE
                                    )
                                    .department(department)
                                    .position(position)
                                    .createdAt(
                                            LocalDateTime.now()
                                    )
                                    .updatedAt(
                                            LocalDateTime.now()
                                    )
                                    .build();

                    em.persist(employee);

                    successCount++;

                    messages.add(
                            "Row " + (i + 1)
                                    + ": Imported - "
                                    + fullName
                    );

                } catch (IllegalArgumentException e) {

                    messages.add(
                            "Row " + (i + 1)
                                    + ": Invalid data - "
                                    + e.getMessage()
                    );
                    errorCount++;

                } catch (Exception e) {

                    messages.add(
                            "Row " + (i + 1)
                                    + ": Error - "
                                    + e.getMessage()
                    );
                    errorCount++;
                }
            }

            messages.add(
                    "Import completed. Success: "
                            + successCount
                            + ", Errors: "
                            + errorCount
            );

            log.info(
                    "Excel import completed. Success: {}, Errors: {}",
                    successCount,
                    errorCount
            );

        } catch (IOException e) {

            messages.add(
                    "Failed to read Excel file: "
                            + e.getMessage()
            );

            log.error("Excel import failed", e);
        }

        return messages;
    }

    public void exportEmployeesToPdf(
            HttpServletResponse response
    ) throws DocumentException, IOException {

        List<EmployeeView> employees = getAllEmployees();

        Document document = new Document(
                PageSize.A4.rotate(),
                20,
                20,
                20,
                20
        );

        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=employees.pdf"
        );

        OutputStream outputStream =
                response.getOutputStream();

        PdfWriter.getInstance(document, outputStream);

        document.open();

        Font titleFont = new Font(Font.BOLD, 16);
        Font headerFont = new Font(Font.BOLD, 8);

        document.add(
                new Paragraph(
                        "Employee Report",
                        titleFont
                )
        );

        document.add(new Paragraph(" "));

        int numCols = EXCEL_HEADERS.length;

        PdfPTable table = new PdfPTable(numCols);
        table.setWidthPercentage(100);

        for (String header : EXCEL_HEADERS) {
            PdfPCell cell = new PdfPCell();
            cell.setPhrase(
                    new com.lowagie.text.Phrase(
                            header,
                            headerFont
                    )
            );
            cell.setHorizontalAlignment(
                    PdfPCell.ALIGN_CENTER
            );
            table.addCell(cell);
        }

        for (int idx = 0; idx < employees.size(); idx++) {

            EmployeeView emp = employees.get(idx);

            table.addCell(String.valueOf(idx + 1));
            table.addCell(
                    emp.getEmployeeCode() != null
                            ? emp.getEmployeeCode()
                            : ""
            );
            table.addCell(
                    emp.getFullName() != null
                            ? emp.getFullName()
                            : ""
            );
            table.addCell(
                    emp.getEmail() != null
                            ? emp.getEmail()
                            : ""
            );
            table.addCell(
                    emp.getPhoneNumber() != null
                            ? emp.getPhoneNumber()
                            : ""
            );
            table.addCell(
                    emp.getGender() != null
                            ? emp.getGender().name()
                            : ""
            );
            table.addCell(
                    emp.getBirthDate() != null
                            ? emp.getBirthDate()
                                    .format(DATE_FORMATTER)
                            : ""
            );
            table.addCell(
                    emp.getHireDate() != null
                            ? emp.getHireDate()
                                    .format(DATE_FORMATTER)
                            : ""
            );
            table.addCell(
                    emp.getStatus() != null
                            ? emp.getStatus().name()
                            : ""
            );
            table.addCell(
                    emp.getDepartmentName() != null
                            ? emp.getDepartmentName()
                            : ""
            );
            table.addCell(
                    emp.getPositionName() != null
                            ? emp.getPositionName()
                            : ""
            );
        }

        document.add(table);

        document.close();

        log.info(
                "PDF export completed. Total employees: {}",
                employees.size()
        );
    }
}
