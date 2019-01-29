package com.magazzino;
import java.util.ArrayList;
import java.util.Iterator;

public class Magazzino {
	private final String idMagazzino;
	private ArrayList<Oggetto> inventario;
	private String nomeMagazzino;
	private String indirizzoMagazzino;

	public Magazzino(String id,String name) {
		this.idMagazzino=id;
		this.inventario=new ArrayList<Oggetto>();
		this.nomeMagazzino= name;
		this.indirizzoMagazzino= "Non inserito";
	}
	public Magazzino(String id,String name, String address) {
		this.idMagazzino=id;
		this.inventario=new ArrayList<Oggetto>();
		this.nomeMagazzino= name;
		this.indirizzoMagazzino= address;
	}
	public String getNomeMagazzino() {
		return this.nomeMagazzino;
	}
	public String getIndirizzoMagazzino() {
		return this.indirizzoMagazzino;
	}
	public void setNomeMagazzino(String name) {
		this.nomeMagazzino=name;
	}
	public void setIndirizzoMagazzino(String address) {
		this.indirizzoMagazzino=address;
	}
	public ArrayList<Oggetto> getInventario() {
		return inventario;
	}
	public void aggiungiOggetto(int id, String nome, int quantita, int soglia) {
		this.inventario.add(new Oggetto(id,nome,quantita,soglia));
	}
	public void rimuoviOggetto(Oggetto oggetto) {
		this.inventario.remove(oggetto);
		//database?
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nomeMagazzino == null) ? 0 : nomeMagazzino.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Magazzino))
			return false;
		Magazzino other = (Magazzino) obj;
		if (nomeMagazzino == null) {
			if (other.nomeMagazzino != null)
				return false;
		} else if (!nomeMagazzino.equals(other.nomeMagazzino))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "[ " + nomeMagazzino + ", " + indirizzoMagazzino+" ]";
	}
	public String getIdMagazzino() {
		return idMagazzino;
	}
	public String stampaInventario() {
		StringBuffer sb= new StringBuffer("");
		sb.append("Inventario del Magazzino Scelto: \n***************\n");
		Iterator<Oggetto> it= this.inventario.iterator();
		while(it.hasNext()) {
			sb.append(it.next().toString()+"\n");
		}
		return sb.toString();
	}

}