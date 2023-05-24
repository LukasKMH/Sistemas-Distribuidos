package servidor.entidades;

public enum TipoIncidente {
    VENTO(1),
    CHUVA(2),
    NEVOEIRO(3),
    NEVE(4),
    GELO_NA_PISTA(5),
    GRANIZO(6),
    TRANSITO_PARADO(7),
    FILAS_DE_TRANSITO(8),
    TRANSITO_LENTO(9),
    ACIDENTE_DESCONHECIDO(10),
    INCIDENTE_DESCONHECIDO(11),
    TRABALHOS_NA_ESTRADA(12),
    BLOQUEIO_DE_PISTA(13),
    BLOQUEIO_DE_ESTRADA(14);

    private final int codigo;

    TipoIncidente(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}
