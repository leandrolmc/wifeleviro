package br.com.wifeleviro.ad.modelo;

public class Evento {

	public static final int GERAR_MENSAGEM = 0;
	public static final int INICIO_TX_PC = 1;
	public static final int FIM_TX_PC = 6;
	public static final int CHEGADA_QUADRO_NO_RX_HUB = 2;
	public static final int INICIO_CHEGADA_QUADRO_NO_RX_TERMINAL = 3;
	public static final int FIM_CHEGADA_QUADRO_NO_RX_TERMINAL = 4;
	public static final int INICIO_REFORCO_COLISAO = 5;
	public static final int FIM_REFORCO_COLISAO = 7;
	
	private int tipoEvento;
	private int terminalOrigem;
	private Quadro quadro;
	
	public Evento(int tipoEvento, int terminalOrigem, Quadro quadro){
		this.tipoEvento = tipoEvento;
		this.terminalOrigem = terminalOrigem;
		this.quadro = quadro;
	}
	
	public int getTipoEvento() {
		return tipoEvento;
	}

	public int getTerminalOrigem() {
		return terminalOrigem;
	}
	
	public Quadro getQuadro() {
		return quadro;
	}

}
