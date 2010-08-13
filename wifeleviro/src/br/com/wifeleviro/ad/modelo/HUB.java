package br.com.wifeleviro.ad.modelo;

// Propaga os eventos que avisam sobre início de meio ocupado e início de meio livre.
public class HUB {

	public static void propagaAviso(int tipoEvento, int idTerminalOrigem, Quadro quadro, Terminal[] pcs, ListaDeEventos lista){
		
		Terminal origem = pcs[idTerminalOrigem];
		double distanciaPropagacaoOrigem = origem.getDistanciaHub();
		
		for(int i = 0; i < pcs.length; i++){
			Terminal destino = pcs[i];
			double distanciaPropagacaoDestino = destino.getDistanciaHub();
			double distanciaTotalPropagacao = distanciaPropagacaoOrigem + distanciaPropagacaoDestino;
			
			Evento propagacao = new Evento(tipoEvento, idTerminalOrigem, i, quadro); 
			double instanteChegadaDestino = lista.getInstanteDeTempoAtual() + MeioFisico.calculaTempoPropagacao(distanciaTotalPropagacao);
			lista.put(instanteChegadaDestino, propagacao);
		}
	}
}
