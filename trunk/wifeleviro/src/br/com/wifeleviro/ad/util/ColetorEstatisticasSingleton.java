package br.com.wifeleviro.ad.util;

public class ColetorEstatisticasSingleton {

	private static ColetorEstatisticasSingleton _instance;
	
	private ColetorEstatisticasSingleton(){}
	
	public static ColetorEstatisticasSingleton getInstance(){
		if(_instance == null){
			_instance = new ColetorEstatisticasSingleton();
		}
		
		return _instance;
	}
	
	
	
}
