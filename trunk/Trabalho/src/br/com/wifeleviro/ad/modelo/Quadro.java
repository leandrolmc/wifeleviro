package br.com.wifeleviro.ad.modelo;

import java.util.Date;

public class Quadro {
	private int idRemetente;
	private int idDestinatario;
	private Date horaEnvio;
	
	public Quadro(int idRemetente, int idDestinatario) {
		this.idRemetente = idRemetente;
		this.idDestinatario = idDestinatario;
	}
	public void setHoraEnvio(Date horaEnvio) {
		this.horaEnvio = horaEnvio;
	}
	public Date getHoraEnvio() {
		return horaEnvio;
	}
	public int getIdRemetente() {
		return idRemetente;
	}
	public void setIdRemetente(int idRemetente) {
		this.idRemetente = idRemetente;
	}
	public int getIdDestinatario() {
		return idDestinatario;
	}
	public void setIdDestinatario(int idDestinatario) {
		this.idDestinatario = idDestinatario;
	}
	
}
