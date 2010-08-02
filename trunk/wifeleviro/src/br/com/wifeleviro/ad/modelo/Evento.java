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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((quadro == null) ? 0 : quadro.hashCode());
		result = prime * result + terminalOrigem;
		result = prime * result + tipoEvento;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Evento other = (Evento) obj;
		if (quadro == null) {
			if (other.quadro != null)
				return false;
		} else if (!quadro.equals(other.quadro))
			return false;
		if (terminalOrigem != other.terminalOrigem)
			return false;
		if (tipoEvento != other.tipoEvento)
			return false;
		return true;
	}

}
