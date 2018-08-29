package com.ias.springboot.app.view.pdf;

import java.awt.Color;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.descriptor.LocalResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.ias.springboot.app.models.Bill;
import com.ias.springboot.app.models.BillItem;
import com.lowagie.text.Document;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Component("bill/view")
public class BillPdfView extends AbstractPdfView {
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private LocaleResolver localeResolver;

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Bill bill = (Bill) model.get("bill");
		Locale locale = localeResolver.resolveLocale(request);
		MessageSourceAccessor messages = getMessageSourceAccessor();
		
		PdfPTable clientTable = new PdfPTable(1);
		clientTable.setSpacingAfter(20);
		
		PdfPCell cell = new PdfPCell(new Phrase(messageSource.getMessage("text.bill.view.data.client", null, locale)));
		cell.setBackgroundColor(new Color(184, 218, 255));
		cell.setPadding(8f);
		clientTable.addCell(cell);
		clientTable.addCell(bill.getClient().getFirst_name() + " " + bill.getClient().getLast_name());
		clientTable.addCell(bill.getClient().getEmail());
		
		PdfPTable billTable = new PdfPTable(1);
		billTable.setSpacingAfter(20);
		
		PdfPCell cell1 = new PdfPCell(new Phrase(messageSource.getMessage("text.bill.view.data.bill", null, locale)));
		cell1.setBackgroundColor(new Color(195, 230, 203));
		cell1.setPadding(8f);
		billTable.addCell(cell1);
		billTable.addCell(messages.getMessage("text.client.bill.number") + ": " + bill.getId());
		billTable.addCell(messages.getMessage("text.client.bill.description") + ": " + bill.getDescription());
		billTable.addCell(messages.getMessage("text.client.bill.date") + ": " + bill.getCreatedAt());
		
		PdfPTable itemsTable = new PdfPTable(4);
		itemsTable.setWidths(new float[] {3.5f, 1, 1, 1});
		itemsTable.addCell(messages.getMessage("text.bill.form.item.name"));
		itemsTable.addCell(messages.getMessage("text.bill.form.item.price"));
		itemsTable.addCell(messages.getMessage("text.bill.form.item.amount"));
		itemsTable.addCell(messages.getMessage("text.bill.form.item.total"));
		
		for (BillItem item: bill.getItems()) {
			itemsTable.addCell(item.getProduct().getName());
			itemsTable.addCell(item.getProduct().getPrice().toString());
			PdfPCell cell2 = new PdfPCell(new Phrase(item.getAmount().toString()));
			cell2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			itemsTable.addCell(cell2);
			itemsTable.addCell(item.calculateImport().toString());
		}
		
		PdfPCell cell11 = new PdfPCell(new Phrase(messages.getMessage("text.bill.form.total") + ": "));
		cell11.setColspan(3);
		cell11.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		
		itemsTable.addCell(cell11);
		itemsTable.addCell(bill.getTotal().toString());
		
		document.add(clientTable);
		document.add(billTable);
		document.add(itemsTable);
	}

	
}
