package br.com.wifeleviro.ad.modelo;

import java.util.GregorianCalendar;
import java.util.Random;

public class Mensagem {

	public static final double TEMPO_TRANSMISAO_POR_QUADRO = 0.001; //segundos por quadro 
	public static final double TEMPO_TRANSMISSAO_REFORCO_COLISAO = 0.0000032; //segundos por reforco
	
	public static final int MENSAGEM_PADRAO = 0;
	public static final int REFORCO_COLISAO = 1;
	
	private long id;
	private double numeroQuadros;
	private int numeroQuadroRestantesParaTransmissao;
	private int tipoMensagem;
	
	public Mensagem(){
		setId(new GregorianCalendar().getTimeInMillis());
		this.setTipoMensagem(REFORCO_COLISAO);
		this.numeroQuadros = 1;
		this.numeroQuadroRestantesParaTransmissao = 0;
	}
	
	public Mensagem(double p){
		
		setId(new GregorianCalendar().getTimeInMillis());
		this.setTipoMensagem(MENSAGEM_PADRAO);
		
		if(p > 0 && p < 1){
			double q = 1 - p;
			Random r = new Random();
			this.numeroQuadros = Math.log(r.nextDouble()%1)/Math.log(q);
		}else{
			this.numeroQuadros = p;
		}
		this.numeroQuadroRestantesParaTransmissao = (int)this.numeroQuadros;
	}
	
	public double getNumeroQuadros() {
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

}
