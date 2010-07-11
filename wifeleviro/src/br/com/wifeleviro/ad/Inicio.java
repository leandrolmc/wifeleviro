package br.com.wifeleviro.ad;

import br.com.wifeleviro.ad.modelo.Hub;
import br.com.wifeleviro.ad.modelo.Link;
import br.com.wifeleviro.ad.modelo.Terminal;

public class Inicio {
	
	public static void main(String[] args) {
		Hub hub = new Hub();
		Terminal pc1 = new Terminal(1);
		Terminal pc2 = new Terminal(2);
		Terminal pc3 = new Terminal(3);
		Terminal pc4 = new Terminal(4);
		new Link(pc1.getPorta(), hub.getPorta1(), 100);
		new Link(pc2.getPorta(), hub.getPorta2(), 80);
		new Link(pc3.getPorta(), hub.getPorta3(), 60);
		new Link(pc4.getPorta(), hub.getPorta4(), 40);
		
		System.out.println(hub.getPorta2().getLink().getDistancia());
		
	}
	
}
