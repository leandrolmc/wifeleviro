package br.com.wifeleviro.ad.util;

public class ResultadoIntervaloDeConfianca {

	private double mediaDasAmostras;
	private double tamanhoDoIntervaloDeConfianca;
	
	protected ResultadoIntervaloDeConfianca(double mediaDasAmostras, double tamanhoDoIntervaloDeConfianca){
		this.mediaDasAmostras = mediaDasAmostras;
		this.tamanhoDoIntervaloDeConfianca = tamanhoDoIntervaloDeConfianca;
	}

	public double getMediaDasAmostras() {
		return mediaDasAmostras;
	}

	public double getTamanhoDoIntervaloDeConfianca() {
		return tamanhoDoIntervaloDeConfianca;
	}
	
}
