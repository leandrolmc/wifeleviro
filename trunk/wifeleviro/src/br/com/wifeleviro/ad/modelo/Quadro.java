package br.com.wifeleviro.ad.modelo;


public class Quadro {
	
	public static final double TEMPO_MINIMO_ENTRE_QUADROS = 0.0000096;
	
	private int idRemetente;
	
	public Quadro(int idRemetente) {
		this.idRemetente = idRemetente;
	}

	public int getIdRemetente() {
		return idRemetente;
	}
	public void setIdRemetente(int idRemetente) {
		this.idRemetente = idRemetente;
	}
}
