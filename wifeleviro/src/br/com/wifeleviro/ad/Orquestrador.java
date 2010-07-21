package br.com.wifeleviro.ad;

import br.com.wifeleviro.ad.modelo.Evento;
import br.com.wifeleviro.ad.modelo.ListaDeEventos;
import br.com.wifeleviro.ad.modelo.MeioFisico;
import br.com.wifeleviro.ad.modelo.Mensagem;
import br.com.wifeleviro.ad.modelo.Quadro;
import br.com.wifeleviro.ad.modelo.Terminal;
import br.com.wifeleviro.ad.util.GeradorRandomicoSingleton;

public class Orquestrador {
	
	public static void main(String[] args) {
		Terminal[] pc = new Terminal[4];
		pc[0] = new Terminal(0, 100, Terminal.TIPO_DETERMINISTICO, 5, 0.1);
		pc[1] = new Terminal(1, 80, Terminal.TIPO_DETERMINISTICO, 4, 10);
		pc[2] = new Terminal(2, 60, Terminal.TIPO_DETERMINISTICO, 3, 20);
		pc[3] = new Terminal(3, 40, Terminal.TIPO_DETERMINISTICO, 2, 0.7);
		
		ListaDeEventos listaEventos = new ListaDeEventos();
		
		for(int i = 0; i<4; i++)
			listaEventos.put(pc[i].getInstanteTempoInicial(), new Evento(Evento.GERAR_MENSAGEM, i, null));
		
		while(!listaEventos.isEmpty()){
			
			Evento e = listaEventos.proximoEvento();
			
			switch(e.getTipoEvento()){
			
				case Evento.GERAR_MENSAGEM:
					tratarEventoGerarMensagem(pc, listaEventos, e);
					break;
				case Evento.INICIO_TX_PC:
					tratarEventoInicioTxPc(pc, listaEventos, e);
					break;
				case Evento.CHEGADA_QUADRO_NO_RX_HUB:
					tratarEventoChegadaDeQuadroNoRxDoHub(pc, listaEventos, e);
					break;
				case Evento.INICIO_CHEGADA_QUADRO_NO_RX_TERMINAL:
					tratarEventoInicioChegadaDeQuadroNoRxDoTerminal(pc, listaEventos, e);
					break;
				case Evento.FIM_CHEGADA_QUADRO_NO_RX_TERMINAL:
					tratarEventoFimChegadaDeQuadroNoRxDoTerminal(pc, listaEventos, e);
					break;
				case Evento.GERAR_REFORCO_COLISAO:
					tratarEventoGerarReforcoColisao(pc, listaEventos, e);
					break;
			}
		}
	}
	
	private static void tratarEventoGerarMensagem(Terminal[] pc, ListaDeEventos lista, Evento e){
	
		int terminalOrigem = e.getTerminalOrigem();
		
		double instanteDeTempo = lista.getInstanteDeTempoAtual();
		
		//Crio a mensagem a ser transmitida.
		Mensagem mensagem = new Mensagem(pc[terminalOrigem].getpMensagens());
		Quadro quadro = new Quadro(terminalOrigem, null, mensagem);
		
		//Crio o primeiro quadro da mensagem para ser transmitido no tx.
		Evento inicioTxPrimeiroQuadroMensagem = new Evento(Evento.INICIO_TX_PC, terminalOrigem, quadro);
		lista.put(instanteDeTempo, inicioTxPrimeiroQuadroMensagem);
				
		//Crio a próxima mensagem.
		Evento proximaMensagem = new Evento(Evento.GERAR_MENSAGEM, terminalOrigem, null);
		double instanteDeTempoDaProximaMensagem = instanteDeTempo + pc[terminalOrigem].gerarProximoInstanteDeTempoDeMensagem();
		lista.put(instanteDeTempoDaProximaMensagem, proximaMensagem);
		
	}
	
	private static void tratarEventoInicioTxPc(Terminal[] pc, ListaDeEventos lista, Evento e){
		
		int terminalOrigem = e.getTerminalOrigem();
		
		if(pc[terminalOrigem].isMeioOcupado()){

			double instanteTempoFimRx = pc[terminalOrigem].getInstanteTempoFimUltimoRx();
			
			double instanteTempoInicioTx = instanteTempoFimRx + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;
			pc[terminalOrigem].setInstanteTempoInicioUltimaTx(instanteTempoInicioTx);
			double instanteTempoFimTx = instanteTempoInicioTx + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO;
			pc[terminalOrigem].setInstanteTempoFimUltimaTx(instanteTempoFimTx);
			
			double instanteTempoFimChegadaRxHub = instanteTempoFimTx + MeioFisico.calculaTempoPropagacao(pc[terminalOrigem].getDistanciaHub()); 
			Evento chegadaRxHub = new Evento(Evento.CHEGADA_QUADRO_NO_RX_HUB, terminalOrigem, e.getQuadro());
			lista.put(instanteTempoFimChegadaRxHub, chegadaRxHub);
		
			if(e.getQuadro().getMensagem().getTipoMensagem() == Mensagem.MENSAGEM_PADRAO){
				e.getQuadro().getMensagem().decrementaNumeroQuadroRestantesParaTransmissao();
				if(e.getQuadro().getMensagem().getNumeroQuadroRestantesParaTransmissao() > 0){
					double instanteDeTempoDoProximoQuadro = instanteTempoFimTx + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;
					Quadro novoQuadro = new Quadro(terminalOrigem, null, e.getQuadro().getMensagem());
					Evento novoEvento = new Evento(Evento.INICIO_TX_PC, terminalOrigem, novoQuadro);
					lista.put(instanteDeTempoDoProximoQuadro, novoEvento);
				}
			}
		}else{
		
			double ultimaTransmissaoTx = lista.getInstanteDeTempoAtual() - pc[terminalOrigem].getInstanteTempoFimUltimaTx();
			double ultimaTransmissaoRx = lista.getInstanteDeTempoAtual() - pc[terminalOrigem].getInstanteTempoFimUltimoRx();
			
			if(ultimaTransmissaoTx > Quadro.TEMPO_MINIMO_ENTRE_QUADROS && ultimaTransmissaoRx > Quadro.TEMPO_MINIMO_ENTRE_QUADROS){
				double instanteTempoInicioDeTx = lista.getInstanteDeTempoAtual();
				double instanteTempoFimDeTx = instanteTempoInicioDeTx + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO; 
				
				pc[terminalOrigem].setInstanteTempoInicioUltimaTx(instanteTempoInicioDeTx);
				pc[terminalOrigem].setInstanteTempoFimUltimaTx(instanteTempoFimDeTx);
				
				double instanteTempoFimChegadaRxHub = instanteTempoFimDeTx + MeioFisico.calculaTempoPropagacao(pc[terminalOrigem].getDistanciaHub()); 
				Evento chegadaRxHub = new Evento(Evento.CHEGADA_QUADRO_NO_RX_HUB, terminalOrigem, e.getQuadro());
				lista.put(instanteTempoFimChegadaRxHub, chegadaRxHub);
				
				if(e.getQuadro().getMensagem().getTipoMensagem() == Mensagem.MENSAGEM_PADRAO){
					Mensagem mensagem = e.getQuadro().getMensagem();
					mensagem.decrementaNumeroQuadroRestantesParaTransmissao();
					
					if(mensagem.getNumeroQuadroRestantesParaTransmissao() > 0){
						double instanteDeTempoDoProximoQuadro = instanteTempoFimDeTx + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;
						Quadro proximoQuadro = new Quadro(terminalOrigem, null, mensagem);
						Evento proximoEvento = new Evento(Evento.INICIO_TX_PC, terminalOrigem, proximoQuadro);
						lista.put(instanteDeTempoDoProximoQuadro, proximoEvento);
					}else{
						// Fim da mensagem.
					}
				}
			}else{
				double instanteTempoMeioVazio = lista.getInstanteDeTempoAtual() + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;
				
				double instanteTempoInicioDeTx = instanteTempoMeioVazio;
				double instanteTempoFimDeTx = instanteTempoInicioDeTx + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO; 
				
				pc[terminalOrigem].setInstanteTempoInicioUltimaTx(instanteTempoInicioDeTx);
				pc[terminalOrigem].setInstanteTempoFimUltimaTx(instanteTempoFimDeTx);
				
				double instanteTempoFimChegadaRxHub = instanteTempoFimDeTx + MeioFisico.calculaTempoPropagacao(pc[terminalOrigem].getDistanciaHub()); 
				Evento chegadaRxHub = new Evento(Evento.CHEGADA_QUADRO_NO_RX_HUB, terminalOrigem, e.getQuadro());
				lista.put(instanteTempoFimChegadaRxHub, chegadaRxHub);
				
				if(e.getQuadro().getMensagem().getTipoMensagem() == Mensagem.MENSAGEM_PADRAO){
					Mensagem mensagem = e.getQuadro().getMensagem();
					mensagem.decrementaNumeroQuadroRestantesParaTransmissao();
					
					if(mensagem.getNumeroQuadroRestantesParaTransmissao() > 0){
						double instanteDeTempoDoProximoQuadro = instanteTempoFimDeTx + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;
						Quadro proximoQuadro = new Quadro(terminalOrigem, null, mensagem);
						Evento proximoEvento = new Evento(Evento.INICIO_TX_PC, terminalOrigem, proximoQuadro);
						lista.put(instanteDeTempoDoProximoQuadro, proximoEvento);
					}else{
						// Fim da mensagem.
					}
				}
			}
		}
	}
	
	private static void tratarEventoChegadaDeQuadroNoRxDoHub(Terminal[] pc, ListaDeEventos lista, Evento e){
		
		int terminalDeOrigem = e.getTerminalOrigem();
		
		double instanteDeTempoDoBroadcast = lista.getInstanteDeTempoAtual();
		
		Evento inicioChegadaQuadroNoPc[] = new Evento[4];
		for(int i = 0; i < 4; i++){
			Quadro quadroi = new Quadro(e.getQuadro().getIdRemetente(), i, e.getQuadro().getMensagem());
			inicioChegadaQuadroNoPc[i] = new Evento(Evento.INICIO_CHEGADA_QUADRO_NO_RX_TERMINAL, terminalDeOrigem, quadroi);
			double instanteDeTempoDeInicioChegadaDoQuadroNoRxTerminal = instanteDeTempoDoBroadcast + MeioFisico.calculaTempoPropagacao(pc[i].getDistanciaHub());
			lista.put(instanteDeTempoDeInicioChegadaDoQuadroNoRxTerminal, inicioChegadaQuadroNoPc[i]);
		}
	}
	
	private static void tratarEventoInicioChegadaDeQuadroNoRxDoTerminal(Terminal[] pc, ListaDeEventos lista, Evento e){
		
		Quadro quadro = e.getQuadro();
		int terminalAtual = quadro.getIdDestinatario();
		
		if(quadro.getIdRemetente() != quadro.getIdDestinatario()){
			pc[terminalAtual].setMeioOcupado(true);
			quadro.incColisoes();
			pc[terminalAtual].setQuadroPendente(quadro);
			
			Mensagem mColisao = new Mensagem();
			Quadro qColisao = new Quadro(terminalAtual, null, mColisao);
			Evento colisao = new Evento(Evento.GERAR_REFORCO_COLISAO, terminalAtual, qColisao);
			lista.put(lista.getInstanteDeTempoAtual(), colisao);
			
		}else{
			pc[terminalAtual].setMeioOcupado(false);
		}
		
		Evento fimChegadaQuadroRxTerminal = new Evento(Evento.FIM_CHEGADA_QUADRO_NO_RX_TERMINAL, quadro.getIdRemetente(), quadro);
		double instanteTempotFimTx = lista.getInstanteDeTempoAtual() + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO;
		lista.put(instanteTempotFimTx, fimChegadaQuadroRxTerminal);
	}

	private static void tratarEventoFimChegadaDeQuadroNoRxDoTerminal(Terminal[] pc, ListaDeEventos lista, Evento e){
		pc[e.getQuadro().getIdDestinatario()].setMeioOcupado(false);
	}
	
	private static void tratarEventoGerarReforcoColisao(Terminal[] pc, ListaDeEventos lista, Evento e){
		
		int terminalAtual = e.getTerminalOrigem();
		Quadro quadroPendente = pc[terminalAtual].getQuadroPendente();
		
		Evento chegadaReforcoColisaoRxHub = new Evento(Evento.CHEGADA_QUADRO_NO_RX_HUB, terminalAtual, e.getQuadro());
		double instanteTempoSaidaTxPc = lista.getInstanteDeTempoAtual() + Mensagem.TEMPO_TRANSMISSAO_REFORCO_COLISAO;
		double instanteTempoChegadaReforcoRxHub = instanteTempoSaidaTxPc + MeioFisico.calculaTempoPropagacao(pc[terminalAtual].getDistanciaHub());
		lista.put(instanteTempoChegadaReforcoRxHub, chegadaReforcoColisaoRxHub);

		if(quadroPendente.getColisoes() < 16){
			Evento retransmissaoMensagemPendente = new Evento(Evento.INICIO_TX_PC, terminalAtual, quadroPendente);
			double instanteTempoAleatorioEscolhido = instanteTempoSaidaTxPc + Orquestrador.gerarAtrasoAleatorioBinaryBackoff(quadroPendente);
			lista.put(instanteTempoAleatorioEscolhido, retransmissaoMensagemPendente);
		}
	}
	
	private static double gerarAtrasoAleatorioBinaryBackoff(Quadro quadroPendente){
		
		int numColisoes = quadroPendente.getColisoes();
		numColisoes = Math.min(10, numColisoes);
		
		double randomico = GeradorRandomicoSingleton.getInstance().gerarProximoRandomico();
		int intervalos = (int)(randomico%(2^numColisoes - 1));
		
		return intervalos*Quadro.SLOT_RETRANSMISSAO;
	}
}
