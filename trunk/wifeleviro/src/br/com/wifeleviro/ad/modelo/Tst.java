package br.com.wifeleviro.ad.modelo;


public class Tst {

	public static void main(String[] args) {

		ListaDeEventos l = new ListaDeEventos();
		
		l.put(0.21, new Evento(4, Evento.GERAR_MENSAGEM));
		l.put(0.21, new Evento(3, Evento.GERAR_MENSAGEM));
		l.put(0.21, new Evento(2, Evento.GERAR_MENSAGEM));
		l.put(0.11, new Evento(1, Evento.GERAR_MENSAGEM));
		l.put(0.31, new Evento(5, Evento.GERAR_MENSAGEM));
		l.put(0.67, new Evento(7, Evento.GERAR_MENSAGEM));
		l.put(0.45, new Evento(6, Evento.GERAR_MENSAGEM));

		while(l.size() > 0){
			
			Evento e = l.proximoEvento();
			System.out.println(e.getIdTerminal());
			
		}
		
		
	}
}
