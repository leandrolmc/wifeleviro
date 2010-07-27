package br.com.wifeleviro.ad;

import br.com.wifeleviro.ad.modelo.Terminal;

public class Simulador {

	public static void main(String[] args) {

		int qtdTerminais = 1;
		
		Terminal pc1 = new Terminal(0, 80, Terminal.TIPO_DETERMINISTICO, 0.08, 1);
		Terminal[] terminais = {pc1};
		
		Orquestrador orch = new Orquestrador(qtdTerminais, terminais);
		
		orch.executarSimulacao();
		
		System.out.println("FIM DA SIMULACAO");
		
	}

}