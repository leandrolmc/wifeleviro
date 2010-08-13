package br.com.wifeleviro.ad.util.estatisticas.metricas;

public class NCm {

		private Double acumuladorAmostras;
		private Long numeroMensagens;
		
		public NCm (){
			this.acumuladorAmostras = (double)0;
			this.numeroMensagens = (long)0;
		}

		public void acumularTempo(Double amostra) {
			this.acumuladorAmostras += amostra;
			++this.numeroMensagens;
		}

		public Double getAcumulador() {
			return acumuladorAmostras;
		}

		public Long getNumeroMensagens() {
			return numeroMensagens;
		}

		
		
	}
