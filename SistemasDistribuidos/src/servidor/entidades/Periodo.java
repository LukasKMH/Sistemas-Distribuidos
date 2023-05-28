package servidor.entidades;

public enum Periodo {
    MANHA(1,"manha"),
    TARDE(2,"tarde"),
    NOITE(3,"noite"),
    MADRUGADA(4,"madrugada");

	private int codigo;
    private final String descricao;

    Periodo(int codigo, String descricao) {
    	this.setCodigo(codigo);
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}
}

