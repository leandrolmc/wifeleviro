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

		//Crio uma chegada no HUB com o acréscimo de tempo necessário para o atraso do meio físico e de transmissão do quadro.
		Evento inicioTxPc = new Evento(terminal, Evento.INICIO_TX_PC);
		double instanteDeTempoInicioTxPc = instanteDeTempoDaMensagem + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO; 
		//Atualizo o contador de tempo local para o instante em que terminei de transmitir no RX do PC.
		instanteDeTempoDaMensagem = instanteDeTempoFimTransmissaoNoTxDoPc;
		//O tempo que chegará no HUB será o tempo final no RX + o tempo de propagação no meio.
		double instanteDeChegadaNoHub = Quadro.TEMPO_MINIMO_ENTRE_QUADROS+instanteDeTempoFimTransmissaoNoTxDoPc + MeioFisico.calculaTempoPropagacao(pc[terminal].getDistanciaHub());
		lista.put(instanteDeChegadaNoHub, chegadaQuadroNoHub);
				
		//Crio mais uma mensagem.
		Evento proximaMensagem = new Evento(terminal, Evento.GERAR_MENSAGEM);
		double instanteDeTempoDaProximaMensagem = instanteDeTempo + pc[terminal].gerarProximoInstanteDeTempoDeMensagem();
		lista.put(instanteDeTempoDaProximaMensagem, proximaMensagem);
		
	}
	
	private static void tratarEventoInicioTxPc(Terminal[] pc, ListaDeEventos lista, Evento e){
		
		
		
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
		pc[e.getIdTerminalDestino()].setMeioOcupado(true);
	}

	private static void tratarEventoFimChegadaDeQuadroNoRxDoTerminal(Terminal[] pc, ListaDeEventos lista, Evento e){
		pc[e.getIdTerminalDestino()].setMeioOcupado(false);
	}
}
