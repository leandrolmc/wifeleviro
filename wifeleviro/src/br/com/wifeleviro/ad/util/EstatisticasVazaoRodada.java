package br.com.wifeleviro.ad.util;


public class EstatisticasVazaoRodada {

	private double inicioDaRodada;
	private double fimDaRodada;
	private long numeroQuadrosTransmitidosComSucesso;
	
	public EstatisticasVazaoRodada(double inicioDaRodada, double fimDaRodada,
			long numeroQuadrosTransmitidosComSucesso) {
		super();
		this.inicioDaRodada = inicioDaRodada;
		this.fimDaRodada = fimDaRodada;
		this.numeroQuadrosTransmitidosComSucesso = numeroQuadrosTransmitidosComSucesso;
	}

	public double getInicioDaRodada() {
		return inicioDaRodada;
	}

	public double getFimDaRodada() {
		return fimDaRodada;
	}

	public long getNumeroQuadrosTransmitidosComSucesso() {
		return numeroQuadrosTransmitidosComSucesso;
	}
	
	
	
}
