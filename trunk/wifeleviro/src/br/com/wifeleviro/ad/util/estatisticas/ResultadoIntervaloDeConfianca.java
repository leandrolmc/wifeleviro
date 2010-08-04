package br.com.wifeleviro.ad.util.estatisticas;

/*
 * Classe genérica para armazenar um resultado de 
 * cálculo do intervalo de confiança, guardando sempre
 * um par média + tamanho do IC.
 */
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
