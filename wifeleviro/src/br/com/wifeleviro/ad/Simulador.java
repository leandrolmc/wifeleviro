package br.com.wifeleviro.ad;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import br.com.wifeleviro.ad.modelo.Terminal;

/*
 * Classe responsável por receber os parâmetros iniciais e
 * iniciar a simulação após configurá-la com estes parâmetros.
 */
public class Simulador {

	public static void main(String[] args) {

		// Para cenários 1 e 2 
//		int qtdTerminais = 2;
		
		// Para cenários 3 e 4
		int qtdTerminais = 4;

		// Cenário 1
//		Terminal pc1 = new Terminal(0, 100, Terminal.TIPO_DETERMINISTICO, 0.08, 40);
//		Terminal pc2 = new Terminal(1, 80, Terminal.TIPO_DETERMINISTICO, 0.08, 40);

		// Cenário 2
//		Terminal pc1 = new Terminal(0, 100, Terminal.TIPO_EXPONENCIAL, 0.08, 40);
//		Terminal pc2 = new Terminal(1, 80, Terminal.TIPO_EXPONENCIAL, 0.08, 40);
		
		// Cenário 3
		Terminal pc1 = new Terminal(0, 100, Terminal.TIPO_DETERMINISTICO, 0.08, 40);
		Terminal pc2 = new Terminal(1, 80, Terminal.TIPO_DETERMINISTICO, 0.016, 1);
		Terminal pc3 = new Terminal(2, 60, Terminal.TIPO_DETERMINISTICO, 0.016, 1);
		Terminal pc4 = new Terminal(3, 40, Terminal.TIPO_DETERMINISTICO, 0.016, 1);

		// Cenário 4
//		Terminal pc1 = new Terminal(0, 100, Terminal.TIPO_DETERMINISTICO, 0.08, 40);
//		Terminal pc2 = new Terminal(1, 80, Terminal.TIPO_EXPONENCIAL, 0.016, 1);
//		Terminal pc3 = new Terminal(2, 60, Terminal.TIPO_EXPONENCIAL, 0.016, 1);
//		Terminal pc4 = new Terminal(3, 40, Terminal.TIPO_EXPONENCIAL, 0.016, 1);


		// Para cenários 1 e 2 
//		Terminal[] terminais = {pc1, pc2};
		
		// Para cenários 3 e 4
		Terminal[] terminais = {pc1, pc2, pc3, pc4};
		
		Orquestrador orch = new Orquestrador(qtdTerminais, terminais);
		
		System.out.println("INICIO DA SIMULACAO: "+(new SimpleDateFormat("HH:mm:ss").format(new GregorianCalendar().getTime())));
		
		orch.executarSimulacao();
		
		System.out.println("FIM DA SIMULACAO: "+(new SimpleDateFormat("HH:mm:ss").format(new GregorianCalendar().getTime())));
		
	}

}
