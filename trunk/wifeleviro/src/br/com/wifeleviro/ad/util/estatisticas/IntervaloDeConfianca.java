package br.com.wifeleviro.ad.util.estatisticas;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import br.com.wifeleviro.ad.util.estatisticas.metricas.TAm;
import br.com.wifeleviro.ad.util.estatisticas.metricas.TAp;
import br.com.wifeleviro.ad.util.estatisticas.metricas.Utilizacao;

/*
 * Classe responsável por calcular os intervalos de confiança a partir
 * das métricas coletas durante a simulação. 
 */
public class IntervaloDeConfianca {

	// Valor assintótico do t-student a 95%.
	private static final double ASSINTOTICO_95 = 1.96;

	// Calcula a média da rodada e o tamanho do intervalo de confiança da TAp.
	public static ResultadoIntervaloDeConfianca calculaTamanhoIntervaloConfiancaTap(
			Collection<TAp> tapsRodadas, int numRodadas) {

		double somaAmostras = 0;
		double mediaAmostras = 0;
		double somaVariancias = 0;
		double varianciaAmostras = 0;
		double mediasTapsDasRodadas[] = new double[numRodadas];

		int i = 0;
		for (TAp tap : tapsRodadas) {
			double somatorioTaps = tap.getAcumuladorTempo();
			long quantidadeQuadros = tap.getNumeroQuadros();
			double mediaTapsDaRodada = somatorioTaps / quantidadeQuadros;
			mediasTapsDasRodadas[i] = mediaTapsDaRodada;
			somaAmostras += mediaTapsDaRodada;
			i++;
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

	// Calcula a média da rodada e o tamanho do intervalo de confiança da TAm.
	public static ResultadoIntervaloDeConfianca calculaTamanhoIntervaloConfiancaTam(
			Collection<TAm> tamsRodadas, int numRodadas) {

		double somaAmostras = 0;
		double mediaAmostras = 0;
		double somaVariancias = 0;
		double varianciaAmostras = 0;
		double mediasTamsDasRodadas[] = new double[numRodadas];
		
		int i = 0;
		for (TAm tam : tamsRodadas) {
			double somatorioTams = tam.getAcumuladorTempo();
			long quantidadeMensagens = tam.getNumeroMensagens();
			double mediaTamsDaRodada = somatorioTams / quantidadeMensagens;
			mediasTamsDasRodadas[i] = mediaTamsDaRodada;
			somaAmostras += mediaTamsDaRodada;
			i++;
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
	
	// Calcula a média da rodada e o tamanho do intervalo de confiança da NCm.
	public static ResultadoIntervaloDeConfianca calculaTamanhoIntervaloConfiancaNcm(Collection<EstatisticasColisaoRodada> estatisticas, int numRodadas) {

		double somaAmostras = 0;
		double mediaAmostras = 0;
		double somaVariancias = 0;
		double varianciaAmostras = 0;
		double mediasNcmsDasRodadas[] = new double[numRodadas];

		int i = 0;
		for (EstatisticasColisaoRodada estatistica : estatisticas) {
			Hashtable<Long, Long> quadrosPorMensagem = estatistica.getQuadros();
			Hashtable<Long, Long> colisoesPorMensagem = estatistica.getColisoes();

			double somatorioNcm = 0;
			int numeroDeMensagens = 0;
			
			Enumeration<Long> idMensagens = quadrosPorMensagem.keys();
			while (idMensagens.hasMoreElements()) {
				Long idMensagem = idMensagens.nextElement();
				Long numQuadrosPorMensagem = quadrosPorMensagem.get(idMensagem);
				Long numColisoesPorMensagem = colisoesPorMensagem.get(idMensagem);
				
				double ncm = numColisoesPorMensagem==null?0:numColisoesPorMensagem / numQuadrosPorMensagem;

				++numeroDeMensagens;
				somatorioNcm += ncm;
			}
			double ncmRodada = somatorioNcm / numeroDeMensagens;
			mediasNcmsDasRodadas[i] = ncmRodada;
			++i;
			somaAmostras += ncmRodada;
		}

		mediaAmostras = somaAmostras / numRodadas;

		for (double mediaNcmsDaRodada : mediasNcmsDasRodadas) {
			double sigma = mediaAmostras - mediaNcmsDaRodada;
			double variancia = sigma * sigma;
			somaVariancias += variancia;
		}

		varianciaAmostras = somaVariancias / numRodadas;

		double tamanhoIntervaloDeConfianca = 2 * Math.sqrt(varianciaAmostras) * ASSINTOTICO_95 / Math.sqrt(numRodadas);
		
		ResultadoIntervaloDeConfianca result = new ResultadoIntervaloDeConfianca(mediaAmostras, tamanhoIntervaloDeConfianca);
		return result;
	}

	// Calcula a média da rodada e o tamanho do intervalo de confiança da utilização.
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

			double periodosUtilizacao = 0;
			Collection<Utilizacao> medicoes = estatisticaUtilizacaoDaRodada.getUtilizacao();
			for (Utilizacao medicao : medicoes) {
				double periodoUtilizacao = medicao.getFim() - medicao.getInicio();
				periodosUtilizacao += periodoUtilizacao;
			}
			double tempoTotalRodada = estatisticaUtilizacaoDaRodada.getFimDaRodada() - estatisticaUtilizacaoDaRodada.getInicioDaRodada();
			utilizacaoDasRodadas[i] = periodosUtilizacao / tempoTotalRodada;

			somaAmostras += utilizacaoDasRodadas[i];
		}

		mediaAmostras = somaAmostras / numRodadas;

		for (double utilizacaoDaRodada : utilizacaoDasRodadas) {
			double sigma = mediaAmostras - utilizacaoDaRodada;
			double variancia = sigma * sigma;
			somaVariancias += variancia;
		}

		varianciaAmostras = somaVariancias / numRodadas;

		double tamanhoIntervaloDeConfianca = 2 * Math.sqrt(varianciaAmostras) * ASSINTOTICO_95 / Math.sqrt(numRodadas);
		
		ResultadoIntervaloDeConfianca result = new ResultadoIntervaloDeConfianca(mediaAmostras, tamanhoIntervaloDeConfianca);
		return result;
	}

	// Calcula a média da rodada e o tamanho do intervalo de confiança da vazão.
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
			double tempoTotalRodada = estatisticaVazaoDaRodada.getFimDaRodada() - estatisticaVazaoDaRodada.getInicioDaRodada();
			vazaoDasRodadas[i] = numQuadros / tempoTotalRodada;

			somaAmostras += vazaoDasRodadas[i];
		}

		mediaAmostras = somaAmostras / numRodadas;

		for (double vazaoDaRodada : vazaoDasRodadas) {
			double sigma = mediaAmostras - vazaoDaRodada;
			double variancia = sigma * sigma;
			somaVariancias += variancia;
		}

		varianciaAmostras = somaVariancias / numRodadas;

		double tamanhoIntervaloDeConfianca = 2 * Math.sqrt(varianciaAmostras) * ASSINTOTICO_95 / Math.sqrt(numRodadas);
		
		ResultadoIntervaloDeConfianca result = new ResultadoIntervaloDeConfianca(mediaAmostras, tamanhoIntervaloDeConfianca);
		return result;
	}
	
	// Recupera o resultado de todos os cálculos realizados e
	// avalia se todos convergiram para dentro do limite.
	public static DadosFinaisDaRodada intervalosDeConfiancaDentroDoLimiteAceitavel(
			Collection<TAp> tapsRodadas,
			Collection<TAm> tamsRodadas,
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
			(tamanhoICTap <= (0.1*mediaTap)) &&
			(tamanhoICTam <= (0.1*mediaTam)) &&
			(tamanhoICNcm <= (0.1*mediaNcm)) &&
			(tamanhoICUtilizacao <= (0.1*mediaUtilizacao)) &&
			(tamanhoICVazao < (0.1*mediaVazao))
		);
		
		DadosFinaisDaRodada dadosRodada = new DadosFinaisDaRodada(tap, tam, ncm, utilizacao, vazao, dentroDoLimite);
		return dadosRodada;
	}
	
}
