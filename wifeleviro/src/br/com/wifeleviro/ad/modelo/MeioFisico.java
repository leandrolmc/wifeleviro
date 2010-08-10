package br.com.wifeleviro.ad.modelo;

/*
 * Classe responsável por calcular o atraso de transmissão do quadro no meio.
 */
public class MeioFisico {

	// Calcula o atraso de transmissão no meio de acordo com a distância informada.
	public static double calculaTempoPropagacao(double distancia){
		return (double)(((5 * 0.000001) * distancia) / 1000);
	}
	
}
