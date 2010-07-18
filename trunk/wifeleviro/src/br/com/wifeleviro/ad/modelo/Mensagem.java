package br.com.wifeleviro.ad.modelo;

import java.util.Random;

public class Mensagem {

	public static final double TEMPO_TRANSMISAO_POR_QUADRO = 0.001; //segundos por quadro 
	
	private double numeroQuadros;
	private int numeroQuadroRestantesParaTransmissao;
	
	public Mensagem(double p){
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
	
}
