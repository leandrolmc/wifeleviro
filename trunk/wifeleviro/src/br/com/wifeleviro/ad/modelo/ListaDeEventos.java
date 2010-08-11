package br.com.wifeleviro.ad.modelo;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/*
 * Classe que representa a lista de eventos �nica do programa.
 */
public class ListaDeEventos {

	// �rvore rubro-negra indexada pelo instante de tempo (Double) de simula��o
	// e armazena em cada instante de tempo um vetor de Evento�s a serem
	// tratados no mesmo.
	private TreeMap<Double, LinkedList<Evento>> tree;
	
	// Instante de tempo atual de simula��o.
	private double instanteDeTempo;
	
	// Subclasse para transporte dos dados do pr�ximo evento.
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
	
	// Construtor inicial padr�o.
	public ListaDeEventos(){
		tree = new TreeMap<Double, LinkedList<Evento>>();
		this.instanteDeTempo = 0;
	}
	
	// Armazena na �rvore rubro-negra, 
	// no instante de tempo informado,
	// o evento dado como par�metro.
	public void put(double instanteDeTempo, Evento e){
		if (instanteDeTempo < 0)
			System.out.println("INSTANTE DE TEMPO: "+instanteDeTempo+" | EVENTO: "+e.getTipoEvento());
		LinkedList<Evento> col = tree.get(instanteDeTempo);
		if(col == null){
			col = new LinkedList<Evento>();
		}
		col.add(e);
		tree.put(instanteDeTempo, col);
	}
	
	// Recupera o pr�ximo evento a ser tratado no simulador.
	public ProximoEvento proximoEvento(){
		this.instanteDeTempo = (Double)tree.firstKey();
		LinkedList<Evento> eventos = (LinkedList<Evento>)tree.get(this.instanteDeTempo);
		tree.remove(this.instanteDeTempo);
		Evento proximoEvento = eventos.pollFirst();
		if(eventos.size()>0)
			tree.put(this.instanteDeTempo, eventos);
		return new ProximoEvento(this.instanteDeTempo, proximoEvento);
	}
	
	// Remove o primeiro evento da �rvore que corresponda ao
	// terminal e tipo de evento solicitados.
	public Evento removeEvento(int terminal, int tipoEvento){
		
		Evento saida = null;
		Map<Double, LinkedList<Evento>> treeTmp = new TreeMap<Double, LinkedList<Evento>>();
			
		while(saida == null && !tree.isEmpty()){
			double instanteTmp = (Double)tree.firstKey();
			LinkedList<Evento> eventos = (LinkedList<Evento>)tree.get(instanteTmp);
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
	
	// Apresenta o tamanho atual da �rvore.
	public int size(){
		return this.tree.size();
	}
	
	// Informa se a �rvore est� vazia.
	public boolean isEmpty(){
		return this.tree.isEmpty();
	}
	
	// Informa o instante de tempo atual da �rvore.
	public double getInstanteDeTempoAtual(){
		return this.instanteDeTempo;
	}
}
