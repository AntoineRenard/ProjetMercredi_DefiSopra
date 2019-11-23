import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner; 
import java.util.List; 
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


public class BatailleMain {
	public static Scanner sc = new Scanner(System.in);
	public static RestClient restClient = new RestClient();
	public static Properties prop = new Properties();
	public static String idEquipe,idPartie,gameStatus, gameBoard,statut_personnage,statut_coup,nom_personnage,action_personnage,cible_personnage,attaque_a_effectuer,nom_equipe_adverse,teamName,teamPwd;
	public static List<String> personnageDisponible,personnageEquipe,actionPersonnageAutoriser,actionPersonnageExistant;
	public static int type_partie,nb_tour,numero_bot,numero_personnage;
	
	
	
	public static void main(String[] args) {

	   
	  
	   
	    

		//test de l'api avec : affiche pong
		//System.out.println("Retour de l'appel de l'api : " +restClient.getPing());
		
	    charger_fichier();
		recuperer_id_equipe();
		initialise_actionPersonnageExistant();
		    //test
		   // System.out.println("ID de l'equipe : "+idEquipe);
		
		System.out.println("Bienvenu sur God of wars");

		System.out.println("Quel type de partie voulez-vous lancer?");
		System.out.println("1- Practice");
		System.out.println("2- Versius");
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
		
		
			
		
					
	}
	
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
	  
	    
	    //test
	   // System.out.println("id de l'entrainement avec un Bot (NA si y a rien) : "+idPartie);
	    
	   
		 
	}

	public static void lancer_versus() {
		do {
			idPartie = restClient.getIdNextBattle(idEquipe);
			  sleep(200);
		}while(idPartie.equals("NA"));
		
		lancer_combat();
		//
	    //test
	    //System.out.println("Prochain affrontement (NA si y a rien) : "+idPartie);
		//System.out.println("Desolé nous n'avons pas encore developpé cette fonctionnalité.");
		 
	}
	public static void lancer_combat() {
		do {
			attendre_mon_tour();
			if(gameStatus.equals("CANPLAY")) {
				nb_tour++;
				reload_gameStatus();
				switch(nb_tour) {
				  case 1:
					  if(type_partie==1) {
						  selectionner_personnage("ORC");
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
						  selectionner_personnage("PRIEST");
					  }else {
						  demander_choix_personnage();
					  }
					    break;
				  default:
					  System.out.println("Etat du jeu : "+gameStatus);
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
		
		reload_gameBoard();
		
		System.out.println("Plateau du jeu : "+gameBoard);
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
			System.out.print("Indiquer votre personnage n°"+nb_tour+" parmis ceux disponible ( "); 
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
		if(numero_bot==3) {
			  action_a_effectuer("ATTACK","E1","DEFEND","A3","HEAL","A2");
		  }else {
			  action_a_effectuer("ATTACK","E2","ATTACK","E1","HEAL","A1");
		  }
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
		}else {
			actionPersonnageExistant.add("HEAL");
		}
		
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
	public static void sleep(int n) {
		
		try {
		      
            Thread.sleep(n) ;
         }  catch (InterruptedException e) {
         
             // gestion de l'erreur
         }
         
	}
}
