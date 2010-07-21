package br.com.wifeleviro.ad.util;

import java.util.Hashtable;
import java.util.TreeMap;
import java.util.Vector;

// Classe �nica de coleta dos dados estat�sticos para avalia��o do programa.
// O padr�o singleton utilizado garante que n�o exista mais que uma int�ncia 
//  desta classe dentro do programa em execu��o. 
public class ColetorEstatisticasSingleton {

	private double instanteInicioSimulacao;
	private double instanteFimSimulacao;
	
	// Vari�vel que armazenar� as estat�sticas de cada esta��o.
	private Estatisticas[] estatisticas;
	
	// Construtor padr�o da classe.
	private ColetorEstatisticasSingleton() {
		
		instanteInicioSimulacao = -1;
		instanteFimSimulacao = -1;
		
		// Inicia uma inst�ncia de Estatisticas para cada esta��o.
		estatisticas = new Estatisticas[4];
		
		for(int i = 0; i < 4; i++){
			estatisticas[i] = new Estatisticas();
		}
	}

	// Vari�vel privada que cont�m a �nica refer�ncia para o coletor de
	// estat�sticas.
	private static ColetorEstatisticasSingleton _instance;

	// Met�do capaz de gerar a inst�ncia �nica do coletor de estat�sticas, caso
	// esta ainda n�o exista. Caso exista, simplesmente retorna uma refer�ncia
	// para a mesma.
	public static synchronized ColetorEstatisticasSingleton getInstance() {
		if (_instance == null) {
			_instance = new ColetorEstatisticasSingleton();
		}

		return _instance;
	}

	// Sub-classe que ser� utilizada unicamente pelo coletor de estat�sticas
	// para armazenamento dos dados coletados.
	private class Estatisticas {

		protected static final int INICIO_PERIODO_OCUPADO = 0;
		protected static final int FIM_PERIODO_OCUPADO = 1;
		
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
		
		protected TreeMap<Double, Integer> periodosOcupados;
		
		protected Long numeroQuadrosTransmitidosComSucesso;
		
		protected Estatisticas(){
			tapMedicaoInicio = new Hashtable<Long, Double>();
			tap = new Vector<Double>();
			tamMedicaoInicio = new Hashtable<Long, Double>();
			tam = new Vector<Double>();
			colisoesPorMensagem = new Hashtable<Long, Long>();
			quadrosPorMensagem = new Hashtable<Long, Long>();
			periodosOcupados = new TreeMap<Double, Integer>();
			numeroQuadrosTransmitidosComSucesso = (long)0;
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

	// Coleta de utiliza��o do Ethernet
	public void coletaInicioSimulacao(double instanteDeTempo){
		this.instanteInicioSimulacao = instanteDeTempo;
	}
	
	public void coletaFimSimulacao(double instanteDeTempo){
		this.instanteFimSimulacao = instanteDeTempo;
	}
	
	public void coletaInicioPeriodoOcupado(double instanteDeTempo){
		this.estatisticas[0].periodosOcupados.put(instanteDeTempo, Estatisticas.INICIO_PERIODO_OCUPADO);
	}
	
	public void coletaFimPeriodoOcupado(double instanteDeTempo){
		this.estatisticas[0].periodosOcupados.put(instanteDeTempo, Estatisticas.FIM_PERIODO_OCUPADO);
	}
	
	// Coleta de vaz�o(i)
	public void coletaTransmissaoDeQuadroComSucessoNaEstacao(int estacao){
		this.estatisticas[estacao].numeroQuadrosTransmitidosComSucesso++;
	}



}
