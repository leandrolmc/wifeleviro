package br.com.wifeleviro.ad.util.estatisticas;

import java.util.Hashtable;

import br.com.wifeleviro.ad.util.estatisticas.metricas.TAm;
import br.com.wifeleviro.ad.util.estatisticas.metricas.TAp;

// Classe �nica de coleta dos dados estat�sticos para avalia��o do programa.
// O padr�o singleton utilizado garante que n�o exista mais que uma int�ncia 
//  desta classe dentro do programa em execu��o. 
public class ColetorEstatisticas {

	// Vari�veis que armazenar�o in�cio e fim de rodada.
	private double instanteInicioRodada;
	private double instanteFimRodada;
	
	// Vari�vel que mant�m dado da quantidade de terminais ativos no sistema.
	private int numTerminais;
	
	// Vari�vel para informar a "cor" da rodada atual.
	private int rodadaAtual;
	
	// Vari�vel que armazenar� as estat�sticas de cada esta��o.
	private Estatisticas[] estatisticas;
	
	// Construtor padr�o da classe.
	public ColetorEstatisticas(int rodadaAtual, int numTerminais) {

		// A "cor" da rodada atual � informada no momento de cria��o do novo coletor.
		this.rodadaAtual = rodadaAtual;
		
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

		protected TAp tap;
		
		protected TAm tam;		
		
		// Hashtable indexada pelo identificador da mensagem que armazena
		// o n�mero de colis�es ocorridas na mensagem.
		protected Hashtable<Long, Long> colisoesPorMensagem;
		// Hashtable indexada pelo identificador da mensagem que armazena
		// o n�mero de quadros que foram necess�rios para transmitir a mensagem.
		protected Hashtable<Long, Long> quadrosPorMensagem;
		
		// Armazena os instantes de tempo iniciais de utiliza��o por quadro,
		// independente se este � um quadro normal ou de refor�o de colis�o.
		protected Hashtable<Integer, Double> utilizacaoInicial;
		
		// Armazena os pares de in�cio e fim de utiliza��o do meio.
		protected double utilizacao;
		
		// Contador inicializado em zero do n�mero de quadros que
		// atingiram RX do HUB com sucesso durante a rodada.
		protected Long numeroQuadrosTransmitidosComSucesso;
		
		// Inicializa��o com valores padr�o ou instancia��o de classe.
		protected Estatisticas(){
			tap = new TAp();
			tam = new TAm();
			colisoesPorMensagem = new Hashtable<Long, Long>();
			quadrosPorMensagem = new Hashtable<Long, Long>();
			utilizacaoInicial = new Hashtable<Integer, Double>();
			utilizacao = 0;
			numeroQuadrosTransmitidosComSucesso = (long)0;
		}
		
		public TAp getTap(){
			return this.tap; 
		}

		public TAm getTam(){
			return this.tam;
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
		public double getUtilizacao() {
			return this.utilizacao;
		}

		// M�todo simples para recupera��o do n�mero
		// de quadros transmitidos com sucesso durante a rodada.
		public Long getNumeroQuadrosTransmitidosComSucesso() {
			return numeroQuadrosTransmitidosComSucesso;
		}
	}

	// Coleta de E[Tap(i)] (tempo de acesso de um quadro na esta��o i)
	// Coleta os valores do tempo quando do fim do acesso de um quadro na esta��o i.
	public void coletaTap(int rodadaDoQuadro, int idEstacao, double inicioAcesso, double fimAcesso) {
		// S� armazena estat�sticas quando o quadro � da mesma "cor" da rodada.
		if(rodadaDoQuadro==this.rodadaAtual && this.rodadaAtual > 0){

			double tap = fimAcesso - inicioAcesso;

			// Acumula o valor do tap no somat�rio de todos os tap�s.
			this.estatisticas[idEstacao].tap.acumularTempo(tap);
		}
	}
	
	// Coleta de E[Tam(i)] (tempo de acesso de uma mensagem na esta��o i)
	// Coleta o valor do tempo quando do fim do acesso 
	// de uma mensagem na esta��o i. L�gica similar a coletaTap.
	public void coletaTam(int rodadaDaMensagem, int idEstacao, double inicioAcesso, double fimAcesso) {
		// S� armazena estat�sticas quando a mensagem � da mesma "cor" da rodada.
		if(rodadaDaMensagem==this.rodadaAtual && this.rodadaAtual > 0){

			double tam = fimAcesso - inicioAcesso;
			
			// Acumula o valor do tam no somat�rio de todos os tam�s.
			this.estatisticas[idEstacao].tam.acumularTempo(tam);
		}
	}
	
	// Coleta de E[NCm(i)]
	
	// Coleta dados sobre a exist�ncia ou n�o de colis�o na transmiss�o de um
	// quadro qualquer, identificando a mensagem transmitida, da qual o quadro 
	// faz parte, e a esta��o, de modo que seja poss�vel calcular a m�dia de 
	// colis�es por mensagem, por esta��o.
	public void coletaColisaoPorMensagem(int rodada, int idEstacao, long idMensagem, long numeroDeColisoes){
		// S� armazena estat�sticas quando o quadro � da mesma "cor" da rodada.
		if(rodada==this.rodadaAtual && this.rodadaAtual > 0){
			// Armazena na hashtable as colis�es contabilizadas at� o momento.
			this.estatisticas[idEstacao].colisoesPorMensagem.put(idMensagem, numeroDeColisoes);
		}
	}
	
	// Coleta gera��o de quadro para transmiss�o de mensagem.
	// Invocado sempre que h� um evento de gera��o de um quadro para transmiss�o.
	public void coletaQuadroPorMensagem(int rodada, int idEstacao, long idMensagem){
		// S� armazena estat�sticas quando o quadro � da mesma "cor" da rodada.
		if(rodada==this.rodadaAtual && this.rodadaAtual > 0){
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
			this.estatisticas[idEstacao].quadrosPorMensagem.put(idMensagem, numeroDeQuadros);
		}
	}

	// Coleta instante de tempo inicial da rodada.
	public void coletaInicioRodada(double instanteDeTempo){
		this.setInstanteInicioRodada(instanteDeTempo);
	}
	
	// Coleta instante de tempo final da rodada.
	public void coletaFimRodada(double instanteDeTempo){
		this.setInstanteFimRodada(instanteDeTempo);
	}
	
	// Coleta valor de in�cio de per�odo de utiliza��o e armazena na hash indexada pelo id do quadro.
	public void coletaInicioPeriodoUtilizacao(int idEstacao, int idEstacaoRemetente, double instanteDeTempo){
		if(this.estatisticas[idEstacao].utilizacaoInicial.get(idEstacaoRemetente)==null)
			this.estatisticas[idEstacao].utilizacaoInicial.put(idEstacaoRemetente, instanteDeTempo);
	}

	// Coleta valor de in�cio de per�odo de utiliza��o na hash pelo id do quadro informado e
	// cria uma inst�ncia de Utilizacao para armazenar no array de in�cio e fim de utiliza��o.
	public void coletaFimPeriodoUtilizacao(int idEstacao, int idEstacaoRemetente, double instanteFinal){
		double instanteInicial = this.estatisticas[idEstacao].utilizacaoInicial.remove(idEstacaoRemetente);
		double utilizacao = instanteFinal - instanteInicial;
		this.estatisticas[idEstacao].utilizacao += utilizacao;
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
