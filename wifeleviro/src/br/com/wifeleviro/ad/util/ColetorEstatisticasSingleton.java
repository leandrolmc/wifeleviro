package br.com.wifeleviro.ad.util;

// Classe �nica de coleta dos dados estat�sticos para avalia��o do programa.
// O padr�o singleton utilizado garante que n�o exista mais que uma int�ncia 
//  desta classe dentro do programa em execu��o. 
public class ColetorEstatisticasSingleton {

	// Construtor padr�o da classe.
	private ColetorEstatisticasSingleton() {
	}

	// Vari�vel privada que cont�m a �nica refer�ncia para o coletor de
	// estat�sticas.
	private static ColetorEstatisticasSingleton _instance;

	// Met�do capaz de gerar a inst�ncia �nica do coletor de estat�sticas, caso
	// esta ainda n�o exista. Caso exista, simplesmente retorna uma refer�ncia
	// para a mesma.
	public static ColetorEstatisticasSingleton getInstance() {
		if (_instance == null) {
			_instance = new ColetorEstatisticasSingleton();
		}

		return _instance;
	}

	// Sub-classe que ser� utilizada unicamente pelo coletor de estat�sticas
	// para armazenamento dos dados coletados.
	private class Estatisticas {

	}

	// Coleta de E[Tap(i)] (tempo de acesso de um quadro na esta��o i)

	// Coleta o valor do tempo quando do in�cio do acesso de um quadro na
	// esta��o i. Todo quadro tem um identificador �nico que ir� index�-lo
	// na hash de armazenamento do tempo inicial.
	public void iniciaColetaTap(long idEstacao, long idQuadro, double tempo) {

	}

	// Coleta o valor do tempo quando do fim do acesso 
	// de um quadro na esta��o i. Busca no hash de armazenamento do tempo
	// inicial o valor referente ao identificador do quadro informado de
	// modo a armazenar este em um agregador de "tap�s" da esta��o i,
	// perdendo a identifica��o �nica do quadro e tornando esta medi��o
	// em um "fregu�s t�pico" do sistema.
	public void finalizaColetaTap(long idEstacao, long idQuadro, double tempo) {

	}

	// Coleta de E[Tam(i)]

	// Coleta o valor do tempo quando do in�cio do acesso de um quadro na
	// esta��o i. Todo quadro tem um identificador �nico que ir� index�-lo
	// na hash de armazenamento do tempo inicial.
	public void iniciaColetaTam(long idEstacao, long idQuadro, double tempo) {

	}

	// Coleta o valor do tempo quando do fim do acesso 
	// de um quadro na esta��o i. Busca no hash de armazenamento do tempo
	// inicial o valor referente ao identificador do quadro informado de
	// modo a armazenar este em um agregador de "tap�s" da esta��o i,
	// perdendo a identifica��o �nica do quadro e tornando esta medi��o
	// em um "fregu�s t�pico" do sistema.
	public void finalizaColetaTam(long idEstacao, long idQuadro, double tempo) {

	}
	
	// Coleta de E[NCm(i)]
	
	// Coleta dados sobre a exist�ncia ou n�o de colis�o na transmiss�o de um
	// quadro qualquer, identificado a mensagem transmitida e a esta��o,
	// de modo que seja poss�vel calcular a m�dia de colis�es por mensagem,
	// por esta��o.
	public void coletaColisaoPorQuadro(long idEstacao, long idMensagem, boolean houveColisao){
		
	}

	// Coleta de vaz�o(i)

	// Coleta de utiliza��o do Ethernet com intervalos de confian�a de 95%

}
