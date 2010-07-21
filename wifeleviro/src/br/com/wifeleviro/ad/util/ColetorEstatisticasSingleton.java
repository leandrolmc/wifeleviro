package br.com.wifeleviro.ad.util;

import java.util.Hashtable;
import java.util.TreeMap;
import java.util.Vector;

// Classe única de coleta dos dados estatísticos para avaliação do programa.
// O padrão singleton utilizado garante que não exista mais que uma intância 
//  desta classe dentro do programa em execução. 
public class ColetorEstatisticasSingleton {

	private double instanteInicioSimulacao;
	private double instanteFimSimulacao;
	
	// Variável que armazenará as estatísticas de cada estação.
	private Estatisticas[] estatisticas;
	
	// Construtor padrão da classe.
	private ColetorEstatisticasSingleton() {
		
		instanteInicioSimulacao = -1;
		instanteFimSimulacao = -1;
		
		// Inicia uma instância de Estatisticas para cada estação.
		estatisticas = new Estatisticas[4];
		
		for(int i = 0; i < 4; i++){
			estatisticas[i] = new Estatisticas();
		}
	}

	// Variável privada que contém a única referência para o coletor de
	// estatísticas.
	private static ColetorEstatisticasSingleton _instance;

	// Metódo capaz de gerar a instância única do coletor de estatísticas, caso
	// esta ainda não exista. Caso exista, simplesmente retorna uma referência
	// para a mesma.
	public static synchronized ColetorEstatisticasSingleton getInstance() {
		if (_instance == null) {
			_instance = new ColetorEstatisticasSingleton();
		}

		return _instance;
	}

	// Sub-classe que será utilizada unicamente pelo coletor de estatísticas
	// para armazenamento dos dados coletados.
	private class Estatisticas {

		protected static final int INICIO_PERIODO_OCUPADO = 0;
		protected static final int FIM_PERIODO_OCUPADO = 1;
		
		// Hashtable que irá armazenar as coletas dos tempos iniciais de cada
		// quadro para medição dos tap´s.
		protected Hashtable<Long, Double> tapMedicaoInicio;
		// Lista encadeada não-ordenada que irá armazenar os tap´s medidos.
		protected Vector<Double> tap;
		
		// Hashtable que irá armazenar as coletas dos tempos iniciais de cada
		// quadro para medição dos tam´s.
		protected Hashtable<Long, Double> tamMedicaoInicio;
		// Lista encadeada não-ordenada que irá armazenar os tam´s medidos.
		protected Vector<Double> tam;
		
		// Hashtable indexada pelo identificador da mensagem que armazena
		// o número de colisões ocorridas na mensagem.
		protected Hashtable<Long, Long> colisoesPorMensagem;
		// Hashtable indexada pelo identificador da mensagem que armazena
		// o número de quadros que foram necessários para transmitir a mensagem.
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

	// Coleta de E[Tap(i)] (tempo de acesso de um quadro na estação i)

	// Coleta o valor do tempo quando do início do acesso de um quadro na
	// estação i. Todo quadro tem um identificador único que irá indexá-lo
	// na hash de armazenamento do tempo inicial.
	public void iniciaColetaTap(int idEstacao, long idQuadro, double tempoInicio) {
		// Armazena na hashtable tapMedicaoInicio referente a estação
		// identificada por idEstacao o tempo de início de acesso do 
		// quadro identificado por idQuadro.
		this.estatisticas[idEstacao].tapMedicaoInicio.put(idQuadro, tempoInicio);
	}

	// Coleta o valor do tempo quando do fim do acesso 
	// de um quadro na estação i. Busca no hash de armazenamento do tempo
	// inicial o valor referente ao identificador do quadro informado de
	// modo a armazenar este em um agregador de "tap´s" da estação i,
	// perdendo a identificação única do quadro e tornando esta medição
	// em um "freguês típico" do sistema.
	public void finalizaColetaTap(int idEstacao, long idQuadro, double tempoFim) {
		// Recupero da hashtable o tempo do início do acesso do quadro
		// identificado por idQuadro.
		double tempoInicio = (Double)this.estatisticas[idEstacao].tapMedicaoInicio.get(idQuadro);
		// O cálculo do tempo de acesso é dado simplesmente pela subtração
		// do instante de tempo inicial do instante de tempo final.
		double tempoAcesso = tempoFim - tempoInicio;
		// Armazena na lista encadeada o tempo de acesso do quadro.
		this.estatisticas[idEstacao].tap.add(tempoAcesso);
	}

	// Coleta de E[Tam(i)]

	// Coleta o valor do tempo quando do início do acesso do primeiro quadro 
	// da mensagem a ser transmitida na estação i. Toda mensagem tem um 
	// identificador único que irá indexá-la na hash de armazenamento 
	// do tempo inicial.
	public void iniciaColetaTam(int idEstacao, long idMensagem, double tempoInicio) {
		// Armazena na hashtable tamMedicaoInicio referente a estação
		// identificada por idEstacao o tempo de início de acesso do 
		// primeiro quadro da mensagem identificada por idMensagem.
		this.estatisticas[idEstacao].tamMedicaoInicio.put(idMensagem, tempoInicio);
	}

	// Coleta o valor do tempo quando do fim do acesso do último quadro 
	// de uma mensagem na estação i. Busca no hash de armazenamento do tempo
	// inicial o valor referente ao identificador da mensagem informado de
	// modo a armazenar este em um agregador de "tam´s" da estação i,
	// perdendo a identificação única da mensagem e tornando esta medição
	// em um "freguês típico" do sistema.
	public void finalizaColetaTam(int idEstacao, long idMensagem, double tempoFim) {
		// Recupero da hashtable o tempo do início do acesso da mensagem
		// identificada por idMensagem.
		double tempoInicio = (Double)this.estatisticas[idEstacao].tamMedicaoInicio.get(idMensagem);
		// O cálculo do tempo de acesso é dado simplesmente pela subtração
		// do instante de tempo inicial do instante de tempo final.
		double tempoAcesso = tempoFim - tempoInicio;
		// Armazena na lista encadeada o tempo de acesso da mensagem.
		this.estatisticas[idEstacao].tam.add(tempoAcesso);
	}
	
	// Coleta de E[NCm(i)]
	
	// Coleta dados sobre a existência ou não de colisão na transmissão de um
	// quadro qualquer, identificando a mensagem transmitida, da qual o quadro 
	// faz parte, e a estação, de modo que seja possível calcular a média de 
	// colisões por mensagem, por estação.
	public void coletaColisaoPorMensagem(int idEstacao, long idMensagem){
		// Recupera o número de colisões registradas na hashtable para a mensagem
		// identificada por idMensagem.
		Long numeroDeColisoes = (Long)this.estatisticas[idEstacao].colisoesPorMensagem.get(idMensagem);
		// Caso não haja registro na hashtable referente a colisões desta mensagem,
		// o contador de colisões é iniciado com o valor 0 (ZERO).
		if(numeroDeColisoes == null){
			numeroDeColisoes = (long)0;
		}
		// Incrementa o número de colisões registradas.
		numeroDeColisoes = numeroDeColisoes + 1;
		// Armazena na hashtable as colisões contabilizadas até o momento.
		this.estatisticas[idEstacao].colisoesPorMensagem.put(idMensagem, numeroDeColisoes);
	}
	
	// Coleta geração de quadro para transmissão de mensagem.
	// Invocado sempre que há um evento de geração de um quadro para transmissão.
	public void coletaQuadroPorMensagem(int idEstacao, long idMensagem){
		// Recupera o número de quadros registrados na hashtable para a mensagem
		// identificada por idMensagem.
		Long numeroDeQuadros = (Long)this.estatisticas[idEstacao].quadrosPorMensagem.get(idMensagem);
		// Caso não haja registro na hashtable referente a quadros desta mensagem,
		// o contador de quadros é iniciado com o valor 0 (ZERO).
		if(numeroDeQuadros == null){
			numeroDeQuadros = (long)0;
		}
		// Incrementa o número de quadros registrados.
		numeroDeQuadros = numeroDeQuadros + 1;
		// Armazena na hashtable os quadros contabilizados até o momento.
		this.estatisticas[idEstacao].colisoesPorMensagem.put(idMensagem, numeroDeQuadros);
	}

	// Coleta de utilização do Ethernet
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
	
	// Coleta de vazão(i)
	public void coletaTransmissaoDeQuadroComSucessoNaEstacao(int estacao){
		this.estatisticas[estacao].numeroQuadrosTransmitidosComSucesso++;
	}



}
