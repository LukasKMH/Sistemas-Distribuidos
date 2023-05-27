package servidor.entidades;

public enum TipoIncidente {
    VENTO(1, "Vento"),
    CHUVA(2, "Chuva"),
    NEVOEIRO(3, "Nevoeiro"),
    NEVE(4, "Neve"),
    GELO_NA_PISTA(5, "Gelo na pista"),
    GRANIZO(6, "Granizo"),
    TRANSITO_PARADO(7, "Transito parado"),
    FILAS_DE_TRANSITO(8, "Filas de transito"),
    TRANSITO_LENTO(9, "Transito lento"),
    ACIDENTE_DESCONHECIDO(10, "Acidente desconhecido"),
    INCIDENTE_DESCONHECIDO(11, "Incidente desconhecido"),
    TRABALHOS_NA_ESTRADA(12, "Trabalhos na estrada"),
    BLOQUEIO_DE_PISTA(13, "Bloqueio de pista"),
    BLOQUEIO_DE_ESTRADA(14, "Bloqueio de estrada");

    private final int codigo;
    private final String mensagem;

    TipoIncidente(int codigo, String mensagem) {
        this.codigo = codigo;
        this.mensagem = mensagem;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public static String getMensagemFromCode(int codigo) {
        for (TipoIncidente tipo : TipoIncidente.values()) {
            if (tipo.getCodigo() == codigo) {
                return tipo.getMensagem();
            }
        }
        return null; // Ou lance uma exceção se necessário
    }
}

