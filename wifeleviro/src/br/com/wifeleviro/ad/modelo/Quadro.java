package br.com.wifeleviro.ad.modelo;

import java.util.GregorianCalendar;


public class Quadro {
	
	public static final double TEMPO_MINIMO_ENTRE_QUADROS = 0.0000096;
	public static final double SLOT_RETRANSMISSAO = 0.0000512;
	
	private long id;
	
	private Integer idRemetente;
	private Integer idDestinatario;
	
	private Mensagem mensagem;
	
	private int colisoes;
	
	public Quadro(Integer idRemetente, Integer idDestinatario, Mensagem mensagem) {
		this.idRemetente = idRemetente;
		this.idDestinatario = idDestinatario;
		this.mensagem = mensagem;
		this.colisoes = 0;
		this.id = new GregorianCalendar().getTimeInMillis();
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
	}
	
	public long getId(){
		return this.id;
	}
}
