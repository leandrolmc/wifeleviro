package br.com.wifeleviro.ad.modelo;

public class Terminal extends Thread {
	private int id;
	private Porta porta;
	
	public Terminal(int id) {
		System.out.println("Terminal ["+id+"] criado.");
		this.id = id;
		porta = new Porta();
	}
	
	public void run() {
		System.out.println("Terminal ["+id+"] iniciado.");
		while (true) {
			int lambda = 500;
			try {
				Thread.sleep(lambda);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Terminal ["+id+"] iniciando envio de quadro.");
			Quadro quadro = new Quadro(id, 4);
			porta.enviar(quadro);
		}
	}
	
	public int getIdTerminal() {
		return id;
	}

	public Porta getPorta() {
		return porta;
	}

	public void setPorta(Porta porta) {
		this.porta = porta;
	}

	public void setIdTerminal(int id) {
		this.id = id;
	}
}
