_Dicas para programarmos adequadamente este maldito sistema!_

  * Uma vez que os números que iremos tratar serão muito grandes, o ideal é abrirmos mão das variáveis do tipo **int** em detrimento do tipo **long**, de modo que não tenhamos problemas com grandes números.

  * Todo tempo será simulado. Sendo assim, o ideal é trabalharmos com o tempo como sendo um valor **double** ao invés de um mero **Calendar**. Lembrando que milissegundos é tempo pra caramba! Vamos trabalhar com precisão muito baixa.

  * Todo quadro transmitido dentro do sistema deverá ser identificado por um número **long** de identificação única para auxiliar na coleta de estatísticas de tempo.

  * Toda mensagem transmitida deverá ter um identificador único do tipo **long** de modo a auxiliar na coleta de estatística de tempo e do número de colisões.

  * Finalmente consegui bolar uma solução para o problema da colisão durante transmissão onde é necessário recalcular início de tx e fim de tx. Deverá ser implementada uma memória em cada pc de "mensagem pendente"! Esta memória irá armazenar a mensagem (contendo o número de quadros restantes) que foi suspensa pela colisão e deverá voltar a ser transmitida segundo o algoritmo de Binary Backoff.

## Coletor de estatísticas ##

O **ColetorEstatisticasSingleton**, como o próprio nome já diz, é um singleton. Ou seja, ele não é instanciável, portanto sempre deverá ser acessado via seu próprio método **getInstance()**. Este método é capaz de instanciá-lo, caso seja necessário, ou simplesmente recuperar a instância única do mesmo no programa.

Sempre que um evento ocorrer no simulador, um dos métodos do coletor de estatísticas deverá ser invocado:

|**coletaQuadroPorMensagem**|Invocado no evento de geração de um quadro. Irá registrar a qual mensagem este pertence.|
|:--------------------------|:------------------------------------------------------------------------------------------|
|**coletaColisaoPorMensagem**|Invocado no evento de geração de colisão. Irá registrar a qual mensagem esta colisão pertence.|
|**iniciaColetaTap**|Invocado no evento transmissão do quadro, independente da situação.|
|**finalizaColetaTap**|Invocado no evento de transmissão do quadro. Caso encontre o meio livre, este deverá conter o mesmo valor que informado para o método **iniciaColetaTap**. Caso contrário, deverá conter o instante de tempo em que o quadro encontrou o meio livre e foi, finalmente, transmitido.|
|**iniciaColetaTam**|Invocado junto com o **iniciaColetaTap** do primeiro quadro da mensagem que está sendo transmitida.|
|**finalizaColetaTam**|Invocado junto com o **finalizaColetaTap** do último quadro da mensagem que está sendo transmitida.|