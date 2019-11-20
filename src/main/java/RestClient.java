import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RestClient {
	
	private static final String API_URI = "http://codeandplay.tech/epic-ws/epic";
	
	    
	public String getPing() {
		
		Client client = ClientBuilder.newClient();
		
		WebTarget webtarget = client.target(API_URI+"/ping");		
		
		return webtarget.request().get(String.class);
	}
	
	public String getIdEquipe(String teamName, String teamPwd) {
		
		Client client = ClientBuilder.newClient();
				
		WebTarget webtarget = client.target(API_URI+"/player/getIdEquipe/"+teamName+"/"+teamPwd);		
		
		return webtarget.request().get(String.class);
	}
	
	public String getIdNextBattle(String idEquipe) {
		
		Client client = ClientBuilder.newClient();
				
		WebTarget webtarget = client.target(API_URI+"/versus/next/"+idEquipe);		
		
		return webtarget.request().get(String.class);
	}
	
	public String getPraticeId(String numeroBot, String idEquipe) {
		
		Client client = ClientBuilder.newClient();
		
		WebTarget webtarget = client.target(API_URI+"/practice/new/"+numeroBot+"/"+idEquipe);		
		
		return webtarget.request().get(String.class);
	}
	
	//Deroulement de la partie : retourne le statut de la partie
	public String getGameStatus(String partieId, String idEquipe) {
		
		Client client = ClientBuilder.newClient();
		
		WebTarget webtarget = client.target(API_URI+"/game/status/"+partieId+"/"+idEquipe);		
		
		return webtarget.request().get(String.class);
	}

}
