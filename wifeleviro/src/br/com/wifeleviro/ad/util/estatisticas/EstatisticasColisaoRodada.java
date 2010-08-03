package br.com.wifeleviro.ad.util.estatisticas;

import java.util.Hashtable;

public class EstatisticasColisaoRodada {

	private Hashtable<Long,Long> colisoes;
	private Hashtable<Long,Long> quadros;
	public EstatisticasColisaoRodada(Hashtable<Long, Long> colisoes,
			Hashtable<Long, Long> quadros) {
		super();
		this.colisoes = colisoes;
		this.quadros = quadros;
	}
	public Hashtable<Long, Long> getColisoes() {
		return colisoes;
	}
	public Hashtable<Long, Long> getQuadros() {
		return quadros;
	}
	
}
