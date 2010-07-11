package br.com.wifeleviro.ad.modelo;

public class Link {
	private Porta portaA;
	private Porta portaB;
	
	private long distancia;
	
	public Link(Porta portaA, Porta portaB, long distancia) {
		this.portaA = portaA;
		this.portaB = portaB;
		this.distancia = distancia;
		portaA.setLink(this);
		portaB.setLink(this);
	}

	public Porta getPortaA() {
		return portaA;
	}

	public Porta getPortaB() {
		return portaB;
	}

	public long getDistancia() {
		return distancia;
	}

	public void setDistancia(long distancia) {
		this.distancia = distancia;
	}
	
}
