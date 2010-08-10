package br.com.wifeleviro.ad.util.estatisticas.metricas;

/*
 * Amostra de TAp.
 */
public class TAp {

	private Double acumuladorTempo;
	private Long numeroQuadros;
	
	public TAp (){
		this.acumuladorTempo = (double)0;
		this.numeroQuadros = (long)0;
	}

	public void acumularTempo(Double tempoAAcumular) {
		this.acumuladorTempo += tempoAAcumular;
		++this.numeroQuadros;
	}

	public Double getAcumuladorTempo() {
		return acumuladorTempo;
	}

	public Long getNumeroQuadros() {
		return numeroQuadros;
	}

	
	
}