package com.ias.springboot.app.view.csv;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.ias.springboot.app.models.Client;

@Component("client_list.csv")
public class ClientCsvView extends AbstractView {
	
	public ClientCsvView() {
		this.setContentType("text/csv");
	}

	@Override
	protected boolean generatesDownloadContent() {
		return true;
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"customers.csv\"");
		response.setContentType(this.getContentType());
		
		@SuppressWarnings("unchecked")
		Page<Client> clients = (Page<Client>) model.get("clients");
		
		ICsvBeanWriter beanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		
		String[] header = {"id", "first_name", "last_name", "email", "created_at"};
		beanWriter.writeHeader(header);
		
		for (Client client: clients) {
			beanWriter.write(client, header);
		}
		
		beanWriter.close();
	}

}
