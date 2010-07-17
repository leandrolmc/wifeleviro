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
				case Evento.CHEGADA_QUADRO_NO_RX_HUB:
					// TODO tratarEventoChegadaDeQuadroNoRxDoHub(pc, listaEventos, e);
					tratarEventoChegadaDeQuadroNoRxDoHub(pc, listaEventos, e);
					break;
				case Evento.CHEGADA_QUADRO_NO_RX_TERMINAL:
					// TODO tratarEventoChegadaDeQuadroNoRxDoTerminal(pc, listaEventos, e);
					tratarEventoChegadaDeQuadroNoRxDoTerminal(pc, listaEventos, e);
					break;
				case Evento.GERAR_REFORCO_COLISAO:
					// TODO tratarEventoGerarReforcoColisao(pc, listaEventos, e);
					tratarEventoGerarReforcoColisao(pc, listaEventos, e);
					break;
			}
		}
	}
	
	private static void tratarEventoGerarMensagem(Terminal[] pc, ListaDeEventos lista, Evento e){
	
		int terminal = e.getIdTerminal();
		
		double instanteDeTempo = lista.getInstanteDeTempoAtual(); 
		
		//Crio a mensagem a ser transmitida.
		Mensagem mensagem = new Mensagem(pc[terminal].getpMensagens());
		double numQuadros = mensagem.getNumeroQuadros();
		
		for(int i = 0; i < numQuadros; i++){
			//Crio uma chegada no HUB com o acréscimo de tempo necessário para o atraso do meio físico e de transmissão do quadro.
			Evento chegadaQuadroNoHub = new Evento(terminal, Evento.CHEGADA_QUADRO_NO_RX_HUB);
			double instanteDeTempoFimTransmissaoNoRxDoPc = instanteDeTempo + mensagem.TEMPO_TRANSMISAO_POR_QUADRO; 
			//Atualizo o contador de tempo local para o instante em que terminei de transmitir no RX do PC.
			instanteDeTempo = instanteDeTempoFimTransmissaoNoRxDoPc;
			//O tempo que chegará no HUB será o tempo final no RX + o tempo de propagação no meio.
			double instanteDeChegadaNoHub = instanteDeTempoFimTransmissaoNoRxDoPc + MeioFisico.calculaTempoPropagacao(pc[terminal].getDistanciaHub());
			lista.put(instanteDeChegadaNoHub, chegadaQuadroNoHub);
		}
				
		//Crio mais uma mensagem.
		Evento proximaMensagem = new Evento(terminal, Evento.GERAR_MENSAGEM);
		double instanteDeTempoDaProximaMensagem = instanteDeTempo + pc[terminal].gerarProximoInstanteDeTempoDeMensagem();
		lista.put(instanteDeTempoDaProximaMensagem, proximaMensagem);
		
	}
	
	
	
}
