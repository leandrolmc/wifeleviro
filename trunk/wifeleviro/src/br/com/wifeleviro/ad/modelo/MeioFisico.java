package br.com.wifeleviro.ad.modelo;

/*
 * Classe respons�vel por calcular o atraso de transmiss�o do quadro no meio.
 */
public class MeioFisico {

	// Calcula o atraso de transmiss�o no meio de acordo com a dist�ncia informada.
	public static double calculaTempoPropagacao(double distancia){
		return (double)(((5 * 0.000001) * distancia) / 1000);
	}
	
}
