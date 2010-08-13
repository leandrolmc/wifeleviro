package br.com.wifeleviro.ad.util.estatisticas;

import br.com.wifeleviro.ad.modelo.Mensagem;
import br.com.wifeleviro.ad.util.estatisticas.metricas.NCm;
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
		
		protected NCm ncm;
		
		// Armazena os pares de início e fim de utilização do meio.
		protected double utilizacao;
		
		// Contador inicializado em zero do número de quadros que
		// atingiram RX do HUB com sucesso durante a rodada.
		protected Long numeroQuadrosTransmitidosComSucesso;
		
		// Inicialização com valores padrão ou instanciação de classe.
		protected Estatisticas(){
			tap = new TAp();
			tam = new TAm();
			ncm = new NCm();
			utilizacao = 0;
			numeroQuadrosTransmitidosComSucesso = (long)0;
		}
		
		public TAp getTap(){
			return this.tap; 
		}

		public TAm getTam(){
			return this.tam;
		}

		public NCm getNCm(){
			return this.ncm;
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
	public void coletaColisaoPorMensagem(int idEstacao, Mensagem m){
		// Só armazena estatísticas quando o quadro é da mesma "cor" da rodada.
//		if(m.getRodada()==this.rodadaAtual && this.rodadaAtual > 0){
			
			long numColisoes = m.getNumeroColisoes();
			long numQuadros = m.getNumeroQuadros();
			
			double amostraNumeroColisoesPorMensagem = (double)numColisoes/(double)numQuadros;

//			System.out.println("Estacao "+idEstacao+" | numColisoes: "+numColisoes+" | numQuadros: "+numQuadros+" | amostra: "+amostraNumeroColisoesPorMensagem);
			
			// Armazena no acumulador de amostras de colisões.
			this.estatisticas[idEstacao].ncm.acumularTempo(amostraNumeroColisoesPorMensagem);
			
//		}
	}

	// Coleta instante de tempo inicial da rodada.
	public void coletaInicioRodada(double instanteDeTempo){
		this.setInstanteInicioRodada(instanteDeTempo);
	}
	
	// Coleta instante de tempo final da rodada.
	public void coletaFimRodada(double instanteDeTempo){
		this.setInstanteFimRodada(instanteDeTempo);
	}
	

	// Coleta valor de início de período de utilização na hash pelo id do quadro informado e
	// cria uma instância de Utilizacao para armazenar no array de início e fim de utilização.
	public void coletaUtilizacao(int idEstacao, double periodoOcupado){
		this.estatisticas[idEstacao].utilizacao += periodoOcupado;
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
