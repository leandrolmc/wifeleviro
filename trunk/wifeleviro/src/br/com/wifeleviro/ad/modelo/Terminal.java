package br.com.wifeleviro.ad.modelo;

import java.util.LinkedList;

import br.com.wifeleviro.ad.util.GeradorRandomicoSingleton;
import br.com.wifeleviro.ad.util.estatisticas.ColetorEstatisticas;

/*
 * Classe que representa um Terminal e todas os dados
 * necessários de serem armazenados para cada terminal.
 */
public class Terminal{

	// Tipos possíveis de fluxo no terminal.
	public static final int TIPO_DETERMINISTICO = 0;
	public static final int TIPO_EXPONENCIAL = 1;
	
	private int tipo;
	private double periodo;
	
	private double pMensagens;
	
	private int id;
	private double distanciaHub;
	
	private double instanteTempoInicial;
	private double instanteTempoAtual;

	private boolean txOcupado;
	
	private int fluxosMeioOcupado;
	private int fluxosEntrantes;
	private double inicioPeriodoOcupado;
	
	private boolean forcarTransmissao;
	private boolean retransmissaoAgendada;
	
	private boolean colidiu;
	
	private LinkedList<Quadro> filaEspera;
	private Quadro filaServico;
	
	public Terminal(int id, double distanciaHub, int tipo, double periodo, double pMensagens, double instanteTempoInicializacao) {
		this.id = id;
		this.distanciaHub = distanciaHub;
		this.instanteTempoAtual = instanteTempoInicializacao;
		if(this.tipo == TIPO_EXPONENCIAL)
			this.instanteTempoAtual = this.gerarInstanteTempoProximoEventoPoisson(this.instanteTempoAtual, this.periodo);
		this.instanteTempoInicial = this.instanteTempoAtual;
		this.periodo = periodo;
		this.setpMensagens(pMensagens);
		this.txOcupado = false;
		this.setForcarTransmissao(false);
		this.setRetransmissaoAgendada(false);
		
		this.filaEspera = new LinkedList<Quadro>();
		this.filaServico = null;
		
		this.fluxosMeioOcupado = 0;
		this.fluxosEntrantes = 0;
		this.inicioPeriodoOcupado = 0;
		
		this.colidiu = false;
	}
	
	public void incFluxosEntrantes(double instante){
		if(this.fluxosEntrantes == 0){
			this.inicioPeriodoOcupado = instante;
		}
		++this.fluxosEntrantes;
	}
	
	public void decFluxosEntrantes(double instante, ColetorEstatisticas coletor){
		--this.fluxosEntrantes;
		if(this.fluxosEntrantes == 0){
			double subPeriodoOcupado = instante - this.inicioPeriodoOcupado;
			coletor.coletaUtilizacao(id, subPeriodoOcupado);
		}
	}
	
	public void incFluxosOcupado(){
		++this.fluxosMeioOcupado;
	}
	
	public void decFluxosOcupado(){
		--this.fluxosMeioOcupado;
	}
	
	public int getQtdFluxosOcupado(){
		return this.fluxosMeioOcupado;
	}
	
	public void novaChegadaFila(Quadro q){
		if(this.filaServico == null && this.filaEspera.isEmpty())
			this.filaServico = q;
		else
			this.filaEspera.add(q);
	}
	
	public void colocarQuadroEmServico(Quadro proximoQuadro){
		this.filaServico = proximoQuadro;
	}
	
	public void mensagemEmServicoFinalizada(){
		this.colocarProximaMensagemEmServico();
	}
	
	private void colocarProximaMensagemEmServico(){
		this.filaServico = this.filaEspera.pollFirst();
	}
	
	public Quadro getQuadroEmServico(){
		return this.filaServico;
	}
	
	public boolean temMensagemEmServico(){
		if(this.filaServico != null)
			return true;
		return false;
	}
	
	public double getInstanteTempoInicial(){
		return this.instanteTempoInicial;
	}
	
	public double gerarProximoInstanteDeTempoDeMensagem(double instanteAtual){
		if(this.tipo == Terminal.TIPO_DETERMINISTICO)
			return gerarInstanteTempoProximoEventoDeterministico(instanteAtual, this.periodo);
		else
			return gerarInstanteTempoProximoEventoPoisson(instanteAtual, this.periodo);
	}
	
	private double gerarInstanteTempoProximoEventoPoisson(double instanteTempoAtual, double periodo){
		double poisson  = (double) (-((Math.log(1-(GeradorRandomicoSingleton.getInstance().gerarProximoDoubleRandomico()%1)))/(1/periodo)));
		return instanteTempoAtual + poisson;
	}
	
	private double gerarInstanteTempoProximoEventoDeterministico(double instanteTempoAtual, double periodo){
		return instanteTempoAtual + periodo;
	} 
	
	
	public int getIdTerminal() {
		return id;
	}

	public void setIdTerminal(int id) {
		this.id = id;
	}

	public void setDistanciaHub(double distanciaHub) {
		this.distanciaHub = distanciaHub;
	}

	public double getDistanciaHub() {
		return distanciaHub;
	}

	public void setpMensagens(double pMensagens) {
		this.pMensagens = pMensagens;
	}

	public double getpMensagens() {
		return pMensagens;
	}

	public void setTxOcupado(boolean txOcupado) {
		this.txOcupado = txOcupado;
	}

	public boolean isTxOcupado() {
		return txOcupado;
	}

	public void setForcarTransmissao(boolean forcarTransmissao) {
		this.forcarTransmissao = forcarTransmissao;
	}

	public boolean isForcarTransmissao() {
		return forcarTransmissao;
	}

	public boolean isMeioOcupado() {
		if(this.fluxosMeioOcupado > 0)
			return true;
		return false;
	}
	
	public void setColidiu(boolean colidiu){
		this.colidiu = colidiu;
	}
	
	public boolean colidiu(){
		return this.colidiu;
	}

	public void setRetransmissaoAgendada(boolean retransmissaoAgendada) {
		this.retransmissaoAgendada = retransmissaoAgendada;
	}

	public boolean isRetransmissaoAgendada() {
		return retransmissaoAgendada;
	}
	
}



