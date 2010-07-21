package br.com.wifeleviro.ad.modelo;

import java.util.GregorianCalendar;
import java.util.Random;


public class Tst {

	public static void main(String[] args) {

		//System.out.println(new GregorianCalendar(1985, 0, 17, 11, 40).getTimeInMillis());
		
		Random r = new Random();
		long limite = new GregorianCalendar(1985, 0, 17, 11, 40).getTimeInMillis();
		
		for (long i = 0; i < limite; i++){
			double next = r.nextDouble()%1;
			if(next>=1)System.out.println(next);
		}
		System.out.println("FIM");
		
	}
}
