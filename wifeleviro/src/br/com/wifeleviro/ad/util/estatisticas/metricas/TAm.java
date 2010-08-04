package br.com.wifeleviro.ad.util.estatisticas.metricas;

/*
 * Amostra de TAm.
 */
public class TAm {

	private Double instanteTempoInicial;
	private Double instanteTempoFinal;
	
	public TAm (){
		this.instanteTempoInicial = null;
		this.instanteTempoFinal = null;
	}

	public void setInstanteTempoInicial(double instanteTempoInicial) {
		if(this.instanteTempoInicial == null)
			this.instanteTempoInicial = instanteTempoInicial;
		else
			this.instanteTempoInicial = Math.min(this.instanteTempoInicial, instanteTempoInicial);
	}

	public double getInstanteTempoInicial() {
		return instanteTempoInicial;
	}

	public void setInstanteTempoFinal(double instanteTempoFinal) {
		if(this.instanteTempoFinal == null)
			this.instanteTempoFinal = instanteTempoFinal;
		else
			this.instanteTempoFinal = Math.max(this.instanteTempoFinal, instanteTempoFinal);
	}

	public Double getInstanteTempoFinal() {
		return instanteTempoFinal;
	}
	
}
