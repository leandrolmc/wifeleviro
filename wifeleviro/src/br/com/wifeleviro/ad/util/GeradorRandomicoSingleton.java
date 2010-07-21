package br.com.wifeleviro.ad.util;

import java.util.Random;

public class GeradorRandomicoSingleton {

	private static GeradorRandomicoSingleton _instance = null; 
	
	private long semente;
	private Random random;
	
	private GeradorRandomicoSingleton (){
		this.semente = Long.parseLong("474820800000");
		this.random = new Random(this.semente);
	}
	
	public static GeradorRandomicoSingleton getInstance(){
		if(_instance == null)
			_instance = new GeradorRandomicoSingleton();
		
		return _instance;
	}
	
	public double gerarProximoRandomico(){
		return random.nextDouble();
	}
	
	
}
