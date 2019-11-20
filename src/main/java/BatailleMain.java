import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class BatailleMain {

	public static void main(String[] args) {
		
	    RestClient restClient = new RestClient();
	    String teamName, teamPwd, idEquipe;
		Properties prop = new Properties();
		
		//test de l'api avec : affiche pong
		System.out.println("Retour de l'appel de l'api : " +restClient.getPing());
		
		try {
		
			InputStream stream = new FileInputStream("C:\\Users\\Utilisateur\\eclipse-workspace\\TP1\\src\\main\\resources\\configuration.properties");
			prop.load(stream);	
			
			//Valuation des paramètres 
			teamName = prop.getProperty("team.name");
			teamPwd = prop.getProperty("team.password");
			
			//Appel de la fonction pour avoir l'Id de l'équipe
		    idEquipe = restClient.getIdEquipe(teamName, teamPwd);
	
		} 
		catch (IOException ex) {
		    ex.printStackTrace();
		}
					
	}

}
