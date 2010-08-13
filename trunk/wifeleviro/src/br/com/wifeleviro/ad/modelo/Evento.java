package br.com.wifeleviro.ad.modelo;

/*
 * Classe que representa um evento a ser tratado.
 */
public class Evento {

	// Tipos de eventos possíveis.
	public static final int GERAR_MENSAGEM = 0;
	public static final int INICIO_TX_PC = 1;
	public static final int FIM_TX_PC = 6;
	public static final int INICIO_CHEGADA_QUADRO_NO_RX_TERMINAL = 3;
	public static final int FIM_CHEGADA_QUADRO_NO_RX_TERMINAL = 4;
	public static final int INICIO_REFORCO_COLISAO = 5;
	public static final int FIM_REFORCO_COLISAO = 7;
	
	private Integer tipoEvento;
	private Integer terminalOrigem;
	private Integer terminalDestino;
	private Quadro quadro;
	
	public Evento(int tipoEvento, int terminalOrigem, Quadro quadro){
		this.tipoEvento = tipoEvento;
		this.terminalOrigem = terminalOrigem;
		this.terminalDestino = null;
		this.quadro = quadro;
	}

	public Evento(int tipoEvento, int terminalOrigem, int terminalDestino, Quadro quadro){
		this.tipoEvento = tipoEvento;
		this.terminalOrigem = terminalOrigem;
		this.terminalDestino = terminalDestino;
		this.quadro = quadro;
	}

	public Integer getTipoEvento() {
		return tipoEvento;
	}

	public Integer getTerminalOrigem() {
		return terminalOrigem;
	}
	
	public Integer getTerminalDestino(){
		return terminalDestino;
	}
	
	public Quadro getQuadro() {
		return quadro;
	}

}
