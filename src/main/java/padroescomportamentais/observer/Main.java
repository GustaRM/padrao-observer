package padroescomportamentais.observer;

public class Main {

    public static void main(String[] args) {
        EstacaoMonitoramento estacao = new EstacaoMonitoramento("Uberabinha");

        DefesaCivil defesaCivil = new DefesaCivil("Defesa Civil de Uberlândia");
        PrefeituraMunicipal prefeitura = new PrefeituraMunicipal("Prefeitura");
        AssinanteSms morador = new AssinanteSms("Sr. João");

        estacao.adicionarObservador(defesaCivil);
        estacao.adicionarObservador(prefeitura);
        estacao.adicionarObservador(morador);

        double[] medicoes = {1.2, 2.8, 3.2, 3.7, 4.3, 2.0};
        for (double nivel : medicoes) {
            estacao.registrarNivel(nivel);
        }

        defesaCivil.getAlertas().forEach(System.out::println);
        prefeitura.getAlertas().forEach(System.out::println);
        morador.getAlertas().forEach(System.out::println);
    }
}
