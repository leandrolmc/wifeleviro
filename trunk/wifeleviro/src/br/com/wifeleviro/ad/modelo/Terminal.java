package br.com.wifeleviro.ad.modelo;

import java.util.GregorianCalendar;
import java.util.Random;

public class Terminal{

	public static final int TIPO_DETERMINISTICO = 0;
	public static final int TIPO_EXPONENCIAL = 1;
	
	private long semente;
	private Random geradorRandomico;
	
	private int tipo;
	private double taxa;
	
	private double pMensagens;
	
	private int id;
	private double distanciaHub;
	
	private double instanteTempoInicial;
	private double instanteTempoAtual;

	private boolean isMeioOcupado;
	
	public Terminal(int id, double distanciaHub, int tipo, double taxa, double pMensagens) {
		this.id = id;
		this.distanciaHub = distanciaHub;
		this.semente = new GregorianCalendar().getTimeInMillis();
		this.geradorRandomico = new Random(this.semente);
		this.instanteTempoAtual = 0;
		if(this.tipo == TIPO_EXPONENCIAL)
			this.instanteTempoAtual = this.gerarInstanteTempoProximoEventoPoisson(this.instanteTempoAtual, this.geradorRandomico, this.taxa);
		this.instanteTempoInicial = this.instanteTempoAtual;
		this.setpMensagens(pMensagens);
		this.setMeioOcupado(false);
	}
	
	public double getInstanteTempoInicial(){
		return this.instanteTempoInicial;
	}
	
	public double gerarProximoInstanteDeTempoDeMensagem(){
		if(this.tipo == Terminal.TIPO_DETERMINISTICO)
			return gerarInstanteTempoProximoEventoDeterministico(this.instanteTempoAtual, this.taxa);
		else
			return gerarInstanteTempoProximoEventoPoisson(this.instanteTempoAtual, this.geradorRandomico, this.taxa);
	}
	
	private double gerarInstanteTempoProximoEventoPoisson(double instanteTempoAtual, Random random, double taxa){
		double poisson  = (-((Math.log(random.nextDouble()))/taxa));
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

}


