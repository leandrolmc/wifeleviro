package br.com.wifeleviro.ad.util;

import java.util.Hashtable;
import java.util.Vector;

// Classe �nica de coleta dos dados estat�sticos para avalia��o do programa.
// O padr�o singleton utilizado garante que n�o exista mais que uma int�ncia 
//  desta classe dentro do programa em execu��o. 
public class ColetorEstatisticas {

	// Vari�veis que armazenar�o in�cio e fim de rodada.
	private double instanteInicioRodada;
	private double instanteFimRodada;
	
	// Vari�vel que mant�m dado da quantidade de terminais ativos no sistema.
	private int numTerminais;
	
	// Vari�vel que armazenar� as estat�sticas de cada esta��o.
	private Estatisticas[] estatisticas;
	
	// Construtor padr�o da classe.
	public ColetorEstatisticas(int numTerminais) {
		
		// Setando valores iniciais para -1 de modo a serem substitu�dos pelas primeiras medi��es corretas.
		setInstanteInicioRodada(-1);
		setInstanteFimRodada(-1);
		
		// Par�metro de inicializa��o do coletor.
		this.numTerminais = numTerminais;
		
		// Inicia uma inst�ncia de Estatisticas para cada esta��o.
		estatisticas = new Estatisticas[this.numTerminais];
		
		for(int i = 0; i < this.numTerminais; i++){
			estatisticas[i] = new Estatisticas();
		}
	}

	// Sub-classe que ser� utilizada unicamente pelo coletor de estat�sticas
	// para armazenamento dos dados coletados.
	public class Estatisticas {

		// Hashtable que ir� armazenar as coletas dos tempos iniciais de cada
		// quadro para medi��o dos tap�s.
		protected Hashtable<Long, Double> tapMedicaoInicio;
		// Lista encadeada n�o-ordenada que ir� armazenar os tap�s medidos.
		protected Vector<Double> tap;
		
		// Hashtable que ir� armazenar as coletas dos tempos iniciais de cada
		// quadro para medi��o dos tam�s.
		protected Hashtable<Long, Double> tamMedicaoInicio;
		// Lista encadeada n�o-ordenada que ir� armazenar os tam�s medidos.
		protected Vector<Double> tam;
		
		// Hashtable indexada pelo identificador da mensagem que armazena
		// o n�mero de colis�es ocorridas na mensagem.
		protected Hashtable<Long, Long> colisoesPorMensagem;
		// Hashtable indexada pelo identificador da mensagem que armazena
		// o n�mero de quadros que foram necess�rios para transmitir a mensagem.
		protected Hashtable<Long, Long> quadrosPorMensagem;
		
		// Armazena os instantes de tempo de in�cio de per�odo ocupado e de
		// fim de per�odo ocupado, com valores negativo e positivo, respectivamente.
		protected Vector<Double> periodosOcupados;
		
		// Contador inicializado em zero do n�mero de quadros que
		// atingiram RX do HUB com sucesso durante a rodada.
		protected Long numeroQuadrosTransmitidosComSucesso;
		
		// Inicializa��o com valores padr�o ou instancia��o de classe.
		protected Estatisticas(){
			tapMedicaoInicio = new Hashtable<Long, Double>();
			tap = new Vector<Double>();
			tamMedicaoInicio = new Hashtable<Long, Double>();
			tam = new Vector<Double>();
			colisoesPorMensagem = new Hashtable<Long, Long>();
			quadrosPorMensagem = new Hashtable<Long, Long>();
			periodosOcupados = new Vector<Double>();
			numeroQuadrosTransmitidosComSucesso = (long)0;
		}
		
		// M�todo de simples recupera��o da cole��o de TAp's.
		public Vector<Double> getTap() {
			return tap;
		}

		// M�todo de simples recupera��o da cole��o de TAm's.
		public Vector<Double> getTam() {
			return tam;
		}

		// M�todo simples para recupera��o do
		// Hashtable indexada pelo id da mensagem contendo o
		// n�mero de colis�es sofridas em cada mensagem.
		public Hashtable<Long, Long> getColisoesPorMensagem() {
			return colisoesPorMensagem;
		}

		// M�todo simples para recupera��o do
		// Hashtable indexada pelo id da mensagem contendo o
		// n�mero de quadros gerados por mensagem.
		public Hashtable<Long, Long> getQuadrosPorMensagem() {
			return quadrosPorMensagem;
		}

		// M�todo simples para recupera��o da cole��o
		// de instantes iniciais e finais de per�odos ocupados.
		public Vector<Double> getPeriodosOcupados() {
			return periodosOcupados;
		}

		// M�todo simples para recupera��o do n�mero
		// de quadros transmitidos com sucesso durante a rodada.
		public Long getNumeroQuadrosTransmitidosComSucesso() {
			return numeroQuadrosTransmitidosComSucesso;
		}
	}

	// Coleta de E[Tap(i)] (tempo de acesso de um quadro na esta��o i)

	// Coleta o valor do tempo quando do in�cio do acesso de um quadro na
	// esta��o i. Todo quadro tem um identificador �nico que ir� index�-lo
	// na hash de armazenamento do tempo inicial.
	public void iniciaColetaTap(int idEstacao, long idQuadro, double tempoInicio) {
		// Armazena na hashtable tapMedicaoInicio referente a esta��o
		// identificada por idEstacao o tempo de in�cio de acesso do 
		// quadro identificado por idQuadro.
		this.estatisticas[idEstacao].tapMedicaoInicio.put(idQuadro, tempoInicio);
	}

	// Coleta o valor do tempo quando do fim do acesso 
	// de um quadro na esta��o i. Busca no hash de armazenamento do tempo
	// inicial o valor referente ao identificador do quadro informado de
	// modo a armazenar este em um agregador de "tap�s" da esta��o i,
	// perdendo a identifica��o �nica do quadro e tornando esta medi��o
	// em um "fregu�s t�pico" do sistema.
	public void finalizaColetaTap(int idEstacao, long idQuadro, double tempoFim) {
		// Recupero da hashtable o tempo do in�cio do acesso do quadro
		// identificado por idQuadro.
		double tempoInicio = (Double)this.estatisticas[idEstacao].tapMedicaoInicio.get(idQuadro);
		// O c�lculo do tempo de acesso � dado simplesmente pela subtra��o
		// do instante de tempo inicial do instante de tempo final.
		double tempoAcesso = tempoFim - tempoInicio;
		// Armazena na lista encadeada o tempo de acesso do quadro.
		this.estatisticas[idEstacao].tap.add(tempoAcesso);
	}

	// Coleta de E[Tam(i)]

	// Coleta o valor do tempo quando do in�cio do acesso do primeiro quadro 
	// da mensagem a ser transmitida na esta��o i. Toda mensagem tem um 
	// identificador �nico que ir� index�-la na hash de armazenamento 
	// do tempo inicial.
	public void iniciaColetaTam(int idEstacao, long idMensagem, double tempoInicio) {
		// Armazena na hashtable tamMedicaoInicio referente a esta��o
		// identificada por idEstacao o tempo de in�cio de acesso do 
		// primeiro quadro da mensagem identificada por idMensagem.
		this.estatisticas[idEstacao].tamMedicaoInicio.put(idMensagem, tempoInicio);
	}

	// Coleta o valor do tempo quando do fim do acesso do �ltimo quadro 
	// de uma mensagem na esta��o i. Busca no hash de armazenamento do tempo
	// inicial o valor referente ao identificador da mensagem informado de
	// modo a armazenar este em um agregador de "tam�s" da esta��o i,
	// perdendo a identifica��o �nica da mensagem e tornando esta medi��o
	// em um "fregu�s t�pico" do sistema.
	public void finalizaColetaTam(int idEstacao, long idMensagem, double tempoFim) {
		// Recupero da hashtable o tempo do in�cio do acesso da mensagem
		// identificada por idMensagem.
		double tempoInicio = (Double)this.estatisticas[idEstacao].tamMedicaoInicio.get(idMensagem);
		// O c�lculo do tempo de acesso � dado simplesmente pela subtra��o
		// do instante de tempo inicial do instante de tempo final.
		double tempoAcesso = tempoFim - tempoInicio;
		// Armazena na lista encadeada o tempo de acesso da mensagem.
		this.estatisticas[idEstacao].tam.add(tempoAcesso);
	}
	
	// Coleta de E[NCm(i)]
	
	// Coleta dados sobre a exist�ncia ou n�o de colis�o na transmiss�o de um
	// quadro qualquer, identificando a mensagem transmitida, da qual o quadro 
	// faz parte, e a esta��o, de modo que seja poss�vel calcular a m�dia de 
	// colis�es por mensagem, por esta��o.
	public void coletaColisaoPorMensagem(int idEstacao, long idMensagem){
		// Recupera o n�mero de colis�es registradas na hashtable para a mensagem
		// identificada por idMensagem.
		Long numeroDeColisoes = (Long)this.estatisticas[idEstacao].colisoesPorMensagem.get(idMensagem);
		// Caso n�o haja registro na hashtable referente a colis�es desta mensagem,
		// o contador de colis�es � iniciado com o valor 0 (ZERO).
		if(numeroDeColisoes == null){
			numeroDeColisoes = (long)0;
		}
		// Incrementa o n�mero de colis�es registradas.
		numeroDeColisoes = numeroDeColisoes + 1;
		// Armazena na hashtable as colis�es contabilizadas at� o momento.
		this.estatisticas[idEstacao].colisoesPorMensagem.put(idMensagem, numeroDeColisoes);
	}
	
	// Coleta gera��o de quadro para transmiss�o de mensagem.
	// Invocado sempre que h� um evento de gera��o de um quadro para transmiss�o.
	public void coletaQuadroPorMensagem(int idEstacao, long idMensagem){
		// Recupera o n�mero de quadros registrados na hashtable para a mensagem
		// identificada por idMensagem.
		Long numeroDeQuadros = (Long)this.estatisticas[idEstacao].quadrosPorMensagem.get(idMensagem);
		// Caso n�o haja registro na hashtable referente a quadros desta mensagem,
		// o contador de quadros � iniciado com o valor 0 (ZERO).
		if(numeroDeQuadros == null){
			numeroDeQuadros = (long)0;
		}
		// Incrementa o n�mero de quadros registrados.
		numeroDeQuadros = numeroDeQuadros + 1;
		// Armazena na hashtable os quadros contabilizados at� o momento.
		this.estatisticas[idEstacao].colisoesPorMensagem.put(idMensagem, numeroDeQuadros);
	}

	// Coleta intante de tempo inicial da rodada.
	public void coletaInicioRodada(double instanteDeTempo){
		this.setInstanteInicioRodada(instanteDeTempo);
	}
	
	// Coleta intante de tempo final da rodada.
	public void coletaFimRodada(double instanteDeTempo){
		this.setInstanteFimRodada(instanteDeTempo);
	}
	
	// Coleta valor de in�cio de per�odo ocupado, o converte para
	// negativo e armazena na cole��o de per�odos ocupados.
	public void coletaInicioPeriodoOcupado(double instanteDeTempo){
		this.estatisticas[0].periodosOcupados.add(-instanteDeTempo);
	}

	// Coleta valor de in�cio de per�odo ocupado e armazena 
	// na cole��o de per�odos ocupados.
	public void coletaFimPeriodoOcupado(double instanteDeTempo){
		this.estatisticas[0].periodosOcupados.add(instanteDeTempo);
	}
	
	// Incrementa o n�mero de quadros transmitidos com sucesso
	// na esta��o i.
	public void coletaTransmissaoDeQuadroComSucessoNaEstacao(int estacao){
		this.estatisticas[estacao].numeroQuadrosTransmitidosComSucesso++;
	}

	// M�todo privado para simples manipula��o do valor de in�cio da rodada.
	private void setInstanteInicioRodada(double instanteInicioRodada) {
		this.instanteInicioRodada = instanteInicioRodada;
	}

	// M�todo para simples recupera��o do valor de in�cio de rodada.
	public double getInstanteInicioRodada() {
		return instanteInicioRodada;
	}

	// M�todo privado para simples manipula��o do valor de final da rodada.
	private void setInstanteFimRodada(double instanteFimRodada) {
		this.instanteFimRodada = instanteFimRodada;
	}

	// M�todo para simples recupera��o do valor de final de rodada.
	public double getInstanteFimRodada() {
		return instanteFimRodada;
	}
	
	// Recupera array de estat�sticas indexado pelo id do terminal
	// para c�lculo de intervalo de confian�a.
	public Estatisticas[] getEstatisticas() {
		return this.estatisticas;
	}
}
