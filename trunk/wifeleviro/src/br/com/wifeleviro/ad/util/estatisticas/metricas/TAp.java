package br.com.wifeleviro.ad.util.estatisticas.metricas;

/*
 * Amostra de TAp.
 */
public class TAp {

	private Double instanteTempoInicial;
	private Double instanteTempoFinal;
	
	public TAp (){
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
