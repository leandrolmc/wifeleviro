package br.com.wifeleviro.ad.util;

import java.util.Random;

public class GeradorRandomicoSingleton {

	private static GeradorRandomicoSingleton _instance = null; 
	
	private long semente;
	private Random random;
	
	private Random randomAux;
	
	private GeradorRandomicoSingleton (){
		this.semente = Long.parseLong("474820800000");
		this.random = new Random(this.semente);
		this.randomAux = new Random(Long.parseLong("514695600000"));
	}
	
	public static GeradorRandomicoSingleton getInstance(){
		if(_instance == null)
			_instance = new GeradorRandomicoSingleton();
		
		return _instance;
	}
	
	public double gerarProximoRandomico(){
		return random.nextDouble();
	}
	
	public long gerarProximoRandomicoAuxiliar(){
		return randomAux.nextLong();
	}
	
	
	
	
}
