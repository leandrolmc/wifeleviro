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

	private int qtdTerminais;
	private Terminal[] terminais;
	private long qtdMensagensNaRodada;

	public Orquestrador(int qtdTerminais, Terminal[] terminais){
		this.qtdTerminais = qtdTerminais;
		this.terminais = terminais;
	}
	
	public void executarSimulacao() {

		int numTerminais = this.qtdTerminais;
		
		Terminal[] pc = this.terminais;

		ListaDeEventos listaEventos = new ListaDeEventos();
		EstatisticasColetadas[] statsColetadas = new EstatisticasColetadas[numTerminais]; 

		double inicioRodada = (2 ^ 31) - 1;

		int rodadaAtual = -1; // Inicia em -1 para incrementar no início do programa: Rodada 0 -> fase transiente

		for (int i = 0; i < numTerminais; i++) {
			listaEventos.put(pc[i].getInstanteTempoInicial(), new Evento(Evento.GERAR_MENSAGEM, i, null));
			inicioRodada = Math.min(inicioRodada, pc[i].getInstanteTempoInicial());
			statsColetadas[i] = new EstatisticasColetadas();
		}

		ColetorEstatisticas coletor = null;
		boolean intervaloDeConfiancaOK = true; // Inicializo com true para compor corretamente o and final.
		do {
			++rodadaAtual;
			this.qtdMensagensNaRodada = 0;
			
			intervaloDeConfiancaOK = true;
			
//			listaEventos.resetContadorEventosPorRodada();
			long numEventosDaRodada = 0;
			
			if(rodadaAtual > 0)
				inicioRodada = listaEventos.getInstanteDeTempoAtual();

			if(rodadaAtual == 0)
				System.out.println("== FASE TRANSIENTE ==");
			else
				System.out.println("== RODADA "+rodadaAtual+" ==");
			
			coletor = new ColetorEstatisticas(numTerminais);
			
			coletor.coletaInicioRodada(inicioRodada);

			double fimDaRodada = 0;

			while ((rodadaAtual == 0 && numEventosDaRodada <= 100000) || (rodadaAtual > 0 && rodadaAtual < 100 && numEventosDaRodada < 100000)) {
				
				if(numEventosDaRodada == 100000){
					System.out.print("");
				}
				
				if(rodadaAtual != 0){
					System.out.print("");
				}
				
				ProximoEvento proximo = listaEventos.proximoEvento();
				fimDaRodada = proximo.getTempo();
				Evento e = proximo.getEvento();

				if(fimDaRodada >= 0.08)
					System.out.print("");
				
				Mensagem msg = null;
				if(e.getQuadro() != null){
					msg = e.getQuadro().getMensagem();
				}
				
				switch (e.getTipoEvento()) {
					case Evento.GERAR_MENSAGEM:
						tratarEventoGerarMensagem(coletor, pc, listaEventos, e);
						++this.qtdMensagensNaRodada;
						//Mensagem msg = e.getQuadro().getMensagem();
						verbosePorEvento(""+fimDaRodada, ""+numEventosDaRodada, ""+e.getTerminalOrigem(), ""+rodadaAtual, msg!=null?""+msg.getId():"MENSAGEM NAO IDENTIFICADA", msg!=null?""+msg.getNumeroQuadroRestantesParaTransmissao():"SEM QUADROS", "Gerar Mensagem");
						++numEventosDaRodada;
						break;
					case Evento.INICIO_TX_PC:
						tratarEventoInicioTxPc(coletor, pc, listaEventos, e);
						verbosePorEvento(""+fimDaRodada, ""+numEventosDaRodada, ""+e.getTerminalOrigem(), ""+rodadaAtual, msg!=null?""+msg.getId():"MENSAGEM NAO IDENTIFICADA", msg!=null?""+msg.getNumeroQuadroRestantesParaTransmissao():"SEM QUADROS", "Inicio TX do PC");
						++numEventosDaRodada;
						break;
					case Evento.FIM_TX_PC:
						tratarEventoFimTxPc(coletor, pc, listaEventos, e);
						verbosePorEvento(""+fimDaRodada, ""+numEventosDaRodada, ""+e.getTerminalOrigem(), ""+rodadaAtual, msg!=null?""+msg.getId():"MENSAGEM NAO IDENTIFICADA", msg!=null?""+msg.getNumeroQuadroRestantesParaTransmissao():"SEM QUADROS", "Fim TX do PC");
						++numEventosDaRodada;
						break;
					case Evento.CHEGADA_QUADRO_NO_RX_HUB:
						tratarEventoChegadaDeQuadroNoRxDoHub(numTerminais, pc, listaEventos, e);
						verbosePorEvento(""+fimDaRodada, ""+numEventosDaRodada, ""+e.getTerminalOrigem(), ""+rodadaAtual, msg!=null?""+msg.getId():"MENSAGEM NAO IDENTIFICADA", msg!=null?""+msg.getNumeroQuadroRestantesParaTransmissao():"SEM QUADROS", "Chegada Quadro no RX do HUB");
						++numEventosDaRodada;
						break;
					case Evento.INICIO_CHEGADA_QUADRO_NO_RX_TERMINAL:
						tratarEventoInicioChegadaDeQuadroNoRxDoTerminal(coletor, pc, listaEventos, e);
						verbosePorEvento(""+fimDaRodada, ""+numEventosDaRodada, ""+e.getTerminalOrigem(), ""+rodadaAtual, msg!=null?""+msg.getId():"MENSAGEM NAO IDENTIFICADA", msg!=null?""+msg.getNumeroQuadroRestantesParaTransmissao():"SEM QUADROS", "Inicio Chegada Quadro no RX do terminal");
						++numEventosDaRodada;
						break;
					case Evento.FIM_CHEGADA_QUADRO_NO_RX_TERMINAL:
						tratarEventoFimChegadaDeQuadroNoRxDoTerminal(coletor, pc, listaEventos, e);
						verbosePorEvento(""+fimDaRodada, ""+numEventosDaRodada, ""+e.getTerminalOrigem(), ""+rodadaAtual, msg!=null?""+msg.getId():"MENSAGEM NAO IDENTIFICADA", msg!=null?""+msg.getNumeroQuadroRestantesParaTransmissao():"SEM QUADROS", "Fim Chegada Quadro no RX do terminal");
						++numEventosDaRodada;
						break;
				}
			}

			coletor.coletaFimRodada(fimDaRodada);
			
			if(rodadaAtual == 0){
				System.out.println("== FIM DA FASE TRANSIENTE ==");
				}else{
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
								statsColetadas[i].getColTam(), 
								statsColetadas[i].getColEstatisticaColisaoRodada(), 
								statsColetadas[i].getColEstatisticaUtilizacaoDaRodada(), 
								statsColetadas[i].getColEstatisticaVazaoDaRodada(), 
								1+rodadaAtual);
						
						intervaloDeConfiancaOK = intervaloDeConfiancaOK &&  dados.getDentroDoLimite();
						
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
						
					}
	
					System.out.println("== FIM RODADA "+rodadaAtual+" ==");
				}
				
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
		Evento inicioTxPrimeiroQuadroMensagem = new Evento(Evento.INICIO_TX_PC, terminalOrigem, quadro);
		lista.put(instanteDeTempo, inicioTxPrimeiroQuadroMensagem);
		coletor.iniciaColetaTam(terminalOrigem, mensagem.getId(),instanteDeTempo);

		// Crio a próxima mensagem.
		Evento proximaMensagem = new Evento(Evento.GERAR_MENSAGEM, terminalOrigem, null);
		double instanteDeTempoDaProximaMensagem = instanteDeTempo + pc[terminalOrigem].gerarProximoInstanteDeTempoDeMensagem();
		lista.put(instanteDeTempoDaProximaMensagem, proximaMensagem);

	}

	private static void tratarEventoInicioTxPc(ColetorEstatisticas coletor, Terminal[] pc,
			ListaDeEventos lista, Evento e) {

		int terminalAtual = e.getTerminalOrigem();
		Quadro quadro = e.getQuadro();

		double instanteTempoInicioTx = -1;
		
		if (pc[terminalAtual].isMeioOcupado()) {
			if(pc[terminalAtual].getIdTerminalUltimoRx() == terminalAtual){
				instanteTempoInicioTx = lista.getInstanteDeTempoAtual();
				pc[terminalAtual].setInstanteTempoInicioUltimaTx(instanteTempoInicioTx);
				double instanteTempoPrevisaoFimTx = instanteTempoInicioTx + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO;
				pc[terminalAtual].setInstanteTempoFimUltimaTx(instanteTempoPrevisaoFimTx);
				Evento fimTx = new Evento(Evento.FIM_TX_PC, terminalAtual, quadro);
				lista.put(instanteTempoPrevisaoFimTx, fimTx);
				
			}else{
				double tempoMeioLivre = pc[terminalAtual].getInstanteTempoFimUltimoRx();
				instanteTempoInicioTx = tempoMeioLivre + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;
				pc[terminalAtual].setInstanteTempoInicioUltimaTx(instanteTempoInicioTx);
				double instanteTempoPrevisaoFimTx = instanteTempoInicioTx + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO;
				pc[terminalAtual].setInstanteTempoFimUltimaTx(instanteTempoPrevisaoFimTx);
				Evento fimTx = new Evento(Evento.FIM_TX_PC, terminalAtual, quadro);
				lista.put(instanteTempoPrevisaoFimTx, fimTx);
			}
		}else{
			instanteTempoInicioTx = lista.getInstanteDeTempoAtual();
			pc[terminalAtual].setInstanteTempoInicioUltimaTx(instanteTempoInicioTx);
			double instanteTempoPrevisaoFimTx = instanteTempoInicioTx + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO;
			pc[terminalAtual].setInstanteTempoFimUltimaTx(instanteTempoPrevisaoFimTx);
			Evento fimTx = new Evento(Evento.FIM_TX_PC, terminalAtual, quadro);
			lista.put(instanteTempoPrevisaoFimTx, fimTx);
		}
		
		//Caso esteja em colisão, significa que já existe um quadro contabilizando tap.
		if(pc[terminalAtual].isEmColisao()){
			pc[terminalAtual].setEmColisao(false);
		}else{
			coletor.iniciaColetaTap(terminalAtual, quadro.getId(), lista.getInstanteDeTempoAtual());
			coletor.finalizaColetaTap(terminalAtual, quadro.getId(), instanteTempoInicioTx);
		}
	}
	
	private static void tratarEventoFimTxPc(ColetorEstatisticas coletor, Terminal[] pc,
			ListaDeEventos lista, Evento e) {
		
		int terminalAtual = e.getTerminalOrigem();
		Quadro quadro = e.getQuadro();
		
		if(pc[terminalAtual].isEmColisao()){
			coletor.coletaColisaoPorMensagem(terminalAtual, quadro.getMensagem().getId());
			quadro.incColisoes();
			if (quadro.getColisoes() < 16) {
				Evento retransmissaoMensagemPendente = new Evento(Evento.INICIO_TX_PC, terminalAtual, quadro);
				double instanteTempoAleatorioEscolhido = pc[terminalAtual].getInstanteTempoColisao() + Mensagem.TEMPO_TRANSMISSAO_REFORCO_COLISAO + Orquestrador.gerarAtrasoAleatorioBinaryBackoff(quadro);
				lista.put(instanteTempoAleatorioEscolhido, retransmissaoMensagemPendente);
			} else {
				coletor.coletaQuadroPorMensagem(terminalAtual, quadro.getMensagem().getId());
			}
			
		}else{
			double instanteTempoFimTx = lista.getInstanteDeTempoAtual();
			
			pc[terminalAtual].setInstanteTempoFimUltimaTx(instanteTempoFimTx);
			double instanteTempoChegadaNoRxHub = instanteTempoFimTx + MeioFisico.calculaTempoPropagacao(pc[terminalAtual].getDistanciaHub());
			Evento chegadaRxHub = new Evento(Evento.CHEGADA_QUADRO_NO_RX_HUB, terminalAtual, quadro);
			lista.put(instanteTempoChegadaNoRxHub, chegadaRxHub);
			
			coletor.coletaQuadroPorMensagem(terminalAtual, quadro.getMensagem().getId());
			coletor.coletaTransmissaoDeQuadroComSucessoNaEstacao(terminalAtual);
			
			Mensagem m = quadro.getMensagem();
			m.decrementaNumeroQuadroRestantesParaTransmissao();
		
			if(m.getNumeroQuadroRestantesParaTransmissao() > 0){
				Quadro novoQuadro = new Quadro(terminalAtual, null, m);	
				
				double instanteTempoProximoQuadro = instanteTempoFimTx + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;
				Evento proximoQuadro = new Evento(Evento.INICIO_TX_PC, terminalAtual, novoQuadro);
				lista.put(instanteTempoProximoQuadro, proximoQuadro);
			}else{
				coletor.finalizaColetaTam(terminalAtual, m.getId(), pc[terminalAtual].getInstanteTempoInicioUltimaTx());
			}
		}

		
	}

	private static void tratarEventoChegadaDeQuadroNoRxDoHub(int numTerminais, Terminal[] pc, ListaDeEventos lista, Evento e) {

		int terminalDeOrigem = e.getTerminalOrigem();

		double instanteDeTempoDoBroadcast = lista.getInstanteDeTempoAtual();

		for (int i = 0; i < numTerminais; i++) {
			Quadro quadroi = new Quadro(e.getQuadro().getIdRemetente(), i, e.getQuadro().getMensagem());
			Evento inicioChegadaQuadroNoPc = new Evento(Evento.INICIO_CHEGADA_QUADRO_NO_RX_TERMINAL, terminalDeOrigem, quadroi);
			double instanteDeTempoDeInicioChegadaDoQuadroNoRxTerminal = instanteDeTempoDoBroadcast + MeioFisico.calculaTempoPropagacao(pc[i].getDistanciaHub());
			lista.put(instanteDeTempoDeInicioChegadaDoQuadroNoRxTerminal, inicioChegadaQuadroNoPc);
		}
	}

	private static void tratarEventoInicioChegadaDeQuadroNoRxDoTerminal(ColetorEstatisticas coletor,
			Terminal[] pc, ListaDeEventos lista, Evento e) {

		Quadro quadro = e.getQuadro();
		int terminalAtual = quadro.getIdDestinatario();

		pc[terminalAtual].setMeioOcupado(true);		
		pc[terminalAtual].setIdTerminalUltimoRx(quadro.getIdRemetente());
		
		if (terminalAtual == 0)
			coletor.coletaInicioPeriodoOcupado(lista.getInstanteDeTempoAtual());

		double instanteAtual = lista.getInstanteDeTempoAtual();
		if((terminalAtual != quadro.getIdRemetente()) && (pc[terminalAtual].getInstanteTempoInicioUltimaTx() < instanteAtual) && (instanteAtual < pc[terminalAtual].getInstanteTempoFimUltimaTx())){
			pc[terminalAtual].setEmColisao(true);
			pc[terminalAtual].setInstanteTempoColisao(instanteAtual);
			
			Mensagem mColisao = new Mensagem();
			Quadro qColisao = new Quadro(terminalAtual, null, mColisao);
			
			Evento chegadaReforcoColisaoRxHub = new Evento(Evento.CHEGADA_QUADRO_NO_RX_HUB, terminalAtual, qColisao);
			double instanteTempoFimTxColisao = lista.getInstanteDeTempoAtual() + Mensagem.TEMPO_TRANSMISSAO_REFORCO_COLISAO;
			double instanteTempoChegadaReforcoRxHub = instanteTempoFimTxColisao + MeioFisico.calculaTempoPropagacao(pc[terminalAtual].getDistanciaHub());
			lista.put(instanteTempoChegadaReforcoRxHub, chegadaReforcoColisaoRxHub);
		}
		
		Evento fimChegadaQuadroRxTerminal = new Evento(Evento.FIM_CHEGADA_QUADRO_NO_RX_TERMINAL, quadro.getIdRemetente(), quadro);
		double instanteTempoFimRx = (quadro.getMensagem().getTipoMensagem() == Mensagem.MENSAGEM_PADRAO)?(lista.getInstanteDeTempoAtual() + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO):(lista.getInstanteDeTempoAtual() + Mensagem.TEMPO_TRANSMISSAO_REFORCO_COLISAO);
		pc[terminalAtual].setInstanteTempoFimUltimoRx(instanteTempoFimRx);
		lista.put(instanteTempoFimRx, fimChegadaQuadroRxTerminal);
	}

	private static void tratarEventoFimChegadaDeQuadroNoRxDoTerminal(ColetorEstatisticas coletor,
			Terminal[] pc, ListaDeEventos lista, Evento e) {
		pc[e.getQuadro().getIdDestinatario()].setMeioOcupado(false);

		if (e.getQuadro().getIdDestinatario() == 0)
			coletor.coletaFimPeriodoOcupado(lista.getInstanteDeTempoAtual());
	}

	private static double gerarAtrasoAleatorioBinaryBackoff(Quadro quadroPendente) {

		int numColisoes = quadroPendente.getColisoes();
		numColisoes = Math.min(10, numColisoes);

		double randomico = GeradorRandomicoSingleton.getInstance().gerarProximoRandomico();
		int intervalos = (int) (randomico % (2 ^ numColisoes - 1));

		return intervalos * Quadro.SLOT_RETRANSMISSAO;
	}
	
	private static void verbosePorEvento(String tempo, String numEventoAtual, String terminal, String rodada, String mensagem, String quadrosRestantes, String tipoEvento){
		System.out.println("Tempo: "+tempo+" | #Evento: "+numEventoAtual+" | Terminal: "+terminal+" | Rodada: "+rodada+" | Quadros restantes: "+quadrosRestantes+" | Mensagem no.: "+mensagem+" Tipo Evento: "+tipoEvento);
	}
}

