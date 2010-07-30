package br.com.wifeleviro.ad.modelo;

import br.com.wifeleviro.ad.util.GeradorRandomicoSingleton;

public class Terminal{

	public static final int TIPO_DETERMINISTICO = 0;
	public static final int TIPO_EXPONENCIAL = 1;
	
	private int tipo;
	private double periodo;
	
	private double pMensagens;
	
	private int id;
	private double distanciaHub;
	
	private double instanteTempoInicial;
	private double instanteTempoAtual;

	private boolean isMeioOcupado;
	private double instanteTempoInicioUltimaTx;
	private double instanteTempoFimUltimaTx;
	private double instanteTempoFimUltimoRx;
	private int idTerminalUltimoRx;
	private Quadro quadroPendente;
	
	private boolean emColisao;
	private double instanteTempoColisao;
	
	public Terminal(int id, double distanciaHub, int tipo, double periodo, double pMensagens) {
		this.id = id;
		this.distanciaHub = distanciaHub;
		this.instanteTempoAtual = 0;
		if(this.tipo == TIPO_EXPONENCIAL)
			this.instanteTempoAtual = this.gerarInstanteTempoProximoEventoPoisson(this.instanteTempoAtual, this.periodo);
		this.instanteTempoInicial = this.instanteTempoAtual;
		this.periodo = periodo;
		this.setpMensagens(pMensagens);
		this.setMeioOcupado(false);
		this.instanteTempoInicioUltimaTx = -1;
		this.instanteTempoFimUltimaTx = -1;
		this.instanteTempoFimUltimoRx = -1;
		this.setIdTerminalUltimoRx(-1);
		this.quadroPendente = null;
		this.setEmColisao(false);
		this.instanteTempoColisao = -1;
	}
	
	public double getInstanteTempoInicial(){
		return this.instanteTempoInicial;
	}
	
	public double gerarProximoInstanteDeTempoDeMensagem(){
		if(this.tipo == Terminal.TIPO_DETERMINISTICO)
			return gerarInstanteTempoProximoEventoDeterministico(this.instanteTempoAtual, this.periodo);
		else
			return gerarInstanteTempoProximoEventoPoisson(this.instanteTempoAtual, this.periodo);
	}
	
	private double gerarInstanteTempoProximoEventoPoisson(double instanteTempoAtual, double periodo){
		double poisson  = (-((Math.log(1-(GeradorRandomicoSingleton.getInstance().gerarProximoRandomico()%1)))/(1/periodo)));
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

	public void setMeioOcupado(boolean isMeioOcupado) {
		this.isMeioOcupado = isMeioOcupado;
	}

	public boolean isMeioOcupado() {
		return isMeioOcupado;
	}

	public void setInstanteTempoFimUltimaTx(double instanteTempoFimUltimaTx) {
		this.instanteTempoFimUltimaTx = instanteTempoFimUltimaTx;
	}

	public double getInstanteTempoFimUltimaTx() {
		return instanteTempoFimUltimaTx;
	}

	public void setInstanteTempoInicioUltimaTx(double instanteTempoInicioUltimaTx) {
		this.instanteTempoInicioUltimaTx = instanteTempoInicioUltimaTx;
	}

	public double getInstanteTempoInicioUltimaTx() {
		return instanteTempoInicioUltimaTx;
	}

	public void setInstanteTempoFimUltimoRx(double instanteTempoFimUltimoRx) {
		this.instanteTempoFimUltimoRx = instanteTempoFimUltimoRx;
	}

	public double getInstanteTempoFimUltimoRx() {
		return instanteTempoFimUltimoRx;
	}

	public void setQuadroPendente(Quadro quadroPendente) {
		this.quadroPendente = quadroPendente;
	}

	public Quadro getQuadroPendente() {
		return quadroPendente;
	}

	public void setIdTerminalUltimoRx(int idTerminalUltimoRx) {
		this.idTerminalUltimoRx = idTerminalUltimoRx;
	}

	public int getIdTerminalUltimoRx() {
		return idTerminalUltimoRx;
	}

	public void setEmColisao(boolean emColisao) {
		this.emColisao = emColisao;
	}

	public boolean isEmColisao() {
		return emColisao;
	}

	public void setInstanteTempoColisao(double instanteTempoColisao) {
		this.instanteTempoColisao = instanteTempoColisao;
	}

	public double getInstanteTempoColisao() {
		return instanteTempoColisao;
	}

}



