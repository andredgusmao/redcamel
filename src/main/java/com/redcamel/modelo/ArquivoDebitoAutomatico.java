package com.redcamel.modelo;

import java.io.StringWriter;
import java.math.BigDecimal;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ArquivoDebitoAutomatico {

	private String solicitante;
	private String agencia;
	private String conta;
	private BigDecimal valor;
	private String hash;

	public ArquivoDebitoAutomatico(String solicitante, String agencia, String conta, BigDecimal valor, String hash) {
		this.solicitante = solicitante;
		this.agencia = agencia;
		this.conta = conta;
		this.valor = valor;
		this.hash = hash;
	}
	
	public String paraXml() throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("debitoAutomatico");
		doc.appendChild(rootElement);
		
		Element noAgencia = doc.createElement("agencia");
		noAgencia.appendChild(doc.createTextNode(this.agencia));

		Element noSolicitante = doc.createElement("solicitante");
		noSolicitante.appendChild(doc.createTextNode(this.solicitante));

		Element noConta = doc.createElement("conta");
		noConta.appendChild(doc.createTextNode(this.conta));
		
		Element noValor = doc.createElement("valor");
		noValor.appendChild(doc.createTextNode(this.valor.toString()));

		Element noHash = doc.createElement("hash");
		noHash.appendChild(doc.createTextNode(this.hash));
		
		rootElement.appendChild(noSolicitante);
		rootElement.appendChild(noAgencia);
		rootElement.appendChild(noConta);
		rootElement.appendChild(noValor);
		rootElement.appendChild(noHash);
		
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		
		String xml = writer.toString();
		
		return xml;
	}
}
