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

	public static void main(String[] args) {
		
	    RestClient restClient = new RestClient();
	    Scanner sc = new Scanner(System.in);
	    String teamName, teamPwd, idEquipe, nextBattleId, practiceId, gameStatus, gameBoard;
	    int type_partie;
		Properties prop = new Properties();
		
		//test de l'api avec : affiche pong
		System.out.println("Retour de l'appel de l'api : " +restClient.getPing());
		
		
		System.out.println("Bienvenu sur God of wars");

		System.out.println("Quel type de partie voulez-vous lancer?");
		System.out.println("1- Practice");
		System.out.println("2- Versius");
		do {
		System.out.println("Indiquez le numero correspondant à votre partie : ");
		type_partie = sc.nextInt();
		}while(type_partie!=1 && type_partie!=2);
		 
		if(type_partie==1) {
			
		}else {
			System.out.println("Desolé nous n'avons pas encore developpé cette fonctionnalité.");
		}
		
		try {
		
			InputStream stream = new FileInputStream("C:\\Users\\Utilisateur\\eclipse-workspace\\TP2_API_REST\\src\\main\\resources\\configuration.properties");
			prop.load(stream);	
			
			//Valuation des paramètres 
			teamName = prop.getProperty("team.name");
			teamPwd = prop.getProperty("team.password");
			
			//Appel de la fonction pour avoir l'Id de l'équipe
		    idEquipe = restClient.getIdEquipe(teamName, teamPwd);
		    //test
		    System.out.println("ID de l'equipe : "+idEquipe);
		    
		    //nextBattleId = restClient.getIdNextBattle(idEquipe);
		    //test
		    //System.out.println("Prochain affrontement (NA si y a rien) : "+nextBattleId);
		    
		    practiceId = restClient.getPraticeId("2", idEquipe);
		    //test
		    System.out.println("id de l'entrainement avec un Bot (NA si y a rien) : "+practiceId);
		    
		    gameStatus = restClient.getGameStatus(practiceId, idEquipe);
		    //test
		    System.out.println("Status du jeu (ex: DEFEAT) : "+gameStatus);
		    
		    gameBoard =restClient.getGameBoard(practiceId);
		    System.out.println("Plateau du jeu : "+gameBoard);
		} 
		catch (IOException ex) {
		    ex.printStackTrace();
		}
					
	}

}
