package br.com.wifeleviro.ad.modelo;

public class Evento {

	public static final int GERAR_MENSAGEM = 0;
	public static final int CHEGADA_QUADRO_NO_RX_HUB = 1;
	public static final int CHEGADA_QUADRO_NO_RX_TERMINAL = 2;
	public static final int GERAR_REFORCO_COLISAO = 3;
	
	private int tipoEvento;
	private int idTerminal;
	
	public Evento(int idTerminal, int tipoEvento){
		this.idTerminal = idTerminal;
		this.tipoEvento = tipoEvento;
	}

	public int getIdTerminal() {
		return idTerminal;
	}
	
	public int getTipoEvento() {
		return tipoEvento;
	}

}

