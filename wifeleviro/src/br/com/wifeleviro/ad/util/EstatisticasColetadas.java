package br.com.wifeleviro.ad.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

public class EstatisticasColetadas {

	Collection<Vector<Double>> colTap;
	Collection<Vector<Double>> colTam;
	Collection<EstatisticasColisaoRodada> colEstatisticaColisaoRodada;
	Collection<EstatisticasUtilizacaoRodada> colEstatisticaUtilizacaoDaRodada; 
	Collection<EstatisticasVazaoRodada> colEstatisticaVazaoDaRodada;
	
	public EstatisticasColetadas(){
		this.colTap = new ArrayList<Vector<Double>>();
		this.colTam = new ArrayList<Vector<Double>>();
		this.colEstatisticaColisaoRodada = new ArrayList<EstatisticasColisaoRodada>();
		this.colEstatisticaUtilizacaoDaRodada = new ArrayList<EstatisticasUtilizacaoRodada>();
		this.colEstatisticaVazaoDaRodada = new ArrayList<EstatisticasVazaoRodada>();
	}
	
	public void armazenar(Vector<Double> tap, Vector<Double> tam, EstatisticasColisaoRodada colisao, EstatisticasUtilizacaoRodada utilizacao, EstatisticasVazaoRodada vazao){
		this.colTap.add(tap);  
		this.colTam.add(tam);
		this.colEstatisticaColisaoRodada.add(colisao);
		this.colEstatisticaUtilizacaoDaRodada.add(utilizacao); 
		this.colEstatisticaVazaoDaRodada.add(vazao);
	}
	
	public Collection<Vector<Double>> getColTap() {
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
