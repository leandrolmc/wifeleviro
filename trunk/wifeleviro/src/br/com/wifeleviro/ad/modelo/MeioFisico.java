package br.com.wifeleviro.ad.modelo;

public class MeioFisico {

	public static double calculaTempoPropagacao(double distancia){
		return ((5 * 0.000001) * distancia) / 1000;
	}
	
}
