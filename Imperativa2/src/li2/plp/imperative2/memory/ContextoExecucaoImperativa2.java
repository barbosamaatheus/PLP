package li2.plp.imperative2.memory;

import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.memory.Contexto;
import li2.plp.expressions2.memory.VariavelJaDeclaradaException;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative1.memory.ContextoExecucaoImperativa;
import li2.plp.imperative1.memory.ListaValor;
import li2.plp.imperative2.TestRunner;
import li2.plp.imperative2.command.ChamadaTeste;
import li2.plp.imperative2.declaration.DefProcedimento;
import li2.plp.imperative2.declaration.DefTeste;
import li2.plp.imperative2.declaration.DefTesteSetup;
import li2.plp.imperative2.declaration.DefTesteTeardown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContextoExecucaoImperativa2 extends ContextoExecucaoImperativa
		implements AmbienteExecucaoImperativa2 {

	/**
	 * O contexto de procedimentos faz as vezes de um contexto de execu��o que
	 * armazena apenas procedimentos.
	 */
	private Contexto<DefProcedimento> contextoProcedimentos;


	private boolean testar, mutar;

	/**
	 * Construtor da classe.
	 */
	public ContextoExecucaoImperativa2(ListaValor entrada, boolean testar, boolean mutar) {
		super(entrada);
		contextoProcedimentos = new Contexto<DefProcedimento>();
		this.mutar = mutar;
		this.testar = testar;
	}

	public ContextoExecucaoImperativa2(ListaValor entrada, boolean testar) {
		super(entrada);
		contextoProcedimentos = new Contexto<DefProcedimento>();
		this.testar = testar;
	}

	@Override
	public void incrementa() {
		super.incrementa();
		this.contextoProcedimentos.incrementa();
	}

	@Override
	public void restaura() {
		if (testar) {
			executaTestes();
		}
		super.restaura();
		this.contextoProcedimentos.restaura();
	}

	private void executaTestes() {
		HashMap<Id, DefProcedimento> procedimentos = contextoProcedimentos.observa();

		List<Id> testes = new ArrayList<>();
		Id setup = null;
		Id teardown = null;

		for (Id id : procedimentos.keySet()) {
			DefProcedimento proc = procedimentos.get(id);
			if (proc instanceof DefTesteSetup) {
				setup = id;
			} else if (proc instanceof DefTesteTeardown) {
				teardown = id;
			} else if (proc instanceof DefTeste) {
				testes.add(id);
			}
		}

		for (Id id : testes) {
			try {
				if (setup != null) {
					new ChamadaTeste(setup).executar(this);
				}
				new ChamadaTeste(id).executar(this);
				TestRunner.addSuccess(id);
			} catch (Exception exc) {
				TestRunner.addFailure(id, exc);
			} finally {
				if (teardown != null) {
					try {
						new ChamadaTeste(teardown).executar(this);
					} catch (Exception exc) {
						TestRunner.addFailure(id, exc);
					}
				}
			}
		}


	}

	/**
	 * Mapeia o id no procedimento dado.
	 * 
	 * @exception ProcedimentoJaDeclaradoException
	 *                se j� existir um mapeamento do identificador nesta tabela.
	 */
	public void mapProcedimento(Id idArg, DefProcedimento procedimentoId)
			throws ProcedimentoJaDeclaradoException {
		try {
			this.contextoProcedimentos.map(idArg, procedimentoId);
		} catch (VariavelJaDeclaradaException e) {
			throw new ProcedimentoJaDeclaradoException(idArg);
		}

	}

	/**
	 * Retorna o procedimento mapeado ao id dado.
	 * 
	 * @exception ProcedimentoNaoDeclaradoException
	 *                se n�o existir nenhum procedimento mapeado ao id dado
	 *                nesta tabela.
	 */
	public DefProcedimento getProcedimento(Id idArg)
			throws ProcedimentoNaoDeclaradoException {
		try {
			return this.contextoProcedimentos.get(idArg);
		} catch (VariavelNaoDeclaradaException e) {
			throw new ProcedimentoNaoDeclaradoException(idArg);
		}

	}

	public boolean getTestar() {
		return testar;
	}

	public boolean getMutar() {
		return mutar;
	}
}
