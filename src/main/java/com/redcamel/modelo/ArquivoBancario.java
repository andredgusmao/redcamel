package com.redcamel.modelo;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.file.GenericFile;

public class ArquivoBancario {

	public boolean filtraZip(Exchange exchange) {
		Message payload = exchange.getIn();
		return payload.getBody(GenericFile.class).getFileName().endsWith("zip");
	}
}
