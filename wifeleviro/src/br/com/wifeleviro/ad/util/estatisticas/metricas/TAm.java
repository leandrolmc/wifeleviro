package br.com.wifeleviro.ad.util.estatisticas.metricas;

/*
 * Amostra de TAm.
 */
public class TAm {

	private Double acumuladorTempo;
	private Long numeroMensagens;
	
	public TAm (){
		this.acumuladorTempo = (double)0;
		this.numeroMensagens = (long)0;
	}

	public void acumularTempo(Double tempoAAcumular) {
		this.acumuladorTempo += tempoAAcumular;
		++this.numeroMensagens;
	}

	public Double getAcumuladorTempo() {
		return acumuladorTempo;
	}

	public Long getNumeroMensagens() {
		return numeroMensagens;
	}

	
	
}