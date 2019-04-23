package ru.psv4.beans;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ru.psv4.dto.DataSubtoalTerminalPayment;
import ru.psv4.dto.DataTerminalPaymentPrinting;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class ExcelSerializer {
    
    public XSSFWorkbook serialize(AntifrodEventParser.Data data) {
        XSSFWorkbook wb = new XSSFWorkbook();
        Styles styles = new Styles(wb);
        XSSFSheet sheet1 = wb.createSheet("Подитог(факт)-Безнал(факт)");
        XSSFSheet sheet2 = wb.createSheet("Безнал(факт)-ПечатьЧека(отпр задание)");
        createSubtotalTerminalPayment(sheet1, styles, data.data1);
        createTerminalPaymentPrinting(sheet2, styles, data.data2);
        return wb;
    }

    private class Styles {

        private XSSFWorkbook wb;
        
        private CellStyle boldStyle;
        private CellStyle dateStyle;

        public Styles(XSSFWorkbook wb) {
            this.wb = wb;
        }

        public CellStyle getBoldStyle() {
            if (boldStyle == null) {
                boldStyle = wb.createCellStyle();
                Font font = wb.createFont();
                font.setBold(true);
                boldStyle.setFont(font);
            }
            return boldStyle;
        }

        public CellStyle getDatetimeStyle() {
            if (dateStyle == null) {
                dateStyle = wb.createCellStyle();
                CreationHelper createHelper = wb.getCreationHelper();
                dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd.MM.yyyy HH:mm:ss"));
            }
            return dateStyle;
        }
    }

    private void createSubtotalTerminalPayment(XSSFSheet sheet, Styles styles, List<DataSubtoalTerminalPayment> elements) {
        int rowNum = 0;
        XSSFRow headerRow = sheet.createRow(0);
        List<String> cols = Arrays.asList("Station", "Checknum", "Datetime1", "Datetime2", "Time sec", "Client-card");
        addStringCells(headerRow, cols, styles.getBoldStyle());
        sheet.createFreezePane(0, 1);
        for (DataSubtoalTerminalPayment el : elements) {
            addCells(el, sheet.createRow(++rowNum), styles);
        }
        for (int i = 0; i < cols.size(); ++i) sheet.autoSizeColumn(i);
    }

    private void createTerminalPaymentPrinting(XSSFSheet sheet, Styles styles, List<DataTerminalPaymentPrinting> elements) {
        int rowNum = 0;
        XSSFRow headerRow = sheet.createRow(0);
        List<String> cols = Arrays.asList("Station", "Checknum", "Datetime1", "Datetime2", "Time sec");
        addStringCells(headerRow, cols, styles.getBoldStyle());
        sheet.createFreezePane(0, 1);
        for (DataTerminalPaymentPrinting el : elements) {
            addCells(el, sheet.createRow(++rowNum), styles);
        }
        for (int i = 0; i < cols.size(); ++i) sheet.autoSizeColumn(i);
    }


    private static void addCells(DataSubtoalTerminalPayment el, XSSFRow row, Styles styles) {
        Cell cell0 = row.createCell(0, CellType.STRING);
        cell0.setCellValue(el.station);

        Cell cell1 = row.createCell(1, CellType.STRING);
        cell1.setCellValue(el.checkNum);

        Cell cell2 = row.createCell(2, CellType.NUMERIC);
        Calendar datetime1 = GregorianCalendar.from(el.datetime1.atZone(ZoneId.systemDefault()));
        cell2.setCellValue(datetime1);
        cell2.setCellStyle(styles.getDatetimeStyle());

        Cell cell3 = row.createCell(3, CellType.NUMERIC);
        Calendar datetime2 = GregorianCalendar.from(el.datetime2.atZone(ZoneId.systemDefault()));
        cell3.setCellValue(datetime2);
        cell3.setCellStyle(styles.getDatetimeStyle());

        Cell cell4 = row.createCell(4, CellType.NUMERIC);
        cell4.setCellValue((double)el.getTime());

        Cell cell5 = row.createCell(5, CellType.STRING);
        cell5.setCellValue(el.clientCard);
    }

    private static void addCells(DataTerminalPaymentPrinting el, XSSFRow row, Styles styles) {
        Cell cell0 = row.createCell(0, CellType.STRING);
        cell0.setCellValue(el.station);

        Cell cell1 = row.createCell(1, CellType.STRING);
        cell1.setCellValue(el.checkNum);

        Cell cell2 = row.createCell(2, CellType.NUMERIC);
        Calendar datetime1 = GregorianCalendar.from(el.datetime1.atZone(ZoneId.systemDefault()));
        cell2.setCellValue(datetime1);
        cell2.setCellStyle(styles.getDatetimeStyle());

        Cell cell3 = row.createCell(3, CellType.NUMERIC);
        Calendar datetime2 = GregorianCalendar.from(el.datetime2.atZone(ZoneId.systemDefault()));
        cell3.setCellValue(datetime2);
        cell3.setCellStyle(styles.getDatetimeStyle());

        Cell cell4 = row.createCell(4, CellType.NUMERIC);
        cell4.setCellValue((double)el.getTime());
    }

    private static void addStringCells(XSSFRow row, List<String> strings, CellStyle style) {
        for (int i = 0; i < strings.size(); i++) {
            Cell cell = row.createCell(i, CellType.STRING);
            cell.setCellValue(strings.get(i));
            cell.setCellStyle(style);
        }
    }
}
