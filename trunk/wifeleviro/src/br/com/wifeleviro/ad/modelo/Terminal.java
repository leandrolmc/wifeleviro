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
	
	private Mensagem transmissaoPendente;
	
	private int id;
	private double distanciaHub;
	
	private double instanteTempoInicial;
	private double instanteTempoAtual;

	private boolean isMeioOcupado;
	private double instanteTempoInicioUltimaTx;
	private double instanteTempoFimUltimaTx;
	private double instanteTempoFimUltimoRx;
	
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
		this.instanteTempoInicioUltimaTx = -1;
		this.instanteTempoFimUltimaTx = -1;
		this.instanteTempoFimUltimoRx = -1;
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

	public void setTransmissaoPendente(Mensagem transmissaoPendente) {
		this.transmissaoPendente = transmissaoPendente;
	}

	public Mensagem atenderTransmissaoPendente() {
		Mensagem pendente = this.transmissaoPendente;
		this.transmissaoPendente = null;
		return pendente;
	}

}



