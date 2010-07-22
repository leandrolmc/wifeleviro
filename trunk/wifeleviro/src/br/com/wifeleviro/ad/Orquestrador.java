package br.com.wifeleviro.ad;

import java.util.Vector;

import br.com.wifeleviro.ad.modelo.Evento;
import br.com.wifeleviro.ad.modelo.ListaDeEventos;
import br.com.wifeleviro.ad.modelo.MeioFisico;
import br.com.wifeleviro.ad.modelo.Mensagem;
import br.com.wifeleviro.ad.modelo.Quadro;
import br.com.wifeleviro.ad.modelo.Terminal;
import br.com.wifeleviro.ad.modelo.ListaDeEventos.ProximoEvento;
import br.com.wifeleviro.ad.util.ColetorEstatisticas;
import br.com.wifeleviro.ad.util.DadosFinaisDaRodada;
import br.com.wifeleviro.ad.util.EstatisticasColetadas;
import br.com.wifeleviro.ad.util.EstatisticasColisaoRodada;
import br.com.wifeleviro.ad.util.EstatisticasUtilizacaoRodada;
import br.com.wifeleviro.ad.util.EstatisticasVazaoRodada;
import br.com.wifeleviro.ad.util.GeradorRandomicoSingleton;
import br.com.wifeleviro.ad.util.IntervaloDeConfianca;
import br.com.wifeleviro.ad.util.ColetorEstatisticas.Estatisticas;

public class Orquestrador {

	public static void main(String[] args) {

		int numTerminais = 4;
		
		Terminal[] pc = new Terminal[numTerminais];
		pc[0] = new Terminal(0, 100, Terminal.TIPO_DETERMINISTICO, 5, 0.1);
		pc[1] = new Terminal(1, 80, Terminal.TIPO_DETERMINISTICO, 4, 10);
		pc[2] = new Terminal(2, 60, Terminal.TIPO_DETERMINISTICO, 3, 20);
		pc[3] = new Terminal(3, 40, Terminal.TIPO_DETERMINISTICO, 2, 0.7);

		ListaDeEventos listaEventos = new ListaDeEventos();
		
		EstatisticasColetadas[] statsColetadas = new EstatisticasColetadas[numTerminais]; 

		double inicioRodada = (2 ^ 31) - 1;

		int rodadaAtual = 0; // Rodada 0 -> fase transiente

		for (int i = 0; i < numTerminais; i++) {
			listaEventos.put(pc[i].getInstanteTempoInicial(), new Evento(Evento.GERAR_MENSAGEM, i, null));
			inicioRodada = Math.min(inicioRodada, pc[i].getInstanteTempoInicial());
		}

		ColetorEstatisticas coletor = null;
		boolean intervaloDeConfiancaOK = true; // Inicializo com true para compor corretamente o and final.
		do {
			if(rodadaAtual > 0)
				inicioRodada = listaEventos.getInstanteDeTempoAtual();

			coletor = new ColetorEstatisticas(numTerminais);
			
			coletor.coletaInicioRodada(inicioRodada);

			double fimDaRodada = 0;

			while ((rodadaAtual == 0 && listaEventos.getNumeroDeEventosNaRodada() < 1000000) || (rodadaAtual > 0 && listaEventos.getNumeroDeEventosNaRodada() < 100000)) {

				ProximoEvento proximo = listaEventos.proximoEvento();
				fimDaRodada = proximo.getTempo();
				Evento e = proximo.getEvento();

				switch (e.getTipoEvento()) {
					case Evento.GERAR_MENSAGEM:
						tratarEventoGerarMensagem(coletor, pc, listaEventos, e);
						break;
					case Evento.INICIO_TX_PC:
						tratarEventoInicioTxPc(coletor, pc, listaEventos, e);
						break;
					case Evento.CHEGADA_QUADRO_NO_RX_HUB:
						tratarEventoChegadaDeQuadroNoRxDoHub(numTerminais, pc, listaEventos, e);
						break;
					case Evento.INICIO_CHEGADA_QUADRO_NO_RX_TERMINAL:
						tratarEventoInicioChegadaDeQuadroNoRxDoTerminal(coletor, pc, listaEventos, e);
						break;
					case Evento.FIM_CHEGADA_QUADRO_NO_RX_TERMINAL:
						tratarEventoFimChegadaDeQuadroNoRxDoTerminal(coletor, pc, listaEventos, e);
						break;
					case Evento.GERAR_REFORCO_COLISAO:
						tratarEventoGerarReforcoColisao(pc, listaEventos, e);
						break;
				}
			}

			coletor.coletaFimRodada(fimDaRodada);
			
			Estatisticas[] estatisticas = coletor.getEstatisticas();
			for(int i = 0; i < numTerminais; i++){
				Vector<Double> tap = estatisticas[i].getTap();
				Vector<Double> tam = estatisticas[i].getTam();
				EstatisticasColisaoRodada colisao = new EstatisticasColisaoRodada(estatisticas[i].getColisoesPorMensagem(), estatisticas[i].getQuadrosPorMensagem());
				EstatisticasUtilizacaoRodada utilizacao = new EstatisticasUtilizacaoRodada(coletor.getInstanteInicioRodada(), coletor.getInstanteFimRodada(), estatisticas[i].getPeriodosOcupados()); 
				EstatisticasVazaoRodada vazao = new EstatisticasVazaoRodada(coletor.getInstanteInicioRodada(), coletor.getInstanteFimRodada(), estatisticas[i].getNumeroQuadrosTransmitidosComSucesso());
				statsColetadas[i].armazenar(tap, tam, colisao, utilizacao, vazao);
				
				DadosFinaisDaRodada dados = IntervaloDeConfianca.intervalosDeConfiancaDentroDoLimiteAceitavel(
						statsColetadas[i].getColTap(), 
						statsColetadas[i].getColTap(), 
						statsColetadas[i].getColEstatisticaColisaoRodada(), 
						statsColetadas[i].getColEstatisticaUtilizacaoDaRodada(), 
						statsColetadas[i].getColEstatisticaVazaoDaRodada(), 
						rodadaAtual);
				
				intervaloDeConfiancaOK = intervaloDeConfiancaOK &&  dados.getDentroDoLimite();
				
				System.out.println("== RODADA "+rodadaAtual+" ==");
				System.out.println("--[TAp("+i+")]--");
				System.out.println("E[TAp("+i+")]: "+dados.getTap().getMediaDasAmostras());
				System.out.println("U(alpha)-L(alpha): "+dados.getTap().getTamanhoDoIntervaloDeConfianca());
				System.out.println("--[TAm("+i+")]--");
				System.out.println("E[TAm("+i+")]: "+dados.getTam().getMediaDasAmostras());
				System.out.println("U(alpha)-L(alpha): "+dados.getTam().getTamanhoDoIntervaloDeConfianca());
				System.out.println("--[NCm("+i+")]--");
				System.out.println("E[NCm("+i+")]: "+dados.getNcm().getMediaDasAmostras());
				System.out.println("U(alpha)-L(alpha): "+dados.getNcm().getTamanhoDoIntervaloDeConfianca());
				System.out.println("--[Utilizacao("+i+")]--");
				System.out.println("E[Utilizacao("+i+")]: "+dados.getUtilizacao().getMediaDasAmostras());
				System.out.println("U(alpha)-L(alpha): "+dados.getUtilizacao().getTamanhoDoIntervaloDeConfianca());
				System.out.println("--[Vazao("+i+")]--");
				System.out.println("E[Vazao("+i+")]: "+dados.getVazao().getMediaDasAmostras());
				System.out.println("U(alpha)-L(alpha): "+dados.getVazao().getTamanhoDoIntervaloDeConfianca());
				System.out.println("== FIM RODADA "+rodadaAtual+" ==");	
			}

			++rodadaAtual;
			intervaloDeConfiancaOK = true;
		} while ((rodadaAtual <= 30) || (rodadaAtual > 30 && !intervaloDeConfiancaOK));
	}

	private static void tratarEventoGerarMensagem(ColetorEstatisticas coletor, Terminal[] pc,
			ListaDeEventos lista, Evento e) {

		int terminalOrigem = e.getTerminalOrigem();

		double instanteDeTempo = lista.getInstanteDeTempoAtual();

		// Crio a mensagem a ser transmitida.
		Mensagem mensagem = new Mensagem(pc[terminalOrigem].getpMensagens());
		Quadro quadro = new Quadro(terminalOrigem, null, mensagem);
		coletor.coletaQuadroPorMensagem(terminalOrigem, mensagem.getId());

		// Crio o primeiro quadro da mensagem para ser transmitido no tx.
		Evento inicioTxPrimeiroQuadroMensagem = new Evento(Evento.INICIO_TX_PC,
				terminalOrigem, quadro);
		lista.put(instanteDeTempo, inicioTxPrimeiroQuadroMensagem);
		coletor.iniciaColetaTam(terminalOrigem, mensagem.getId(),
				instanteDeTempo);

		// Crio a próxima mensagem.
		Evento proximaMensagem = new Evento(Evento.GERAR_MENSAGEM,
				terminalOrigem, null);
		double instanteDeTempoDaProximaMensagem = instanteDeTempo
				+ pc[terminalOrigem].gerarProximoInstanteDeTempoDeMensagem();
		lista.put(instanteDeTempoDaProximaMensagem, proximaMensagem);

	}

	private static void tratarEventoInicioTxPc(ColetorEstatisticas coletor, Terminal[] pc,
			ListaDeEventos lista, Evento e) {

		int terminalAtual = e.getTerminalOrigem();
		Quadro quadro = e.getQuadro();

		if (pc[terminalAtual].isMeioOcupado()) {

			double instanteTempoFimRx = pc[terminalAtual].getInstanteTempoFimUltimoRx();
			double instanteTempoInicioTx = instanteTempoFimRx + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;

			pc[terminalAtual].setInstanteTempoInicioUltimaTx(instanteTempoInicioTx);
			double instanteTempoFimTx = instanteTempoInicioTx + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO;
			pc[terminalAtual].setInstanteTempoFimUltimaTx(instanteTempoFimTx);

			double instanteTempoFimChegadaRxHub = instanteTempoFimTx + MeioFisico.calculaTempoPropagacao(pc[terminalAtual].getDistanciaHub());
			Evento chegadaRxHub = new Evento(Evento.CHEGADA_QUADRO_NO_RX_HUB, terminalAtual, quadro);
			lista.put(instanteTempoFimChegadaRxHub, chegadaRxHub);
			coletor.coletaTransmissaoDeQuadroComSucessoNaEstacao(terminalAtual);

			if (quadro.getMensagem().getTipoMensagem() == Mensagem.MENSAGEM_PADRAO) {
				coletor.iniciaColetaTap(terminalAtual, quadro.getId(), instanteTempoFimRx);
				coletor.finalizaColetaTap(terminalAtual, quadro.getId(), instanteTempoFimChegadaRxHub);

				quadro.getMensagem().decrementaNumeroQuadroRestantesParaTransmissao();
				if (quadro.getMensagem().getNumeroQuadroRestantesParaTransmissao() > 0) {
					double instanteDeTempoDoProximoQuadro = instanteTempoFimTx + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;
					Quadro novoQuadro = new Quadro(terminalAtual, null, quadro.getMensagem());
					coletor.coletaQuadroPorMensagem(terminalAtual, quadro.getMensagem().getId());
					Evento novoEvento = new Evento(Evento.INICIO_TX_PC, terminalAtual, novoQuadro);
					lista.put(instanteDeTempoDoProximoQuadro, novoEvento);
				}
			}

			if (terminalAtual == 0)
				coletor.coletaInicioPeriodoOcupado(instanteTempoInicioTx);
			if (terminalAtual == 0)
				coletor.coletaFimPeriodoOcupado(instanteTempoFimTx);
		} else {

			double ultimaTransmissaoTx = lista.getInstanteDeTempoAtual() - pc[terminalAtual].getInstanteTempoFimUltimaTx();
			double ultimaTransmissaoRx = lista.getInstanteDeTempoAtual() - pc[terminalAtual].getInstanteTempoFimUltimoRx();

			if (ultimaTransmissaoTx > Quadro.TEMPO_MINIMO_ENTRE_QUADROS && ultimaTransmissaoRx > Quadro.TEMPO_MINIMO_ENTRE_QUADROS) {
				double instanteTempoInicioDeTx = lista.getInstanteDeTempoAtual();
				double instanteTempoFimDeTx = instanteTempoInicioDeTx + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO;

				pc[terminalAtual].setInstanteTempoInicioUltimaTx(instanteTempoInicioDeTx);
				pc[terminalAtual].setInstanteTempoFimUltimaTx(instanteTempoFimDeTx);

				double instanteTempoFimChegadaRxHub = instanteTempoFimDeTx + MeioFisico.calculaTempoPropagacao(pc[terminalAtual].getDistanciaHub());
				Evento chegadaRxHub = new Evento(Evento.CHEGADA_QUADRO_NO_RX_HUB, terminalAtual, quadro);
				lista.put(instanteTempoFimChegadaRxHub, chegadaRxHub);
				coletor.coletaTransmissaoDeQuadroComSucessoNaEstacao(terminalAtual);

				if (quadro.getMensagem().getTipoMensagem() == Mensagem.MENSAGEM_PADRAO) {
					coletor.iniciaColetaTap(terminalAtual, quadro.getId(), instanteTempoInicioDeTx);
					coletor.finalizaColetaTap(terminalAtual, quadro.getId(), instanteTempoFimChegadaRxHub);

					Mensagem mensagem = quadro.getMensagem();
					mensagem.decrementaNumeroQuadroRestantesParaTransmissao();

					if (mensagem.getNumeroQuadroRestantesParaTransmissao() > 0) {
						double instanteDeTempoDoProximoQuadro = instanteTempoFimDeTx + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;
						Quadro proximoQuadro = new Quadro(terminalAtual, null, mensagem);
						coletor.coletaQuadroPorMensagem(terminalAtual, mensagem.getId());
						Evento proximoEvento = new Evento(Evento.INICIO_TX_PC, terminalAtual, proximoQuadro);
						lista.put(instanteDeTempoDoProximoQuadro, proximoEvento);
					} else {
						coletor.finalizaColetaTam(terminalAtual, mensagem.getId(), instanteTempoFimChegadaRxHub);
					}
				}

				if (terminalAtual == 0)
					coletor.coletaInicioPeriodoOcupado(instanteTempoInicioDeTx);
				if (terminalAtual == 0)
					coletor.coletaFimPeriodoOcupado(instanteTempoFimDeTx);
			} else {
				double instanteTempoMeioVazio = lista.getInstanteDeTempoAtual() + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;

				double instanteTempoInicioDeTx = instanteTempoMeioVazio;
				double instanteTempoFimDeTx = instanteTempoInicioDeTx + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO;

				pc[terminalAtual].setInstanteTempoInicioUltimaTx(instanteTempoInicioDeTx);
				pc[terminalAtual].setInstanteTempoFimUltimaTx(instanteTempoFimDeTx);

				double instanteTempoFimChegadaRxHub = instanteTempoFimDeTx + MeioFisico.calculaTempoPropagacao(pc[terminalAtual].getDistanciaHub());
				Evento chegadaRxHub = new Evento(Evento.CHEGADA_QUADRO_NO_RX_HUB, terminalAtual, quadro);
				lista.put(instanteTempoFimChegadaRxHub, chegadaRxHub);
				coletor.coletaTransmissaoDeQuadroComSucessoNaEstacao(terminalAtual);

				if (quadro.getMensagem().getTipoMensagem() == Mensagem.MENSAGEM_PADRAO) {
					coletor.iniciaColetaTap(terminalAtual, quadro.getId(),lista.getInstanteDeTempoAtual());
					coletor.finalizaColetaTap(terminalAtual, quadro.getId(), instanteTempoFimChegadaRxHub);

					Mensagem mensagem = quadro.getMensagem();
					mensagem.decrementaNumeroQuadroRestantesParaTransmissao();

					if (mensagem.getNumeroQuadroRestantesParaTransmissao() > 0) {
						double instanteDeTempoDoProximoQuadro = instanteTempoFimDeTx + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;
						Quadro proximoQuadro = new Quadro(terminalAtual, null, mensagem);
						coletor.coletaQuadroPorMensagem(terminalAtual, mensagem.getId());
						Evento proximoEvento = new Evento(Evento.INICIO_TX_PC, terminalAtual, proximoQuadro);
						lista.put(instanteDeTempoDoProximoQuadro, proximoEvento);
					} else {
						coletor.finalizaColetaTam(terminalAtual, mensagem.getId(), instanteTempoFimChegadaRxHub);
					}
				}

				if (terminalAtual == 0)
					coletor.coletaInicioPeriodoOcupado(instanteTempoInicioDeTx);
				if (terminalAtual == 0)
					coletor.coletaFimPeriodoOcupado(instanteTempoFimDeTx);
			}
		}
	}

	private static void tratarEventoChegadaDeQuadroNoRxDoHub(int numTerminais, Terminal[] pc, ListaDeEventos lista, Evento e) {

		int terminalDeOrigem = e.getTerminalOrigem();

		double instanteDeTempoDoBroadcast = lista.getInstanteDeTempoAtual();

		Evento inicioChegadaQuadroNoPc[] = new Evento[numTerminais];
		for (int i = 0; i < numTerminais; i++) {
			Quadro quadroi = new Quadro(e.getQuadro().getIdRemetente(), i, e.getQuadro().getMensagem());
			inicioChegadaQuadroNoPc[i] = new Evento(Evento.INICIO_CHEGADA_QUADRO_NO_RX_TERMINAL, terminalDeOrigem, quadroi);
			double instanteDeTempoDeInicioChegadaDoQuadroNoRxTerminal = instanteDeTempoDoBroadcast + MeioFisico.calculaTempoPropagacao(pc[i].getDistanciaHub());
			lista.put(instanteDeTempoDeInicioChegadaDoQuadroNoRxTerminal, inicioChegadaQuadroNoPc[i]);
		}
	}

	private static void tratarEventoInicioChegadaDeQuadroNoRxDoTerminal(ColetorEstatisticas coletor,
			Terminal[] pc, ListaDeEventos lista, Evento e) {

		Quadro quadro = e.getQuadro();
		int terminalAtual = quadro.getIdDestinatario();

		if (terminalAtual == 0)
			coletor.coletaInicioPeriodoOcupado(lista.getInstanteDeTempoAtual());

		if (quadro.getIdRemetente() != quadro.getIdDestinatario()) {
			pc[terminalAtual].setMeioOcupado(true);
			if (terminalAtual == 0)
				coletor.coletaInicioPeriodoOcupado(lista
						.getInstanteDeTempoAtual());
			quadro.incColisoes();
			pc[terminalAtual].setQuadroPendente(quadro);
			coletor.coletaColisaoPorMensagem(terminalAtual, quadro
					.getMensagem().getId());

			Mensagem mColisao = new Mensagem();
			Quadro qColisao = new Quadro(terminalAtual, null, mColisao);
			Evento colisao = new Evento(Evento.GERAR_REFORCO_COLISAO,
					terminalAtual, qColisao);
			lista.put(lista.getInstanteDeTempoAtual(), colisao);

		} else {
			pc[terminalAtual].setMeioOcupado(false);
			if (terminalAtual == 0)
				coletor
						.coletaFimPeriodoOcupado(lista
								.getInstanteDeTempoAtual());
		}

		Evento fimChegadaQuadroRxTerminal = new Evento(
				Evento.FIM_CHEGADA_QUADRO_NO_RX_TERMINAL, quadro
						.getIdRemetente(), quadro);
		double instanteTempoFimTx = lista.getInstanteDeTempoAtual()
				+ Mensagem.TEMPO_TRANSMISAO_POR_QUADRO;
		lista.put(instanteTempoFimTx, fimChegadaQuadroRxTerminal);
	}

	private static void tratarEventoFimChegadaDeQuadroNoRxDoTerminal(ColetorEstatisticas coletor,
			Terminal[] pc, ListaDeEventos lista, Evento e) {
		pc[e.getQuadro().getIdDestinatario()].setMeioOcupado(false);

		if (e.getQuadro().getIdDestinatario() == 0)
			coletor.coletaInicioPeriodoOcupado(lista.getInstanteDeTempoAtual());
	}

	private static void tratarEventoGerarReforcoColisao(Terminal[] pc,
			ListaDeEventos lista, Evento e) {

		int terminalAtual = e.getTerminalOrigem();
		Quadro quadroPendente = pc[terminalAtual].getQuadroPendente();

		Evento chegadaReforcoColisaoRxHub = new Evento(
				Evento.CHEGADA_QUADRO_NO_RX_HUB, terminalAtual, e.getQuadro());
		double instanteTempoSaidaTxPc = lista.getInstanteDeTempoAtual()
				+ Mensagem.TEMPO_TRANSMISSAO_REFORCO_COLISAO;
		double instanteTempoChegadaReforcoRxHub = instanteTempoSaidaTxPc
				+ MeioFisico.calculaTempoPropagacao(pc[terminalAtual]
						.getDistanciaHub());
		lista.put(instanteTempoChegadaReforcoRxHub, chegadaReforcoColisaoRxHub);

		if (quadroPendente.getColisoes() < 16) {
			Evento retransmissaoMensagemPendente = new Evento(
					Evento.INICIO_TX_PC, terminalAtual, quadroPendente);
			double instanteTempoAleatorioEscolhido = instanteTempoSaidaTxPc
					+ Orquestrador
							.gerarAtrasoAleatorioBinaryBackoff(quadroPendente);
			lista.put(instanteTempoAleatorioEscolhido,
					retransmissaoMensagemPendente);
		}
	}

	private static double gerarAtrasoAleatorioBinaryBackoff(
			Quadro quadroPendente) {

		int numColisoes = quadroPendente.getColisoes();
		numColisoes = Math.min(10, numColisoes);

		double randomico = GeradorRandomicoSingleton.getInstance()
				.gerarProximoRandomico();
		int intervalos = (int) (randomico % (2 ^ numColisoes - 1));

		return intervalos * Quadro.SLOT_RETRANSMISSAO;
	}
}
