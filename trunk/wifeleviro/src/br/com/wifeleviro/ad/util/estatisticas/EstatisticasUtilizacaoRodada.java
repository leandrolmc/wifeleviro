package br.com.wifeleviro.ad.util.estatisticas;

import java.util.Collection;

public class EstatisticasUtilizacaoRodada {

	private double inicioDaRodada;
	private double fimDaRodada;
	private Collection<Double> medicaoInstanteUtilizacao;
	
	public EstatisticasUtilizacaoRodada(double inicioDaRodada, double fimDaRodada,
			Collection<Double> medicaoInstanteUtilizacao) {
		super();
		this.inicioDaRodada = inicioDaRodada;
		this.fimDaRodada = fimDaRodada;
		this.medicaoInstanteUtilizacao = medicaoInstanteUtilizacao;
	}

	public double getInicioDaRodada() {
		return inicioDaRodada;
	}

	public double getFimDaRodada() {
		return fimDaRodada;
	}

	public Collection<Double> getMedicaoInstanteUtilizacao() {
		return medicaoInstanteUtilizacao;
	}
	
}
