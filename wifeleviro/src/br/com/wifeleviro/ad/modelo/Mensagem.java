package br.com.wifeleviro.ad.modelo;

import java.util.Random;

import br.com.wifeleviro.ad.util.GeradorRandomicoSingleton;

/*
 * Classe que representa uma mensagem.
 */
public class Mensagem {

	public static final double TEMPO_TRANSMISAO_POR_QUADRO = (double)0.0008; //segundos por quadro 
	public static final double TEMPO_TRANSMISSAO_REFORCO_COLISAO = (double)0.0000032; //segundos por reforco
	
	public static final int MENSAGEM_PADRAO = 0;
	public static final int REFORCO_COLISAO = 1;
	
	private long id;
	private int rodada;
	
	private long numeroQuadros;
	private int numeroQuadroRestantesParaTransmissao;
	private int tipoMensagem;
	
	private long numeroColisoes;
	
	private Double instanteTempoInicioAcesso;
	private Double instanteTempoFimAcesso;
	
	public Mensagem(int rodada){
		setId(GeradorRandomicoSingleton.getInstance().gerarProximoRandomicoAuxiliar());
		this.setTipoMensagem(REFORCO_COLISAO);
		this.numeroQuadros = 1;
		this.numeroQuadroRestantesParaTransmissao = 0;
		this.rodada = rodada;
	}
	
	public Mensagem(int rodada, double p){
		this.rodada = rodada;
		
		setId(GeradorRandomicoSingleton.getInstance().gerarProximoRandomicoAuxiliar());
		this.setTipoMensagem(MENSAGEM_PADRAO);
		
		this.numeroColisoes = 0;
		
		if(p > 0 && p < 1){
			double q = 1 - p;
			Random r = new Random();
			this.numeroQuadros = (long)(Math.log(r.nextDouble()%1)/Math.log(q));
		}else{
			this.numeroQuadros = (long)p;
		}
		this.numeroQuadroRestantesParaTransmissao = (int)this.numeroQuadros;
		
		this.instanteTempoInicioAcesso = null;
		this.instanteTempoFimAcesso = null;
	}
	
	public void setInstanteTempoInicioTxQuadro(double instanteTempo) {
		if(this.instanteTempoInicioAcesso == null)
			this.instanteTempoInicioAcesso = instanteTempo;
		this.instanteTempoFimAcesso = instanteTempo;
	}

	public double getInstanteTempoFimAcesso() {
		return instanteTempoFimAcesso;
	}

	public double getInstanteTempoInicioAcesso() {
		return instanteTempoInicioAcesso;
	}
	
	public void incNumeroColisoes(){
		++this.numeroColisoes;
	}
	
	public long getNumeroColisoes(){
		return this.numeroColisoes;
	}
	
	public long getNumeroQuadros() {
		return numeroQuadros;
	}

	public int getNumeroQuadroRestantesParaTransmissao() {
		return (int)numeroQuadroRestantesParaTransmissao;
	}
	
	public void decrementaNumeroQuadroRestantesParaTransmissao(){
		this.numeroQuadroRestantesParaTransmissao--;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setTipoMensagem(int tipoMensagem) {
		this.tipoMensagem = tipoMensagem;
	}

	public int getTipoMensagem() {
		return tipoMensagem;
	}

	public int getRodada(){
		return this.rodada;
	}

}
