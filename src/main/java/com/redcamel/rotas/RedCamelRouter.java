package com.redcamel.rotas;

import java.util.Iterator;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.dataformat.ZipFileDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.redcamel.modelo.ArquivoBancario;
import com.redcamel.modelo.ProcessaArquivoDebitoAutomatico;

@Component
public class RedCamelRouter extends RouteBuilder {
	@Value("${redcamel.ftp.url}")
	private String ftpUrl;

	@Value("${redcamel.direto.url}")
	private String diretoUrl;
	
	@Value("${redcamel.activemq.broker.url}")
	private String brokerUrl;
	
	@Autowired
	private CamelContext camel;
	
	@Override
	public void configure() throws Exception {
		camel.addComponent("activemq", ActiveMQComponent.activeMQComponent(brokerUrl));
		
		from("direct:ping")
		   .log("Corpo da mensagem: ${body}");
		
		from(ftpUrl)
	  	  .routeId("sftp-transacoes-bancarias")
	  	    .filter()
	  	      .method(new ArquivoBancario(), "filtraZip")
	  	        .log("Arquivo vindo do FTP: ${file:name} (${file:length} B)")
	  	          .unmarshal(zipDataFormat())
	  	            .split(bodyAs(Iterator.class))
	                    .streaming()
	                      .convertBodyTo(String.class)
	      	              .to("seda:processa-arquivos")
	      	                .log("Arquivo ${file:name} entregue.");
		
		from("seda:processa-arquivos")
	  	  .routeId("activemq-sftp-transacoes-bancarias")
	  	    .process(new ProcessaArquivoDebitoAutomatico())
	  	      .log("Arquivo XML: ${body}")
	  	        .to("activemq:queue:DebitoAutomaticoQueue");
		
		
		from("direct:rest-transacoes-bancarias")
		   .log("nova transacao bancaria: ${body}")
		   	.routeId("activemq-rest-transacoes-bancarias")
		   	  .to("activemq:queue:PagamentoQueue");
	}

	private DataFormatDefinition zipDataFormat() {
		ZipFileDataFormat zf = new ZipFileDataFormat();
		zf.setUsingIterator(true);
		return zf;
	}
}
