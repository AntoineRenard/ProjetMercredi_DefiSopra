import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner; 
import java.util.List; 
import java.util.ArrayList;
import java.util.HashMap; 
import java.util.Map; 

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.gson.Gson;


public class BatailleMain {
	public static Scanner sc = new Scanner(System.in);
	public static RestClient restClient = new RestClient();
	public static Properties prop = new Properties();
	public static String idEquipe,idPartie,gameStatus, gameBoard,statut_personnage,statut_coup,nom_personnage,action_personnage,cible_personnage,attaque_a_effectuer,nom_equipe_adverse,teamName,teamPwd;
	public static List<String> personnageDisponible,personnageEquipe,actionPersonnageAutoriser,actionPersonnageExistant;
	public static int type_partie,nb_tour,numero_bot,numero_personnage,p_orc,p_priest,p_guard,p_paladin,p_archer,p_chamanint,d_effet_a=0,d_effet_p=0;
	public static ArrayList<EpicHero> myFighters,enemyFighters;
	public static Gson gson;
	public static HashMap<String, Integer> liste_ennemi_actuel ; 
	
	
	
	public static void main(String[] args) {

		//test de l'api avec : affiche pong
		//System.out.println("Retour de l'appel de l'api : " +restClient.getPing());
		
	    charger_fichier();
		recuperer_id_equipe();
		initialise_actionPersonnageExistant();
		    //test
		   // System.out.println("ID de l'equipe : "+idEquipe);
		
		System.out.println("Bienvenue sur EPIC GAME");

		System.out.println("Quel type de partie voulez-vous lancer?");
		System.out.println("1- Practice");
		System.out.println("2- Versus");
		do {
			System.out.print("Indiquez le numero correspondant à votre partie : ");
			type_partie = sc.nextInt();
		}while(type_partie!=1 && type_partie!=2);
		
		renitialise_variable();
		
		if(type_partie==1) {
			lancer_practice();
		}else {
			lancer_versus();
		}
		
	} //fin main
	
	public static void initialise_actionPersonnageExistant() {
		actionPersonnageExistant= new ArrayList();
		actionPersonnageExistant.add("ATTACK");
		actionPersonnageExistant.add("DEFEND");
		actionPersonnageExistant.add("YELL");
		actionPersonnageExistant.add("HEAL");
		actionPersonnageExistant.add("PROTECT");
		actionPersonnageExistant.add("REST");
	}
	
	public static void renitialise_variable() {
		nb_tour=0;
		gameStatus="";
		gameBoard="";
		statut_coup="";
		liste_ennemi_actuel = new HashMap<>();
		personnageDisponible= new ArrayList();
		personnageDisponible.add("ORC");
		personnageDisponible.add("GUARD");
		personnageDisponible.add("PRIEST");
		
		personnageEquipe= new ArrayList();
	  	actionPersonnageAutoriser= new ArrayList();
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
		 teamName = prop.getProperty("team.name");
		 teamPwd = prop.getProperty("team.password");

		//Appel de la fonction pour avoir l'Id de l'équipe
		idEquipe= restClient.getIdEquipe(teamName, teamPwd);
		 
	}

	public static void lancer_practice() {
		//
		  
		do {
			do{
				System.out.print("Indiquer le numero du bot : ");
				numero_bot = sc.nextInt();
			}while(numero_bot<1 || numero_bot>5);
			
			 idPartie = restClient.getPraticeId(String.valueOf(numero_bot), idEquipe);
			  sleep(200);
			  
			  if(idPartie.equals("NA")) {
					 System.out.println("Le bot N°"+numero_bot+" est occupé");
				 }
		}while(idPartie.equals("NA"));
		 
		  lancer_combat();  
		 
	}

	public static void lancer_versus() {
		do {
			idPartie = restClient.getIdNextBattle(idEquipe);
			  sleep(200);
		}while(idPartie.equals("NA"));
		
		lancer_combat();
		 
	}
	public static void lancer_combat() {
		do {
			attendre_mon_tour();
			if(gameStatus.equals("CANPLAY")) {
				nb_tour++;
				reload_gameStatus();
				 System.out.println("Tour N°"+nb_tour);
				switch(nb_tour) {
				  case 1:
					  if(type_partie==1) {
						  selectionner_personnage("PALADIN");
					  }else {
						  demander_choix_personnage();
					  }
					  
					  
				    break;
				  case 2:
					  if(type_partie==1) {
						  selectionner_personnage("GUARD");
					  }else {
						  demander_choix_personnage();
					  }
				    break;
				  case 3:
					  if(type_partie==1) {
						  selectionner_personnage("ARCHER");
					  }else {
						  demander_choix_personnage();
					  }
					  
					    break;
				  default:
					  System.out.println("Etat du jeu : "+gameStatus);
					  afficher_statut_hero();
					  if(type_partie==1){
						  strategie_bot();
					  }else {
						  strategie_joueur();
					  }
					  
					  
				}	
				
			}
			System.out.println(gameStatus);
			System.out.println(statut_coup);
		}while(!gameStatus.equals("VICTORY")&&!gameStatus.equals("DEFEAT")&&!gameStatus.equals("CANCELLED")&&!gameStatus.equals("DRAW")&&!statut_coup.equals("DEFEAT"));
		
		
		
	}
	
	//Fonction permettant d'afficher les statuts actuels des personnages
	public static void afficher_statut_hero() {
		reload_gameBoard();
		
		//System.out.println("Plateau du jeu : "+gameBoard);

		gson = new Gson();
		Board playerBoard = gson.fromJson(gameBoard, Board.class);
		showHeroesStatus(playerBoard);		
	}
	//Fonction permettant d'afficher les statuts actuels des personnages

	public static void showHeroesStatus(Board playerBord) {  
	  if(nb_tour > 3){
		  //Récupération du dernier coup de l'adversaire
		  String enemyLastMove = restClient.getLastEnnemieMove(idPartie, idEquipe);
		  
		  //Split de la chaine en 3 tableau
		  String [] arrayAction = enemyLastMove.split("\\$");	 
				 		  
		  System.out.println("The Builders                                   "+restClient.getNameEquipeAdverse(idPartie, idEquipe));
		  int i = 0;
		  myFighters = new ArrayList<EpicHero>();
		  enemyFighters = new ArrayList<EpicHero>();
	
		  for (EpicHeroesLeague ehl : playerBord.getPlayerBoards()) {
			  i++;
			  if(ehl.getFighters()!=null) {
				  for (EpicHero eh : ehl.getFighters()) {
					  if(i == 1) {
						  myFighters.add(eh);
						  
					  }
					  if(i == 2) {
						  enemyFighters.add(eh);
						 
					  }
				   }
			  }
		   }
		   for (int j = 0; j<3; j++) {
				
		
		    System.out.println((j+1)+" "+myFighters.get(j).getFighterClass()+"                                           "+(j+1)+" "+enemyFighters.get(j).getFighterClass());
		    System.out.println("Vie : "+myFighters.get(j).getCurrentLife()+"                                       Vie : "+enemyFighters.get(j).getCurrentLife());
		    System.out.println("Mana : "+myFighters.get(j).getCurrentMana()+"                                      Mana : "+enemyFighters.get(j).getCurrentMana());
		   
		    int statut1 = myFighters.get(j).getStates()!=null ? myFighters.get(j).getStates().get(0).getRemainingDuration() : 0;
		    String typeStatut1 = myFighters.get(j).getStates()!=null ? myFighters.get(j).getStates().get(0).getType() : "'Aucun'";
		    String typeStatut2 = enemyFighters.get(j).getStates()!=null ? enemyFighters.get(j).getStates().get(0).getType() : "'Aucun'";
		    int statut2 = enemyFighters.get(j).getStates()!=null ? enemyFighters.get(j).getStates().get(0).getRemainingDuration() : 0;
		    
		    System.out.println("Statut : Type = "+typeStatut1+", Durée restante => "+statut1+" Statut :Type = "+typeStatut2+" et Durée restante => "+statut2);
		    if(nb_tour >4 & j< arrayAction.length) {
		 	 
		    	System.out.println("----------------------------------------------------------------------------");
		    	String [] last_action_ennemie=arrayAction[j].split(",",3);	
		    	  enemyFighters.get(j).setLastAction(last_action_ennemie[1]);
				   enemyFighters.get(j).setLastCible(last_action_ennemie[2]);
		    	System.out.println("        Dernier coup enemi: ACTION = '"+last_action_ennemie[1]+"', CIBLE = '"+last_action_ennemie[2]+"'");
		    	System.out.println("---------------------------------------------------------------------------");
		    }
		    
		    liste_ennemi_actuel.put(enemyFighters.get(j).getFighterClass(),j);
		    
		   
		}
	}

	}

	public static void reload_gameBoard() {
		  gameBoard =restClient.getGameBoardSorted(idPartie, idEquipe);
		  System.out.println("Etat du jeu : "+gameStatus);
	}

	public static void reload_gameStatus() {
		  gameStatus = restClient.getGameStatus(idPartie, idEquipe);
	}

	public static void get_name_adversaire() {
		  nom_equipe_adverse = restClient.getNameEquipeAdverse(idPartie, idEquipe);
	}

	public static void attendre_mon_tour() {
		reload_gameStatus();
		do {
			sleep(300);
		}while(gameStatus.equals("CANTPLAY"));
	}

	public static void selectionner_personnage(String nom) {
		statut_personnage = restClient.lancerAttaque(idPartie, idEquipe,nom);
		
		System.out.println("Etat du personnage "+nom+": "+statut_personnage);
	}

	public static void demander_choix_personnage() {
		
		do {
			 System.out.print("Indiquer votre personnage n°"+nb_tour+" parmi ceux disponibles ( "); 
		     parcourir_list(personnageDisponible);
		     nom_personnage = sc.nextLine();
			
		}while(!personnageDisponible.contains(nom_personnage));
		personnageDisponible.remove(nom_personnage);
		personnageEquipe.add(nom_personnage);
		
		statut_personnage = restClient.lancerAttaque(idPartie, idEquipe,nom_personnage);
		
		System.out.println("Etat du personnage "+nom_personnage+": "+statut_personnage);
	}
	public static void action_a_effectuer(String a1,String c1,String a2,String c2,String a3,String c3) {
		statut_coup = restClient.lancerAttaque(idPartie, idEquipe,"A1,"+a1+","+c1+"$"+"A2,"+a2+","+c2+"$"+"A3,"+a3+","+c3);
		
		System.out.println("Etat du coup : "+statut_coup);
	}

	public static void action_a_effectuer(String attack) {
		statut_coup = restClient.lancerAttaque(idPartie, idEquipe,attack);
		
		System.out.println("Etat du coup : "+statut_coup);
	}

	public static void strategie_bot() {
		if(numero_bot==51) {

			  String action="HEAL",cible_p;
			  int cible_e=p_priest+1;
			  int cible_a;
			  if(myFighters.get(0).getCurrentLife()>myFighters.get(1).getCurrentLife()) {
				  cible_a=2;
			  }else {
				  cible_a=1;
			  }
			  if(enemyFighters.get(p_priest).getCurrentLife()==0) {
				  cible_e=p_orc+1;
			  }

			  if(enemyFighters.get(p_orc).getCurrentLife()==0) {
				  cible_e=p_guard+1;
			  }
			  if(myFighters.get(2).getCurrentMana()==1) {
				  action="REST";
				  cible_p="A3";
			  }else {
				  cible_p="A"+cible_a;
			  }
			   action_a_effectuer("ATTACK","E"+cible_e,"ATTACK","E"+cible_e,action,cible_p);
		  }else if(numero_bot==25) {
			  String action="HEAL",cible_p;
			  int cible_e=p_orc+1;
			  int cible_a;
			  if(myFighters.get(0).getCurrentLife()>myFighters.get(1).getCurrentLife()) {
				  cible_a=2;
			  }else {
				  cible_a=1;
			  }
			  if(enemyFighters.get(p_orc).getCurrentLife()==0) {
				  cible_e=p_guard+1;
			  }

			  if(enemyFighters.get(p_guard).getCurrentLife()==0) {
				  cible_e=p_priest+1;
			  }
			  if(myFighters.get(2).getCurrentMana()==1) {
				  action="REST";
				  cible_p="A3";
			  }else {
				  cible_p="A"+cible_a;
			  }

			  action_a_effectuer("ATTACK","E"+cible_e,"ATTACK","E"+cible_e,action,cible_p);
		  }else if(numero_bot==24) {
			  String action="HEAL",cible_p;
			  int cible_e=p_priest+1;
			  int cible_a;
			  if(myFighters.get(0).getCurrentLife()>myFighters.get(1).getCurrentLife()) {
				  cible_a=2;
			  }else {
				  cible_a=1;
			  }
			  if(enemyFighters.get(p_priest).getCurrentLife()==0) {
				  cible_e=p_orc+1;
			  }
			  
			  
			  if(myFighters.get(2).getCurrentMana()==1) {
				  action="REST";
				  cible_p="A3";
			  }else {
				  cible_p="A"+cible_a;
			  }
			  
			  action_a_effectuer("ATTACK","E"+cible_e,"ATTACK","E"+cible_e,action,cible_p);
			  
		  }else {

			  String action_a,action_p,action_g,cible_g,liste_ennemi_vivant,liste_ennemi_rested,nom_cible_faible,nom_cible_rested,cible_p,cible_a;
			  int p_cible_faible,p_cible_rest=0;
			  
			  
			  liste_ennemi_vivant=liste_ennemi_vivant();
			  nom_cible_faible=get_cible_prioritaire(liste_ennemi_vivant);
			  p_cible_faible=liste_ennemi_actuel.get(nom_cible_faible)+1;
			  
			  liste_ennemi_rested=get_rested_enemy();
			  nom_cible_rested=get_cible_prioritaire(liste_ennemi_rested);
			  if(nom_cible_rested.equals("")) {
				  nom_cible_rested="E0";
			  }
			  if(!nom_cible_rested.equals("E0") ) {
					System.out.println(nom_cible_rested);

					System.out.println(liste_ennemi_actuel);
				  p_cible_rest=liste_ennemi_actuel.get(nom_cible_rested)+1;
			  }
			//Action Paladin
			  if(myFighters.get(0).getCurrentMana()==1) {
				  action_p="REST";
				  cible_p="A1";
			  }else {
				  
				  if(d_effet_p>0 || nom_cible_rested.equals("E0") ) {
					  action_p="ATTACK";
					  cible_p="E"+p_cible_faible;
					  d_effet_p--;
				  }else {
					  action_p="CHARGE";
					  cible_p="E"+p_cible_rest;
					  d_effet_p=2;
				  }
			  }
			  
			  //Action GUARD
			//
			 /* if(myFighters.get(0).getCurrentLife()>myFighters.get(2).getCurrentLife()) {
				  cible_g="A3";
			  }else {
				  cible_g="A1";
			  }
			  */
			  if(myFighters.get(1).getCurrentMana()==1) {
				  action_g="REST";
				  cible_g="A2";
			  }else {
				  String hero_attaquer="A"+get_most_attacked_hero();
				  if(!hero_attaquer.equals("A0")) {
					  action_g="PROTECT";
					  cible_g=hero_attaquer;
				  }else {
					  action_g="ATTACK";
					  //get_cible_prioritaire();
					  cible_g="E"+p_cible_faible;
				  }
			}
			 
			//Action ARCHER
			  if(myFighters.get(2).getCurrentMana()==1) {
				  action_a="REST";
				  cible_a="A3";
			  }else {
				  cible_a="E"+p_cible_faible;
				  if(d_effet_a>0) {
					  action_a="ATTACK";
					  d_effet_a--;
				  }else {
					  action_a="FIREBOLT";
					  d_effet_a=3;
				  }
			  }
			
			  System.out.println(action_a);
			  System.out.println();
			  System.out.println(action_a);
			  System.out.println(cible_a);
			  
			   action_a_effectuer(action_p,cible_p,action_g,cible_g,action_a,cible_a);
		  }
	}	
	
	public static String liste_ennemi_vivant() {
		String liste_ennemi_vivant="";
	    for (String i : liste_ennemi_actuel.keySet()) {
	    	int p_ennemi= liste_ennemi_actuel.get(i);
	    	if(enemyFighters.get(p_ennemi).getCurrentLife()>0) {
	    		liste_ennemi_vivant+=" "+i;
			  }

	      }
	    return liste_ennemi_vivant;
	}
	public static void strategie_joueur() {
		int i=0;
		attaque_a_effectuer="";
		for(String p:personnageEquipe) {
			i++;
			
			attaque_a_effectuer+="A"+i+",";
			 demander_action_personnage(p,i);
			 
			 if(i!=3) {
		    	 attaque_a_effectuer+="$";
		     }
	     }
		action_a_effectuer(attaque_a_effectuer);
	}

	public static void demander_action_personnage(String nom,int i) {
		System.out.println("Indiquer l'action du : "+nom);
		get_action_possible_personnage(nom);
	
		demander_type_attaque();
		demander_type_cible_attaque();
		demander_numero_cible_attaque();
	     
		
	     //cible_personnage;
	}
	public static void get_action_possible_personnage(String nom) {
		actionPersonnageExistant= new ArrayList();
		actionPersonnageExistant.add("ATTACK");
		actionPersonnageExistant.add("DEFEND");
		actionPersonnageExistant.add("REST");
		if(nom.equals("ORC")) {
			actionPersonnageExistant.add("YELL");
		}else if(nom.equals("GUARD")) {
			actionPersonnageExistant.add("PROTECT");
		}else if(nom.equals("PRIEST")) {
			actionPersonnageExistant.add("HEAL");
		} else if(nom.equals("CHAMAN")) {
		actionPersonnageExistant.add("CLEANSE");
		}else if(nom.equals("ARCHER")) {
		actionPersonnageExistant.add("FIREBOLT");
		}else if(nom.equals("PALADIN")) {
		actionPersonnageExistant.add("CHARGE");
		}
		
	}
	//Fonction permetant de retourner le hero qui subit lus d'une attaque
	public static String get_rested_enemy() {

	for (EpicHero ef : enemyFighters) {
		if(ef.getLastAction()!=null) {
			
			if(ef.getLastAction().equals("REST")) {
				return ef.getFighterClass();
			}
			
		}
	}


	return "E0";

	}

	public static void demander_type_attaque() {
		do {
			System.out.println("Type d'attaque ");
			 parcourir_list(actionPersonnageAutoriser); 
			 action_personnage = sc.nextLine();
		}while(!actionPersonnageExistant.contains(action_personnage));
		attaque_a_effectuer+=action_personnage+",";
	}
	public static void demander_type_cible_attaque() {
		do {
			System.out.println("Cible attaque ( Allié->A, Ennemie->E ) :");
			cible_personnage = sc.nextLine();
		}while(!cible_personnage.equals("A") && !cible_personnage.equals("E"));
		attaque_a_effectuer+=cible_personnage;
	}

	public static void demander_numero_cible_attaque() {
		do {
			System.out.println("Numero de la cible ( 1, 2, ou 3 ) :");
			numero_personnage = sc.nextInt();
		}while(numero_personnage!=1 && numero_personnage!=2 && numero_personnage!=3);
		attaque_a_effectuer+=numero_personnage;
	}
	public static void parcourir_list(List<String> liste) {
		System.out.println("( ");
		 for(String l:liste) {
	    	 System.out.print(l+" ");
	     }
	     System.out.print(") :");  
	}
	//Fonction permettant de retourner la cible prioritaire
	public static String get_cible_prioritaire(String listeEnemy) {
	if(listeEnemy.contains("PRIEST"))
	return "PRIEST";

	if(listeEnemy.contains("ARCHER"))
	return "ARCHER";

	if(listeEnemy.contains("ORC"))
	return "ORC";

	if(listeEnemy.contains("PALADIN"))
	return "PALADIN";

	if(listeEnemy.contains("CHAMAN"))
	return "CHAMAN";

	if(listeEnemy.contains("GUARD"))
	return "GUARD";

	return "";
	}
	
	//get max
	public static int get_max_int(int a, int b, int c) {
	int max1 = a > b ? a : b;
	return max1 > c ? max1 : c;
	}
	//Fonction permetant de retourner le hero qui subit lus d'une attaque
	public static String get_most_attacked_hero() {
	int n1 =0,  n2 = 0, n3 = 0;
	HashMap<String, Integer> map = new HashMap();
	String result;
	for (EpicHero ef : enemyFighters) {
		String last_cible=ef.getLastCible();
		if(last_cible==null) {
			last_cible="";
		}
		
	if(last_cible.equals("E1")) {
	n1++;
	} else if(last_cible.equals("E2")) {
	n2++;
	} else if(last_cible.equals("E3")) {
	n3++;
	}
	map.put("E1", n1);
	map.put("E2", n2);
	map.put("E3", n3);
	int max = get_max_int(n1, n2, n3);

	if(max > 1) {
	for (String key : map.keySet()) {
	if(max == map.get(key))
		return key.substring(1);
	}
	}
	}

	return "0";

	}
	public static void sleep(int n) {
		
		try {
		      
            Thread.sleep(n) ;
         }  catch (InterruptedException e) {
         
             // gestion de l'erreur
         }
         
	}
}

class State {
	
		private String type;
		private int remainingDuration;
		
		public String getType() {
		return type;
		}
		public void setType(String type) {
		this.type = type;
		}
		public int getRemainingDuration() {
		return remainingDuration;
		}
		public void setRemainingDuration(int remainingDuration) {
		this.remainingDuration = remainingDuration;
		}

}

class EpicHero {

		private String fighterClass;
		private int orderNumberInTeam;
		private boolean isDead;
		private int maxAvailableMana;
		private int maxAvailableLife;
		private int currentMana;
		private int currentLife;
		private String lastAction;
		private String lastCible;
		
		public String getLastAction() {
			return lastAction;
		}
		public void setLastAction(String lastAction) {
			this.lastAction = lastAction;
		}
		public String getLastCible() {
			return lastCible;
		}
		public void setLastCible(String lastCible) {
			this.lastCible = lastCible;
		}
		private ArrayList<State> states ;
		private String fighterID;
		public String getFighterClass() {
		return fighterClass;
		}
		public void setFighterClass(String fighterClass) {
		this.fighterClass = fighterClass;
		}
		public int getOrderNumberInTeam() {
		return orderNumberInTeam;
		}
		public void setOrderNumberInTeam(int orderNumberInTeam) {
		this.orderNumberInTeam = orderNumberInTeam;
		}
		public boolean isDead() {
		return isDead;
		}
		public void setDead(boolean isDead) {
		this.isDead = isDead;
		}
		public int getMaxAvailableMana() {
		return maxAvailableMana;
		}
		public void setMaxAvailableMana(int maxAvailableMana) {
		this.maxAvailableMana = maxAvailableMana;
		}
		public int getMaxAvailableLife() {
		return maxAvailableLife;
		}
		public void setMaxAvailableLife(int maxAvailableLife) {
		this.maxAvailableLife = maxAvailableLife;
		}
		public int getCurrentMana() {
		return currentMana;
		}
		public void setCurrentMana(int currentMana) {
		this.currentMana = currentMana;
		}
		public int getCurrentLife() {
		return currentLife;
		}
		public void setCurrentLife(int currentLife) {
		this.currentLife = currentLife;
		}
		public ArrayList<State> getStates() {
		return states;
		}
		public void setStates(ArrayList<State> states) {
		this.states = states;
		}
		public String getFighterID() {
		return fighterID;
		}
		public void setFighterID(String fighterID) {
		this.fighterID = fighterID;
		}
}

class EpicHeroesLeague  {

		private String playerId;
		private String playerName;
		private ArrayList<EpicHero> fighters;
		
		public String getPlayerId() {
		return playerId;
		}
		public void setPlayerId(String playerId) {
		this.playerId = playerId;
		}
		public String getPlayerName() {
		return playerName;
		}
		public void setPlayerName(String playerName) {
		this.playerName = playerName;
		}
		public ArrayList<EpicHero> getFighters() {
		return fighters;
		}
		public void setFighters(ArrayList<EpicHero> fighters) {
		this.fighters = fighters;
		}


}

class Board {

		private ArrayList<EpicHeroesLeague> playerBoards;
		private int nbrTurnsLeft;
		
		public ArrayList<EpicHeroesLeague> getPlayerBoards() {
		return playerBoards;
		}
		public void setPlayerBoards(ArrayList<EpicHeroesLeague> playerBoards) {
		this.playerBoards = playerBoards;
		}
		public int getNbrTurnsLeft() {
		return nbrTurnsLeft;
		}
		public void setNbrTurnsLeft(int nbrTurnsLeft) {
		this.nbrTurnsLeft = nbrTurnsLeft;
		}
		@Override
		public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
		}


}
