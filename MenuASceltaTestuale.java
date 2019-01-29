package com.magazzino;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class MenuASceltaTestuale {
private Scanner s;
private String fraseIniziale;
private ArrayList<String> risposteConsentite;
private String fraseFinale;
private String errore;
public MenuASceltaTestuale(String fraseIniziale, Scanner s) {
	this.s=s;
	this.fraseIniziale=fraseIniziale;
	this.risposteConsentite=new ArrayList<String>();
	this.fraseFinale=null;
	this.errore="Input non riconosciuto";
	}
public void aggiungiRisposta(String risposta) {
	this.risposteConsentite.add(risposta);
}

public void aggiungiRisposte(String risposta1, String risposta2) {
	this.risposteConsentite.add(risposta1);
	this.risposteConsentite.add(risposta2);
}

public void aggiungiRisposte(String risposta1, String risposta2, String risposta3) {
	this.risposteConsentite.add(risposta1);
	this.risposteConsentite.add(risposta2);
	this.risposteConsentite.add(risposta3);
}

public void aggiungiRisposte(String risposta1, String risposta2, String risposta3, String risposta4) {
	this.risposteConsentite.add(risposta1);
	this.risposteConsentite.add(risposta2);
	this.risposteConsentite.add(risposta3);
	this.risposteConsentite.add(risposta4);
}

public void aggiungiRisposte(String risposta1, String risposta2, String risposta3, String risposta4, String risposta5) {
	this.risposteConsentite.add(risposta1);
	this.risposteConsentite.add(risposta2);
	this.risposteConsentite.add(risposta3);
	this.risposteConsentite.add(risposta4);
	this.risposteConsentite.add(risposta5);
}

public void setFraseFinale(String fraseFinale) {
	this.fraseFinale=fraseFinale;
}
public void setErrore(String fraseErrore) {
	errore=fraseErrore;
}
public String iteration() {
	System.out.println(fraseIniziale);
	Iterator<String> it = this.risposteConsentite.iterator();
	while(it.hasNext())
	System.out.print("<"+it.next()+"> ");
	String scelta=s.nextLine();
	while(!this.risposteConsentite.contains(scelta)) {
		if(this.errore!=null)
			System.out.println(errore);
		System.out.print(fraseIniziale+"\npuoi ");
		it = this.risposteConsentite.iterator();
		while(it.hasNext())
		System.out.print("<"+it.next()+"> ");
		scelta=s.nextLine();
	}
	if(this.fraseFinale!=null)
	System.out.println(fraseFinale);
	return scelta;
}
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((errore == null) ? 0 : errore.hashCode());
	result = prime * result + ((fraseFinale == null) ? 0 : fraseFinale.hashCode());
	result = prime * result + ((fraseIniziale == null) ? 0 : fraseIniziale.hashCode());
	result = prime * result + ((risposteConsentite == null) ? 0 : risposteConsentite.hashCode());
	return result;
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (!(obj instanceof MenuASceltaTestuale))
		return false;
	MenuASceltaTestuale other = (MenuASceltaTestuale) obj;
	if (errore == null) {
		if (other.errore != null)
			return false;
	} else if (!errore.equals(other.errore))
		return false;
	if (fraseFinale == null) {
		if (other.fraseFinale != null)
			return false;
	} else if (!fraseFinale.equals(other.fraseFinale))
		return false;
	if (fraseIniziale == null) {
		if (other.fraseIniziale != null)
			return false;
	} else if (!fraseIniziale.equals(other.fraseIniziale))
		return false;
	if (risposteConsentite == null) {
		if (other.risposteConsentite != null)
			return false;
	} else if (!risposteConsentite.equals(other.risposteConsentite))
		return false;
	return true;
}

}
