package br.com.wifeleviro.ad.modelo;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class ListaDeEventos {

	private TreeMap<Double, ArrayList<Evento>> tree;
	
	private double instanteDeTempo;
	
	public class ProximoEvento{
		
		private double tempo;
		private Evento proximo;
		
		public ProximoEvento(double tempo, Evento proximo){
			this.tempo = tempo;
			this.proximo = proximo;
		}
		
		public double getTempo(){
			return this.tempo;
		}
		
		public Evento getEvento(){
			return this.proximo;
		}
	}
	
	public ListaDeEventos(){
		tree = new TreeMap<Double, ArrayList<Evento>>();
		this.instanteDeTempo = 0;
	}
	
	public void put(double instanteDeTempo, Evento e){
		if (instanteDeTempo < 0)
			System.out.println("INSTANTE DE TEMPO: "+instanteDeTempo+" | EVENTO: "+e.getTipoEvento());
		ArrayList<Evento> col = tree.get(instanteDeTempo);
		if(col == null){
			col = new ArrayList<Evento>();
		}
		col.add(e);
		tree.put(instanteDeTempo, col);
	}
	
	public ProximoEvento proximoEvento(){
		this.instanteDeTempo = (Double)tree.firstKey();
		ArrayList<Evento> eventos = (ArrayList<Evento>)tree.get(this.instanteDeTempo);
		tree.remove(this.instanteDeTempo);
		Evento proximoEvento = eventos.remove(0);
		if(eventos.size()>0)
			tree.put(this.instanteDeTempo, eventos);
		return new ProximoEvento(this.instanteDeTempo, proximoEvento);
	}
	
	public Evento removeEvento(int terminal, int tipoEvento){
		
		Evento saida = null;
		Map<Double, ArrayList<Evento>> treeTmp = new TreeMap<Double, ArrayList<Evento>>();
			
		while(saida == null && !tree.isEmpty()){
			double instanteTmp = (Double)tree.firstKey();
			ArrayList<Evento> eventos = (ArrayList<Evento>)tree.get(instanteTmp);
			tree.remove(instanteTmp);
			for(Evento evento : eventos){
				if(evento.getTerminalOrigem() == terminal && evento.getTipoEvento() == tipoEvento){
					saida = evento;
					eventos.remove(evento);
					break;
				}
			}
			if(eventos.size()>0) treeTmp.put(instanteTmp, eventos);
		}
			
		tree.putAll(treeTmp);
		
		return saida;
	}
	
	public int size(){
		return this.tree.size();
	}
	
	public boolean isEmpty(){
		return this.tree.isEmpty();
	}
	
	public double getInstanteDeTempoAtual(){
		return this.instanteDeTempo;
	}
}
