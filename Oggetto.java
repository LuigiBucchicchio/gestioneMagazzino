package com.magazzino;

public class Oggetto {
	private final int idOggetto;
	private String nome;
	private int quantita;
	private int soglia=5;
	public Oggetto(int id,String name, int quantita) {
		this.idOggetto=id;
		this.setNome(name);
		this.setQuantita(quantita);
	}
	public Oggetto(int id, String name, int quantita, int soglia) {
		this.idOggetto=id;
		this.setNome(name);
		this.setQuantita(quantita);
		this.setSoglia(soglia);
	}
	public  String getNome() {
		return nome;
	}
	public void setNome(String name) {
		this.nome = name;
	}
	public int getQuantita() {
		return quantita;
	}
	public void setQuantita(int quantita) {
		this.quantita = quantita;
	}
	public void aumentaQuantita(int num) {
		this.quantita=this.quantita+num;
	}

	public boolean diminuisciQuantita(int num) {
		this.quantita=this.quantita-num;
		if(this.quantita<=soglia)
			return false;
		else return true;
	}
	public int getSoglia() {
		return this.soglia;
	}
	public void setSoglia(int x) {
		this.soglia=x;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idOggetto;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Oggetto))
			return false;
		Oggetto other = (Oggetto) obj;
		if (idOggetto != other.idOggetto)
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "[ "+nome + " q.ta: " + quantita + " soglia: "+soglia+" ]";
	}
	public int getIdOggetto() {
		return idOggetto;
	}
}