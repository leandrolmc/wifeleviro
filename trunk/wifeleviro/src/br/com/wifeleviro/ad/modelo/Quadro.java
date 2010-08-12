package br.com.wifeleviro.ad.modelo;

import br.com.wifeleviro.ad.util.GeradorRandomicoSingleton;

/*
 * Classe que representa um Quadro que é transmitido
 * no fluxo do simulador.
 */
public class Quadro {
	
	public static final double TEMPO_MINIMO_ENTRE_QUADROS = (double)0.0000096;
	public static final double SLOT_RETRANSMISSAO = (double)0.0000512;
	
	private long id;
	private int rodada;
	
	private Integer idRemetente;
	private Integer idDestinatario;
	
	private Mensagem mensagem;
	
	private int colisoes;
	
	private Double instanteTempoInicioTx;
	
	public Quadro(int rodada, Integer idRemetente, Integer idDestinatario, Mensagem mensagem) {
		this.rodada = rodada;
		this.idRemetente = idRemetente;
		this.idDestinatario = idDestinatario;
		this.mensagem = mensagem;
		this.colisoes = 0;
		this.id = GeradorRandomicoSingleton.getInstance().gerarProximoRandomicoAuxiliar();
		this.instanteTempoInicioTx = null;
	}

	public void setInstanteTempoInicioTx(double instante){
		this.instanteTempoInicioTx = instante;
	}
	
	public double getInstanteTempoInicioTx(){
		return this.instanteTempoInicioTx;
	}
	
	public Integer getIdRemetente() {
		return idRemetente;
	}
	public void setIdRemetente(Integer idRemetente) {
		this.idRemetente = idRemetente;
	}

	public void setIdDestinatario(Integer idDestinatario) {
		this.idDestinatario = idDestinatario;
	}

	public Integer getIdDestinatario() {
		return idDestinatario;
	}

	public void setMensagem(Mensagem mensagem) {
		this.mensagem = mensagem;
	}

	public Mensagem getMensagem() {
		return mensagem;
	}

	public void setColisoes(int colisoes) {
		this.colisoes = colisoes;
	}

	public int getColisoes() {
		return colisoes;
	}
	
	public void incColisoes(){
		++this.colisoes;
		this.mensagem.incNumeroColisoes();
	}
	
	public long getId(){
		return this.id;
	}
	
	public int getRodada(){
		return this.rodada;
	}
}
