package br.com.wifeleviro.ad;

import br.com.wifeleviro.ad.modelo.Terminal;

public class Simulador {

	public static void main(String[] args) {

//		int qtdTerminais = 1;
		int qtdTerminais = 2;
		
		Terminal pc1 = new Terminal(0, 100, Terminal.TIPO_DETERMINISTICO, 0.08, 5);
		Terminal pc2 = new Terminal(1, 80, Terminal.TIPO_DETERMINISTICO, 0.08, 5);
//		Terminal[] terminais = {pc1};
		Terminal[] terminais = {pc1, pc2};
		
		Orquestrador orch = new Orquestrador(qtdTerminais, terminais);
		
		orch.executarSimulacao();
		
		System.out.println("FIM DA SIMULACAO");
		
	}

}
