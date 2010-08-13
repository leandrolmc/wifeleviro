package br.com.wifeleviro.ad.util.estatisticas;

import java.util.ArrayList;
import java.util.Collection;

import br.com.wifeleviro.ad.util.estatisticas.metricas.NCm;
import br.com.wifeleviro.ad.util.estatisticas.metricas.TAm;
import br.com.wifeleviro.ad.util.estatisticas.metricas.TAp;

/*
 * Classe de transporte de todas as estatísticas coletas.
 */
public class EstatisticasColetadas {

	Collection<TAp> colTap;
	Collection<TAm> colTam;
	Collection<NCm> colNCm;
	Collection<EstatisticasUtilizacaoRodada> colEstatisticaUtilizacaoDaRodada; 
	Collection<EstatisticasVazaoRodada> colEstatisticaVazaoDaRodada;
	
	public EstatisticasColetadas(){
		this.colTap = new ArrayList<TAp>();
		this.colTam = new ArrayList<TAm>();
		this.colNCm = new ArrayList<NCm>();
		this.colEstatisticaUtilizacaoDaRodada = new ArrayList<EstatisticasUtilizacaoRodada>();
		this.colEstatisticaVazaoDaRodada = new ArrayList<EstatisticasVazaoRodada>();
	}
	
	public void armazenar(TAp tap, TAm tam, NCm colisao, EstatisticasUtilizacaoRodada utilizacao, EstatisticasVazaoRodada vazao){
		this.colTap.add(tap);  
		this.colTam.add(tam);
		this.colNCm.add(colisao);
		this.colEstatisticaUtilizacaoDaRodada.add(utilizacao); 
		this.colEstatisticaVazaoDaRodada.add(vazao);
	}
	
	public Collection<TAp> getColTap() {
		return colTap;
	}
	public Collection<TAm> getColTam() {
		return colTam;
	}
	public Collection<NCm> getColNCm() {
		return colNCm;
	}

	public Collection<EstatisticasUtilizacaoRodada> getColEstatisticaUtilizacaoDaRodada() {
		return colEstatisticaUtilizacaoDaRodada;
	}
	public Collection<EstatisticasVazaoRodada> getColEstatisticaVazaoDaRodada() {
		return colEstatisticaVazaoDaRodada;
	}
	
	
	
}
