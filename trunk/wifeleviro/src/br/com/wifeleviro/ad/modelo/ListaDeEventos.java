package br.com.wifeleviro.ad.modelo;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/*
 * Classe que representa a lista de eventos única do programa.
 */
public class ListaDeEventos {

	// Árvore rubro-negra indexada pelo instante de tempo (Double) de simulação
	// e armazena em cada instante de tempo um vetor de Evento´s a serem
	// tratados no mesmo.
	private TreeMap<Double, ArrayList<Evento>> tree;
	
	// Instante de tempo atual de simulação.
	private double instanteDeTempo;
	
	// Subclasse para transporte dos dados do próximo evento.
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
	
	// Construtor inicial padrão.
	public ListaDeEventos(){
		tree = new TreeMap<Double, ArrayList<Evento>>();
		this.instanteDeTempo = 0;
	}
	
	// Armazena na árvore rubro-negra, 
	// no instante de tempo informado,
	// o evento dado como parâmetro.
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
	
	// Recupera o próximo evento a ser tratado no simulador.
	public ProximoEvento proximoEvento(){
		this.instanteDeTempo = (Double)tree.firstKey();
		ArrayList<Evento> eventos = (ArrayList<Evento>)tree.get(this.instanteDeTempo);
		tree.remove(this.instanteDeTempo);
		Evento proximoEvento = eventos.remove(0);
		if(eventos.size()>0)
			tree.put(this.instanteDeTempo, eventos);
		return new ProximoEvento(this.instanteDeTempo, proximoEvento);
	}
	
	// Remove o primeiro evento da árvore que corresponda ao
	// terminal e tipo de evento solicitados.
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
	
	// Apresenta o tamanho atual da árvore.
	public int size(){
		return this.tree.size();
	}
	
	// Informa se a árvore está vazia.
	public boolean isEmpty(){
		return this.tree.isEmpty();
	}
	
	// Informa o instante de tempo atual da árvore.
	public double getInstanteDeTempoAtual(){
		return this.instanteDeTempo;
	}
}
