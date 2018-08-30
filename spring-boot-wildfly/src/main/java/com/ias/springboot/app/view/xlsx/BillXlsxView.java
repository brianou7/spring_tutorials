package com.ias.springboot.app.view.xlsx;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import com.ias.springboot.app.models.Bill;
import com.ias.springboot.app.models.BillItem;

@Component("bill/view.xlsx")
public class BillXlsxView extends AbstractXlsxView{

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"invoice_view.xlsx\"");
		Bill bill = (Bill) model.get("bill");
		Sheet sheet = workbook.createSheet("Spring invoice");
		MessageSourceAccessor messages = getMessageSourceAccessor();
		
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(messages.getMessage("text.bill.view.data.client"));
		
		row = sheet.createRow(1);
		cell = row.createCell(0);
		cell.setCellValue(bill.getClient().getFirst_name().concat(" ").concat(bill.getClient().getLast_name()));
		
		row = sheet.createRow(2);
		cell = row.createCell(0);
		cell.setCellValue(bill.getClient().getEmail());
		
		sheet.createRow(4).createCell(0).setCellValue(messages.getMessage("text.bill.view.data.bill"));
		sheet.createRow(5).createCell(0).setCellValue(messages.getMessage("text.client.bill.number").concat(": ").concat(bill.getId().toString()));
		sheet.createRow(6).createCell(0).setCellValue(messages.getMessage("text.client.bill.description").concat(": ").concat(bill.getDescription()));
		sheet.createRow(7).createCell(0).setCellValue(messages.getMessage("text.client.bill.date").concat(": ").concat(bill.getCreatedAt().toString()));
		
		CellStyle theaderStyle = workbook.createCellStyle();
		theaderStyle.setBorderBottom(BorderStyle.MEDIUM);
		theaderStyle.setBorderTop(BorderStyle.MEDIUM);
		theaderStyle.setBorderLeft(BorderStyle.MEDIUM);
		theaderStyle.setBorderRight(BorderStyle.MEDIUM);
		
		CellStyle tbodyStyle = workbook.createCellStyle();
		tbodyStyle.setBorderTop(BorderStyle.THIN);
		tbodyStyle.setBorderBottom(BorderStyle.THIN);
		tbodyStyle.setBorderLeft(BorderStyle.THIN);
		tbodyStyle.setBorderRight(BorderStyle.THIN);
		theaderStyle.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());
		theaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		Row header = sheet.createRow(9);
		header.createCell(0).setCellValue(messages.getMessage("text.bill.form.item.name"));
		header.createCell(1).setCellValue(messages.getMessage("text.bill.form.item.price"));
		header.createCell(2).setCellValue(messages.getMessage("text.bill.form.item.amount"));
		header.createCell(3).setCellValue(messages.getMessage("text.bill.form.item.total"));
		
		int rowNumber = 10; 
		
		for (BillItem item: bill.getItems()) {
			Row rowItem = sheet.createRow(rowNumber ++);
			rowItem.createCell(0).setCellValue(item.getProduct().getName());
			rowItem.createCell(1).setCellValue(item.getProduct().getPrice());
			rowItem.createCell(2).setCellValue(item.getAmount());
			rowItem.createCell(3).setCellValue(item.calculateImport());
			
			for (int i = 0; i <= 3; i++) {
				rowItem.getCell(i).setCellStyle(tbodyStyle);
			}
		}
		
		for (int i = 0; i <= 3; i++) {
			header.getCell(i).setCellStyle(theaderStyle);
		}
		
		Row totalRow = sheet.createRow(rowNumber);
		cell = totalRow.createCell(2);
		cell.setCellValue(messages.getMessage("text.bill.form.total"));
		cell.setCellStyle(tbodyStyle);
		
		cell = totalRow.createCell(3);
		cell.setCellValue(bill.getTotal());
		cell.setCellStyle(tbodyStyle);
	}

}
