package br.com.wifeleviro.ad.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class IntervaloDeConfianca {

	private static final double ASSINTOTICO_95 = 1.96;

	public static ResultadoIntervaloDeConfianca calculaTamanhoIntervaloConfiancaTap(
			Collection<Vector<Double>> tapsRodadas, int numRodadas) {

		double somaAmostras = 0;
		double mediaAmostras = 0;
		double somaVariancias = 0;
		double varianciaAmostras = 0;
		double mediasTapsDasRodadas[] = new double[numRodadas];

		for (Vector<Double> taps : tapsRodadas) {
			double somatorioTaps = 0;
			for (Double tap : taps)
				somatorioTaps += tap;
			double mediaTapsDaRodada = somatorioTaps / taps.size();
			somaAmostras += mediaTapsDaRodada;
		}

		mediaAmostras = somaAmostras / numRodadas;

		for (double mediaTapsDaRodada : mediasTapsDasRodadas) {
			double sigma = mediaAmostras - mediaTapsDaRodada;
			double variancia = sigma * sigma;
			somaVariancias += variancia;
		}

		varianciaAmostras = somaVariancias / (numRodadas - 1);

		double tamanhoIntervaloDeConfianca = 2 * Math.sqrt(varianciaAmostras) * ASSINTOTICO_95 / Math.sqrt(numRodadas);
		
		ResultadoIntervaloDeConfianca result = new ResultadoIntervaloDeConfianca(mediaAmostras, tamanhoIntervaloDeConfianca);
		return result;
	}

	public static ResultadoIntervaloDeConfianca calculaTamanhoIntervaloConfiancaTam(
			Collection<Vector<Double>> tamsRodadas, int numRodadas) {

		double somaAmostras = 0;
		double mediaAmostras = 0;
		double somaVariancias = 0;
		double varianciaAmostras = 0;
		double mediasTamsDasRodadas[] = new double[numRodadas];

		for (Vector<Double> tams : tamsRodadas) {
			double somatorioTams = 0;
			for (Double tam : tams)
				somatorioTams += tam;
			double mediaTamsDaRodada = somatorioTams / tams.size();
			somaAmostras += mediaTamsDaRodada;
		}

		mediaAmostras = somaAmostras / numRodadas;

		for (double mediaTamsDaRodada : mediasTamsDasRodadas) {
			double sigma = mediaAmostras - mediaTamsDaRodada;
			double variancia = sigma * sigma;
			somaVariancias += variancia;
		}

		varianciaAmostras = somaVariancias / (numRodadas - 1);

		double tamanhoIntervaloDeConfianca = 2 * Math.sqrt(varianciaAmostras) * ASSINTOTICO_95 / Math.sqrt(numRodadas);
		
		ResultadoIntervaloDeConfianca result = new ResultadoIntervaloDeConfianca(mediaAmostras, tamanhoIntervaloDeConfianca);
		return result;
	}

	public static ResultadoIntervaloDeConfianca calculaTamanhoIntervaloConfiancaNcm(
			Hashtable<Long, Long>[] colisoesPorMensagemDasRodadas,
			Hashtable<Long, Long>[] quadrosPorMensagemDasRodadas, int numRodadas) {

		double somaAmostras = 0;
		double mediaAmostras = 0;
		double somaVariancias = 0;
		double varianciaAmostras = 0;
		double mediasNcmsDasRodadas[] = new double[numRodadas];

		for (int i = 0; i < quadrosPorMensagemDasRodadas.length; i++) {
			Hashtable<Long, Long> quadrosPorMensagem = quadrosPorMensagemDasRodadas[i];
			Hashtable<Long, Long> colisoesPorMensagem = colisoesPorMensagemDasRodadas[i];

			double somatorioNcmPorMensagem = 0;

			Enumeration<Long> idMensagens = quadrosPorMensagem.keys();
			while (idMensagens.hasMoreElements()) {
				Long idMensagem = idMensagens.nextElement();
				Long numQuadrosPorMensagem = quadrosPorMensagem.get(idMensagem);
				Long numColisoesPorMensagem = colisoesPorMensagem
						.get(idMensagem);

				double razaoPorMensagem = numColisoesPorMensagem
						/ numQuadrosPorMensagem;

				somatorioNcmPorMensagem += razaoPorMensagem;
			}

			double ncmRodada = somatorioNcmPorMensagem
					/ quadrosPorMensagem.size();

			somaAmostras += ncmRodada;
		}

		mediaAmostras = somaAmostras / numRodadas;

		for (double mediaNcmsDaRodada : mediasNcmsDasRodadas) {
			double sigma = mediaAmostras - mediaNcmsDaRodada;
			double variancia = sigma * sigma;
			somaVariancias += variancia;
		}

		varianciaAmostras = somaVariancias / (numRodadas - 1);

		double tamanhoIntervaloDeConfianca = 2 * Math.sqrt(varianciaAmostras) * ASSINTOTICO_95 / Math.sqrt(numRodadas);
		
		ResultadoIntervaloDeConfianca result = new ResultadoIntervaloDeConfianca(mediaAmostras, tamanhoIntervaloDeConfianca);
		return result;
	}

	public static ResultadoIntervaloDeConfianca calculaTamanhoIntervaloConfiancaUtilizacaoDoEthernet(
			Collection<EstatisticasUtilizacaoRodada> estatisticasUtilizacaoDasRodadas,
			int numRodadas) {

		double somaAmostras = 0;
		double mediaAmostras = 0;
		double somaVariancias = 0;
		double varianciaAmostras = 0;
		double utilizacaoDasRodadas[] = new double[numRodadas];

		Iterator<EstatisticasUtilizacaoRodada> it = estatisticasUtilizacaoDasRodadas.iterator();
		for (int i = 0; it.hasNext(); i++) {
			EstatisticasUtilizacaoRodada estatisticaUtilizacaoDaRodada = (EstatisticasUtilizacaoRodada) it.next();

			double periodoOcupado = 0;
			Collection<Double> medicoes = estatisticaUtilizacaoDaRodada
					.getMedicaoInstanteUtilizacao();
			for (Double medicao : medicoes) {
				periodoOcupado += medicao;
			}
			utilizacaoDasRodadas[i] = periodoOcupado / estatisticaUtilizacaoDaRodada.getFimDaRodada() - estatisticaUtilizacaoDaRodada.getInicioDaRodada();

			somaAmostras += utilizacaoDasRodadas[i];
		}

		mediaAmostras = somaAmostras / numRodadas;

		for (double utilizacaoDaRodada : utilizacaoDasRodadas) {
			double sigma = mediaAmostras - utilizacaoDaRodada;
			double variancia = sigma * sigma;
			somaVariancias += variancia;
		}

		varianciaAmostras = somaVariancias / (numRodadas - 1);

		double tamanhoIntervaloDeConfianca = 2 * Math.sqrt(varianciaAmostras) * ASSINTOTICO_95 / Math.sqrt(numRodadas);
		
		ResultadoIntervaloDeConfianca result = new ResultadoIntervaloDeConfianca(mediaAmostras, tamanhoIntervaloDeConfianca);
		return result;
	}

	public static ResultadoIntervaloDeConfianca calculaTamanhoIntervaloConfiancaVazao(
			Collection<EstatisticasVazaoRodada> estatisticasVazaoDasRodadas,
			int numRodadas) {

		double somaAmostras = 0;
		double mediaAmostras = 0;
		double somaVariancias = 0;
		double varianciaAmostras = 0;
		double vazaoDasRodadas[] = new double[numRodadas];

		Iterator<EstatisticasVazaoRodada> it = estatisticasVazaoDasRodadas.iterator();
		for (int i = 0; it.hasNext(); i++) {
			EstatisticasVazaoRodada estatisticaVazaoDaRodada = (EstatisticasVazaoRodada) it.next();

			double numQuadros = estatisticaVazaoDaRodada.getNumeroQuadrosTransmitidosComSucesso();
			vazaoDasRodadas[i] = numQuadros / estatisticaVazaoDaRodada.getFimDaRodada() - estatisticaVazaoDaRodada.getInicioDaRodada();

			somaAmostras += vazaoDasRodadas[i];
		}

		mediaAmostras = somaAmostras / numRodadas;

		for (double vazaoDaRodada : vazaoDasRodadas) {
			double sigma = mediaAmostras - vazaoDaRodada;
			double variancia = sigma * sigma;
			somaVariancias += variancia;
		}

		varianciaAmostras = somaVariancias / (numRodadas - 1);

		double tamanhoIntervaloDeConfianca = 2 * Math.sqrt(varianciaAmostras) * ASSINTOTICO_95 / Math.sqrt(numRodadas);
		
		ResultadoIntervaloDeConfianca result = new ResultadoIntervaloDeConfianca(mediaAmostras, tamanhoIntervaloDeConfianca);
		return result;
	}
	
}