package padroescomportamentais.observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DefesaCivil implements Observador {

    public static final double LIMITE_METROS = 3.0;

    private final String nome;
    private int totalLeituras = 0;
    private final List<String> alertas = new ArrayList<>();

    public DefesaCivil(String nome) {
        this.nome = nome;
    }

    @Override
    public void atualizar(EstacaoMonitoramento estacao) {
        totalLeituras++;
        double nivel = estacao.getNivelAtual();
        if (nivel >= LIMITE_METROS) {
            String valor = String.format(Locale.ROOT, "%.1f", nivel);
            alertas.add("[DEFESA CIVIL] " + nome + ": Rio " + estacao.getNomeRio() + " em " + valor + " m, acima da cota de alerta");
        }
    }

    public String getNome() {
        return nome;
    }

    public int getTotalLeituras() {
        return totalLeituras;
    }

    public List<String> getAlertas() {
        return Collections.unmodifiableList(alertas);
    }

    public String getUltimoAlerta() {
        return alertas.isEmpty() ? null : alertas.get(alertas.size() - 1);
    }
}
