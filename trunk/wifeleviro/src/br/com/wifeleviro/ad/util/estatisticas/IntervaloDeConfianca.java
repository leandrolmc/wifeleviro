package br.com.wifeleviro.ad.util.estatisticas;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import br.com.wifeleviro.ad.util.estatisticas.metricas.TAm;
import br.com.wifeleviro.ad.util.estatisticas.metricas.TAp;

public class IntervaloDeConfianca {

	private static final double ASSINTOTICO_95 = 1.96;

	public static ResultadoIntervaloDeConfianca calculaTamanhoIntervaloConfiancaTap(
			Collection<Hashtable<Long, TAp>> tapsRodadas, int numRodadas) {

		double somaAmostras = 0;
		double mediaAmostras = 0;
		double somaVariancias = 0;
		double varianciaAmostras = 0;
		double mediasTapsDasRodadas[] = new double[numRodadas];
		long numeroAmostrasValidas = 0;

		int i = 0;
		for (Hashtable<Long, TAp> taps : tapsRodadas) {
			double somatorioTaps = 0;
			Enumeration<Long> enm = taps.keys();
			numeroAmostrasValidas = 0;
			while(enm.hasMoreElements()){
				TAp tap = taps.get(enm.nextElement());
				if(tap.getInstanteTempoFinal()==null)
					continue;
				somatorioTaps += tap.getInstanteTempoFinal() - tap.getInstanteTempoInicial();
				++numeroAmostrasValidas;
			}
			double mediaTapsDaRodada = somatorioTaps / numeroAmostrasValidas;
//			System.out.println("Rodada i: "+(i+1)+" | Media TAps: "+mediaTapsDaRodada+" = ("+somatorioTaps+"/"+numeroAmostrasValidas+")");
			mediasTapsDasRodadas[i] = mediaTapsDaRodada;
			somaAmostras += mediaTapsDaRodada;
		}

		mediaAmostras = somaAmostras / numRodadas;

		for (double mediaTapsDaRodada : mediasTapsDasRodadas) {
			double sigma = mediaAmostras - mediaTapsDaRodada;
			double variancia = sigma * sigma;
			somaVariancias += variancia;
		}

		varianciaAmostras = somaVariancias / numRodadas;

		double tamanhoIntervaloDeConfianca = (2 * Math.sqrt(varianciaAmostras) * ASSINTOTICO_95) / Math.sqrt(numRodadas);
		
		ResultadoIntervaloDeConfianca result = new ResultadoIntervaloDeConfianca(mediaAmostras, tamanhoIntervaloDeConfianca);
		return result;
	}

	public static ResultadoIntervaloDeConfianca calculaTamanhoIntervaloConfiancaTam(
			Collection<Hashtable<Long, TAm>> tamsRodadas, int numRodadas) {

		double somaAmostras = 0;
		double mediaAmostras = 0;
		double somaVariancias = 0;
		double varianciaAmostras = 0;
		double mediasTamsDasRodadas[] = new double[numRodadas];
		long numeroAmostrasValidas = 0;
		
		int i = 0;
		for (Hashtable<Long, TAm> tams : tamsRodadas) {
			double somatorioTams = 0;
			numeroAmostrasValidas = 0;
			Enumeration<Long> enm = tams.keys();
			while(enm.hasMoreElements()){
				TAm tam = tams.get(enm.nextElement());
				if(tam.getInstanteTempoFinal()==null)
					continue;
				somatorioTams += tam.getInstanteTempoFinal() - tam.getInstanteTempoInicial();
				++numeroAmostrasValidas;
			}
			double mediaTamsDaRodada = somatorioTams / numeroAmostrasValidas;
			mediasTamsDasRodadas[i] = mediaTamsDaRodada;
			i++;
			somaAmostras += mediaTamsDaRodada;
		}
		
		mediaAmostras = somaAmostras / numRodadas;

		for (double mediaTamsDaRodada : mediasTamsDasRodadas) {
			double sigma = mediaAmostras - mediaTamsDaRodada;
			double variancia = sigma * sigma;
			somaVariancias += variancia;
		}

		varianciaAmostras = somaVariancias / numRodadas;

		double tamanhoIntervaloDeConfianca = 2 * Math.sqrt(varianciaAmostras) * ASSINTOTICO_95 / Math.sqrt(numRodadas);
		
		ResultadoIntervaloDeConfianca result = new ResultadoIntervaloDeConfianca(mediaAmostras, tamanhoIntervaloDeConfianca);
		return result;
	}

	public static ResultadoIntervaloDeConfianca calculaTamanhoIntervaloConfiancaNcm(Collection<EstatisticasColisaoRodada> estatisticas, int numRodadas) {

		double somaAmostras = 0;
		double mediaAmostras = 0;
		double somaVariancias = 0;
		double varianciaAmostras = 0;
		double mediasNcmsDasRodadas[] = new double[numRodadas];

		for (EstatisticasColisaoRodada estatistica : estatisticas) {
			Hashtable<Long, Long> quadrosPorMensagem = estatistica.getQuadros();
			Hashtable<Long, Long> colisoesPorMensagem = estatistica.getColisoes();

			double somatorioNcmPorMensagem = 0;

			Enumeration<Long> idMensagens = quadrosPorMensagem.keys();
			while (idMensagens.hasMoreElements()) {
				Long idMensagem = idMensagens.nextElement();
				Long numQuadrosPorMensagem = quadrosPorMensagem.get(idMensagem);
				Long numColisoesPorMensagem = colisoesPorMensagem
						.get(idMensagem);

				double razaoPorMensagem = numColisoesPorMensagem / numQuadrosPorMensagem;

				somatorioNcmPorMensagem += razaoPorMensagem;
			}

			double ncmRodada = somatorioNcmPorMensagem / quadrosPorMensagem.size();

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
			Collection<Double> medicoes = estatisticaUtilizacaoDaRodada.getMedicaoInstanteUtilizacao();
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
	
	public static DadosFinaisDaRodada intervalosDeConfiancaDentroDoLimiteAceitavel(
			Collection<Hashtable<Long, TAp>> tapsRodadas,
			Collection<Hashtable<Long, TAm>> tamsRodadas,
			Collection<EstatisticasColisaoRodada> estatisticasColisaoDasRodadas,
			Collection<EstatisticasUtilizacaoRodada> estatisticasUtilizacaoDasRodadas,
			Collection<EstatisticasVazaoRodada> estatisticasVazaoDasRodadas,
			int numRodadas){
		
		ResultadoIntervaloDeConfianca tap = calculaTamanhoIntervaloConfiancaTap(tapsRodadas, numRodadas);
		ResultadoIntervaloDeConfianca tam = calculaTamanhoIntervaloConfiancaTam(tamsRodadas, numRodadas);
		ResultadoIntervaloDeConfianca ncm = calculaTamanhoIntervaloConfiancaNcm(estatisticasColisaoDasRodadas, numRodadas);
		ResultadoIntervaloDeConfianca utilizacao = calculaTamanhoIntervaloConfiancaUtilizacaoDoEthernet(estatisticasUtilizacaoDasRodadas, numRodadas);
		ResultadoIntervaloDeConfianca vazao = calculaTamanhoIntervaloConfiancaVazao(estatisticasVazaoDasRodadas, numRodadas);
		
		double mediaTap = tap.getMediaDasAmostras();
		double tamanhoICTap = tap.getTamanhoDoIntervaloDeConfianca();
		
		double mediaTam = tam.getMediaDasAmostras();
		double tamanhoICTam = tam.getTamanhoDoIntervaloDeConfianca();
		
		double mediaNcm = ncm.getMediaDasAmostras();
		double tamanhoICNcm = ncm.getTamanhoDoIntervaloDeConfianca();

		double mediaUtilizacao = utilizacao.getMediaDasAmostras();
		double tamanhoICUtilizacao = utilizacao.getTamanhoDoIntervaloDeConfianca();
		
		double mediaVazao = vazao.getMediaDasAmostras();
		double tamanhoICVazao = vazao.getTamanhoDoIntervaloDeConfianca();
		
		boolean dentroDoLimite = (
			(tamanhoICTap < (0.1*mediaTap)) &&
			(tamanhoICTam < (0.1*mediaTam)) &&
			(tamanhoICNcm < (0.1*mediaNcm)) &&
			(tamanhoICUtilizacao < (0.1*mediaUtilizacao)) &&
			(tamanhoICVazao < (0.1*mediaVazao))
		);
		
		DadosFinaisDaRodada dadosRodada = new DadosFinaisDaRodada(tap, tam, ncm, utilizacao, vazao, dentroDoLimite);
		return dadosRodada;
	}
	
}
