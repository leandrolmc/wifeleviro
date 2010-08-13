package br.com.wifeleviro.ad.util.estatisticas;

import java.util.Hashtable;

import br.com.wifeleviro.ad.util.estatisticas.metricas.TAm;
import br.com.wifeleviro.ad.util.estatisticas.metricas.TAp;

// Classe única de coleta dos dados estatísticos para avaliação do programa.
// O padrão singleton utilizado garante que não exista mais que uma intância 
//  desta classe dentro do programa em execução. 
public class ColetorEstatisticas {

	// Variáveis que armazenarão início e fim de rodada.
	private double instanteInicioRodada;
	private double instanteFimRodada;
	
	// Variável que mantém dado da quantidade de terminais ativos no sistema.
	private int numTerminais;
	
	// Variável para informar a "cor" da rodada atual.
	private int rodadaAtual;
	
	// Variável que armazenará as estatísticas de cada estação.
	private Estatisticas[] estatisticas;
	
	// Construtor padrão da classe.
	public ColetorEstatisticas(int rodadaAtual, int numTerminais) {

		// A "cor" da rodada atual é informada no momento de criação do novo coletor.
		this.rodadaAtual = rodadaAtual;
		
		// Setando valores iniciais para -1 de modo a serem substituídos pelas primeiras medições corretas.
		setInstanteInicioRodada(-1);
		setInstanteFimRodada(-1);
		
		// Parâmetro de inicialização do coletor.
		this.numTerminais = numTerminais;
		
		// Inicia uma instância de Estatisticas para cada estação.
		estatisticas = new Estatisticas[this.numTerminais];
		
		for(int i = 0; i < this.numTerminais; i++){
			estatisticas[i] = new Estatisticas();
		}
	}

	// Sub-classe que será utilizada unicamente pelo coletor de estatísticas
	// para armazenamento dos dados coletados.
	public class Estatisticas {

		protected TAp tap;
		
		protected TAm tam;		
		
		// Hashtable indexada pelo identificador da mensagem que armazena
		// o número de colisões ocorridas na mensagem.
		protected Hashtable<Long, Long> colisoesPorMensagem;
		// Hashtable indexada pelo identificador da mensagem que armazena
		// o número de quadros que foram necessários para transmitir a mensagem.
		protected Hashtable<Long, Long> quadrosPorMensagem;
		
		// Armazena os instantes de tempo iniciais de utilização por quadro,
		// independente se este é um quadro normal ou de reforço de colisão.
		protected Hashtable<Integer, Double> utilizacaoInicial;
		
		// Armazena os pares de início e fim de utilização do meio.
		protected double utilizacao;
		
		// Contador inicializado em zero do número de quadros que
		// atingiram RX do HUB com sucesso durante a rodada.
		protected Long numeroQuadrosTransmitidosComSucesso;
		
		// Inicialização com valores padrão ou instanciação de classe.
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

		// Método simples para recuperação do
		// Hashtable indexada pelo id da mensagem contendo o
		// número de colisões sofridas em cada mensagem.
		public Hashtable<Long, Long> getColisoesPorMensagem() {
			return colisoesPorMensagem;
		}

		// Método simples para recuperação do
		// Hashtable indexada pelo id da mensagem contendo o
		// número de quadros gerados por mensagem.
		public Hashtable<Long, Long> getQuadrosPorMensagem() {
			return quadrosPorMensagem;
		}

		// Método simples para recuperação da coleção
		// de instantes iniciais e finais de períodos ocupados.
		public double getUtilizacao() {
			return this.utilizacao;
		}

		// Método simples para recuperação do número
		// de quadros transmitidos com sucesso durante a rodada.
		public Long getNumeroQuadrosTransmitidosComSucesso() {
			return numeroQuadrosTransmitidosComSucesso;
		}
	}

	// Coleta de E[Tap(i)] (tempo de acesso de um quadro na estação i)
	// Coleta os valores do tempo quando do fim do acesso de um quadro na estação i.
	public void coletaTap(int rodadaDoQuadro, int idEstacao, double inicioAcesso, double fimAcesso) {
		// Só armazena estatísticas quando o quadro é da mesma "cor" da rodada.
		if(rodadaDoQuadro==this.rodadaAtual && this.rodadaAtual > 0){

			double tap = fimAcesso - inicioAcesso;

			// Acumula o valor do tap no somatório de todos os tap´s.
			this.estatisticas[idEstacao].tap.acumularTempo(tap);
		}
	}
	
	// Coleta de E[Tam(i)] (tempo de acesso de uma mensagem na estação i)
	// Coleta o valor do tempo quando do fim do acesso 
	// de uma mensagem na estação i. Lógica similar a coletaTap.
	public void coletaTam(int rodadaDaMensagem, int idEstacao, double inicioAcesso, double fimAcesso) {
		// Só armazena estatísticas quando a mensagem é da mesma "cor" da rodada.
		if(rodadaDaMensagem==this.rodadaAtual && this.rodadaAtual > 0){

			double tam = fimAcesso - inicioAcesso;
			
			// Acumula o valor do tam no somatório de todos os tam´s.
			this.estatisticas[idEstacao].tam.acumularTempo(tam);
		}
	}
	
	// Coleta de E[NCm(i)]
	
	// Coleta dados sobre a existência ou não de colisão na transmissão de um
	// quadro qualquer, identificando a mensagem transmitida, da qual o quadro 
	// faz parte, e a estação, de modo que seja possível calcular a média de 
	// colisões por mensagem, por estação.
	public void coletaColisaoPorMensagem(int rodada, int idEstacao, long idMensagem, long numeroDeColisoes){
		// Só armazena estatísticas quando o quadro é da mesma "cor" da rodada.
		if(rodada==this.rodadaAtual && this.rodadaAtual > 0){
			// Armazena na hashtable as colisões contabilizadas até o momento.
			this.estatisticas[idEstacao].colisoesPorMensagem.put(idMensagem, numeroDeColisoes);
		}
	}
	
	// Coleta geração de quadro para transmissão de mensagem.
	// Invocado sempre que há um evento de geração de um quadro para transmissão.
	public void coletaQuadroPorMensagem(int rodada, int idEstacao, long idMensagem){
		// Só armazena estatísticas quando o quadro é da mesma "cor" da rodada.
		if(rodada==this.rodadaAtual && this.rodadaAtual > 0){
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
	
	// Coleta valor de início de período de utilização e armazena na hash indexada pelo id do quadro.
	public void coletaInicioPeriodoUtilizacao(int idEstacao, int idEstacaoRemetente, double instanteDeTempo){
		if(this.estatisticas[idEstacao].utilizacaoInicial.get(idEstacaoRemetente)==null)
			this.estatisticas[idEstacao].utilizacaoInicial.put(idEstacaoRemetente, instanteDeTempo);
	}

	// Coleta valor de início de período de utilização na hash pelo id do quadro informado e
	// cria uma instância de Utilizacao para armazenar no array de início e fim de utilização.
	public void coletaFimPeriodoUtilizacao(int idEstacao, int idEstacaoRemetente, double instanteFinal){
		double instanteInicial = this.estatisticas[idEstacao].utilizacaoInicial.remove(idEstacaoRemetente);
		double utilizacao = instanteFinal - instanteInicial;
		this.estatisticas[idEstacao].utilizacao += utilizacao;
	}
	
	// Incrementa o número de quadros transmitidos com sucesso
	// na estação i.
	public void coletaTransmissaoDeQuadroComSucessoNaEstacao(int estacao){
		this.estatisticas[estacao].numeroQuadrosTransmitidosComSucesso++;
	}

	// Método privado para simples manipulação do valor de início da rodada.
	private void setInstanteInicioRodada(double instanteInicioRodada) {
		this.instanteInicioRodada = instanteInicioRodada;
	}

	// Método para simples recuperação do valor de início de rodada.
	public double getInstanteInicioRodada() {
		return instanteInicioRodada;
	}

	// Método privado para simples manipulação do valor de final da rodada.
	private void setInstanteFimRodada(double instanteFimRodada) {
		this.instanteFimRodada = instanteFimRodada;
	}

	// Método para simples recuperação do valor de final de rodada.
	public double getInstanteFimRodada() {
		return instanteFimRodada;
	}
	
	// Recupera array de estatísticas indexado pelo id do terminal
	// para cálculo de intervalo de confiança.
	public Estatisticas[] getEstatisticas() {
		return this.estatisticas;
	}
}
