package br.com.wifeleviro.ad.util;

// Classe única de coleta dos dados estatísticos para avaliação do programa.
// O padrão singleton utilizado garante que não exista mais que uma intância 
//  desta classe dentro do programa em execução. 
public class ColetorEstatisticasSingleton {

	// Construtor padrão da classe.
	private ColetorEstatisticasSingleton() {
	}

	// Variável privada que contém a única referência para o coletor de
	// estatísticas.
	private static ColetorEstatisticasSingleton _instance;

	// Metódo capaz de gerar a instância única do coletor de estatísticas, caso
	// esta ainda não exista. Caso exista, simplesmente retorna uma referência
	// para a mesma.
	public static ColetorEstatisticasSingleton getInstance() {
		if (_instance == null) {
			_instance = new ColetorEstatisticasSingleton();
		}

		return _instance;
	}

	// Sub-classe que será utilizada unicamente pelo coletor de estatísticas
	// para armazenamento dos dados coletados.
	private class Estatisticas {

	}

	// Coleta de E[Tap(i)] (tempo de acesso de um quadro na estação i)

	// Coleta o valor do tempo quando do início do acesso de um quadro na
	// estação i. Todo quadro tem um identificador único que irá indexá-lo
	// na hash de armazenamento do tempo inicial.
	public void iniciaColetaTap(long idEstacao, long idQuadro, double tempo) {

	}

	// Coleta o valor do tempo quando do fim do acesso 
	// de um quadro na estação i. Busca no hash de armazenamento do tempo
	// inicial o valor referente ao identificador do quadro informado de
	// modo a armazenar este em um agregador de "tap´s" da estação i,
	// perdendo a identificação única do quadro e tornando esta medição
	// em um "freguês típico" do sistema.
	public void finalizaColetaTap(long idEstacao, long idQuadro, double tempo) {

	}

	// Coleta de E[Tam(i)]

	// Coleta o valor do tempo quando do início do acesso de um quadro na
	// estação i. Todo quadro tem um identificador único que irá indexá-lo
	// na hash de armazenamento do tempo inicial.
	public void iniciaColetaTam(long idEstacao, long idQuadro, double tempo) {

	}

	// Coleta o valor do tempo quando do fim do acesso 
	// de um quadro na estação i. Busca no hash de armazenamento do tempo
	// inicial o valor referente ao identificador do quadro informado de
	// modo a armazenar este em um agregador de "tap´s" da estação i,
	// perdendo a identificação única do quadro e tornando esta medição
	// em um "freguês típico" do sistema.
	public void finalizaColetaTam(long idEstacao, long idQuadro, double tempo) {

	}
	
	// Coleta de E[NCm(i)]
	
	// Coleta dados sobre a existência ou não de colisão na transmissão de um
	// quadro qualquer, identificado a mensagem transmitida e a estação,
	// de modo que seja possível calcular a média de colisões por mensagem,
	// por estação.
	public void coletaColisaoPorQuadro(long idEstacao, long idMensagem, boolean houveColisao){
		
	}

	// Coleta de vazão(i)

	// Coleta de utilização do Ethernet com intervalos de confiança de 95%

}
