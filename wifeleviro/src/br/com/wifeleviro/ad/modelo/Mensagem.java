package br.com.wifeleviro.ad.modelo;

import java.util.Random;

public class Mensagem {

	public static final double TEMPO_TRANSMISAO_POR_QUADRO = 0.001; //segundos por quadro 
	
	private double numeroQuadros;
	
	public Mensagem(double p){
		if(p > 0 && p < 1){
			double q = 1 - p;
			Random r = new Random();
			this.setNumeroQuadros(Math.log(r.nextDouble()%1)/Math.log(q));
		}else{
			this.setNumeroQuadros(p);
		}
	}

	public void setNumeroQuadros(double numeroQuadros) {
		this.numeroQuadros = numeroQuadros;
	}

	public double getNumeroQuadros() {
		return numeroQuadros;
	}
	
	
	
}
