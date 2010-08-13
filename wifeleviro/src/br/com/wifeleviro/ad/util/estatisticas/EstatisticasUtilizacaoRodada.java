package br.com.wifeleviro.ad.util.estatisticas;


/*
 * Classe de transporte das coletas de utilização durante a rodada.
 */
public class EstatisticasUtilizacaoRodada {

	private double inicioDaRodada;
	private double fimDaRodada;
	private double utilizacao;
	
	public EstatisticasUtilizacaoRodada(double inicioDaRodada, double fimDaRodada, double utilizacao) {
		super();
		this.inicioDaRodada = inicioDaRodada;
		this.fimDaRodada = fimDaRodada;
		this.utilizacao = utilizacao;
	}

	public double getInicioDaRodada() {
		return inicioDaRodada;
	}

	public double getFimDaRodada() {
		return fimDaRodada;
	}

	public double getUtilizacao() {
		return utilizacao;
	}
	
}
