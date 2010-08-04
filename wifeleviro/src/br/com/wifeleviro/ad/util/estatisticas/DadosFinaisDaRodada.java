package br.com.wifeleviro.ad.util.estatisticas;

/*
 * Classe que armazena os resultados dos cálculos de intervalo de confiança
 * de todas as métricas da rodada.
 */
public class DadosFinaisDaRodada {

	ResultadoIntervaloDeConfianca tap;
	ResultadoIntervaloDeConfianca tam;
	ResultadoIntervaloDeConfianca ncm;
	ResultadoIntervaloDeConfianca utilizacao;
	ResultadoIntervaloDeConfianca vazao;
	Boolean dentroDoLimite;
	
	public DadosFinaisDaRodada(ResultadoIntervaloDeConfianca tap,
			ResultadoIntervaloDeConfianca tam,
			ResultadoIntervaloDeConfianca ncm,
			ResultadoIntervaloDeConfianca utilizacao,
			ResultadoIntervaloDeConfianca vazao,
			Boolean dentroDoLimite) {
		super();
		this.tap = tap;
		this.tam = tam;
		this.ncm = ncm;
		this.utilizacao = utilizacao;
		this.vazao = vazao;
		this.dentroDoLimite = dentroDoLimite;
	}

	public ResultadoIntervaloDeConfianca getTap() {
		return tap;
	}

	public ResultadoIntervaloDeConfianca getTam() {
		return tam;
	}

	public ResultadoIntervaloDeConfianca getNcm() {
		return ncm;
	}

	public ResultadoIntervaloDeConfianca getUtilizacao() {
		return utilizacao;
	}

	public ResultadoIntervaloDeConfianca getVazao() {
		return vazao;
	}
	
	public Boolean getDentroDoLimite() {
		return dentroDoLimite;
	}
}
