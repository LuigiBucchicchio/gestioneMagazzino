package com.magazzino;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Scanner;

/* 
 * manca grafichetta per l'applicativo desktop. (finora è tutto testuale)
 * 
 * Sarebbe auspicabile poter dotare l'applicazione di grafica o supporto su dispositivi Mobili.
 * 
 */

public class SistemaGestione {

	private static Connection connection=null;
	private static String connectionString="jdbc:mysql://";
	private static String hostedon="localhost:3306";
	private static String nomedb="inventoryschema";
	private static Scanner s;
	private static String inputUtente="";
	private static String username="root";
	private static String password="password";
	private static Magazzino currentLocation=null;

	public static void main(String[] args) throws IOException {
		System.out.println("Benvenuto\n");
		inizializzaConnessione();
		connessione();
		menuApplicazione();
	}

	public static void inizializzaConnessione() {
		s=new Scanner(System.in);
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Driver ok");
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("Driver non trovato");
			System.exit(5);
		}
		System.out.println("Inserisci nome utente (def. root): ");
		username=s.nextLine();
		System.out.println("Inserisci password (def. password): ");
		password=s.nextLine();
		connectionString=connectionString+hostedon;
		connectionString=connectionString+"/"+nomedb;
	}

	public static void connessione() {
		try {
			connection = DriverManager.getConnection(connectionString,username,password);
			System.out.println("Connessione effettuata con successo");
		} catch (SQLException e) {
			System.out.println("Errore di connessione, prova con credenziali diverse");
			e.printStackTrace();
			System.exit(6);
		}
	}

	public static void menuApplicazione() throws IOException {
		boolean uscita= false;
		while(!uscita) {
			String greet="Benvenuto\n  In questo applicativo puoi creare magazzini o gestirne di esistenti";
			MenuASceltaTestuale menu=new MenuASceltaTestuale(greet,s);
			menu.aggiungiRisposte("crea", "gestisci", "uscire");
			inputUtente=menu.iteration();
			if(inputUtente.equals("uscire"))
				uscita();
			else if(inputUtente.equals("crea")) {
				uscita=creaMagazzino();
			}else {
				uscita=gestioneMagazzino();
			}
		}
		uscita();
	}

	public static void uscita() {
		s.close();
		System.out.println("Arrivederci!");
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e1) {
			System.out.println("Errore in chiusura della connessione");
			System.exit(6);
		}
		System.exit(2);
	}

	public static boolean creaMagazzino() throws IOException {
		String nomeLocazione;
		String indirizzoLocazione;
		System.out.println("Sei nella fase di Creazione, per creare un Magazzino inserisci il nome:");
		nomeLocazione=s.nextLine();
		MenuASceltaTestuale menu=new MenuASceltaTestuale("vuoi inserire un indirizzo?",s);
		menu.aggiungiRisposte("si", "no");
		inputUtente=menu.iteration();
		if(inputUtente.equals("no")) 
			return salvaMagazzinoSuDb(nomeLocazione,null);
		else {
			System.out.println("Inserisci un indirizzo:");
			indirizzoLocazione=s.nextLine();
			return salvaMagazzinoSuDb(nomeLocazione,indirizzoLocazione);
		}
	}

	public static boolean salvaMagazzinoSuDb(String nomeLocazione,String indirizzoLocazione) throws IOException {
		try {
			PreparedStatement prepared = connection.prepareStatement("insert into magazzini (nomeMagazzino, indirizzoMagazzino) values (?,?)");
			prepared.setString(1, nomeLocazione);
			prepared.setString(2, indirizzoLocazione);
			prepared.executeUpdate();
		}catch (SQLException e) {
			System.out.println("Errore SQL");
			uscita();
		}
		MenuASceltaTestuale menu=new MenuASceltaTestuale("Magazzino salvato, vuoi uscire?",s);
		menu.aggiungiRisposte("si", "no");
		inputUtente=menu.iteration();
		if(inputUtente.equals("si"))
			return true;
		return false;
	}

	public static boolean gestioneMagazzino() {
		while(true) {
			boolean stessoMagazzino=true;
			sceltaGestioneMagazzino();
			while(stessoMagazzino) {
				try {
					Statement stm = connection.createStatement();
					ResultSet rs;
					currentLocation.getInventario().clear();
					rs=stm.executeQuery("select * from oggetti where codiceMagazzino="+currentLocation.getIdMagazzino());
					while(rs.next()) 
						currentLocation.aggiungiOggetto(rs.getInt("idOggetto"), rs.getString("nomeOggetto"),rs.getInt("quantitaOggetto"),rs.getInt("valoreSoglia"));
					//inventario
					System.out.println(currentLocation.stampaInventario());
					MenuASceltaTestuale menu=new MenuASceltaTestuale("Cambia il nome o  l'indirizzo,"
							+ "gestisci l'inventario, cancella questo magazzino oppure esci",s);
					menu.aggiungiRisposte("nome", "indirizzo","inventario","cancella","esci");
					inputUtente=menu.iteration();
					if(inputUtente.equals("esci"))
						uscita();
					else if(inputUtente.equals("nome")) {
						modificaNomeMagazzino();
					}else if(inputUtente.equals("indirizzo")) {
						modificaIndirizzo();
					}else if(inputUtente.equals("inventario")){
						gestioneOggetti();
					}else {
						cancellaMagazzino();
					}
				} catch (SQLException e) {
					System.out.println("Errore SQL");
					uscita();
				}
				MenuASceltaTestuale menu=new MenuASceltaTestuale("Continuare ad usare lo stesso magazzino?",s);
				menu.aggiungiRisposte("si","no");
				inputUtente=menu.iteration();
				if(inputUtente.equals("si"))
					stessoMagazzino=true;
				else
					stessoMagazzino=false;
			}
			MenuASceltaTestuale menu=new MenuASceltaTestuale("Gestisci un magazzino, torna al menu o esci",s);
			menu.aggiungiRisposte("gestisci", "menu","esci");
			inputUtente=menu.iteration();
			if(inputUtente.equals("esci"))
				return true;
			else if(inputUtente.equals("menu"))
				return false;
		}
	}

	public static void sceltaGestioneMagazzino() {
		try {
			Statement stm = connection.createStatement();
			ResultSet rs = stm.executeQuery("select * from magazzini");
			System.out.println("Magazzini nel Database:\n************");
			while (rs.next()) {
				System.out.println("Magazzino: "+rs.getString("nomeMagazzino")+", " + rs.getString("indirizzoMagazzino"));
			}
			System.out.println("Inserisci il nome del magazzino che vuoi gestire: ");
			inputUtente=s.nextLine();
			try {
				rs=stm.executeQuery("select * from magazzini where nomeMagazzino = \""+inputUtente+"\"");
			}catch (SQLException e) {
				System.out.println("magazzino non trovato. Errore SQL");
				uscita();
			}
			rs.next();
			currentLocation= new Magazzino(rs.getString("idMagazzino"),rs.getString("nomeMagazzino"),rs.getString("indirizzoMagazzino"));	
		} catch (SQLException e) {
			System.out.println("Errore SQL");
			uscita();
		}
	}

	public static void modificaNomeMagazzino() {
		System.out.println("Inserire nuovo nome ");
		inputUtente=s.nextLine();
		Statement stm;
		try {
			stm = connection.createStatement();
			String nome=inputUtente;
			stm.executeUpdate("update magazzini SET nomeMagazzino=\""+nome+"\" where idMagazzino = "+currentLocation.getIdMagazzino());
			currentLocation.setNomeMagazzino(nome);
		}catch(SQLException e){
			System.out.println("Errore SQL");
			uscita();
		}
	}

	public static void modificaIndirizzo() {
		System.out.println("Inserire nuovo indirizzo");
		inputUtente=s.nextLine();
		Statement stm;
		try {
			stm = connection.createStatement();
			String indirizzo=inputUtente;
			stm.executeUpdate("update magazzini SET indirizzoMagazzino=\""+indirizzo+"\" where idMagazzino = "+currentLocation.getIdMagazzino());
			currentLocation.setIndirizzoMagazzino(indirizzo);
		}catch(SQLException e){
			System.out.println("Errore SQL");
			uscita();
		}
	}

	public static void cancellaMagazzino() {
		MenuASceltaTestuale menu=new MenuASceltaTestuale("Vuoi davvero eliminare il magazzino "+currentLocation.getNomeMagazzino()+"?",s);
		menu.aggiungiRisposte("si", "no");
		inputUtente=menu.iteration();
		if(inputUtente.equals("no"))
			uscita();
		else {
			PreparedStatement pstm;
			try {
				pstm =connection.prepareStatement("delete from magazzini where idMagazzino="+currentLocation.getIdMagazzino());
				pstm.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Errore SQL");
				uscita();
			}	
			System.out.println("cancellazione effettuata con successo");
			uscita();
		}
	}

	public static void gestioneOggetti() {
		boolean continuaInventario=true;
		while(continuaInventario) {
			MenuASceltaTestuale menu=new MenuASceltaTestuale("Puoi creare un oggetto o gestire un oggetto, altrimenti uscire",s);
			menu.aggiungiRisposte("crea", "gestisci","esci");
			inputUtente=menu.iteration();
			if(inputUtente.equals("esci"))
				uscita();
			else if(inputUtente.equals("crea")) {
				continuaInventario=creaOggetto();
			}else {
				continuaInventario=sceltaModificaOggetto();
			}
		}
	}

	public static boolean creaOggetto(){
		boolean tornaInventario=false;
		System.out.println("Inserisci il nome dell'oggetto: ");
		String nome=s.nextLine();
		System.out.println("Inserisci quantita' attuali dell'oggetto: ");
		int quantita=Integer.valueOf(s.nextLine());
		System.out.println("Inserisci valore di Soglia: ");
		int soglia=Integer.valueOf(s.nextLine());
		tornaInventario=salvaOggettoSuDb(nome, quantita, soglia);
		return tornaInventario;
	}

	public static boolean salvaOggettoSuDb(String nome,int quantita,int soglia) {
		try {
			PreparedStatement prepared = connection.prepareStatement("insert into oggetti (nomeOggetto, quantitaOggetto, valoreSoglia, codiceMagazzino) values (?,?,?,?)");
			prepared.setString(1, nome);
			prepared.setInt(2, quantita);
			prepared.setInt(3, soglia);
			prepared.setString(4, currentLocation.getIdMagazzino());
			prepared.executeUpdate();
			Statement st;
			st=connection.createStatement();
			ResultSet rs;
			rs=st.executeQuery("select * from oggetti where nomeOggetto=\""+nome+"\"");
			rs.next();
			int id=rs.getInt(1);
			currentLocation.aggiungiOggetto(id, nome, quantita, soglia);
		}catch (SQLException e) {
			System.out.println("Errore SQL");
			uscita();
		}
		return continuaGestoneInventario();
	}

	public static boolean sceltaModificaOggetto() {
		Oggetto oggettoTemporaneo=null;
		if(currentLocation.getInventario().isEmpty()) {
			System.out.println("magazzino vuoto");
			return true;
		}
		do {
			System.out.println("inserisci il nome dell'oggetto da modificare: ");
			inputUtente=s.nextLine();
			oggettoTemporaneo=selezionaOggetto(inputUtente);
			if(oggettoTemporaneo==null)
				System.out.println("oggetto non trovato");
		}while(oggettoTemporaneo==null);
		return modificaOggetto(oggettoTemporaneo);
	}

	public static Oggetto selezionaOggetto(String name) {
		Oggetto compare;
		Iterator<Oggetto> iterator = currentLocation.getInventario().iterator();
		while(iterator.hasNext()) {
			compare=iterator.next();
			if(compare.getNome().equals(name))
				return compare;
		}
		return null;
	}

	public static boolean modificaOggetto(Oggetto oggetto) {
		String scelte="Selezionato *"+oggetto.getNome()+"* Puoi aumentare, diminuire, riassegnare le quantita' oppure"
				+ " modificare il nome o il valore di soglia di oppure cancellarlo";
		MenuASceltaTestuale menu=new MenuASceltaTestuale(scelte,s);
		menu.aggiungiRisposte("aumentare", "diminuire","riassegnare");
		menu.aggiungiRisposte("soglia", "nome","cancellare");
		inputUtente=menu.iteration();
		if(inputUtente.equals("aumentare")) {
			return incrementaQuantita(oggetto);
		}else if(inputUtente.equals("diminuire")) {
			return decrementaQuantita(oggetto);
		}else if(inputUtente.equals("riassegnare")){
			return riassegnaQuantita(oggetto);
		}else if(inputUtente.equals("soglia")){
			return modificaSoglia(oggetto);
		}else if(inputUtente.equals("nome")){
			return modificaNome(oggetto);
		}else {
			return cancellaOggetto(oggetto);
		}
	}

	public static boolean incrementaQuantita(Oggetto oggetto) {
		System.out.println("di quanto vuoi aumentare la quantita' ?");
		inputUtente=s.nextLine();
		Statement stm;
		try {
			stm = connection.createStatement();
			int quantitaIniziale=oggetto.getQuantita();
			int quantitaFinale=quantitaIniziale+Integer.valueOf(inputUtente);
			stm.executeUpdate("update oggetti SET quantitaOggetto="+quantitaFinale+" where idOggetto = "+oggetto.getIdOggetto());
			oggetto.setQuantita(quantitaFinale);
		} catch (SQLException e) {
			System.out.println("Errore SQL");
			uscita();
		}
		return continuaGestoneInventario();
	}

	public static boolean decrementaQuantita(Oggetto oggetto) {
		System.out.println("di quanto vuoi diminuire la quantita' ?");
		inputUtente=s.nextLine();
		Statement stm;
		try {
			stm = connection.createStatement();
			int quantitaIniziale=oggetto.getQuantita();
			int soglia=oggetto.getSoglia();
			int quantitaFinale=quantitaIniziale-Integer.valueOf(inputUtente);
			if(quantitaFinale<0) {
				System.out.println("decremento non possibile. Rimanenze negative.");
			}else if(quantitaFinale<soglia) {
				System.out.println("ATTENZIONE: Oggetto sotto la soglia minima di "+soglia+" unita'");
				stm.executeUpdate("update oggetti SET quantitaOggetto="+quantitaFinale+" where idOggetto = "+oggetto.getIdOggetto());
				oggetto.setQuantita(quantitaFinale);
			}else {
				stm.executeUpdate("update oggetti SET quantitaOggetto="+quantitaFinale+" where idOggetto = "+oggetto.getIdOggetto());
				oggetto.setQuantita(quantitaFinale);
			}
		} catch (SQLException e) {
			System.out.println("Errore SQL");
			uscita();
		}
		return continuaGestoneInventario();
	}

	public static boolean riassegnaQuantita(Oggetto oggetto) {
		System.out.println("qual è la nuova quantita' ?");
		inputUtente=s.nextLine();
		Statement stm;
		try {
			stm = connection.createStatement();
			int soglia=oggetto.getSoglia();
			int quantitaFinale=Integer.valueOf(inputUtente);
			if(quantitaFinale<0) {
				System.out.println("decremento non possibile. Rimanenze negative.");
			}else if(soglia<quantitaFinale) {
				System.out.println("ATTENZIONE: Oggetto sotto la soglia minima di "+soglia+" unita'");
				stm.executeUpdate("update oggetti SET quantitaOggetto="+quantitaFinale+" where idOggetto = "+oggetto.getIdOggetto());
				oggetto.setQuantita(quantitaFinale);
			}else {
				stm.executeUpdate("update oggetti SET quantitaOggetto="+quantitaFinale+" where idOggetto = "+oggetto.getIdOggetto());
				oggetto.setQuantita(quantitaFinale);
			}
		} catch (SQLException e) {
			System.out.println("Errore SQL");
			uscita();
		}
		return continuaGestoneInventario();
	}

	public static boolean modificaSoglia(Oggetto oggetto) {
		System.out.println("Inserire nuovo valore di soglia ");
		inputUtente=s.nextLine();
		Statement stm;
		try {
			stm = connection.createStatement();
			int soglia=Integer.valueOf(inputUtente);
			stm.executeUpdate("update oggetti SET valoreSoglia="+soglia+" where idOggetto = "+oggetto.getIdOggetto());
			oggetto.setSoglia(soglia);
		}catch(SQLException e){
			System.out.println("Errore SQL");
			uscita();
		}
		return continuaGestoneInventario();
	}

	public static boolean modificaNome(Oggetto oggetto) {
		System.out.println("Inserire nuovo nome dell'oggetto ");
		inputUtente=s.nextLine();
		Statement stm;
		try {
			stm = connection.createStatement();
			String nome=inputUtente;
			stm.executeUpdate("update oggetti SET nomeOggetto="+nome+" where idOggetto = "+oggetto.getIdOggetto());
			oggetto.setNome(nome);
		}catch(SQLException e){
			System.out.println("Errore SQL");
			uscita();
		}
		return continuaGestoneInventario();
	}

	public static boolean cancellaOggetto(Oggetto oggetto) {
		MenuASceltaTestuale menu=new MenuASceltaTestuale("Vuoi davvero eliminare l/'oggetto "+oggetto.getNome()+"?",s);
		menu.aggiungiRisposte("si", "no");
		inputUtente=menu.iteration();
		if(inputUtente.equals("no"))
			uscita();
		else {
			Statement stm;
			try {
				stm = connection.createStatement();
				stm.executeQuery("DELETE FROM oggetti where idOggetto="+oggetto.getIdOggetto());
			} catch (SQLException e) {
				System.out.println("Errore SQL");
				uscita();
			}	
			System.out.println("cancellazione eseguita con successo");
			uscita();
		}
		return continuaGestoneInventario();
	}

	public static boolean continuaGestoneInventario() {
		MenuASceltaTestuale menu=new MenuASceltaTestuale("continuare a gestire l'inventario?",s);
		menu.aggiungiRisposte("si", "no");
		inputUtente=menu.iteration();
		if(inputUtente.equals("si"))
			return true;
		else return false;
	}
}