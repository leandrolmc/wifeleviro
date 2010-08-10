package br.com.wifeleviro.ad.util.estatisticas;

import java.util.ArrayList;
import java.util.Collection;

import br.com.wifeleviro.ad.util.estatisticas.metricas.TAm;
import br.com.wifeleviro.ad.util.estatisticas.metricas.TAp;

/*
 * Classe de transporte de todas as estat�sticas coletas.
 */
public class EstatisticasColetadas {

	Collection<TAp> colTap;
	Collection<TAm> colTam;
	Collection<EstatisticasColisaoRodada> colEstatisticaColisaoRodada;
	Collection<EstatisticasUtilizacaoRodada> colEstatisticaUtilizacaoDaRodada; 
	Collection<EstatisticasVazaoRodada> colEstatisticaVazaoDaRodada;
	
	public EstatisticasColetadas(){
		this.colTap = new ArrayList<TAp>();
		this.colTam = new ArrayList<TAm>();
		this.colEstatisticaColisaoRodada = new ArrayList<EstatisticasColisaoRodada>();
		this.colEstatisticaUtilizacaoDaRodada = new ArrayList<EstatisticasUtilizacaoRodada>();
		this.colEstatisticaVazaoDaRodada = new ArrayList<EstatisticasVazaoRodada>();
	}
	
	public void armazenar(TAp tap, TAm tam, EstatisticasColisaoRodada colisao, EstatisticasUtilizacaoRodada utilizacao, EstatisticasVazaoRodada vazao){
		this.colTap.add(tap);  
		this.colTam.add(tam);
		this.colEstatisticaColisaoRodada.add(colisao);
		this.colEstatisticaUtilizacaoDaRodada.add(utilizacao); 
		this.colEstatisticaVazaoDaRodada.add(vazao);
	}
	
	public Collection<TAp> getColTap() {
		return colTap;
	}
	public Collection<TAm> getColTam() {
		return colTam;
	}
	public Collection<EstatisticasColisaoRodada> getColEstatisticaColisaoRodada() {
		return colEstatisticaColisaoRodada;
	}

	public Collection<EstatisticasUtilizacaoRodada> getColEstatisticaUtilizacaoDaRodada() {
		return colEstatisticaUtilizacaoDaRodada;
	}
	public Collection<EstatisticasVazaoRodada> getColEstatisticaVazaoDaRodada() {
		return colEstatisticaVazaoDaRodada;
	}
	
	
	
}
