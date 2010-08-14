package br.com.wifeleviro.ad.util;

import java.util.GregorianCalendar;
import java.util.Random;

/*
 * �nico respons�vel por gerar amostras de n�meros
 * rand�micos em todo o programa.
 */
public class GeradorRandomicoSingleton {

	private static GeradorRandomicoSingleton _instance = null; 
	
	private long semente;
	// Gerador rand�mico principal.
	private Random random;
	// Gerador rand�mico auxiliar.
	private Random randomAux;
	
	private GeradorRandomicoSingleton (){
		// Semente inicial travada.
//		this.semente = Long.parseLong("474820800000");
		long seed1 = new GregorianCalendar().getTimeInMillis();
		this.semente = seed1;
		this.random = new Random(this.semente);
		// Construtor do gerador rand�mico auxiliar
		// tamb�m com semente inicial travada.
//		this.randomAux = new Random(Long.parseLong("514695600000"));
		long seed2 = seed1*seed1;
		this.randomAux = new Random(seed2);
	}
	
	// �nica forma de recuperar a inst�ncia do gerador rand�mico.
	public static GeradorRandomicoSingleton getInstance(){
		if(_instance == null)
			_instance = new GeradorRandomicoSingleton();
		
		return _instance;
	}
	
	// Utilizado para gerar um ponto flutuante.
	public double gerarProximoDoubleRandomico(){
		return random.nextDouble();
	}

	// Utilizado para gerar um inteiro.
	public int gerarProximoIntRandomico(){
		return random.nextInt();
	}
	
	// Utilizado para gerar ID�s de quadros e mensagens.
	public long gerarProximoRandomicoAuxiliar(){
		return randomAux.nextLong();
	}
	
	
	
	
}
