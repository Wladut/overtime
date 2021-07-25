package com.overtime;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Vrinceanu Vladut 22-05-2021
 * Time 17:12
 */

/*
    Class which create / read additional excel file with overtime history
    and sum overall overtime
 */

public class WritingExcelOvertimeStatus {

    private final String FILE_NAME= "\\Status_Overtime.xlsx";
    private final String SHEET_NAME = "Overtime";
    private Workbook workbook;
    private Sheet sheet;
    private Row row;
    private Cell cell;
    private File file;
    private int lastRowNumber;

    WritingExcelOvertimeStatus(String path) {
        String fullPathWithFileNameIncluded = path + FILE_NAME;
        file = new File(fullPathWithFileNameIncluded);
        System.out.println(fullPathWithFileNameIncluded);
        if (!file.exists()) {
            try {
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet(SHEET_NAME);
                FileOutputStream fileOut = new FileOutputStream(fullPathWithFileNameIncluded);
                setCellVal(2,1,"Date");
                setCellVal(2, 2,"Name");
                setCellVal(2,5, "Overall");
                createRowBorders(5, 500, 1, 5);
                workbook.write(fileOut);
                fileOut.close();
            } catch (IOException exception) {
                System.out.println("File wasn't created because: " + exception.getMessage());
            }
        }
        lastRowNumber();
    }

    double lastOverallValue(){
        if(lastRowNumber == 0 || lastRowNumber == 5){
            return 0;
        }
        String cellValue = getCellValue(sheet.getRow(lastRowNumber - 1).getCell(5));
        try{
            return Double.parseDouble(cellValue);
        } catch (NumberFormatException e) {}
        return 0;
    }

    boolean writeLine(double overtime, double overall){
        LocalDate localDate = LocalDateTime.now().toLocalDate();
        String dateToWrite = localDate.format(DateTimeFormatter.ofPattern("dd.MM.YY"));
        int month = localDate.getMonthValue();
        int year = localDate.getYear();
        boolean monthExists = monthExists(month, year);

        try {
            OutputStream outputStream =  new FileOutputStream(file);
            sheet = workbook.getSheet(SHEET_NAME);
            row = sheet.getRow(lastRowNumber);
            CellStyle style = workbook.createCellStyle();
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);

            if(!monthExists){
                row = sheet.getRow(lastRowNumber + 1);
                row.createCell(0);
                row.getCell(0).setCellValue(month + "." + year);
            }
            try{
                row.createCell(1); row.createCell(2);
                row.createCell(3); row.createCell(4); row.createCell(5);
                row.getCell(1).setCellStyle(style);
                row.getCell(2).setCellStyle(style); row.getCell(3).setCellStyle(style);
                row.getCell(4).setCellStyle(style); row.getCell(5).setCellStyle(style);
            } catch (Exception e){}
            row.getCell(1).setCellValue(dateToWrite);
            row.getCell(2).setCellValue("overtime");
            row.getCell(3).setCellValue(overtime);
            row.getCell(4).setCellValue("");
            row.getCell(5).setCellValue(overall);
            workbook.write(outputStream);
            outputStream.close();
        } catch (IOException exception){
            System.out.println(exception.getMessage());
            return false;}
        return false;
    }

    boolean monthExists(int month, int year){
        String monthAndYear = month + "." + year;
        for(int rowNumber  = 5; rowNumber <= lastRowNumber; rowNumber++){
            try {
                if (monthAndYear.equals(getCellValue(sheet.getRow(rowNumber).getCell(0)))) {
                    return true;
                }
            } catch (NullPointerException ignore){
                System.out.println(ignore.getMessage());
            }
        }
        return false;
    }

     private void lastRowNumber() {
        lastRowNumber = 0;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            workbook = new XSSFWorkbook(inputStream);
            sheet = workbook.getSheet(SHEET_NAME);
            if(getCellValue(sheet.getRow(5).getCell(1)).isEmpty()){
                lastRowNumber =  5;
            }
            for(int rowNumber = 5; rowNumber <= sheet.getLastRowNum(); rowNumber ++){
                String firstString = getCellValue(sheet.getRow(rowNumber).getCell(1));
                String secondString = getCellValue(sheet.getRow(rowNumber + 1).getCell(1));
                if(firstString.isEmpty() && secondString.isEmpty()){
                    lastRowNumber = rowNumber;
                    break;
                }
            }
            inputStream.close();
        } catch (IOException ignored){
            System.out.println(ignored.getMessage());
        }
    }

    private void setCellVal(int rowNumber, int columnNumber, String cellValue){
        if(sheet.getRow(rowNumber) == null){
            row = sheet.createRow(rowNumber);
        }
        row = sheet.getRow(rowNumber);
        cell = row.createCell(columnNumber);
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        cell.setCellStyle(style);
        cell.setCellValue(cellValue);
    }

    private void createRowBorders(int startRowNumber, int stopRowNumber, int startColumnNumber, int stopColumnNumber) {
        for (int rowNumber = startRowNumber; rowNumber <= stopRowNumber; rowNumber++) {
            if (sheet.getRow(rowNumber) == null) {
                row = sheet.createRow(rowNumber);
            }
            row = sheet.getRow(rowNumber);
            CellStyle style = workbook.createCellStyle();
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            cell = row.createCell(startColumnNumber - 1, CellType.STRING);
            for (int actualColumn = startColumnNumber; actualColumn <= stopColumnNumber; actualColumn++) {
                if(actualColumn == 1 || actualColumn == 2 || actualColumn == 4){
                    cell = row.createCell(actualColumn, CellType.STRING);
                } else {
                    cell = row.createCell(actualColumn, CellType.NUMERIC);
                }

                cell.setCellStyle(style);
            }
        }
    }

    private String getCellValue(Cell cell) {
        if(cell == null){
            return "";
        }
        switch (cell.getCellTypeEnum()) {
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case STRING:
                return cell.getRichStringCellValue().getString();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return Double.toString(cell.getNumericCellValue());
                }
            case FORMULA:
                switch(cell.getCachedFormulaResultTypeEnum()){
                    case BOOLEAN:
                        return Boolean.toString(cell.getBooleanCellValue());
                    case STRING:
                        return cell.getRichStringCellValue().getString();
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            return cell.getDateCellValue().toString();
                        } else {
                            return Double.toString(cell.getNumericCellValue());
                        }
                    case BLANK:
                        return "";
                }
                return "";
            default:
                return "";
        }
    }
}
