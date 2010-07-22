package br.com.wifeleviro.ad.util;

public class ResultadoIntervaloDeConfianca {

	private double mediaDasAmostras;
	private double tamanhoDoIntervaloDeConfianca;
	
	protected ResultadoIntervaloDeConfianca(double mediaDasAmostras, double tamanhoDoIntervaloDeConfianca){
		this.mediaDasAmostras = mediaDasAmostras;
		this.tamanhoDoIntervaloDeConfianca = tamanhoDoIntervaloDeConfianca;
	}

	protected double getMediaDasAmostras() {
		return mediaDasAmostras;
	}

	protected double getTamanhoDoIntervaloDeConfianca() {
		return tamanhoDoIntervaloDeConfianca;
	}
	
}
