package br.com.wifeleviro.ad;

import br.com.wifeleviro.ad.modelo.Evento;
import br.com.wifeleviro.ad.modelo.ListaDeEventos;
import br.com.wifeleviro.ad.modelo.MeioFisico;
import br.com.wifeleviro.ad.modelo.Mensagem;
import br.com.wifeleviro.ad.modelo.Quadro;
import br.com.wifeleviro.ad.modelo.Terminal;

public class Orquestrador {
	
	public static void main(String[] args) {
		Terminal[] pc = new Terminal[4];
		pc[0] = new Terminal(0, 100, Terminal.TIPO_DETERMINISTICO, 5, 0.1);
		pc[1] = new Terminal(1, 80, Terminal.TIPO_DETERMINISTICO, 4, 10);
		pc[2] = new Terminal(2, 60, Terminal.TIPO_DETERMINISTICO, 3, 20);
		pc[3] = new Terminal(3, 40, Terminal.TIPO_DETERMINISTICO, 2, 0.7);
		
		ListaDeEventos listaEventos = new ListaDeEventos();
		
		for(int i = 0; i<4; i++)
			listaEventos.put(pc[i].getInstanteTempoInicial(), new Evento(i, Evento.GERAR_MENSAGEM));
		
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
					// TODO tratarEventoGerarReforcoColisao(pc, listaEventos, e);
					tratarEventoGerarReforcoColisao(pc, listaEventos, e);
					break;
			}
		}
	}
	
	private static void tratarEventoGerarMensagem(Terminal[] pc, ListaDeEventos lista, Evento e){
	
		int terminal = e.getIdTerminalOrigemDaMensagem();
		
		double instanteDeTempo = lista.getInstanteDeTempoAtual();
		double instanteDeTempoDaMensagem = instanteDeTempo;
		
		//Crio a mensagem a ser transmitida.
		Mensagem mensagem = new Mensagem(pc[terminal].getpMensagens());

		//Crio o primeiro quadro da mensagem para ser transmitido no tx.
		Evento inicioTxPrimeiroQuadroMensagem = new Evento(terminal, mensagem, Evento.INICIO_TX_PC);
		double instanteDeTempoInicioTxPrimeiroQuadroMensagem = instanteDeTempoDaMensagem;
		lista.put(instanteDeTempoInicioTxPrimeiroQuadroMensagem, inicioTxPrimeiroQuadroMensagem);
				
		//Crio a próxima mensagem.
		Evento proximaMensagem = new Evento(terminal, Evento.GERAR_MENSAGEM);
		double instanteDeTempoDaProximaMensagem = instanteDeTempo + pc[terminal].gerarProximoInstanteDeTempoDeMensagem();
		lista.put(instanteDeTempoDaProximaMensagem, proximaMensagem);
		
	}
	
	private static void tratarEventoInicioTxPc(Terminal[] pc, ListaDeEventos lista, Evento e){
		
		int terminal = e.getIdTerminalOrigemDaMensagem();
		
		if(pc[terminal].isMeioOcupado()){

			double instanteTempoFimRx = pc[terminal].getInstanteTempoFimUltimoRx();
			
			double instanteTempoInicioTx = instanteTempoFimRx + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;
			double instanteTempoFimTx = instanteTempoInicioTx + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO;
			pc[terminal].setInstanteTempoInicioUltimaTx(instanteTempoInicioTx);
			pc[terminal].setInstanteTempoFimUltimaTx(instanteTempoFimTx);
			
			double instanteTempoFimChegadaRxHub = instanteTempoFimTx + MeioFisico.calculaTempoPropagacao(pc[terminal].getDistanciaHub()); 
			Evento chegadaRxHub = new Evento(terminal, Evento.CHEGADA_QUADRO_NO_RX_HUB);
			lista.put(instanteTempoFimChegadaRxHub, chegadaRxHub);
		
			e.getMensagem().decrementaNumeroQuadroRestantesParaTransmissao();
			if(e.getMensagem().getNumeroQuadroRestantesParaTransmissao() > 0){
				double instanteDeTempoDoProximoQuadro = instanteTempoFimTx + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;
				lista.put(instanteDeTempoDoProximoQuadro, e);
			}
			
		}else{
		
			if((lista.getInstanteDeTempoAtual() - pc[terminal].getInstanteTempoFimUltimaTx()) > Quadro.TEMPO_MINIMO_ENTRE_QUADROS){
				double instanteTempoInicioDeTx = lista.getInstanteDeTempoAtual();
				double instanteTempoFimDeTx = instanteTempoInicioDeTx + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO; 
				
				pc[terminal].setInstanteTempoInicioUltimaTx(instanteTempoInicioDeTx);
				pc[terminal].setInstanteTempoFimUltimaTx(instanteTempoFimDeTx);
				
				Mensagem mensagem = e.getMensagem();
				mensagem.decrementaNumeroQuadroRestantesParaTransmissao();
				
				if(mensagem.getNumeroQuadroRestantesParaTransmissao() > 0){
					double instanteDeTempoDoProximoQuadro = instanteTempoFimDeTx + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;
					lista.put(instanteDeTempoDoProximoQuadro, e);
				}else{
					// Fim da mensagem.
				}
			}else{
				double acrescimoInstanteTempoTx = Quadro.TEMPO_MINIMO_ENTRE_QUADROS - (lista.getInstanteDeTempoAtual() - pc[terminal].getInstanteTempoFimUltimaTx());
				double instanteTempoInicioTx = lista.getInstanteDeTempoAtual() + acrescimoInstanteTempoTx;
				double instanteTempoFimTx = instanteTempoInicioTx + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO;
				pc[terminal].setInstanteTempoInicioUltimaTx(instanteTempoInicioTx);
				pc[terminal].setInstanteTempoFimUltimaTx(instanteTempoFimTx);
				
				double instanteTempoFimChegadaRxHub = instanteTempoFimTx + MeioFisico.calculaTempoPropagacao(pc[terminal].getDistanciaHub()); 
				Evento chegadaRxHub = new Evento(terminal, Evento.CHEGADA_QUADRO_NO_RX_HUB);
				lista.put(instanteTempoFimChegadaRxHub, chegadaRxHub);
			
				e.getMensagem().decrementaNumeroQuadroRestantesParaTransmissao();
				if(e.getMensagem().getNumeroQuadroRestantesParaTransmissao() > 0){
					double instanteDeTempoDoProximoQuadro = instanteTempoFimTx + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;
					lista.put(instanteDeTempoDoProximoQuadro, e);
				}
			}
		}
	}
	
	private static void tratarEventoChegadaDeQuadroNoRxDoHub(Terminal[] pc, ListaDeEventos lista, Evento e){
		
		int terminalDeOrigem = e.getIdTerminalOrigemDaMensagem();
		
		double instanteDeTempoDoBroadcast = lista.getInstanteDeTempoAtual();
		
		Evento inicioChegadaQuadroNoPc[] = new Evento[4];
		Evento fimChegadaQuadroNoPc[] = new Evento[4];
		for(int i = 0; i < 4; i++){
			inicioChegadaQuadroNoPc[i] = new Evento(terminalDeOrigem, i, Evento.INICIO_CHEGADA_QUADRO_NO_RX_TERMINAL);
			double instanteDeTempoDeInicioChegadaDoQuadroNoRxTerminal = instanteDeTempoDoBroadcast + MeioFisico.calculaTempoPropagacao(pc[i].getDistanciaHub());
			lista.put(instanteDeTempoDeInicioChegadaDoQuadroNoRxTerminal, inicioChegadaQuadroNoPc[i]);
			
			fimChegadaQuadroNoPc[i] = new Evento(terminalDeOrigem, i, Evento.FIM_CHEGADA_QUADRO_NO_RX_TERMINAL);
			double instanteDeTempoDeFimChegadaDoQuadroNoRxTerminal = instanteDeTempoDeInicioChegadaDoQuadroNoRxTerminal + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO;
			lista.put(instanteDeTempoDeFimChegadaDoQuadroNoRxTerminal, fimChegadaQuadroNoPc[i]);
		}
	}
	
	private static void tratarEventoInicioChegadaDeQuadroNoRxDoTerminal(Terminal[] pc, ListaDeEventos lista, Evento e){
		if(e.getIdTerminalOrigemDaMensagem() != e.getIdTerminalDestino()){
			pc[e.getIdTerminalDestino()].setMeioOcupado(true);
			pc[e.getIdTerminalDestino()].setInstanteTempoFimUltimoRx(lista.getInstanteDeTempoAtual() + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO);
		
			// TODO Implementar colisão durante transmissão.
			
		}else
			pc[e.getIdTerminalDestino()].setMeioOcupado(false);
	}

	private static void tratarEventoFimChegadaDeQuadroNoRxDoTerminal(Terminal[] pc, ListaDeEventos lista, Evento e){
		pc[e.getIdTerminalDestino()].setMeioOcupado(false);
	}
}
