package br.com.wifeleviro.ad;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import br.com.wifeleviro.ad.modelo.Evento;
import br.com.wifeleviro.ad.modelo.HUB;
import br.com.wifeleviro.ad.modelo.ListaDeEventos;
import br.com.wifeleviro.ad.modelo.Mensagem;
import br.com.wifeleviro.ad.modelo.Quadro;
import br.com.wifeleviro.ad.modelo.Terminal;
import br.com.wifeleviro.ad.modelo.ListaDeEventos.ProximoEvento;
import br.com.wifeleviro.ad.util.GeradorRandomicoSingleton;
import br.com.wifeleviro.ad.util.estatisticas.ColetorEstatisticas;
import br.com.wifeleviro.ad.util.estatisticas.DadosFinaisDaRodada;
import br.com.wifeleviro.ad.util.estatisticas.EstatisticasColetadas;
import br.com.wifeleviro.ad.util.estatisticas.EstatisticasUtilizacaoRodada;
import br.com.wifeleviro.ad.util.estatisticas.EstatisticasVazaoRodada;
import br.com.wifeleviro.ad.util.estatisticas.IntervaloDeConfianca;
import br.com.wifeleviro.ad.util.estatisticas.ColetorEstatisticas.Estatisticas;
import br.com.wifeleviro.ad.util.estatisticas.metricas.NCm;
import br.com.wifeleviro.ad.util.estatisticas.metricas.TAm;
import br.com.wifeleviro.ad.util.estatisticas.metricas.TAp;
/*
 * Classe respons�vel por gerir os fluxos do programa
 */
public class Orquestrador {

	private int qtdTerminais;
	// Vetor que armazena cada objeto Terminal para acesso exclusivo a cada um.
	private Terminal[] terminais;
	// Armazena o n�mero de mensagens em cada rodada e � zerado no in�cio de cada rodada.
	private long qtdMensagensNaRodada;
	// Armazena o n�mero da rodada atual ("cor" da rodada atual).
	private int rodadaAtual;
	
	private boolean verbose;

	// Construtor invocado pela classe Simulador que recebe os terminais j� inicializados
	// e a quantidade dos mesmos.
	public Orquestrador(int qtdTerminais, Terminal[] terminais, boolean verbose){
		this.qtdTerminais = qtdTerminais;
		this.terminais = terminais;
		this.verbose = verbose;
	}
	
	// M�todo que faz a m�gica acontecer!
	public void executarSimulacao() {

		// Vari�vel local que armazena o n�mero de terminais ativos na simula��o.
		int numTerminais = this.qtdTerminais;
		
		// Vetor local que manipula o vetor de terminais inicializado na classe Simulador.
		Terminal[] pc = this.terminais;

		// Inicializa��o da lista de eventos vazia.
		ListaDeEventos listaEventos = new ListaDeEventos();
		// Inicializa��o de um vetor para manipular os resultados de estat�sticas
		// coletadas para cada terminal.
		EstatisticasColetadas[] statsColetadas = new EstatisticasColetadas[numTerminais]; 

		// Veri�vel para verificar o tempo de in�cio de rodada, pois este ser� comparado
		// com o m�nimo valor gerado pelas amostras dos pcs.
		double inicioRodada = (2 ^ 31) - 1;

		// Vari�vel inicializada com -1 pois ser� incrementada no in�cio de cada rodada.
		// A rodada 0 (ZERO) corresponde a fase transisente.
		this.rodadaAtual = -1; 

		// Coloca na lista de eventos um evento de GERAR_MENSAGEM para cada terminal ativo,
		// inicializa uma vari�vel de coleta de estat�sticas para cada terminal ativo e 
		// atualiza a vari�vel inicioRodada para o instante de tempo m�nimo gerado o evento
		// GERAR_MENSAGEM dentre os terminais ativos.
		for (int i = 0; i < numTerminais; i++) {
			listaEventos.put(pc[i].getInstanteTempoInicial(), new Evento(Evento.GERAR_MENSAGEM, i, null));
			inicioRodada = Math.min(inicioRodada, pc[i].getInstanteTempoInicial());
			statsColetadas[i] = new EstatisticasColetadas();
		}
		
		// Zera o coletor de estat�sticas.
		ColetorEstatisticas coletor = null;
		// Inicializo com true para compor corretamente o and final.
		boolean intervaloDeConfiancaOK = true; 
		// Loop principal. Cada passagem no loop, � uma rodada.
		do {
			// Incrementa a vari�vel indicadora da rodada atual.
			++this.rodadaAtual;
			// Zera a vari�vel armazenadora de quantidade de mensagens
			// geradas por rodada.
			this.qtdMensagensNaRodada = 0;
			
			// Em cada rodada, reinicializa a vari�vel para true para compor
			// o AND final que avalia o crit�rio de parada da simula��o.
			intervaloDeConfiancaOK = true;
			
			// Zera o contador do n�mero de eventos por rodada que � utilizado
			// como crit�rio de parada por rodada (transiente e normal).
			long numEventosDaRodada = 0;
			
			// Coleta o valor do inicioRodada caso a rodadaAtual n�o seja
			// a fase transiente.
			if(this.rodadaAtual > 0)
				inicioRodada = listaEventos.getInstanteDeTempoAtual();

			// Marca no console o in�cio da rodada.
			if(this.rodadaAtual == 0){
				System.out.println("== FASE TRANSIENTE ==");
				System.out.println("== "+(new SimpleDateFormat("HH:mm:ss").format(new GregorianCalendar().getTime()))+" ==");
			}else
				System.out.println("== RODADA "+this.rodadaAtual+" ==");
			
			// Inicia o coletor de estat�sticas para o n�mero de terminais ativos.
			coletor = new ColetorEstatisticas(this.rodadaAtual, numTerminais);
			// coleta o valor do instante de tempo do in�cio da rodada.
			coletor.coletaInicioRodada(inicioRodada);

			// Inicializa a vari�vel que armazena o instante de tempo de fim
			// da rodada atual.
			double fimDaRodada = 0;

			// Loop de eventos. Cada passagem no loop � o tratamento de um evento.
			while ((this.rodadaAtual == 0 && numEventosDaRodada <= 500000) || (this.rodadaAtual > 0 && numEventosDaRodada < 100000)) {
				
				// Recupera da lista de eventos o pr�ximo evento a ser executado.
				ProximoEvento proximo = listaEventos.proximoEvento();
				// Deixa sempre atualizado o valor da vari�vel fimDaRodada com o 
				// instante de tempo do �ltimo evento.
				fimDaRodada = proximo.getTempo();
				// Recupera o evento de fato da vari�vel proximo.
				Evento e = proximo.getEvento();

				// Zera a mensagem a ser tratada  neste evento.
				Mensagem msg = null;
				// Caso o evento j� esteja transportando um quadro, recupera a mensagem
				// vinculada a este quadro.
				if(e.getQuadro() != null){
					msg = e.getQuadro().getMensagem();
					if(msg.getId()==Long.parseLong("8995691935100721120"))
						System.out.print("");
					
					if(e.getQuadro().getId()==Long.parseLong("-3161264910536798674"))
						System.out.print("");
				}
				
				// Controlador de fluxo principal que desvia de acordo com o tipo de evento.
				// O controlador de fluxo ativa o tratamento adequado do evento de acordo com 
				// seu tipo, incrementa o n�mero de eventos tratados durante a rodada em seu fim
				// e ativa m�todos de verbose para depura��o do evento ou do quadro em quest�o.
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
						tratarEventoInicioTxPc(this.rodadaAtual, coletor, pc, listaEventos, e);
							verbosePorEvento(""+fimDaRodada, ""+numEventosDaRodada, ""+e.getTerminalOrigem(), ""+rodadaAtual, msg!=null?
									""+msg.getId():"MENSAGEM NAO IDENTIFICADA", msg!=null?""+msg.getNumeroQuadroRestantesParaTransmissao():
										"SEM QUADROS", "Inicio TX do PC");
							verbosePorQuadro(""+fimDaRodada, ""+e.getTerminalOrigem(), ""+e.getTerminalDestino(),""+msg.getId(), ""+e.getQuadro().getId(), ""+e.getQuadro().getColisoes(), ""+msg.getNumeroQuadroRestantesParaTransmissao(),"INICIO TX PC");
						++numEventosDaRodada;
						break;
					case Evento.FIM_TX_PC:
						tratarEventoFimTxPc(this.rodadaAtual, coletor, pc, listaEventos, e);
							verbosePorEvento(""+fimDaRodada, ""+numEventosDaRodada, ""+e.getTerminalOrigem(), ""+rodadaAtual, msg!=null?
									""+msg.getId():"MENSAGEM NAO IDENTIFICADA", msg!=null?""+msg.getNumeroQuadroRestantesParaTransmissao():
										"SEM QUADROS", "Fim TX do PC");
							verbosePorQuadro(""+fimDaRodada, ""+e.getTerminalOrigem(), ""+e.getTerminalDestino(), ""+msg.getId(), ""+e.getQuadro().getId(), ""+msg.getNumeroQuadroRestantesParaTransmissao(),"FIM TX PC");
						++numEventosDaRodada;
						break;
					case Evento.INICIO_CHEGADA_QUADRO_NO_RX_TERMINAL:
						tratarEventoInicioChegadaDeQuadroNoRxDoTerminal(this.rodadaAtual, coletor, pc, listaEventos, e);
							verbosePorEvento(""+fimDaRodada, ""+numEventosDaRodada, ""+e.getTerminalOrigem(), ""+rodadaAtual, msg!=null?
									""+msg.getId():"MENSAGEM NAO IDENTIFICADA", msg!=null?""+msg.getNumeroQuadroRestantesParaTransmissao():
										"SEM QUADROS", "Inicio Chegada Quadro no RX do terminal");
							verbosePorQuadro(""+fimDaRodada, ""+e.getTerminalOrigem(), ""+e.getTerminalDestino(), ""+msg.getId(), ""+e.getQuadro().getId(), ""+msg.getNumeroQuadroRestantesParaTransmissao(),"INICIO CHEGADA QUADRO NO RX TERMINAL");
						++numEventosDaRodada;
						break;
					case Evento.FIM_CHEGADA_QUADRO_NO_RX_TERMINAL:
						tratarEventoFimChegadaDeQuadroNoRxDoTerminal(this.rodadaAtual, coletor, pc, listaEventos, e);
							verbosePorEvento(""+fimDaRodada, ""+numEventosDaRodada, ""+e.getTerminalOrigem(), ""+rodadaAtual, msg!=null?
									""+msg.getId():"MENSAGEM NAO IDENTIFICADA", msg!=null?""+msg.getNumeroQuadroRestantesParaTransmissao():
										"SEM QUADROS", "Fim Chegada Quadro no RX do terminal");
							verbosePorQuadro(""+fimDaRodada, ""+e.getTerminalOrigem(), ""+e.getTerminalDestino(),""+msg.getId(), ""+e.getQuadro().getId(), ""+msg.getNumeroQuadroRestantesParaTransmissao(),"FIM CHEGADA QUADRO NO RX TERMINAL");
						++numEventosDaRodada;
						break;
					case Evento.INICIO_REFORCO_COLISAO:
						tratarEventoInicioReforcoColisao(coletor, pc, listaEventos, e);
							verbosePorEvento(""+fimDaRodada, ""+numEventosDaRodada, ""+e.getTerminalOrigem(), ""+rodadaAtual, msg!=null?
									""+msg.getId():"MENSAGEM NAO IDENTIFICADA", msg!=null?""+msg.getNumeroQuadroRestantesParaTransmissao():
										"SEM QUADROS", "Inicio TX Reforco de Colisao");
							verbosePorQuadro(""+fimDaRodada, ""+e.getTerminalOrigem(), ""+e.getTerminalDestino(), ""+msg.getId(), ""+e.getQuadro().getId(), ""+msg.getNumeroQuadroRestantesParaTransmissao(),"INICIO REFORCO COLISAO");
						++numEventosDaRodada;
						break;
					case Evento.FIM_REFORCO_COLISAO:
						tratarEventoFimReforcoColisao(this.rodadaAtual, coletor, pc, listaEventos, e);
							verbosePorEvento(""+fimDaRodada, ""+numEventosDaRodada, ""+e.getTerminalOrigem(), ""+rodadaAtual, msg!=null?
									""+msg.getId():"MENSAGEM NAO IDENTIFICADA", msg!=null?""+msg.getNumeroQuadroRestantesParaTransmissao():
										"SEM QUADROS", "Fim TX Reforco de Colisao");
							verbosePorQuadro(""+fimDaRodada, ""+e.getTerminalOrigem(), ""+e.getTerminalDestino(), ""+msg.getId(), ""+e.getQuadro().getId(), ""+msg.getNumeroQuadroRestantesParaTransmissao(),"FIM REFORCO COLISAO");
						++numEventosDaRodada;
						break;
				}
			}
			
			// Apresenta no console o n�mero de mensagens geradas na rodada.
			System.out.println("MSG NA RODADA: "+this.qtdMensagensNaRodada);

			// Coleta para as estat�sticas o instante de tempo de fim da rodada.
			coletor.coletaFimRodada(fimDaRodada);
			
			// Caso a rodada atual seja a fase transiente, simplesmente finaliza a rodada
			// sem maiores tratamentos. Do contr�rio, avalia as estat�sticas da rodada.
			if(this.rodadaAtual == 0){
				System.out.println("== FIM DA FASE TRANSIENTE ==");
				System.out.println("== "+(new SimpleDateFormat("HH:mm:ss").format(new GregorianCalendar().getTime()))+" ==");
			}else{
				// No tratamento de estat�sticas da rodada, primeiro converte as estat�sticas
				// que est�o orientadas por rodadas para uma orienta��o por terminal.
				Estatisticas[] estatisticas = coletor.getEstatisticas();
				for(int i = 0; i < numTerminais; i++){
					TAp tap = estatisticas[i].getTap();
					TAm tam = estatisticas[i].getTam();
					NCm ncm = estatisticas[i].getNCm(); 
					EstatisticasUtilizacaoRodada utilizacao = new EstatisticasUtilizacaoRodada(coletor.getInstanteInicioRodada(), coletor.getInstanteFimRodada(), estatisticas[i].getUtilizacao()); 
					EstatisticasVazaoRodada vazao = new EstatisticasVazaoRodada(coletor.getInstanteInicioRodada(), coletor.getInstanteFimRodada(), estatisticas[i].getNumeroQuadrosTransmitidosComSucesso());
					statsColetadas[i].armazenar(tap, tam, ncm, utilizacao, vazao);
					
					// Calcula se o intervalo de confian�a est� dentro do limite aceit�vel para o terminal i.					
					DadosFinaisDaRodada dados = IntervaloDeConfianca.intervalosDeConfiancaDentroDoLimiteAceitavel(
							statsColetadas[i].getColTap(), 
							statsColetadas[i].getColTam(), 
							statsColetadas[i].getColNCm(), 
							statsColetadas[i].getColEstatisticaUtilizacaoDaRodada(), 
							statsColetadas[i].getColEstatisticaVazaoDaRodada(), 
							this.rodadaAtual);
					
					// Busca nos dados se o c�lculo do intervalo de confian�a est� aceit�vel.
					// Como este c�lculo � realizado POR TERMINAL, foi necess�rio realizar esta
					// l�gica de ac�mulo. Caso todos acumulem dados.getDentroDoLimite() verdadeiro,
					// os intervalos de confian�a foram atingidos e o programa est� pronto para parar.
					intervaloDeConfiancaOK = intervaloDeConfiancaOK &&  dados.getDentroDoLimite();
					// Apresenta no console as m�tricas coletadas por rodada, por esta��o.
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
			
		// Os crit�rio de parada do loop principal da simula��o est�o aqui. Estes s�o:
		// caso a rodada atual seja maior que 30 e menor ou igual a 100 e tenha atingido o intervalo de
		// confian�a desejado, p�ra. Caso a rodada ultrapasse a 100� para ap�s sua execu��o. 
		} while ((this.rodadaAtual < 30) || (this.rodadaAtual >= 30 && rodadaAtual <= 100 && !intervaloDeConfiancaOK));
	}

	// Trata o evento do tipo GERAR_MENSAGEM
	private static void tratarEventoGerarMensagem(int rodadaAtual, ColetorEstatisticas coletor, Terminal[] pc,
			ListaDeEventos lista, Evento e) {

		int terminalOrigem = e.getTerminalOrigem();

		Double instanteDeTempo = lista.getInstanteDeTempoAtual();

		// Crio a mensagem a ser transmitida.
		Mensagem mensagem = new Mensagem(rodadaAtual, pc[terminalOrigem].getpMensagens());
		// Crio um quadro novo para esta mensagem.
		Quadro quadro = new Quadro(rodadaAtual, terminalOrigem, mensagem);
		// Caso a fila esteja vazia, segue direto para servi�o, do contr�rio, entra na fila de espera.
		if(pc[terminalOrigem].temMensagemEmServico()){
			pc[terminalOrigem].novaChegadaFila(quadro);
		}else{
			pc[terminalOrigem].novaChegadaFila(quadro);
			// Crio o primeiro quadro da mensagem para ser transmitido no tx.
			Evento inicioTxPrimeiroQuadroMensagem = new Evento(Evento.INICIO_TX_PC, terminalOrigem, quadro);
			// Coloco o evento na lista.
			lista.put(instanteDeTempo, inicioTxPrimeiroQuadroMensagem);
		}
		
		// Crio a pr�xima mensagem.
		Evento proximaMensagem = new Evento(Evento.GERAR_MENSAGEM, terminalOrigem, null);
		// Calculo o instante de tempo da pr�xima mensagem.
		double instanteDeTempoDaProximaMensagem = pc[terminalOrigem].gerarProximoInstanteDeTempoDeMensagem(instanteDeTempo);
		// Coloco a pr�xima mensagem na lista.
		lista.put(instanteDeTempoDaProximaMensagem, proximaMensagem);

	}

	// Trata o evento do tipo INICIO_TX_PC
	private static void tratarEventoInicioTxPc(int rodadaAtual, ColetorEstatisticas coletor, Terminal[] pc,
			ListaDeEventos lista, Evento e) {

		int terminalAtual = e.getTerminalOrigem();
		double instanteAtual = lista.getInstanteDeTempoAtual();

		// Sempre que um INICIO_TX_PC acontece, posso setar o flag de agendamento de retransmiss�o
		// para falso, de modo que outras retransmiss�es possam ser agendadas.
		pc[terminalAtual].setRetransmissaoAgendada(false);
		
		// Recupero o quadro para tratamento do mesmo.
		Quadro quadro = e.getQuadro();
		
		quadro.setInstanteTempoInicioTx(instanteAtual);
		
		Mensagem mensagem = quadro.getMensagem();
		
		mensagem.setInstanteTempoInicioTxQuadro(instanteAtual);
		
		// Inicio instante de tempo de inicio de tx.
		if (pc[terminalAtual].isMeioOcupado() && !pc[terminalAtual].isForcarTransmissao()){ 
			
			// Seto for�ar transmiss�o para true para depois recuperar 
			// no instante de FIM_RX_TERMINAL se esta est� com o for�ar transmiss�o ativado,
			// porque, se estiver, significa que ela encontrou o meio ocupado e
			// ficou com transmiss�o pendente.
			pc[terminalAtual].setForcarTransmissao(true);
				
		}else{
			
			// Caso seja identificado que o meio est� livre transmite normalmente o quadro, 
			// observando o FIM_TX do �ltimo quadro que foi transmitido e o intervalo 
			// de transmiss�o por quadro. Como INICIO_TX_PC sempre est� encadeado no final de 
			// uma transmiss�o com sucesso ou no final da chegada em RX, os atrasos entre
			// quadros j� foram calculados.
			pc[terminalAtual].setTxOcupado(true);
			// A transmiss�o j� iniciou, ent�o posso desligar o flag que for�a transmiss�o.
			pc[terminalAtual].setForcarTransmissao(false);
			
			// Manda o primeiro bit para todos os terminais deixando o meio ocupado
			// nos terminais diferentes do terminal atual.
			HUB.propagaAviso(Evento.INICIO_CHEGADA_QUADRO_NO_RX_TERMINAL, terminalAtual, quadro, pc, lista);
			
			// Calculo a previs�o do FIM_TX_PC e adiciono um evento destes na lista
			// para ser tratado caso o quadro n�o sofra colis�o durante sua transmiss�o.
			double instanteTempoPrevisaoFimTx = instanteAtual + Mensagem.TEMPO_TRANSMISAO_POR_QUADRO;
			quadro.setInstanteTempoInicioTx(instanteAtual);
			// Gera um evento de FIM_TX_PC e coloca na lista de eventos.
			Evento fimTx = new Evento(Evento.FIM_TX_PC, terminalAtual, quadro);
			lista.put(instanteTempoPrevisaoFimTx, fimTx);
		}
	}
	
	// Trata evento do tipo FIM_TX_PC
	private static void tratarEventoFimTxPc(int rodadaAtual, ColetorEstatisticas coletor, Terminal[] pc,
			ListaDeEventos lista, Evento e) {
		
		// Recupera o instante atual.
		double instanteAtual = lista.getInstanteDeTempoAtual();
		// Recupera o terminal atual para verificar qual a origem da TX.
		int terminalAtual = e.getTerminalOrigem();
		// Recupera o quadro que est� sendo transmitido.
		Quadro quadro = e.getQuadro();
		
		// Aqui mostra que o quadro foi transmitido com sucesso, sendo assim,
		// � o momento correto de finalizar a coleta de TAp.
		// Recupero o instante de in�cio de acesso do quadro armazenado nele mesmo e
		// recupero o �ltimo instante de in�cio de tx para calcular o TAp.
		double tempoInicioTAp = quadro.getInstanteTempoInicioAcesso();
		double tempoFimTAp = quadro.getInstanteTempoInicioTx();
		coletor.coletaTap(quadro.getRodada(), terminalAtual, tempoInicioTAp, tempoFimTAp);

		// Libera o terminal atual para outra TX.
		pc[terminalAtual].setTxOcupado(false);
		
		HUB.propagaAviso(Evento.FIM_CHEGADA_QUADRO_NO_RX_TERMINAL, terminalAtual, quadro, pc, lista);
		
		// Coleta a amostra de transmiss�o de quadro com sucesso no terminal.
		if(quadro.getRodada() == rodadaAtual && rodadaAtual > 0)
			coletor.coletaTransmissaoDeQuadroComSucessoNaEstacao(terminalAtual);
		
		// Recupero a mensagem a qual o quadro est� associado.
		Mensagem m = quadro.getMensagem();
		// Decrementa o n�mero de quadros que faltam ser transmitidos para a mensagem.
		m.decrementaNumeroQuadroRestantesParaTransmissao();
		
		// Caso a mensagem ainda tenha quadros pendente para transmiss�o
		// cria um novo evento INICIO_TX_PC e o coloca na lista de eventos.
		if(m.getNumeroQuadroRestantesParaTransmissao() > 0){
			Quadro novoQuadro = new Quadro(rodadaAtual, terminalAtual, m);	
			pc[terminalAtual].colocarQuadroEmServico(novoQuadro);
			
			double instanteTempoProximoQuadro = instanteAtual + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;
			Evento proximoQuadro = new Evento(Evento.INICIO_TX_PC, terminalAtual, pc[terminalAtual].getQuadroEmServico());
			lista.put(instanteTempoProximoQuadro, proximoQuadro);
		// Do contr�rio, coleta o fim de TAm e verifica se h� nova mensagem
		// para ser transmitida.
		}else{
			if(m.getTipoMensagem() == Mensagem.MENSAGEM_PADRAO){
				if(rodadaAtual > 0){
					coletor.coletaTam(m.getRodada(), terminalAtual, m.getInstanteTempoInicioAcesso(), m.getInstanteTempoFimAcesso());
					coletor.coletaColisaoPorMensagem(terminalAtual, m);
				}
				pc[terminalAtual].mensagemEmServicoFinalizada();
				
				if(pc[terminalAtual].temMensagemEmServico()){
					Quadro novoQuadro = pc[terminalAtual].getQuadroEmServico();	
					
					double instanteTempoProximaMensagem = instanteAtual + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;
					Evento proximaMensagem = new Evento(Evento.INICIO_TX_PC, terminalAtual, novoQuadro);
					lista.put(instanteTempoProximaMensagem, proximaMensagem);
				}
			}
		}
	}

	// Trata o evento INICIO_CHEGADA_QUADRO_NO_RX_TERMINAL
	private static void tratarEventoInicioChegadaDeQuadroNoRxDoTerminal(int rodadaAtual, ColetorEstatisticas coletor, Terminal[] pc, ListaDeEventos lista, Evento e) {

		double instanteAtual = lista.getInstanteDeTempoAtual();
		
		Quadro quadro = e.getQuadro();
		int terminalAtual = e.getTerminalDestino();

		pc[terminalAtual].incFluxosEntrantes(e.getTerminalOrigem(), quadro.getMensagem().getTipoMensagem(), instanteAtual);

//		System.out.println("Terminal: "+terminalAtual+" | Fluxos Entrantes: "+pc[terminalAtual].getFluxosEntrantes());
		
		// Avalia se id do remetente � o terminal atual, do contr�rio, trata colis�o.
		if(terminalAtual != quadro.getIdRemetente()){
			// Avisa ao terminal atual que tem um fluxo entrante e, caso este n�o seja do terminal atual,
			// incrementa o contador de fluxos de meio ocupado.
			pc[terminalAtual].incFluxosOcupado();
			// Se o tx estiver ocupado, ou seja, uma transmiss�o estiver ocorrendo, colido!
			if(pc[terminalAtual].isTxOcupado()){
				// C O L I D I U //
				pc[terminalAtual].setColidiu(true);
				pc[terminalAtual].setTxOcupado(false);
				
				// Cria uma mensagem espec�fica de colis�o com um �nico quadro
				// que ser� transmitido for�ado a partir deste instante de tempo
				// pelo tempo de transmiss�o de um refor�o de colis�o.
				Mensagem mColisao = new Mensagem(rodadaAtual);
				Quadro qColisao = new Quadro(rodadaAtual, terminalAtual, mColisao);
				
				// Coloca o INICIO_REFORCO_COLISAO na fila de eventos para tratamento.
				Evento geracaoReforcoColisao = new Evento(Evento.INICIO_REFORCO_COLISAO, terminalAtual, qColisao);
				lista.put(instanteAtual, geracaoReforcoColisao);
				
				// Cancelo o pr�ximo FIM TX do terminal atual.
				Evento fimTxCancelado = lista.removeEvento(terminalAtual, Evento.FIM_TX_PC);
				// Caso o evento de FIM TX cancelado n�o exista � porque j� foi cancelado antes e entrou outra colis�o.
				if(fimTxCancelado != null){
					// Recupero o quadro cancelado.
					Quadro quadroCancelado = pc[terminalAtual].getQuadroEmServico();
					
					// Incremento o n�mero de colis�es no quadro.
					quadroCancelado.incColisoes();
				}
			}
		}
	}

	// Trata o evento FIM_CHEGADA_QUADRO_NO_RX_TERMINAL
	private static void tratarEventoFimChegadaDeQuadroNoRxDoTerminal(int rodadaAtual, ColetorEstatisticas coletor, Terminal[] pc, ListaDeEventos lista, Evento e) {
		double instanteAtual = lista.getInstanteDeTempoAtual();
		int terminalAtual = e.getTerminalDestino();
		
		pc[terminalAtual].decFluxosEntrantes(e.getTerminalOrigem(), e.getQuadro().getMensagem().getTipoMensagem(), instanteAtual, coletor);
		
//		System.out.println("Terminal: "+terminalAtual+" | Fluxos Entrantes: "+pc[terminalAtual].getFluxosEntrantes());
		if(pc[terminalAtual].getFluxosEntrantes() < 0)
			System.out.print("");
		
		if(terminalAtual != e.getTerminalOrigem()){
			pc[terminalAtual].decFluxosOcupado();
			// Se for refor�o de colis�o, decrementa o fluxo que foi suspenso gerando o refor�o. 
			if(e.getQuadro().getMensagem().getTipoMensagem() == Mensagem.REFORCO_COLISAO)
				pc[terminalAtual].decFluxosOcupado();
		}

		// Verifico se o meio ficou livre para tratamento de pend�ncias.
		if(!pc[terminalAtual].isMeioOcupado()){ // <<---- MEIO LIVRE
			
			// Vou avaliar se tem algu�m pronto para for�ar transmiss�o,
			// que identifica que algu�m foi suspenso e deve entrar em transmiss�o 
			// agora que o meio est� livre novamente.
			if(pc[terminalAtual].isForcarTransmissao() && !pc[terminalAtual].isRetransmissaoAgendada()){
				
				Quadro quadroSuspenso = pc[terminalAtual].getQuadroEmServico();
				
				double instanteNovoAgendamento = instanteAtual + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;
				Evento novoAgendamento = new Evento(Evento.INICIO_TX_PC, terminalAtual, quadroSuspenso);
				
				lista.put(instanteNovoAgendamento, novoAgendamento);
				
				pc[terminalAtual].setRetransmissaoAgendada(true);
			}
		}
	}

	// Trata o evento INICIO_REFORCO_COLISAO
	private static void tratarEventoInicioReforcoColisao(ColetorEstatisticas coletor, Terminal[] pc, ListaDeEventos lista, Evento e){
		
		int terminalColidindo = e.getTerminalOrigem();
		
		pc[terminalColidindo].setTxOcupado(false);
		
		HUB.propagaAviso(Evento.INICIO_CHEGADA_QUADRO_NO_RX_TERMINAL, terminalColidindo, e.getQuadro(), pc, lista);
		
		// Crio o evento de FIM_REFORCO_COLISAO e coloco na lista de eventos.
		Evento fimReforcoColisao = new Evento(Evento.FIM_REFORCO_COLISAO, terminalColidindo, e.getQuadro());
		double instanteTempoFimReforcoColisao = lista.getInstanteDeTempoAtual() + Mensagem.TEMPO_TRANSMISSAO_REFORCO_COLISAO;
		lista.put(instanteTempoFimReforcoColisao, fimReforcoColisao);
	}

	// Trata o evento FIM_REFORCO_COLISAO	
	private static void tratarEventoFimReforcoColisao(int rodadaAtual, ColetorEstatisticas coletor, Terminal[] pc, ListaDeEventos lista, Evento e){

		double instanteAtual = lista.getInstanteDeTempoAtual();
		
		int terminalAtual = e.getTerminalOrigem();
		
		HUB.propagaAviso(Evento.FIM_CHEGADA_QUADRO_NO_RX_TERMINAL, terminalAtual, e.getQuadro(), pc, lista);
		
		// Se colidiu, vou avaliar se o quadro com transmiss�o cancelada na colis�o
		// deve voltar para servi�o ou ser descartado.
		Quadro quadroCancelado = pc[terminalAtual].getQuadroEmServico();
		
		if (quadroCancelado.getColisoes() < 16) {
			
			if(!pc[terminalAtual].isRetransmissaoAgendada()){
				// Crio um novo evento de INICIO TX para o terminal atual com o quadro retirado de FIM TX.
				Evento retransmissaoMensagemPendente = new Evento(Evento.INICIO_TX_PC, terminalAtual, quadroCancelado);
				// Calculo o novo instante de tempo a partir do final da transmiss�o do refor�o de colis�o.
				double instanteTempoAleatorioEscolhido = instanteAtual + Orquestrador.gerarAtrasoAleatorioBinaryBackoff(quadroCancelado);
				lista.put(instanteTempoAleatorioEscolhido, retransmissaoMensagemPendente);
				
				pc[terminalAtual].setRetransmissaoAgendada(true);
			}
			
		}else{
			Mensagem m = quadroCancelado.getMensagem();
			// Decrementa o quadro que foi descartado.
			m.decrementaNumeroQuadroRestantesParaTransmissao();
			// Caso a mensagem ainda tenha quadros pendente para transmiss�o
			// cria um novo evento INICIO_TX_PC e o coloca na lista de eventos.
			if(m.getNumeroQuadroRestantesParaTransmissao() > 0){
				Quadro novoQuadro = new Quadro(rodadaAtual, terminalAtual, m);	
				pc[terminalAtual].colocarQuadroEmServico(novoQuadro);
				
				// Coloco o novo quadro para tentar transmitir e sofrer o tratamento normal,
				// porque, se o meio ainda estiver ocupado, o quadro ir� ficar em espera, sen�o
				// o quadro ser� transmitido.
				Evento proximoQuadro = new Evento(Evento.INICIO_TX_PC, terminalAtual, pc[terminalAtual].getQuadroEmServico());
				lista.put(instanteAtual, proximoQuadro);
			// Do contr�rio, coleta o fim de TAm e verifica se h� nova mensagem
			// para ser transmitida.
			}else{
				if(m.getTipoMensagem() == Mensagem.MENSAGEM_PADRAO){
					if(rodadaAtual > 0){
						coletor.coletaTam(m.getRodada(), terminalAtual, m.getInstanteTempoInicioAcesso(), m.getInstanteTempoFimAcesso());
						coletor.coletaColisaoPorMensagem(terminalAtual, m);
					}
					pc[terminalAtual].mensagemEmServicoFinalizada();
					
					if(pc[terminalAtual].temMensagemEmServico()){
						Quadro novoQuadro = pc[terminalAtual].getQuadroEmServico();	
						
						double instanteTempoProximaMensagem = instanteAtual + Quadro.TEMPO_MINIMO_ENTRE_QUADROS;
						Evento proximaMensagem = new Evento(Evento.INICIO_TX_PC, terminalAtual, novoQuadro);
						lista.put(instanteTempoProximaMensagem, proximaMensagem);
					}
				}
			}
		}
	}
	
	// Calcula o atraso do binary backoff segundo o algoritmo.
	private static double gerarAtrasoAleatorioBinaryBackoff(Quadro quadroPendente) {

		int numColisoes = quadroPendente.getColisoes();
		numColisoes = Math.min(10, numColisoes);

		int randomico = GeradorRandomicoSingleton.getInstance().gerarProximoIntRandomico();
		int intervalos = Math.abs(randomico % (int)(Math.pow(2, numColisoes)));

		return intervalos * Quadro.SLOT_RETRANSMISSAO;
	}
	
	// M�todos para depura��o de resultados.
	
	private void verbosePorEvento(String tempo, String numEventoAtual, String terminal, String rodada, String mensagem, String quadrosRestantes, String tipoEvento){
//		if(!verbose)return;
//		System.out.println("Tempo: "+tempo+" | #Evento: "+numEventoAtual+" | PC org: "+terminal+" | Rodada: "+rodada+" | Quadros restantes: "+quadrosRestantes+" | Mensagem no.: "+mensagem+" | Tipo Evento: "+tipoEvento);
	}
	
	private void verbosePorQuadro(String tempo, String trmOrg, String trmDst, String mensagem, String quadro, String quadrosRestantes, String tipoEvento){
		if(!verbose)return;
		System.out.println("PC org: "+trmOrg+" | PC dst: "+((trmDst==null||trmDst.equalsIgnoreCase("null"))?"?":trmDst)+" | Tempo: "+tempo+" | Tipo Evento: "+tipoEvento+" | Msg: "+mensagem+" | Qdro: "+quadro+" | Quadros restantes: "+quadrosRestantes+" ");
	}
	
	private void verbosePorQuadro(String tempo, String trmOrg, String trmDst, String mensagem, String quadro, String colisoes, String quadrosRestantes, String tipoEvento){
		if(!verbose)return;
		System.out.println("PC org: "+trmOrg+" | PC dst: "+((trmDst==null||trmDst.equalsIgnoreCase("null"))?"?":trmDst)+" | Tempo: "+tempo+" | Tipo Evento: "+tipoEvento+" | Msg: "+mensagem+" | Qdro: "+quadro+" | Colisoes: "+colisoes+" | Quadros restantes: "+quadrosRestantes+" ");
	}

}

