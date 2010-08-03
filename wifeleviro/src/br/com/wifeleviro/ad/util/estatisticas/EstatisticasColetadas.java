package br.com.wifeleviro.ad.util.estatisticas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;

import br.com.wifeleviro.ad.util.estatisticas.metricas.TAp;

public class EstatisticasColetadas {

	Collection<Hashtable<Long, TAp>> colTap;
	Collection<Vector<Double>> colTam;
	Collection<EstatisticasColisaoRodada> colEstatisticaColisaoRodada;
	Collection<EstatisticasUtilizacaoRodada> colEstatisticaUtilizacaoDaRodada; 
	Collection<EstatisticasVazaoRodada> colEstatisticaVazaoDaRodada;
	
	public EstatisticasColetadas(){
		this.colTap = new ArrayList<Hashtable<Long, TAp>>();
		this.colTam = new ArrayList<Vector<Double>>();
		this.colEstatisticaColisaoRodada = new ArrayList<EstatisticasColisaoRodada>();
		this.colEstatisticaUtilizacaoDaRodada = new ArrayList<EstatisticasUtilizacaoRodada>();
		this.colEstatisticaVazaoDaRodada = new ArrayList<EstatisticasVazaoRodada>();
	}
	
	public void armazenar(Hashtable<Long, TAp> tap, Vector<Double> tam, EstatisticasColisaoRodada colisao, EstatisticasUtilizacaoRodada utilizacao, EstatisticasVazaoRodada vazao){
		this.colTap.add(tap);  
		this.colTam.add(tam);
		this.colEstatisticaColisaoRodada.add(colisao);
		this.colEstatisticaUtilizacaoDaRodada.add(utilizacao); 
		this.colEstatisticaVazaoDaRodada.add(vazao);
	}
	
	public Collection<Hashtable<Long, TAp>> getColTap() {
		return colTap;
	}
	public Collection<Vector<Double>> getColTam() {
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
