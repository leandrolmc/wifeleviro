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

		try{
			int cenario = Integer.parseInt(args[0]);
			
			int qtdTerminais = 0;
			Terminal[] terminais = null;

			// Cenário ZERO representa os testes de correção.
			if(cenario == 0){
//				qtdTerminais = 2;
//				Terminal pc1 = new Terminal(0, 100, Terminal.TIPO_DETERMINISTICO, (float)0.08, 40);
//				Terminal pc2 = new Terminal(1, 100, Terminal.TIPO_DETERMINISTICO, (float)0.08, 40);
//				terminais = new Terminal[qtdTerminais];
//				terminais[0] = pc1;
//				terminais[1] = pc2;
				
				qtdTerminais = 1;
				Terminal pc1 = new Terminal(0, 100, Terminal.TIPO_DETERMINISTICO, (float)0.08, 40);
				terminais = new Terminal[qtdTerminais];
				terminais[0] = pc1;
			
			}else if(cenario == 1){
				qtdTerminais = 2;
				Terminal pc1 = new Terminal(0, 100, Terminal.TIPO_DETERMINISTICO, (float)0.08, 40);
				Terminal pc2 = new Terminal(1, 80, Terminal.TIPO_DETERMINISTICO, (float)0.08, 40);
				terminais = new Terminal[qtdTerminais]; 
				terminais[0] = pc1;
				terminais[1] = pc2;
			}else if(cenario == 2){
				qtdTerminais = 2;
				Terminal pc1 = new Terminal(0, 100, Terminal.TIPO_EXPONENCIAL, (float)0.08, 40);
				Terminal pc2 = new Terminal(1, 80, Terminal.TIPO_EXPONENCIAL, (float)0.08, 40);
				terminais = new Terminal[qtdTerminais]; 
				terminais[0] = pc1;
				terminais[1] = pc2;
			}else if(cenario == 3){
				qtdTerminais = 4;
				Terminal pc1 = new Terminal(0, 100, Terminal.TIPO_DETERMINISTICO, (float)0.08, 40);
				Terminal pc2 = new Terminal(1, 80, Terminal.TIPO_DETERMINISTICO, (float)0.016, 1);
				Terminal pc3 = new Terminal(2, 60, Terminal.TIPO_DETERMINISTICO, (float)0.016, 1);
				Terminal pc4 = new Terminal(3, 40, Terminal.TIPO_DETERMINISTICO, (float)0.016, 1);
				terminais = new Terminal[qtdTerminais]; 
				terminais[0] = pc1;
				terminais[1] = pc2;
				terminais[2] = pc3;
				terminais[3] = pc4;
			}else if(cenario == 4){
				qtdTerminais = 4;
				Terminal pc1 = new Terminal(0, 100, Terminal.TIPO_DETERMINISTICO, (float)0.08, 40);
				Terminal pc2 = new Terminal(1, 80, Terminal.TIPO_EXPONENCIAL, (float)0.016, 1);
				Terminal pc3 = new Terminal(2, 60, Terminal.TIPO_EXPONENCIAL, (float)0.016, 1);
				Terminal pc4 = new Terminal(3, 40, Terminal.TIPO_EXPONENCIAL, (float)0.016, 1);
				terminais = new Terminal[qtdTerminais]; 
				terminais[0] = pc1;
				terminais[1] = pc2;
				terminais[2] = pc3;
				terminais[3] = pc4;
			}else{
				System.out.println("O cenário informado não existe.");
			}
				
			Orquestrador orch = new Orquestrador(qtdTerminais, terminais);
			
			System.out.println("INICIO DA SIMULACAO: "+(new SimpleDateFormat("HH:mm:ss").format(new GregorianCalendar().getTime())));
			
			orch.executarSimulacao();
			
			System.out.println("FIM DA SIMULACAO: "+(new SimpleDateFormat("HH:mm:ss").format(new GregorianCalendar().getTime())));
		}catch(NumberFormatException e){
			System.out.println("Deve ser informado um inteiro entre 1 e 4, correspondente ao cenário, como entrada.");
		}catch(Exception e){
			System.out.println("Erro inesperado: "+e.getMessage());
			e.printStackTrace();
		}
		
	}

}
