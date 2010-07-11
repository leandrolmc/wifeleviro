package br.com.wifeleviro.ad.modelo;

public class Hub {
	private Porta porta1;
	private Porta porta2;
	private Porta porta3;
	private Porta porta4;

	public Hub() {
		porta1 = new Porta();
		porta2 = new Porta();
		porta3 = new Porta();
		porta4 = new Porta();
	}
	
	public Porta getPorta1() {
		return porta1;
	}
	public void setPorta1(Porta porta1) {
		this.porta1 = porta1;
	}
	public Porta getPorta2() {
		return porta2;
	}
	public void setPorta2(Porta porta2) {
		this.porta2 = porta2;
	}
	public Porta getPorta3() {
		return porta3;
	}
	public void setPorta3(Porta porta3) {
		this.porta3 = porta3;
	}
	public Porta getPorta4() {
		return porta4;
	}
	public void setPorta4(Porta porta4) {
		this.porta4 = porta4;
	}
	
}
