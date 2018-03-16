package com.redcamel.modelo;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Scanner;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class ProcessaArquivoDebitoAutomatico implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		InputStream stream = exchange.getIn().getBody(InputStream.class); 
		Scanner scanner = new Scanner(stream);
		
		String header = scanner.nextLine();
		
		if(headerEhValido(header)) {
			String solicitante = scanner.nextLine();
			String agencia = scanner.nextLine();
			String conta = scanner.nextLine();
			String valor = scanner.nextLine();
			String hash = scanner.nextLine();
			ArquivoDebitoAutomatico arquivoDebitoAutomatico = 
					new ArquivoDebitoAutomatico(solicitante, agencia, conta, new BigDecimal(valor), hash);
			exchange.getOut().setBody(arquivoDebitoAutomatico.paraXml());
		}
		scanner.close();
	}

	private boolean headerEhValido(String header) {
		return true;
	}

}
