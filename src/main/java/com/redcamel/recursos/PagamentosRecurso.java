package com.redcamel.recursos;

import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;

import com.redcamel.modelo.Pagamento;

@Controller
@RequestMapping(value = "/api/pagamentos")
public class PagamentosRecurso {
	public static final Logger logger = LoggerFactory.getLogger(PagamentosRecurso.class);
	
	@Autowired
	ProducerTemplate producerTemplate;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<String> ping() {
		producerTemplate.sendBody("direct:ping", "Enviando ping para o Camel");
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseEntity<String> novo(@RequestBody Pagamento pagamento, UriComponentsBuilder ucBuilder) {
		logger.info("Criando Pagamento: {}", pagamento);
		pagamento.geraId();
		
		producerTemplate.sendBody("direct:rest-transacoes-bancarias", pagamento.toJson());
		
		HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/pagamentos/{id}").buildAndExpand(pagamento.getId()).toUri());
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}
	
}
