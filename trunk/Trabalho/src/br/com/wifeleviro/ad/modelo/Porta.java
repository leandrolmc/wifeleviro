package br.com.wifeleviro.ad.modelo;

import java.util.ArrayList;

public class Porta {
	private Link link;
	private ArrayList<Quadro> bufferRx;
	private ArrayList<Quadro> bufferTx;
	
	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	public void enviar(Quadro quadro) {
		// TODO Auto-generated method stub
		
	}
	
}
