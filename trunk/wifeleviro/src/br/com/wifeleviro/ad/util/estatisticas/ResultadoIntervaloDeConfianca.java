package br.com.wifeleviro.ad.util.estatisticas;

/*
 * Classe gen�rica para armazenar um resultado de 
 * c�lculo do intervalo de confian�a, guardando sempre
 * um par m�dia + tamanho do IC.
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
