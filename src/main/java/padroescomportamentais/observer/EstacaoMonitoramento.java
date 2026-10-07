package padroescomportamentais.observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EstacaoMonitoramento {

    private final String nomeRio;
    private double nivelAtual;
    private final List<Observador> observadores = new ArrayList<>();

    public EstacaoMonitoramento(String nomeRio) {
        this.nomeRio = nomeRio;
    }

    public void adicionarObservador(Observador observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void removerObservador(Observador observador) {
        observadores.remove(observador);
    }

    public List<Observador> getObservadores() {
        return Collections.unmodifiableList(observadores);
    }

    public void registrarNivel(double nivelEmMetros) {
        if (nivelEmMetros < 0) {
            throw new IllegalArgumentException("O nível do rio não pode ser negativo");
        }
        this.nivelAtual = nivelEmMetros;
        notificarObservadores();
    }

    private void notificarObservadores() {
        // cópia defensiva: um observador pode se remover durante a notificação
        for (Observador o : new ArrayList<>(observadores)) {
            o.atualizar(this);
        }
    }

    public double getNivelAtual() {
        return nivelAtual;
    }

    public String getNomeRio() {
        return nomeRio;
    }
}
