import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner; 


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


public class BatailleMain {
	public static Scanner sc = new Scanner(System.in);
	public static RestClient restClient = new RestClient();
	public static Properties prop = new Properties();
	public static String idEquipe,idPartie,gameStatus, gameBoard;
	public static int type_partie;
	
	public static void main(String[] args) {

	   
	  
	   
	    
		
		//test de l'api avec : affiche pong
		//System.out.println("Retour de l'appel de l'api : " +restClient.getPing());
		
	    charger_fichier();
		recuperer_id_equipe();
		    //test
		   // System.out.println("ID de l'equipe : "+idEquipe);
		System.out.println("Bienvenu sur God of wars");

		System.out.println("Quel type de partie voulez-vous lancer?");
		System.out.println("1- Practice");
		System.out.println("2- Versius");
		do {
		System.out.println("Indiquez le numero correspondant à votre partie : ");
		type_partie = sc.nextInt();
		}while(type_partie!=1 && type_partie!=2);
		
		
		if(type_partie==1) {
			lancer_practice();
		}else {
			lancer_versus();
		}
		
		
			
		
					
	}
	
	public static void charger_fichier() {
		
		try {
				
				InputStream stream = new FileInputStream("C:\\Users\\Utilisateur\\eclipse-workspace\\TP2_API_REST\\src\\main\\resources\\configuration.properties");
				prop.load(stream);	
				
			} 
			catch (IOException ex) {
			    ex.printStackTrace();
			    System.err.println("Le fichier de configuration ne s'est pas chargé");
			    System.exit(1);
			    
			}
	}
	public static void recuperer_id_equipe() {
		 
		//Valuation des paramètres 
		String teamName = prop.getProperty("team.name");
		String teamPwd = prop.getProperty("team.password");

		//Appel de la fonction pour avoir l'Id de l'équipe
		idEquipe= restClient.getIdEquipe(teamName, teamPwd);
		 
	}

	public static void lancer_practice() {
		//
		 int numero_bot; 
		do {
			do{
				System.out.println("Indiquer le niveau du bot : ");
				numero_bot = sc.nextInt();
			}while(numero_bot<1 || numero_bot>5);
			
			 idPartie = restClient.getPraticeId(String.valueOf(numero_bot), idEquipe);
			  sleep(200);
			  
			  if(idPartie.equals("NA")) {
					 System.out.println("Le bot N°"+numero_bot+" est occupé");
				 }
		}while(idPartie.equals("NA"));
		 
		  lancer_combat();
	  
	    
	    //test
	   // System.out.println("id de l'entrainement avec un Bot (NA si y a rien) : "+idPartie);
	    
	   
		 
	}

	public static void lancer_versus() {
		do {
			idPartie = restClient.getIdNextBattle(idEquipe);
			  sleep(200);
		}while(idPartie.equals("NA"));
		//
	    //test
	    //System.out.println("Prochain affrontement (NA si y a rien) : "+idPartie);
		System.out.println("Desolé nous n'avons pas encore developpé cette fonctionnalité.");
		 
	}
	public static void lancer_combat() {
		reload_gameStatus();
		    //test
		    System.out.println("Status du jeu (ex: DEFEAT) : "+gameStatus);
		    reload_gameBoard();
		   
		    System.out.println("Plateau du jeu : "+gameBoard);
	}
	public static void reload_gameBoard() {
		  gameBoard =restClient.getGameBoard(idPartie);
	}
	public static void reload_gameStatus() {
		  gameStatus = restClient.getGameStatus(idPartie, idEquipe);
	}
	public static void sleep(int n) {
		try {
		      
            Thread.sleep(n) ;
         }  catch (InterruptedException e) {
         
             // gestion de l'erreur
         }
	}
}
