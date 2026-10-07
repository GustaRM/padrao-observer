package padroescomportamentais.observer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EstacaoMonitoramentoTest {

    private EstacaoMonitoramento estacao;
    private DefesaCivil defesaCivil;
    private PrefeituraMunicipal prefeitura;
    private AssinanteSms morador;

    @BeforeEach
    void setUp() {
        estacao = new EstacaoMonitoramento("Uberabinha");
        defesaCivil = new DefesaCivil("Defesa Civil");
        prefeitura = new PrefeituraMunicipal("Prefeitura");
        morador = new AssinanteSms("Sr. João");
        estacao.adicionarObservador(defesaCivil);
        estacao.adicionarObservador(prefeitura);
        estacao.adicionarObservador(morador);
    }

    @Test
    void semMedicaoNinguemEhNotificado() {
        assertEquals(0, defesaCivil.getTotalLeituras());
        assertEquals(0, prefeitura.getTotalLeituras());
        assertEquals(0, morador.getTotalLeituras());
    }

    @Test
    void todosOsObservadoresRecebemCadaMedicao() {
        estacao.registrarNivel(1.0);
        estacao.registrarNivel(1.5);

        assertEquals(2, defesaCivil.getTotalLeituras());
        assertEquals(2, prefeitura.getTotalLeituras());
        assertEquals(2, morador.getTotalLeituras());
    }

    @Test
    void observadoresPodemConsultarOEstadoDoSubject() {
        estacao.registrarNivel(2.5);
        assertEquals(2.5, estacao.getNivelAtual());
        assertEquals("Uberabinha", estacao.getNomeRio());
    }

    @Test
    void nivelBaixoNaoGeraAlerta() {
        estacao.registrarNivel(2.9);
        assertTrue(defesaCivil.getAlertas().isEmpty());
        assertTrue(prefeitura.getAlertas().isEmpty());
        assertTrue(morador.getAlertas().isEmpty());
        assertNull(defesaCivil.getUltimoAlerta());
    }

    @Test
    void cadaObservadorTemSeuProprioLimite() {
        estacao.registrarNivel(3.2); // só Defesa Civil (>= 3.0)
        assertEquals(1, defesaCivil.getAlertas().size());
        assertEquals(0, morador.getAlertas().size());
        assertEquals(0, prefeitura.getAlertas().size());

        estacao.registrarNivel(3.7); // Defesa Civil + SMS (>= 3.5)
        assertEquals(2, defesaCivil.getAlertas().size());
        assertEquals(1, morador.getAlertas().size());
        assertEquals(0, prefeitura.getAlertas().size());

        estacao.registrarNivel(4.3); // todos
        assertEquals(3, defesaCivil.getAlertas().size());
        assertEquals(2, morador.getAlertas().size());
        assertEquals(1, prefeitura.getAlertas().size());
    }

    @Test
    void limiteExatoGeraAlerta() {
        estacao.registrarNivel(DefesaCivil.LIMITE_METROS);
        assertEquals(1, defesaCivil.getAlertas().size());
    }

    @Test
    void mensagensDeAlertaTemOConteudoEsperado() {
        estacao.registrarNivel(4.3);

        assertEquals("[DEFESA CIVIL] Defesa Civil: Rio Uberabinha em 4.3 m, acima da cota de alerta",
                defesaCivil.getUltimoAlerta());
        assertEquals("[PREFEITURA] Prefeitura: emergência! Rio Uberabinha em 4.3 m",
                prefeitura.getUltimoAlerta());
        assertEquals("[SMS] Sr. João, risco de enchente: Rio Uberabinha em 4.3 m. Procure local seguro.",
                morador.getUltimoAlerta());
    }

    @Test
    void observadorRemovidoNaoRecebeMais() {
        estacao.registrarNivel(4.0);
        estacao.removerObservador(morador);
        estacao.registrarNivel(4.5);

        assertEquals(1, morador.getTotalLeituras());
        assertEquals(2, defesaCivil.getTotalLeituras());
    }

    @Test
    void observadorNaoEhRegistradoDuasVezes() {
        estacao.adicionarObservador(defesaCivil);
        estacao.registrarNivel(1.0);

        assertEquals(1, defesaCivil.getTotalLeituras());
        assertEquals(3, estacao.getObservadores().size());
    }

    @Test
    void observadorNuloEhIgnorado() {
        estacao.adicionarObservador(null);
        assertEquals(3, estacao.getObservadores().size());
        assertDoesNotThrow(() -> estacao.registrarNivel(1.0));
    }

    @Test
    void observadorAdicionadoDepoisSoRecebeMedicoesFuturas() {
        estacao.registrarNivel(4.0);
        DefesaCivil regional = new DefesaCivil("Defesa Civil Regional");
        estacao.adicionarObservador(regional);
        estacao.registrarNivel(4.2);

        assertEquals(1, regional.getTotalLeituras());
        assertEquals(1, regional.getAlertas().size());
    }

    @Test
    void nivelNegativoLancaExcecaoENaoNotifica() {
        assertThrows(IllegalArgumentException.class, () -> estacao.registrarNivel(-1.0));
        assertEquals(0, defesaCivil.getTotalLeituras());
        assertEquals(0.0, estacao.getNivelAtual());
    }

    @Test
    void estacoesDiferentesSaoIndependentes() {
        EstacaoMonitoramento outra = new EstacaoMonitoramento("Araguari");
        DefesaCivil outraDefesa = new DefesaCivil("Defesa Civil Araguari");
        outra.adicionarObservador(outraDefesa);

        outra.registrarNivel(5.0);

        assertEquals(1, outraDefesa.getTotalLeituras());
        assertEquals(0, defesaCivil.getTotalLeituras());
    }

    @Test
    void observadorPodeSeRemoverDuranteANotificacao() {
        Observador autoRemovivel = new Observador() {
            @Override
            public void atualizar(EstacaoMonitoramento e) {
                e.removerObservador(this);
            }
        };
        estacao.adicionarObservador(autoRemovivel);

        assertDoesNotThrow(() -> estacao.registrarNivel(1.0));
        assertEquals(3, estacao.getObservadores().size());
        assertEquals(1, defesaCivil.getTotalLeituras());
    }
}
