package br.com.wifeleviro.ad.modelo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;


public class Tst {

	public static void main(String[] args) {

		System.out.println(new GregorianCalendar(1986, 3, 24).getTimeInMillis());
		
		Date d = new Date(Long.parseLong("514695600000"));
		
//		Random r = new Random();
//		long limite = new GregorianCalendar(1986, 3, 24).getTimeInMillis();
//		
//		for (long i = 0; i < limite; i++){
//			double next = r.nextDouble()%1;
//			if(next>=1)System.out.println(next);
//		}
//		System.out.println("FIM");
		
	}
}
