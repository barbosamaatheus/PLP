package li2.plp.imperative2.command;

import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.ValorConcreto;
import li2.plp.expressions2.memory.IdentificadorJaDeclaradoException;
import li2.plp.expressions2.memory.IdentificadorNaoDeclaradoException;
import li2.plp.imperative1.command.Comando;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative1.memory.EntradaVaziaException;
import li2.plp.imperative1.memory.ErroTipoEntradaException;
import li2.plp.imperative2.declaration.DefProcedimento;
import li2.plp.imperative2.declaration.ListaDeclaracaoParametro;
import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;
import li2.plp.imperative2.memory.ContextoExecucaoImperativa2;

public class ChamadaAssert implements Comando {
	
	private Expressao esq;
	private Expressao dir;
	
	public ChamadaAssert(Expressao esq, Expressao dir) {
		this.esq = esq;
		this.dir = dir;
	}
	
	public Expressao getEsq() {
		return esq;
	}

	public Expressao getDir() {
		return dir;
	}

	@Override
	public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa ambiente)
			throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException, EntradaVaziaException,
			ErroTipoEntradaException {
		
		ContextoExecucaoImperativa2 contexto = (ContextoExecucaoImperativa2) ambiente;
		
		if (contexto.getTestar()) {


			ValorConcreto esq = (ValorConcreto) this.esq.avaliar(ambiente);
			ValorConcreto dir = (ValorConcreto) this.dir.avaliar(ambiente);

			if (!esq.isEquals(dir)) {
				contexto.restaura();
				throw new AssertFalhouException(ambiente, this);
			}
		}
		
		return ambiente;
	}

	@Override
	public AmbienteExecucaoImperativa mutar(AmbienteExecucaoImperativa ambiente) throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException, EntradaVaziaException, ErroTipoEntradaException {
		ContextoExecucaoImperativa2 contexto = (ContextoExecucaoImperativa2) ambiente;

		if (contexto.getTestar()) {


			ValorConcreto esq = (ValorConcreto) this.esq.avaliar(ambiente);
			ValorConcreto dir = (ValorConcreto) this.dir.avaliar(ambiente);

			if (!esq.isEquals(dir)) {
				contexto.restaura();
				throw new AssertFalhouException(ambiente, this);
			}
		}

		return ambiente;
	}

	@Override
	public boolean checaTipo(AmbienteCompilacaoImperativa ambiente)
			throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException, EntradaVaziaException {
		return esq.getTipo(ambiente).eIgual(dir.getTipo(ambiente));
	}

}
