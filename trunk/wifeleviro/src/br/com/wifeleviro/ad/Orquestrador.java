package br.com.wifeleviro.ad;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import br.com.wifeleviro.ad.modelo.Evento;
import br.com.wifeleviro.ad.modelo.ListaDeEventos;
import br.com.wifeleviro.ad.modelo.MeioFisico;
import br.com.wifeleviro.ad.modelo.Mensagem;
import br.com.wifeleviro.ad.modelo.Quadro;
import br.com.wifeleviro.ad.modelo.Terminal;
import br.com.wifeleviro.ad.modelo.ListaDeEventos.ProximoEvento;
import br.com.wifeleviro.ad.util.GeradorRandomicoSingleton;
import br.com.wifeleviro.ad.util.estatisticas.ColetorEstatisticas;
import br.com.wifeleviro.ad.util.estatisticas.DadosFinaisDaRodada;
import br.com.wifeleviro.ad.util.estatisticas.EstatisticasColetadas;
import br.com.wifeleviro.ad.util.estatisticas.EstatisticasColisaoRodada;
import br.com.wifeleviro.ad.util.estatisticas.EstatisticasUtilizacaoRodada;
import br.com.wifeleviro.ad.util.estatisticas.EstatisticasVazaoRodada;
import br.com.wifeleviro.ad.util.estatisticas.IntervaloDeConfianca;
import br.com.wifeleviro.ad.util.estatisticas.ColetorEstatisticas.Estatisticas;
import br.com.wifeleviro.ad.util.estatisticas.metricas.TAm;
import br.com.wifeleviro.ad.util.estatisticas.metricas.TAp;
/*
 * Classe responsável por gerir os fluxos do programa
 */
public class Orquestrador {

	// Armazena a quantidade de terminais configuradas para a simulação.
	private int qtdTerminais;
	// Vetor que armazena cada objeto Terminal para acesso exclusivo a cada um.
	private Terminal[] terminais;
	// Armazena o número de mensagens em cada rodada e é zerado no início de cada rodada.
	private long qtdMensagensNaRodada;
	// Armazena o número da rodada atual ("cor" da rodada atual).
	private int rodadaAtual;

	// Construtor invocado pela classe Simulador que recebe os terminais já inicializados
	// e a quantidade dos mesmos.
	public Orquestrador(int qtdTerminais, Terminal[] terminais){
		this.qtdTerminais = qtdTerminais;
		this.terminais = terminais;
	}
	
	// Método que faz a mágica acontecer!
	public void executarSimulacao() {

		// Variável local que armazena o número de terminais ativos na simulação.
		int numTerminais = this.qtdTerminais;
		
		// Vetor local que manipula o vetor de terminais inicializado na classe Simulador.
		Terminal[] pc = this.terminais;

		// Inicialização da lista de eventos vazia.
		ListaDeEventos listaEventos = new ListaDeEventos();
		// Inicialização de um vetor para manipular os resultados de estatísticas
		// coletadas para cada terminal.
		EstatisticasColetadas[] statsColetadas = new EstatisticasColetadas[numTerminais]; 

		// Veriável para verificar o tempo de início de rodada, pois este será comparado
		// com o mínimo valor gerado pelas amostras dos pcs.
		double inicioRodada = (2 ^ 31) - 1;

		// Variável inicializada com -1 pois será incrementada no início de cada rodada.
		// A rodada 0 (ZERO) corresponde a fase transisente.
		this.rodadaAtual = -1; 

		// Coloca na lista de eventos um evento de GERAR_MENSAGEM para cada terminal ativo,
		// inicializa uma variável de coleta de estatísticas para cada terminal ativo e 
		// atualiza a variável inicioRodada para o instante de tempo mínimo gerado o evento
		// GERAR_MENSAGEM dentre os terminais ativos.
		for (int i = 0; i < numTerminais; i++) {
			listaEventos.put(pc[i].getInstanteTempoInicial(), new Evento(Evento.GERAR_MENSAGEM, i, null));
			inicioRodada = Math.min(inicioRodada, pc[i].getInstanteTempoInicial());
			statsColetadas[i] = new EstatisticasColetadas();
		}

		// Zera o coletor de estatísticas.
		ColetorEstatisticas coletor = null;
		// Inicializo com true para compor corretamente o and final.
		boolean intervaloDeConfiancaOK = true; 
		// Loop principal. Cada passagem no loop, é uma rodada.
		do {
			// Incrementa a variável indicadora da rodada atual.
			++this.rodadaAtual;
			// Zera a variável armazenadora de quantidade de mensagens
			// geradas por rodada.
			this.qtdMensagensNaRodada = 0;
			
			// Em cada rodada, reinicializa a variável para true para compor
			// o AND final que avalia o critério de parada da simulação.
			intervaloDeConfiancaOK = true;
			
			// Zera o contador do número de eventos por rodada que é utilizado
			// como critério de parada por rodada (transiente e normal).
			long numEventosDaRodada = 0;
			
			// Coleta o valor do inicioRodada caso a rodadaAtual não seja
			// a fase transiente.
			if(this.rodadaAtual > 0)
				inicioRodada = listaEventos.getInstanteDeTempoAtual();

			// Marca no console o início da rodada.
			if(this.rodadaAtual == 0){
				System.out.println("== FASE TRANSIENTE ==");
				System.out.println("== "+(new SimpleDateFormat("HH:mm:ss").format(new GregorianCalendar().getTime()))+" ==");
			}else
				System.out.println("== RODADA "+this.rodadaAtual+" ==");
			
			// Inicia o coletor de estatísticas para o número de terminais ativos.
			coletor = new ColetorEstatisticas(this.rodadaAtual, numTerminais);
			// coleta o valor do instante de tempo do início da rodada.
			coletor.coletaInicioRodada(inicioRodada);

			// Inicializa a variável que armazena o instante de tempo de fim
			// da rodada atual.
			double fimDaRodada = 0;

			// Loop de eventos. Cada passagem no loop é o tratamento de um evento.
			while ((this.rodadaAtual == 0 && numEventosDaRodada <= 1000000) || (this.rodadaAtual > 0 && numEventosDaRodada < 500000)) {
				
				// Recupera da lista de eventos o próximo evento a ser executado.
				ProximoEvento proximo = listaEventos.proximoEvento();
				// Deixa sempre atualizado o valor da variável fimDaRodada com o 
				// instante de tempo do último evento.
				fimDaRodada = proximo.getTempo();
				// Recupera o evento de fato da variável proximo.
				Evento e = proximo.getEvento();

				// Zera a mensagem a ser tratada  neste evento.
				Mensagem msg = null;
				// Caso o evento já esteja transportando um quadro, recupera a mensagem
				// vinculada a este quadro.
				if(e.getQuadro() != null){
					msg = e.getQuadro().getMensagem();
				}
				
				// Controlador de fluxo principal que desvia de acordo com o tipo de evento.
				// O controlador de fluxo ativa o tratamento adequado do evento de acordo com 
				// seu tipo, incrementa o número de eventos tratados durante a rodada em seu fim
				// e ativa métodos de verbose para depuração do evento ou do quadro em questão.
				switch (e.getTipoEvento()) {
					case Evento.GERAR_MENSAGEM:
						tratarEventoGerarMensagem(this.rodadaAtual, coletor, pc, listaEventos, e);
						++this.qtdMensagensNaRodada;
						verbosePorEvento(""+fimDaRodada, ""+numEventosDaRodada, ""+e.getTerminalOrigem(), ""+rodadaAtual, msg!=null?
								""+msg.getId():"MENSAGEM NAO IDENTIFICADA", msg!=null?""+msg.getNumeroQuadroRestantesParaTransmissao():
									"SEM QUADROS", "Gerar Mensagem");
						verbosePorQuadro(""+fimDaRodada, ""+e.getTerminalOrigem(), ""+null, "VAZIO", "VAZIO", "VAZIO","GERAR MENSAGEM");
						++numEventosDaRodada;
						break;
					case Evento.INICIO_TX_PC:
						tratarEventoInicioTxPc(coletor, pc, listaEventos, e);
						verbosePorEvento(""+fimDaRodada, ""+numEventosDaRodada, ""+e.getTerminalOrigem(), ""+rodadaAtual, msg!=null?
								""+msg.getId():"MENSAGEM NAO IDENTIFICADA", msg!=null?""+msg.getNumeroQuadroRestantesParaTransmissao():
									"SEM QUADROS", "Inicio TX do PC");
						verbosePorQuadro(""+fimDaRodada, ""+e.getTerminalOrigem(), ""+e.getQuadro().getIdDestinatario(),""+msg.getId(), ""+e.getQuadro().getId(), ""+e.getQuadro().getColisoes(), ""+msg.getNumeroQuadroRestantesParaTransmissao(),"INICIO TX PC");
						++numEventosDaRodada;
						break;
					case Evento.FIM_TX_PC:
						tratarEventoFimTxPc(this.rodadaAtual, coletor, pc, listaEventos, e);
						verbosePorEvento(""+fimDaRodada, ""+numEventosDaRodada, ""+e.getTerminalOrigem(), ""+rodadaAtual, msg!=null?
								""+msg.getId():"MENSAGEM NAO IDENTIFICADA", msg!=null?""+msg.getNumeroQuadroRestantesParaTransmissao():
									"SEM QUADROS", "Fim TX do PC");
						verbosePorQuadro(""+fimDaRodada, ""+e.getTerminalOrigem(), ""+e.getQuadro().getIdDestinatario(), ""+msg.getId(), ""+e.getQuadro().getId(), ""+msg.getNumeroQuadroRestantesParaTransmissao(),"FIM TX PC");
						++numEventosDaRodada;
						break;
					case Evento.CHEGADA_QUADRO_NO_RX_HUB:
						tratarEventoChegadaDeQuadroNoRxDoHub(this.rodadaAtual, numTerminais, pc, listaEventos, e);
						verbosePorEvento(""+fimDaRodada, ""+numEventosDaRodada, ""+e.getTerminalOrigem(), ""+rodadaAtual, msg!=null?
								""+msg.getId():"MENSAGEM NAO IDENTIFICADA", msg!=null?""+msg.getNumeroQuadroRestantesParaTransmissao():
									"SEM QUADROS", "Chegada Quadro no RX do HUB");
						verbosePorQuadro(""+fimDaRodada, ""+e.getTerminalOrigem(), ""+e.getQuadro().getIdDestinatario(), ""+msg.getId(), ""+e.getQuadro().getId(), ""+msg.getNumeroQuadroRestantesParaTransmissao(), "CHEGADA QUADRO NO RX HUB");
						++numEventosDaRodada;
						break;
					case Evento.INICIO_CHEGADA_QUADRO_NO_RX_TERMINAL:
						tratarEventoInicioChegadaDeQuadroNoRxDoTerminal(this.rodadaAtual, coletor, pc, listaEventos, e);
						verbosePorEvento(""+fimDaRodada, ""+numEventosDaRodada, ""+e.getTerminalOrigem(), ""+rodadaAtual, msg!=null?
								""+msg.getId():"MENSAGEM NAO IDENTIFICADA", msg!=null?""+msg.getNumeroQuadroRestantesParaTransmissao():
									"SEM QUADROS", "Inicio Chegada Quadro no RX do terminal");
						verbosePorQuadro(""+fimDaRodada, ""+e.getTerminalOrigem(), ""+e.getQuadro().getIdDestinatario(), ""+msg.getId(), ""+e.getQuadro().getId(), ""+msg.getNumeroQuadroRestantesParaTransmissao(),"INICIO CHEGADA QUADRO NO RX TERMINAL");
						++numEventosDaRodada;
						break;
					case Evento.FIM_CHEGADA_QUADRO_NO_RX_TERMINAL:
						tratarEventoFimChegadaDeQuadroNoRxDoTerminal(this.rodadaAtual, coletor, pc, listaEventos, e);
						verbosePorEvento(""+fimDaRodada, ""+numEventosDaRodada, ""+e.getTerminalOrigem(), ""+rodadaAtual, msg!=null?
								""+msg.getId():"MENSAGEM NAO IDENTIFICADA", msg!=null?""+msg.getNumeroQuadroRestantesParaTransmissao():
									"SEM QUADROS", "Fim Chegada Quadro no RX do terminal");
						verbosePorQuadro(""+fimDaRodada, ""+e.getTerminalOrigem(), ""+e.getQuadro().getIdDestinatario(),""+msg.getId(), ""+e.getQuadro().getId(), ""+msg.getNumeroQuadroRestantesParaTransmissao(),"FIM CHEGADA QUADRO NO RX TERMINAL");
						++numEventosDaRodada;
						break;
					case Evento.INICIO_REFORCO_COLISAO:
						tratarEventoInicioReforcoColisao(coletor, pc, listaEventos, e);
						verbosePorEvento(""+fimDaRodada, ""+numEventosDaRodada, ""+e.getTerminalOrigem(), ""+rodadaAtual, msg!=null?
								""+msg.getId():"MENSAGEM NAO IDENTIFICADA", msg!=null?""+msg.getNumeroQuadroRestantesParaTransmissao():
									"SEM QUADROS", "Inicio TX Reforco de Colisao");
						verbosePorQuadro(""+fimDaRodada, ""+e.getTerminalOrigem(), ""+e.getQuadro().getIdDestinatario(), ""+msg.getId(), ""+e.getQuadro().getId(), ""+msg.getNumeroQuadroRestantesParaTransmissao(),"INICIO REFORCO COLISAO");
						++numEventosDaRodada;
						break;
					case Evento.FIM_REFORCO_COLISAO:
						tratarEventoFimReforcoColisao(coletor, pc, listaEventos, e);
						verbosePorEvento(""+fimDaRodada, ""+numEventosDaRodada, ""+e.getTerminalOrigem(), ""+rodadaAtual, msg!=null?
								""+msg.getId():"MENSAGEM NAO IDENTIFICADA", msg!=null?""+msg.getNumeroQuadroRestantesParaTransmissao():
									"SEM QUADROS", "Fim TX Reforco de Colisao");
						verbosePorQuadro(""+fimDaRodada, ""+e.getTerminalOrigem(), ""+e.getQuadro().getIdDestinatario(), ""+msg.getId(), ""+e.getQuadro().getId(), ""+msg.getNumeroQuadroRestantesParaTransmissao(),"FIM REFORCO COLISAO");
						++numEventosDaRodada;
						break;
				}
			}
			
			// Apresenta no console o número de mensagens geradas na rodada.
			System.out.println("MSG NA RODADA: "+this.qtdMensagensNaRodada);

			// Coleta para as estatísticas o instante de tempo de fim da rodada.
			coletor.coletaFimRodada(fimDaRodada);
			
			// Caso a rodada atual seja a fase transiente, simplesmente finaliza a rodada
			// sem maiores tratamentos. Do contrário, avalia as estatísticas da rodada.
			if(this.rodadaAtual == 0){
				System.out.println("== FIM DA FASE TRANSIENTE ==");
				System.out.println("== "+(new SimpleDateFormat("HH:mm:ss").format(new GregorianCalendar().getTime()))+" ==");
			}else{
				// No tratamento de estatísticas da rodada, primeiro converte as estatísticas
				// que estão orientadas por rodadas para uma orientação por terminal.
				Estatisticas[] estatisticas = coletor.getEstatisticas();
				for(int i = 0; i < numTerminais; i++){
					Hashtable<Long, TAp> tap = estatisticas[i].getTap();
					Hashtable<Long, TAm> tam = estatisticas[i].getTam();
					EstatisticasColisaoRodada colisao = new EstatisticasColisaoRodada(estatisticas[i].getColisoesPorMensagem(), estatisticas[i].getQuadrosPorMensagem());
					EstatisticasUtilizacaoRodada utilizacao = new EstatisticasUtilizacaoRodada(coletor.getInstanteInicioRodada(), coletor.getInstanteFimRodada(), estatisticas[i].getUtilizacao()); 
					EstatisticasVazaoRodada vazao = new EstatisticasVazaoRodada(coletor.getInstanteInicioRodada(), coletor.getInstanteFimRodada(), estatisticas[i].getNumeroQuadrosTransmitidosComSucesso());
					statsColetadas[i].armazenar(tap, tam, colisao, utilizacao, vazao);
					
					// Calcula se o intervalo de confiança está dentro do limite aceitável para o terminal i.					
					DadosFinaisDaRodada dados = IntervaloDeConfianca.intervalosDeConfiancaDentroDoLimiteAceitavel(
							statsColetadas[i].getColTap(), 
							statsColetadas[i].getColTam(), 
							statsColetadas[i].getColEstatisticaColisaoRodada(), 
							statsColetadas[i].getColEstatisticaUtilizacaoDaRodada(), 
							statsColetadas[i].getColEstatisticaVazaoDaRodada(), 
							this.rodadaAtual);
					
					// Busca nos dados se o cálculo do intervalo de confiança está aceitável.
					// Como este cálculo é realizado POR TERMINAL, foi necessário realizar esta
					// lógica de acúmulo. Caso todos acumulem dados.getDentroDoLimite() verdadeiro,
					// os intervalos de confiança foram atingidos e o programa está pronto para parar.
					intervaloDeConfiancaOK = intervaloDeConfiancaOK &&  dados.getDentroDoLimite();
					// Apresenta no console as métricas coletadas por rodada, por estação.
					System.out.println("*************************************");
					System.out.println("--[TAp("+i+")]--");
					System.out.println("E[TAp("+i+")]: "+dados.getTap().getMediaDasAmostras());
					System.out.println("U(alpha)-L(alpha): "+dados.getTap().getTamanhoDoIntervaloDeConfianca());
					System.out.println("*************************************");
					System.out.println("--[TAm("+i+")]--");
					System.out.println("E[TAm("+i+")]: "+dados.getTam().getMediaDasAmostras());
					System.out.println("U(alpha)-L(alpha): "+dados.getTam().getTamanhoDoIntervaloDeConfianca());
					System.out.println("*************************************");
					System.out.println("--[NCm("+i+")]--");
					System.out.println("E[NCm("+i+")]: "+dados.getNcm().getMediaDasAmostras());
					System.out.println("U(alpha)-L(alpha): "+dados.getNcm().getTamanhoDoIntervaloDeConfianca());
					System.out.println("*************************************");
					if(i==0){
						System.out.println("--[Utilizacao("+i+")]--");
						System.out.println("E[Utilizacao("+i+")]: "+dados.getUtilizacao().getMediaDasAmostras());
						System.out.println("U(alpha)-L(alpha): "+dados.getUtilizacao().getTamanhoDoIntervaloDeConfianca());
						System.out.println("*************************************");
					}
					System.out.println("--[Vazao("+i+")]--");
					System.out.println("E[Vazao("+i+")]: "+dados.getVazao().getMediaDasAmostras());
					System.out.println("U(alpha)-L(alpha): "+dados.getVazao().getTamanhoDoIntervaloDeConfianca());
					System.out.println("*************************************");
				}
				
				System.out.println("== FIM RODADA "+this.rodadaAtual+" ==");
			}
			
		// Os critério de parada do loop principal da simulação estão aqui. Estes são:
		// caso a rodada atual seja maior que 30 e menor ou igual a 100 e tenha atingido o intervalo de
		// confiança desejado, pára. Caso a rodada ultrapasse a 100ª para após sua execução. 
		} while ((this.rodadaAtual <= 30) || (this.rodadaAtual > 30 && rodadaAtual <= 100 && !intervaloDeConfiancaOK));
	}

	// Trata o evento do tipo GERAR_MENSAGEM
	private static void tratarEventoGerarMensagem(int rodadaAtual, ColetorEstatisticas coletor, Terminal[] pc,
			ListaDeEventos lista, Evento e) {

		int terminalOrigem = e.getTerminalOrigem();

		double instanteDeTempo = lista.getInstanteDeTempoAtual();

		// Crio a mensagem a ser transmitida.
		Mensagem mensagem = new Mensagem(rodadaAtual, pc[terminalOrigem].getpMensagens());
		// Crio um quadro novo para esta mensagem.
		Quadro quadro = new Quadro(rodadaAtual, terminalOrigem, null, mensagem);

		// Crio o primeiro quadro da mensagem para ser transmitido no tx.
		Evento inicioTxPrimeiroQuadroMensagem = new Evento(Evento.INICIO_TX_PC, terminalOrigem, quadro);
		// Coloco o evento na lista.
		lista.put(instanteDeTempo, inicioTxPrimeiroQuadroMensagem);
		// Coleta o inicio de TAm quando o primeiro quadro é considerado para transmissão.
		coletor.iniciaColetaTam(rodadaAtual, terminalOrigem, mensagem.getId(), instanteDeTempo);

		// Crio a próxima mensagem.
		Evento proximaMensagem = new Evento(Evento.GERAR_MENSAGEM, terminalOrigem, null);
		// Calculo o instante de tempo da próxima mensagem.
		double instanteDeTempoDaProximaMensagem = instanteDeTempo + pc[terminalOrigem].gerarProximoInstanteDeTempoDeMensagem();
		// Coloco a próxima mensagem na lista.
		lista.put(instanteDeTempoDaProximaMensagem, proximaMensagem);

	}

	// Trata o evento do tipo INICIO_TX_PC
	private static void tratarEventoInicioTxPc(ColetorEstatisticas coletor, Terminal[] pc,
			ListaDeEventos lista, Evento e) {

		int terminalAtual = e.getTerminalOrigem();
		// Se chegou um quadro para transmissão e o terminal já se encontra em transmissão, coloca o quadro na fila de pendência.
		if(pc[terminalAtual].isTxOcupado()){
			try{
				pc[terminalAtual].enfileirarQuadroPendente(e);
			}catch(Exception ex){
				ex.printStackTrace();
				System.exit(1);
			}
			return;
		}
		
		// Recupero o quadro para tratamento do mesmo.
		Quadro quadro = e.getQuadro();
		
		// Inicio instante de tempo de inicio de tx;
		double instanteTempoInicioTx = -1;
		
		if (pc[terminalAtual].isMeioOcupado() && !pc[terminalAtual].isForcarTransmissao()) {
			if(pc[terminalAtual].getIdTerminalUltimoRx() == terminalAtual){
				// Caso seja identificado que o meio está ocupado mas que a transmissão é
				// pelo próprio terminal, transmite normalmente o quadro, observando o FIM_TX
				// do último quadro que foi transmitido e o intervalo de transmissão por quadro.
				instanteTempoInicioTx = lista.getInstanteDeTempoAtual();
				coletor.iniciaColetaTap(quadro.getRodada(), terminalAtual, quadro.getId(), instanteTempoInicioTx);
				pc[terminalAtual].setTxOcupado(true);
				pc[terminalAtual].setInstanteTempoInicioUltimaTx(instanteTempoInicioTx);
				double instanteTempoPrevisaoFimTx = instanteTempoInicioTx + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO;
				pc[terminalAtual].setInstanteTempoFimUltimaTx(instanteTempoPrevisaoFimTx);
				// Gera um evento de FIM_TX_PC e coloca na lista de eventos.
				Evento fimTx = new Evento(Evento.FIM_TX_PC, terminalAtual, quadro);
				lista.put(instanteTempoPrevisaoFimTx, fimTx);
				
			}else{
				// Caso seja identificado que o meio está ocupado e que a transmissão não é pelo
				// próprio terminal, atrasa o quadro para o fim te último RX.
				// Coloca novamente o evento de INICIO_TX_PC na lista de eventos para tratamento
				// no instante de tempo adequado recalculado e ativa o flag para transmissão forçada
				// no terminal atual, de modo que o quadro não precise mais avaliar meio ocupado
				// quando for sofrer tratamento.
				pc[terminalAtual].setForcarTransmissao(true);
				double tempoMeioLivre = pc[terminalAtual].getInstanteTempoFimUltimoRx();
				instanteTempoInicioTx = tempoMeioLivre + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;
				coletor.iniciaColetaTap(quadro.getRodada(), terminalAtual, quadro.getId(), instanteTempoInicioTx);
				lista.put(instanteTempoInicioTx, e);
			}
		}else{
			// Caso seja identificado que o meio está livre transmite normalmente o quadro, 
			// observando o FIM_TX do último quadro que foi transmitido e o intervalo 
			// de transmissão por quadro.
			instanteTempoInicioTx = lista.getInstanteDeTempoAtual();
			coletor.iniciaColetaTap(quadro.getRodada(), terminalAtual, quadro.getId(), instanteTempoInicioTx);
			pc[terminalAtual].setTxOcupado(true);
			// A transmissão já iniciou, então posso desligar o flag que força transmissão.
			pc[terminalAtual].setForcarTransmissao(false);
			pc[terminalAtual].setInstanteTempoInicioUltimaTx(instanteTempoInicioTx);
			double instanteTempoPrevisaoFimTx = instanteTempoInicioTx + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO;
			pc[terminalAtual].setInstanteTempoFimUltimaTx(instanteTempoPrevisaoFimTx);
			// Gera um evento de FIM_TX_PC e coloca na lista de eventos.
			Evento fimTx = new Evento(Evento.FIM_TX_PC, terminalAtual, quadro);
			lista.put(instanteTempoPrevisaoFimTx, fimTx);
		}
		
		//Caso esteja em colisão, significa que já existe um quadro contabilizando tap.
		if(pc[terminalAtual].isEmColisao()){
			pc[terminalAtual].setEmColisao(false);
		}
	}
	
	// Trata evento do tipo FIM_TX_PC
	private static void tratarEventoFimTxPc(int rodadaAtual, ColetorEstatisticas coletor, Terminal[] pc,
			ListaDeEventos lista, Evento e) {
		
		// Recupera o terminal atual para verificar qual a origem da TX.
		int terminalAtual = e.getTerminalOrigem();
		// Recupera o quadro que está sendo transmitido.
		Quadro quadro = e.getQuadro();
		
		// Aqui mostra que o quadro foi transmitido com sucesso, sendo assim,
		// é o momento correto de finalizar a coleta de TAp.
		coletor.finalizaColetaTap(quadro.getRodada(), terminalAtual, quadro.getId(), lista.getInstanteDeTempoAtual() - Mensagem.TEMPO_TRANSMISAO_POR_QUADRO);

		// Calcula o instante de tempo FIM TX e libera o terminal atual para outra TX.
		double instanteTempoFimTx = lista.getInstanteDeTempoAtual();
		pc[terminalAtual].setTxOcupado(false);
		pc[terminalAtual].setInstanteTempoFimUltimaTx(instanteTempoFimTx);
		
		// Calcula o instante de tempo do evento de chegada no HUB para o quadro em questão,
		// gera um novo evento e o encadeia na lista de eventos.
		double instanteTempoChegadaNoRxHub = instanteTempoFimTx + MeioFisico.calculaTempoPropagacao(pc[terminalAtual].getDistanciaHub());
		Evento chegadaRxHub = new Evento(Evento.CHEGADA_QUADRO_NO_RX_HUB, terminalAtual, quadro);
		lista.put(instanteTempoChegadaNoRxHub, chegadaRxHub);
		
		// Coleta a amostra de transmissão de quadro com sucesso no terminal.
		if(quadro.getRodada() == rodadaAtual && rodadaAtual > 0)
			coletor.coletaTransmissaoDeQuadroComSucessoNaEstacao(terminalAtual);
		
		// Recupero a mensagem a qual o quadro está associado.
		Mensagem m = quadro.getMensagem();
		// Decrementa o número de quadros que faltam ser transmitidos para a mensagem.
		m.decrementaNumeroQuadroRestantesParaTransmissao();
		
		// Coleta a amostra de transmissão de um quadro para a mensagem.
		if(m.getRodada()==rodadaAtual)
			coletor.coletaQuadroPorMensagem(rodadaAtual, terminalAtual, m.getId());

		// Caso a mensagem ainda tenha quadros pendente para transmissão
		// cria um novo evento INICIO_TX_PC e o coloca na lista de eventos.
		if(m.getNumeroQuadroRestantesParaTransmissao() > 0){
			Quadro novoQuadro = new Quadro(rodadaAtual, terminalAtual, null, m);	
			
			double instanteTempoProximoQuadro = instanteTempoFimTx + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;
			Evento proximoQuadro = new Evento(Evento.INICIO_TX_PC, terminalAtual, novoQuadro);
			lista.put(instanteTempoProximoQuadro, proximoQuadro);
		// Do contrário, coleta o fim de TAm.
		}else{
			if(rodadaAtual > 0)
				if(m.getTipoMensagem() == Mensagem.MENSAGEM_PADRAO)
					coletor.finalizaColetaTam(m.getRodada(), terminalAtual, m.getId(), pc[terminalAtual].getInstanteTempoInicioUltimaTx());
		}
	}

	// Trata o evento CHEGADA_QUADRO_NO_RX_HUB
	private static void tratarEventoChegadaDeQuadroNoRxDoHub(int rodadaAtual, int numTerminais, Terminal[] pc, ListaDeEventos lista, Evento e) {

		// Recupera o terminal de origem.
		int terminalDeOrigem = e.getTerminalOrigem();

		// Recupera o instante de tempo do broadcast.
		double instanteDeTempoDoBroadcast = lista.getInstanteDeTempoAtual();

		// Gera um evento de INICIO_CHEGADA_QUADRO_NO_RX_TERMINAL em cada estação
		// em seu devido instante de tempo.
		for (int i = 0; i < numTerminais; i++) {
			Quadro quadroi = new Quadro(rodadaAtual, e.getQuadro().getIdRemetente(), i, e.getQuadro().getMensagem());
			Evento inicioChegadaQuadroNoPc = new Evento(Evento.INICIO_CHEGADA_QUADRO_NO_RX_TERMINAL, terminalDeOrigem, quadroi);
			double instanteDeTempoDeInicioChegadaDoQuadroNoRxTerminal = instanteDeTempoDoBroadcast + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO + MeioFisico.calculaTempoPropagacao(pc[i].getDistanciaHub());
			lista.put(instanteDeTempoDeInicioChegadaDoQuadroNoRxTerminal, inicioChegadaQuadroNoPc);
		}
	}

	// Trata o evento INICIO_CHEGADA_QUADRO_NO_RX_TERMINAL
	private static void tratarEventoInicioChegadaDeQuadroNoRxDoTerminal(int rodadaAtual, ColetorEstatisticas coletor, Terminal[] pc, ListaDeEventos lista, Evento e) {

		Quadro quadro = e.getQuadro();
		int terminalAtual = quadro.getIdDestinatario();

		// Avisa ao terminal atual que o meio está ocupado.
		pc[terminalAtual].setMeioOcupado(true);		
		// Indica para o terminal atual quem foi o remetente do quadro que chegou.
		pc[terminalAtual].setIdTerminalUltimoRx(quadro.getIdRemetente());
		
		// Caso esteja fora da fase transiente e o quadro seja da rodada, 
		// coleta amostra de início de utilização.
		if (terminalAtual == 0 && rodadaAtual > 0 && quadro.getRodada() == rodadaAtual)
			coletor.coletaInicioPeriodoUtilizacao(terminalAtual, quadro.getId(), lista.getInstanteDeTempoAtual());

		// Avalia se id do remetente é o terminal atual, do contrário, trata colisão.
		double instanteAtual = lista.getInstanteDeTempoAtual();
		if((terminalAtual != quadro.getIdRemetente()) && (pc[terminalAtual].getInstanteTempoInicioUltimaTx() < instanteAtual) && (instanteAtual < pc[terminalAtual].getInstanteTempoFimUltimaTx())){
			// C O L I D I U //
			pc[terminalAtual].setEmColisao(true);
			pc[terminalAtual].setInstanteTempoColisao(instanteAtual);
			
			// Cria uma mensagem específica de colisão com um único quadro
			// que será transmitido forçado a partir deste instante de tempo
			// pelo tempo de transmissão de um reforço de colisão.
			Mensagem mColisao = new Mensagem(rodadaAtual);
			Quadro qColisao = new Quadro(rodadaAtual, terminalAtual, null, mColisao);
			
			// Coloca o INICIO_REFORCO_COLISAO na fila de eventos para tratamento.
			Evento chegadaReforcoColisaoRxHub = new Evento(Evento.INICIO_REFORCO_COLISAO, terminalAtual, qColisao);
			lista.put(lista.getInstanteDeTempoAtual(), chegadaReforcoColisaoRxHub);
			
			// Cancelo o próximo FIM TX do terminal atual.
			Evento fimTxCancelado = lista.removeEvento(terminalAtual, Evento.FIM_TX_PC);
			// Caso o evento de FIM TX cancelado não exista é porque já foi cancelado antes e entrou outra colisão.
			if(fimTxCancelado != null){
				// Recupero o quadro cancelado.
				Quadro quadroCancelado = fimTxCancelado.getQuadro();
				
				// Caso a rodada do quadro cancelado seja igual a rodada atual,
				// coleto amostra de colisão na mensagem.
				if(quadroCancelado.getRodada() == rodadaAtual)
					coletor.coletaColisaoPorMensagem(rodadaAtual, terminalAtual, quadroCancelado.getMensagem().getId());
				
				// Incremento o número de colisões no quadro e o coloco na lista em tempo de INICIO_TX_PC
				// recalculado pelo binary backoff.
				quadroCancelado.incColisoes();
				if (quadroCancelado.getColisoes() < 16) {
					// Crio um novo evento de INICIO TX para o terminal atual com o quadro retirado de FIM TX.
					Evento retransmissaoMensagemPendente = new Evento(Evento.INICIO_TX_PC, terminalAtual, quadroCancelado);
					// Calculo o novo instante de tempo a partir do final da transmissão do reforço de colisão.
					double instanteTempoAleatorioEscolhido = 
						instanteAtual + 
						Mensagem.TEMPO_TRANSMISSAO_REFORCO_COLISAO + 
						Orquestrador.gerarAtrasoAleatorioBinaryBackoff(quadroCancelado);
					pc[terminalAtual].setInstanteTempoInicioUltimaTx(instanteTempoAleatorioEscolhido);
					lista.put(instanteTempoAleatorioEscolhido, retransmissaoMensagemPendente);
				}			
			}
		}
		
		// Coloco na lista um evento de FIM_CHEGADA_QUADRO_NO_RX_TERMINAL em um tempo que depende do seu tipo:
		// ou mensagem padrão, ou reforço de colisão.
		Evento fimChegadaQuadroRxTerminal = new Evento(Evento.FIM_CHEGADA_QUADRO_NO_RX_TERMINAL, quadro.getIdRemetente(), quadro);
		double instanteTempoFimRx = (quadro.getMensagem().getTipoMensagem() == Mensagem.MENSAGEM_PADRAO)?(lista.getInstanteDeTempoAtual() + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO):(lista.getInstanteDeTempoAtual() + Mensagem.TEMPO_TRANSMISSAO_REFORCO_COLISAO);
		pc[terminalAtual].setInstanteTempoFimUltimoRx(instanteTempoFimRx);
		lista.put(instanteTempoFimRx, fimChegadaQuadroRxTerminal);
	}

	// Trata o evento FIM_CHEGADA_QUADRO_NO_RX_TERMINAL
	private static void tratarEventoFimChegadaDeQuadroNoRxDoTerminal(int rodadaAtual, ColetorEstatisticas coletor, Terminal[] pc, ListaDeEventos lista, Evento e) {
		int terminalAtual = e.getQuadro().getIdDestinatario();
		
		pc[terminalAtual].setMeioOcupado(false);

		// Caso não esteja na fase transiente e a rodada seja a mesma da rodada atual
		// coleto uma amostra de fim de utilização.
		if (terminalAtual == 0 && rodadaAtual > 0 && e.getQuadro().getRodada() == rodadaAtual)
			coletor.coletaFimPeriodoUtilizacao(terminalAtual, e.getQuadro().getId(), lista.getInstanteDeTempoAtual());
		
		// Recupero se há algum quadro pendente para enfileirara para tentativa de retransmissão.
		double instanteTempoInicioProximoQuadroPendente = lista.getInstanteDeTempoAtual() + Quadro.TEMPO_MINIMO_ENTRE_QUADROS; 
		while(pc[terminalAtual].temQuadrosPendentes()){
			Evento quadroPendente = pc[terminalAtual].proximoEventoQuadroPendente();
			lista.put(instanteTempoInicioProximoQuadroPendente, quadroPendente);
			instanteTempoInicioProximoQuadroPendente += Mensagem.TEMPO_TRANSMISAO_POR_QUADRO + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;
		}
	}

	// Trata o evento INICIO_REFORCO_COLISAO
	private static void tratarEventoInicioReforcoColisao(ColetorEstatisticas coletor, Terminal[] pc, ListaDeEventos lista, Evento e){
		
		int terminalColidindo = e.getTerminalOrigem();
		// Ativo um flag que informa ao terminal que estou em TX.
		pc[terminalColidindo].setTxOcupado(true);
		
		// Crio o evento de FIM_REFORCO_COLISAO e coloco na lista de eventos.
		Evento fimReforcoColisao = new Evento(Evento.FIM_REFORCO_COLISAO, terminalColidindo, e.getQuadro());
		double instanteTempoFimReforcoColisao = lista.getInstanteDeTempoAtual() + Mensagem.TEMPO_TRANSMISSAO_REFORCO_COLISAO;
		lista.put(instanteTempoFimReforcoColisao, fimReforcoColisao);
	}

	// Trata o evento FIM_REFORCO_COLISAO	
	private static void tratarEventoFimReforcoColisao(ColetorEstatisticas coletor, Terminal[] pc, ListaDeEventos lista, Evento e){
	
		int terminalColidindo = e.getTerminalOrigem();
		// Desligo o flag informando que estou saindo de TX.
		pc[terminalColidindo].setTxOcupado(false);
		
		// Crio evento de CHEGADA_QUADRO_NO_RX_HUB e coloco na lista de eventos.
		Evento chegadaReforcoColisaoRxHub = new Evento(Evento.CHEGADA_QUADRO_NO_RX_HUB, terminalColidindo, e.getQuadro());
		double instanteTempoChegadaReforcoRxHub = lista.getInstanteDeTempoAtual() + MeioFisico.calculaTempoPropagacao(pc[terminalColidindo].getDistanciaHub());
		lista.put(instanteTempoChegadaReforcoRxHub, chegadaReforcoColisaoRxHub);
	}
	
	// Calcula o atraso do binary backoff segundo o algoritmo.
	private static double gerarAtrasoAleatorioBinaryBackoff(Quadro quadroPendente) {

		int numColisoes = quadroPendente.getColisoes();
		numColisoes = Math.min(10, numColisoes);

		int randomico = GeradorRandomicoSingleton.getInstance().gerarProximoIntRandomico();
		int intervalos = Math.abs(randomico % (int)(Math.pow(2, numColisoes)));

		return intervalos * Quadro.SLOT_RETRANSMISSAO;
	}
	
	// Métodos para depuração de resultados.
	
	private static void verbosePorEvento(String tempo, String numEventoAtual, String terminal, String rodada, String mensagem, String quadrosRestantes, String tipoEvento){
//		System.out.println("Tempo: "+tempo+" | #Evento: "+numEventoAtual+" | PC org: "+terminal+" | Rodada: "+rodada+" | Quadros restantes: "+quadrosRestantes+" | Mensagem no.: "+mensagem+" | Tipo Evento: "+tipoEvento);
	}
	
	private static void verbosePorQuadro(String tempo, String trmOrg, String trmDst, String mensagem, String quadro, String quadrosRestantes, String tipoEvento){
//		System.out.println("PC org: "+trmOrg+" | PC dst: "+((trmDst==null||trmDst.equalsIgnoreCase("null"))?"?":trmDst)+" | Tempo: "+tempo+" | Tipo Evento: "+tipoEvento+" | Msg: "+mensagem+" | Qdro: "+quadro+" | Quadros restantes: "+quadrosRestantes+" ");
	}
	
	private static void verbosePorQuadro(String tempo, String trmOrg, String trmDst, String mensagem, String quadro, String colisoes, String quadrosRestantes, String tipoEvento){
//		System.out.println("PC org: "+trmOrg+" | PC dst: "+((trmDst==null||trmDst.equalsIgnoreCase("null"))?"?":trmDst)+" | Tempo: "+tempo+" | Tipo Evento: "+tipoEvento+" | Msg: "+mensagem+" | Qdro: "+quadro+" | Colisoes: "+colisoes+" | Quadros restantes: "+quadrosRestantes+" ");
	}

}

