package br.com.wifeleviro.ad.util.estatisticas;

import java.util.Collection;

import br.com.wifeleviro.ad.util.estatisticas.metricas.Utilizacao;

/*
 * Classe de transporte das coletas de utilização durante a rodada.
 */
public class EstatisticasUtilizacaoRodada {

	private double inicioDaRodada;
	private double fimDaRodada;
	private Collection<Utilizacao> utilizacao;
	
	public EstatisticasUtilizacaoRodada(double inicioDaRodada, double fimDaRodada,
			Collection<Utilizacao> utilizacao) {
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

	public Collection<Utilizacao> getUtilizacao() {
		return utilizacao;
	}
	
}
