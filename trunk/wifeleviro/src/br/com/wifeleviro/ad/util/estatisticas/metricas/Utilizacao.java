package br.com.wifeleviro.ad.util.estatisticas.metricas;

public class Utilizacao {

	private Double inicio;
	private Double fim;
	
	public Utilizacao(double inicio, double fim){
		this.inicio = inicio;
		this.fim = fim;
	}

	public double getInicio() {
		return inicio;
	}

	public double getFim() {
		return fim;
	}
	
}
