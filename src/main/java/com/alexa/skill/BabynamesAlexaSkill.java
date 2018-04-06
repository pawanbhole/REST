package com.alexa.skill;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
 
@Path("/babynames")
public class BabynamesAlexaSkill {
	 public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	 public static final String MYSQL_URL = "jdbc:mysql://127.6.24.130:3306/javaTestDB?"
	                                            + "user=adminIiwjHWP&password=IgX6pwUruJv5";
	
	@POST 
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/suggest")
	public Response postMsg(Map msg) {		
		System.out.println("Received request..........................");
		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputMap.put("version", "1.0");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		Map<String, Object> outputSpeechMap = new HashMap<String, Object>();
		
		boolean isQuestion = false;
		String finalSearchString = "";
		String gender = "";
		String finalMessage = "";
		String names = "";
		String intent = "Welcome";
		System.out.println("msg:"+msg);
		if(((Map)msg.get("request")).get("intent") == null) {
			intent = "Welcome";
			System.out.println("intent is null.");
		} else {
			String firstLetter = null;
			String secondLetter = null;
			String thirdLetter = null;
			Map intentMap = (Map)((Map)msg.get("request")).get("intent");
			System.out.println("intentMap :"+intentMap);
			if(intentMap.get("slots") != null 
					&& ((Map)intentMap.get("slots")).get("firstLetter") != null
					&& ((Map)((Map)intentMap.get("slots")).get("firstLetter")).get("value") != null ) {
				firstLetter = (String) ((Map)((Map)intentMap.get("slots")).get("firstLetter")).get("value");
				finalSearchString += firstLetter;
				System.out.println("firstLetter :"+firstLetter);
				System.out.println("finalSearchString :"+finalSearchString);
			}
			if(intentMap.get("slots") != null 
					&& ((Map)intentMap.get("slots")).get("secondLetter") != null
					&& ((Map)((Map)intentMap.get("slots")).get("secondLetter")).get("value") != null ) {
				secondLetter = (String) ((Map)((Map)intentMap.get("slots")).get("secondLetter")).get("value");
				finalSearchString += secondLetter;
				System.out.println("secondLetter :"+secondLetter);
				System.out.println("finalSearchString :"+finalSearchString);
			}
			if(intentMap.get("slots") != null 
					&& ((Map)intentMap.get("slots")).get("thirdLetter") != null
					&& ((Map)((Map)intentMap.get("slots")).get("thirdLetter")).get("value") != null ) {
				thirdLetter = (String) ((Map)((Map)intentMap.get("slots")).get("thirdLetter")).get("value");
				finalSearchString += thirdLetter;
				System.out.println("thirdLetter :"+thirdLetter);
				System.out.println("finalSearchString :"+finalSearchString);
			}
			if(intentMap.get("slots") != null 
					&& ((Map)intentMap.get("slots")).get("gender") != null
					&& ((Map)((Map)intentMap.get("slots")).get("gender")).get("value") != null ) {
				gender = (String) ((Map)((Map)intentMap.get("slots")).get("gender")).get("value");
				System.out.println("gender :"+gender);
			}
			if(finalSearchString != null) {
				intent = (String) intentMap.get("name");
			}
		}

		if(finalSearchString != null) {
			finalSearchString =finalSearchString.replaceAll("\\.","");

			System.out.println("finalSearchString after replace . :"+finalSearchString);
			names = this.getMatchingNames(finalSearchString, gender);
		}
		System.out.println("names :"+names);
		System.out.println("intent :"+intent);
		if(intent.equals("Welcome") || intent.equals("AMAZON.HelpIntent")) {
			finalMessage = "Welcome to baby names. To get all names starting from character <say-as interpret-as='characters'>aaa</say-as> you can say 'suggest names from <say-as interpret-as='characters'>a</say-as>'. So how can i help you?";
			isQuestion = true;
		} else if(intent.equals("suggestName")) {
			String namesFor = "baby";
			if(gender != null && !gender.equals("")){
				namesFor = gender;
			}
			if(finalSearchString != null && !finalSearchString.equals("") && names != null && !names.equals(""))
				finalMessage = "Here are the names for "+namesFor+" starting from character <say-as interpret-as='characters'>"+finalSearchString+"</say-as>. "+names;
			else
				finalMessage = "Baby names starting from character <say-as interpret-as='characters'>"+finalSearchString+"</say-as> not found.";
		} else if(intent.equals("AMAZON.CancelIntent") || intent.equals("AMAZON.StopIntent")) {
			finalMessage = "Thanks. buy Buy.";
		}
		System.out.println("finalMessage:"+finalMessage);
		outputSpeechMap.put("type", "SSML");
		outputSpeechMap.put("ssml", "<speak> "+finalMessage+" </speak>");
		responseMap.put("outputSpeech",outputSpeechMap);
		responseMap.put("shouldEndSession", !isQuestion);
		outputMap.put("response", responseMap);
		Map<String, Object> sessionAttributesMap = new HashMap<String, Object>();
		outputMap.put("sessionAttributes", sessionAttributesMap);
		System.out.println("Output:"+outputMap);
		return Response.status(200).entity(outputMap).build();
	}
	
	
	private String[] getNameArray(String gender) {
		if(gender == null || gender.equals("")) {
			return null;
		}
		if(gender.equals("male") || gender.equals("boy") ||  gender.equals("man")) {
			return MALE_NAMES;
		} else {
			return FEMALE_NAMES;
		}
	}
	private String getMatchingNames(String query, String gender) {
		String[] NAMES = this.getNameArray(gender);
		if(NAMES != null) {
			return this.findMatchingNames(query, NAMES);
		} else {
			return this.findMatchingNames(query, MALE_NAMES) + this.findMatchingNames(query, FEMALE_NAMES);
		}
	}
	
	private String findMatchingNames(String query, String[] NAMES) {
		List<String> matchedString = new ArrayList<String>();
		StringBuilder matchingNames = new StringBuilder();
		boolean matched = false;
		for(int i = 0; i < NAMES.length; i++) {
			if(NAMES[i].toUpperCase().startsWith(query.toUpperCase())) {
				matched = true;
				matchedString.add(NAMES[i]);
			} else if(matched) {
				break;
			}
		}	
		for(int i = 0; i < matchedString.size(); i++) {
			matchingNames.append("'");
			matchingNames.append(matchedString.get(i));
			matchingNames.append("', ");
		}
		return matchingNames.toString();
	}
	private static String[] MALE_NAMES = new String[] {"ABEL", "ABRAHAM", "ADAM", "ADAN", "ADEN", "ADITYA", "ADONIS", "ADRIAN", "ADRIEL", "Adyan", "AHARON", "AHMAD", "AHMED", "Ahnaf", "AHRON", "AIDAN", "AIDEN", "AKIVA", "ALAN", "ALBERT", "ALBERTO", "ALDO", "ALEC", "ALEJANDRO", "ALEKSANDER", "ALEX", "ALEXANDER", "ALEXIS", "Alfred", "ALFREDO", "ALI", "ALIJAH", "ALLAN", "ALLEN", "Alonso", "ALPHA", "ALSTON", "ALTER", "ALVIN", "AMADOU", "AMARE", "AMAR'E", "AMARI", "AMIR", "AMROM", "ANDERSON", "ANDRE", "ANDRES", "ANDREW", "ANDY", "ANGEL", "ANGELO", "ANGUS", "ANSON", "ANTHONY", "Anton", "ANTONIO", "ARCHER", "Arham", "ARI", "Arian", "ARIEL", "ARJUN", "Arlo", "ARMAAN", "ARMANDO", "ARMANI", "ARON", "ARTHUR", "ARTURO", "Arvin", "ARYAN", "Arye", "ARYEH", "ASHER", "ASHTON", "ATTICUS", "AUGUST", "AUGUSTUS", "AUSTIN", "AVERY", "AVI", "AVRAHAM", "AVROHOM", "AVRUM", "AXEL", "AYAAN", "AYAN", "AYDEN", "Aydin", "Azaan", "BARUCH", "BECKETT", "BEN", "BENJAMIN", "BENNETT", "BENSON", "BENTZION", "BENZION", "BERISH", "Berl", "Binyamin", "BINYOMIN", "BLAKE", "BORUCH", "BOUBACAR", "Bowen", "BRADLEY", "BRADY", "BRANDON", "BRAYAN", "BRAYDEN", "BRENDAN", "BRIAN", "BRODY", "Brooks", "BRUCE", "BRYAN", "BRYANT", "BRYCE", "BRYSON", "BYRON", "CADEN", "Caiden", "Cairo", "CALEB", "CALVIN", "Camden", "CAMERON", "CAMILO", "CAMREN", "CARLOS", "CARMELO", "CARMINE", "CARSON", "CARTER", "CAYDEN", "CESAR", "CHAD", "CHAIM", "CHANCE", "CHARLES", "CHARLIE", "CHASE", "CHESKEL", "CHESKY", "CHRIS", "CHRISTIAN", "CHRISTOPHER", "CODY", "COLE", "COLIN", "Colton", "CONNOR", "CONOR", "COOPER", "COREY", "Cormac", "CRISTIAN", "CRISTOFER", "CRISTOPHER", "CYRUS", "DAMANI", "DAMIAN", "DAMIEN", "Damon", "DANIEL", "DANNY", "DANTE", "DARIEL", "DARIUS", "DARREN", "DARWIN", "DASHIELL", "DAVID", "DEAN", "DECLAN", "Denis", "DENNIS", "DENZEL", "DERECK", "DEREK", "DERICK", "DERRICK", "Desmond", "DEVIN", "DEVON", "DIEGO", "Dilan", "DOMINIC", "DOMINICK", "DONOVAN", "DOV", "DOVID", "DREW", "Duvid", "DWAYNE", "DYLAN", "EASON", "EDDIE", "EDDY", "Eden", "EDGAR", "EDISON", "EDUARDO", "EDWARD", "EDWIN", "EFRAIM", "EITAN", "ELAN", "ELI", "Eliam", "ELIAN", "ELIAS", "ELIEZER", "ELIJAH", "ELIMELECH", "ELIYAHU", "ELLIOT", "ELLIOTT", "ELLIS", "ELUZER", "ELVIN", "ELVIS", "EMANUEL", "EMILIANO", "EMILIO", "EMMANUEL", "EMMETT", "ENRIQUE", "Enzo", "EPHRAIM", "ERIC", "ERICK", "ERIK", "ESTEBAN", "ETHAN", "EVAN", "EVERETT", "EZEKIEL", "EZEQUIEL", "EZRA", "EZRIEL", "FABIAN", "FARHAN", "Felipe", "FELIX", "FERNANDO", "FILIP", "FINN", "FRANCESCO", "Francis", "FRANCISCO", "FRANK", "FRANKLIN", "FREDDY", "FREDERICK", "GABRIEL", "GAEL", "Gary", "GAVIN", "GAVRIEL", "GEORGE", "Gerard", "GERARDO", "Giancarlo", "Gianluca", "GIOVANI", "GIOVANNI", "GIOVANNY", "Giuseppe", "GORDON", "GRAHAM", "GRANT", "GRAYSON", "GREGORY", "GREYSON", "GRIFFIN", "GUSTAVO", "HAMZA", "Hanley", "HAO", "HARRISON", "HARRY", "HARVEY", "HASSAN", "HAYDEN", "HECTOR", "HENRY", "HERSH", "HERSHEL", "HERSHY", "Hillel", "HUDSON", "HUGO", "HUNTER", "IAN", "IBRAHIM", "IBRAHIMA", "IKER", "ILAN", "Imran", "ISAAC", "ISAIAH", "Isaias", "ISHAAN", "ISHMAEL", "ISIAH", "ISMAEL", "ISRAEL", "ISSAC", "IVAN", "IZAIAH", "JACE", "JACK", "JACKSON", "JACKY", "JACOB", "JAD", "JADEN", "JADIEL", "JAEL", "JAHEIM", "Jahir", "JAHMIR", "JAIDEN", "JAIME", "Jair", "JAKE", "Jakub", "JALEN", "JAMAL", "Jamar", "JAMARI", "JAMEL", "JAMES", "JAMIR", "JANIEL", "JANUEL", "JARED", "JARIEL", "JASIAH", "JASON", "JASPER", "JAVIER", "Jax", "JAXON", "Jaxson", "JAY", "JAYCE", "Jayceon", "Jaycob", "JAYDEN", "JAYLEN", "Jaylin", "JAYREN", "JAYSON", "JEAN", "JEANCARLOS", "JEFFERSON", "JEFFREY", "JELANI", "JENCARLOS", "JEREMIAH", "JEREMIAS", "JEREMY", "JERMAINE", "JERRY", "JESSE", "JESUS", "JIA", "JIMMY", "JOAQUIN", "JOEL", "JOHAN", "JOHANN", "JOHN", "JOHNATHAN", "JOHNNY", "JONAH", "JONAS", "JONATHAN", "JORDAN", "JORGE", "JOSE", "JOSEPH", "JOSHUA", "JOSIAH", "JOSUE", "JUAN", "JUDAH", "JUDE", "JULIAN", "JULIEN", "JULIO", "JULIUS", "JUN", "JUNIOR", "JUSTICE", "JUSTIN", "Kabir", "KACPER", "KADEN", "KAI", "KAIDEN", "KALEB", "KAMARI", "KAMERON", "Karas", "KAREEM", "Karter", "KAYDEN", "KEITH", "KELVIN", "Kendrick", "KENNETH", "KENNY", "KEVIN", "KHALIL", "KIERAN", "Kimi", "KING", "KINGSLEY", "KINGSTON", "KIYAN", "KRISH", "KRISTIAN", "KYLE", "KYMANI", "Kyrie", "Laith", "LAMAR", "LANDON", "LARRY", "LAWRENCE", "LAZER", "LEANDRO", "Leibish", "LENNY", "LEO", "LEON", "Leonard", "LEONARDO", "LEONEL", "Leonidas", "LEV", "LEVI", "LIAM", "Lian", "Lincoln", "LIPA", "LOGAN", "LORENZO", "LOUIS", "LUCA", "LUCAS", "Lucien", "LUIS", "LUKA", "LUKAS", "LUKE", "Maddox", "MAHAMADOU", "Maison", "Major", "MAKAI", "MAKHI", "MAKSIM", "MALACHI", "MALCOLM", "MALIK", "MAMADOU", "MANUEL", "MARC", "Marcel", "Marcelo", "MARCO", "MARCOS", "MARCUS", "MARIO", "MARK", "MARLON", "MARQUIS", "MARTIN", "MARVIN", "MASON", "Massimo", "MATEO", "MATHEW", "MATHIAS", "MATIAS", "MATTEO", "MATTHEW", "MAURICE", "MAURICIO", "MAX", "MAXIM", "MAXIMILIAN", "MAXIMILIANO", "MAXIMO", "MAXIMUS", "MAXWELL", "MAYER", "Mayson", "Md", "MEILECH", "MEIR", "MEKHI", "MELVIN", "MENACHEM", "MENASHE", "MENDEL", "MENDY", "MESSIAH", "MEYER", "MICAH", "MICHAEL", "MIGUEL", "MIKE", "Milan", "MILES", "MILO", "Misael", "MOHAMED", "MOHAMMAD", "MOHAMMED", "MOISES", "MOISHE", "Mordche", "MORDECHAI", "MORRIS", "MOSES", "MOSHE", "Mouhamed", "MOUSSA", "MUHAMMAD", "MUSA", "MUSTAFA", "MYLES", "NACHMAN", "NAFTALI", "NAFTULI", "NANA", "NASIR", "NATHAN", "NATHANAEL", "NATHANIEL", "Nazir", "NEHEMIAH", "NEIL", "NELSON", "NEYMAR", "NICHOLAS", "NICO", "NICOLAS", "NIGEL", "NIKHIL", "NIKITA", "NIKOLAS", "NM", "NOAH", "NOAM", "NOEL", "NOLAN", "NOSSON", "OLIVER", "OMAR", "OMARI", "Ori", "Orion", "Orlando", "OSCAR", "Otto", "OUMAR", "OUSMANE", "OWEN", "PABLO", "PARKER", "PATRICK", "PAUL", "PEDRO", "PETER", "PEYTON", "PHILIP", "PINCHAS", "PINCHUS", "PRESTON", "PRINCE", "Princeton", "QUINCY", "QUINN", "RAFAEL", "RANDY", "RAPHAEL", "RAUL", "RAYAN", "RAYMOND", "RAYYAN", "REED", "REHAN", "REID", "Reuben", "REUVEN", "Rhys", "RICARDO", "RICHARD", "RICKY", "RILEY", "ROBERT", "ROBERTO", "ROCCO", "RODNEY", "RODRIGO", "ROGER", "ROHAN", "ROMAN", "ROMEO", "RONALD", "RONAN", "RORY", "ROWAN", "ROY", "ROYCE", "RUBEN", "RYAN", "RYDER", "SALVATORE", "SAM", "SAMI", "SAMIR", "SAMUEL", "SANTIAGO", "SANTINO", "SAUL", "Savion", "Sawyer", "SEAN", "SEBASTIAN", "SEKOU", "SERGIO", "SETH", "SHALOM", "SHANE", "SHAUL", "SHAWN", "SHAYA", "SHAYAAN", "SHAYAN", "SHEA", "SHIA", "SHIMON", "SHLOIME", "SHLOIMY", "SHLOMA", "SHLOME", "SHLOMO", "SHMIEL", "SHMUEL", "SHNEUR", "SHOLOM", "Shraga", "SHULEM", "SIDNEY", "SILAS", "SIMCHA", "SIMON", "SINCERE", "SKYLER", "SOLOMON", "SPENCER", "STANLEY", "STEPHEN", "STEVE", "STEVEN", "SUBHAN", "SYED", "TAYLOR", "TENZIN", "TERRELL", "TERRENCE", "TERRY", "THEO", "THEODORE", "Thiago", "THOMAS", "TIMOTHY", "TOMAS", "TONY", "TRAVIS", "TRISTAN", "TROY", "TYLER", "TZVI", "URIEL", "USHER", "Valentino", "VICTOR", "VIHAAN", "VINCENT", "VINCENZO", "Walter", "WESLEY", "Weston", "WILLIAM", "WILSON", "WINSTON", "Wolf", "WYATT", "Xander", "XAVIER", "YAAKOV", "YADIEL", "YAEL", "YAHIR", "Yahya", "YAIR", "YAKOV", "YANDEL", "YANIEL", "Yanky", "YARIEL", "YASEEN", "Yassin", "YECHEZKEL", "YECHIEL", "YEHOSHUA", "YEHUDA", "YEHUDAH", "YERIK", "Yeshaya", "YIDA", "YIDEL", "YISRAEL", "YISROEL", "YITZCHAK", "YITZCHOK", "YOEL", "YONA", "YONAH", "YOSEF", "YOSSI", "YOUSEF", "YOUSSEF", "YUSUF", "Zachariah", "ZACHARY", "Zahir", "Zaiden", "ZAIN", "ZAIRE", "ZALMEN", "Zamir", "Zane", "ZAYAN", "ZAYDEN", "ZEV", "ZION", "ZYAIRE"};
	private static String[] FEMALE_NAMES = new String[] {"Aarya", "ABBY", "ABIGAIL", "Abrielle", "Abril", "ADA", "ADDISON", "ADELAIDE", "ADELE", "Adelina", "ADELINE", "Adelyn", "ADINA", "ADRIANA", "ADRIANNA", "AHUVA", "AICHA", "AILEEN", "AIMEE", "AISHA", "Aissata", "AISSATOU", "AIZA", "ALANA", "ALANI", "Alanis", "Alanna", "ALEENA", "ALEJANDRA", "Aleksandra", "ALESSANDRA", "ALESSIA", "ALEXA", "ALEXANDRA", "ALEXANDRIA", "ALEXIA", "ALEXIS", "ALICE", "ALICIA", "ALINA", "ALISA", "ALISHA", "ALISON", "Alissa", "ALISSON", "ALIYAH", "ALIZA", "ALLISON", "ALLYSON", "ALMA", "ALONDRA", "ALYSON", "ALYSSA", "AMALIA", "AMANDA", "AMANI", "Amara", "AMAYA", "AMBER", "AMBERLY", "AMELIA", "AMELIE", "Amia", "AMINA", "AMINATA", "AMIRA", "AMIRAH", "Amiya", "AMIYAH", "AMY", "ANA", "Anabel", "Anabella", "Anabelle", "ANAIS", "ANALIA", "Ananya", "ANASTASIA", "ANAYA", "Anayah", "ANDREA", "ANGEL", "ANGELA", "ANGELICA", "ANGELINA", "Angeline", "ANGELIQUE", "ANGELY", "ANGIE", "ANIKA", "Anisa", "ANISHA", "ANIYA", "ANIYAH", "ANNA", "ANNABEL", "Annabella", "ANNABELLE", "Annalise", "ANNE", "ANNIE", "Annika", "ANTONIA", "ANYA", "APRIL", "Arabella", "ARELY", "ARIA", "ARIANA", "ARIANNA", "ARIANNY", "ARIEL", "ARIELA", "ARIELLA", "ARIELLE", "ARISHA", "ARYA", "ASHLEY", "ASHLY", "Asia", "ATARA", "ATHENA", "AUBREE", "AUBREY", "AUDREY", "AURORA", "AUTUMN", "AVA", "AVERY", "AVIGAIL", "Avital", "AVIVA", "AVRIL", "AWA", "AYA", "Ayala", "AYANNA", "AYESHA", "AYLA", "AYLEEN", "AYLIN", "Azaria", "Aziza", "BAILA", "BARBARA", "Basya", "BATSHEVA", "BATYA", "BEATRICE", "Beatrix", "BELLA", "BIANCA", "BINTOU", "BLAKE", "Blessing", "BLIMA", "BLIMY", "BONNIE", "BRACHA", "BREINDY", "BRENDA", "Bria", "BRIANA", "BRIANNA", "BRIANNY", "BRIDGET", "BRIELLE", "Brigitte", "BRITNEY", "BRITTANY", "BROOKE", "BROOKLYN", "BRUCHA", "BRUCHY", "BRYANNA", "BRYNN", "CAITLIN", "Cali", "CAMERON", "CAMILA", "CAMILLA", "CAMILLE", "Carina", "Carla", "Carly", "CARMEN", "CAROLINA", "CAROLINE", "CASEY", "CASSANDRA", "Cassidy", "Cataleya", "CATALINA", "CATHERINE", "Cecelia", "CECILIA", "CELESTE", "CELIA", "Celine", "CHANA", "Chanel", "CHANIE", "CHANY", "CHARLIE", "CHARLOTTE", "CHAVA", "CHAVY", "CHAYA", "CHELSEA", "CHEYENNE", "Chiara", "CHLOE", "CHRISTINA", "CHRISTINE", "CHRISTY", "CINDY", "CLAIRE", "CLARA", "Claudia", "Clementine", "COLETTE", "CONNIE", "CORA", "CRISTINA", "CRYSTAL", "CYNTHIA", "DAHLIA", "DAISY", "DAKOTA", "Daleyza", "DALIA", "DAMARIS", "DANA", "DANIELA", "DANIELLA", "DANIELLE", "DANNA", "DAPHNE", "DAYANA", "DEBORAH", "DELILAH", "DENISE", "DESTINY", "DEVORA", "DEVORAH", "DIANA", "DINA", "DIYA", "DORIS", "DULCE", "DYLAN", "EDEN", "Edith", "EGYPT", "EILEEN", "ELAINE", "ELEANOR", "ELENA", "Eleni", "ELIANA", "ELIANNA", "ELIANNY", "ELINA", "ELISA", "ELISE", "ELISHEVA", "ELIZA", "ELIZABETH", "ELLA", "ELLE", "ELLIANA", "ELLIE", "ELOISE", "ELSA", "Emaan", "Emani", "EMELY", "EMERSON", "EMILIA", "EMILY", "EMMA", "Emmeline", "ERICA", "ERIKA", "ERIN", "Eshaal", "ESHAL", "ESME", "ESMERALDA", "ESSENCE", "ESTELLE", "ESTER", "ESTHER", "ESTRELLA", "ESTY", "ETTY", "Eunice", "EVA", "EVANGELINE", "EVE", "EVELYN", "Everly", "Evie", "FAIGA", "FAIGY", "FAITH", "FANTA", "Farah", "FATIMA", "FATOU", "FATOUMATA", "FERNANDA", "FINLEY", "FIONA", "FRADEL", "FRADY", "FRAIDY", "FRANCES", "FRANCESCA", "Freya", "FRIMET", "GABRIELA", "GABRIELLA", "GABRIELLE", "GEMMA", "GENESIS", "GENEVIEVE", "GEORGIA", "GERALDINE", "GIA", "GIADA", "GIANNA", "GIOVANNA", "GISELLE", "GITTEL", "GITTY", "GIULIA", "GIULIANA", "GOLDA", "Goldie", "GOLDY", "GRACE", "GRETA", "GUADALUPE", "HADASSA", "HADASSAH", "HADLEY", "HAFSA", "HAILEY", "Hailie", "HALEY", "HANA", "HANNA", "HANNAH", "HAREEM", "Harlow", "HARMONY", "HARPER", "HAWA", "HAYLEE", "HAYLEY", "HAZEL", "HEAVEN", "HEIDI", "HEIDY", "HELEN", "HELENA", "Henchy", "HENNY", "HINDA", "HINDY", "Hope", "IDY", "ILANA", "IMANI", "Inaaya", "Inaya", "INES", "INGRID", "IRENE", "IRIS", "ISABEL", "ISABELA", "ISABELLA", "ISABELLE", "ISIS", "ISLA", "ITZEL", "IVANNA", "IVY", "IZABELLA", "JACQUELINE", "JADA", "JADE", "JAELYN", "JAELYNN", "JALIYAH", "JAMIE", "JANA", "Janae", "JANE", "JANELLE", "JANICE", "JANIYA", "JANIYAH", "JANNAT", "JASLENE", "JASMIN", "Jasmina", "JASMINE", "JAYDA", "JAYLA", "JAYLAH", "Jaylee", "JAYLEEN", "JAYLENE", "JAYLIN", "JAYLYN", "JAZLYN", "JAZMIN", "JAZMINE", "JAZZLYN", "JENNA", "JENNIFER", "JENNY", "JESSICA", "JESSIE", "JIA", "JIMENA", "JOANNA", "JOCELYN", "JOHANNA", "JOLIE", "JOLIN", "JORDAN", "JORDYN", "JOSELYN", "JOSEPHINE", "Journee", "Journey", "JOY", "JOYCE", "JUDY", "JULIA", "JULIANA", "JULIANNA", "JULIE", "JULIET", "JULIETTE", "JULISSA", "JUNE", "Juniper", "Kadiatou", "KAELYN", "KAI", "KAILEY", "KAILYN", "KAITLYN", "Kali", "KALIYAH", "KAMILA", "KAMIYAH", "KAREN", "KARINA", "KARLA", "KASSANDRA", "KATE", "KATELYN", "KATELYNN", "Katerina", "KATHERINE", "KATHRYN", "KATIE", "KAYA", "KAYLA", "KAYLEE", "KAYLEEN", "KAYLEIGH", "KAYLIE", "KAYLIN", "Keila", "KEILY", "KEIRA", "KELLY", "KELSEY", "KENDRA", "KENNEDY", "KENYA", "KEYLA", "Khadija", "Khadijah", "KHLOE", "KIARA", "KIMBERLY", "KIMORA", "KIRA", "Kourtney", "Kristen", "KRYSTAL", "KYLA", "KYLEE", "KYLIE", "LAILA", "Lailah", "LANA", "LARA", "LAURA", "LAUREN", "Lauryn", "Layan", "LAYLA", "LEA", "LEAH", "Leanna", "LEELA", "LEILA", "LEILANI", "LENA", "Leona", "LEORA", "LESLEY", "LESLIE", "LESLY", "LEYLA", "LIA", "LIANA", "LIBA", "LIBBY", "LILA", "LILAH", "Lilian", "LILIANA", "LILLIAN", "LILLY", "LILY", "LINA", "LINDA", "LINDSAY", "Lisa", "LITZY", "LIV", "LIVIA", "LIZ", "LIZBETH", "Logan", "LOLA", "LONDON", "LONDYN", "LOUISA", "Louise", "LUCIA", "Luciana", "Lucille", "LUCY", "LUNA", "LUZ", "LYDIA", "LYLA", "LYRIC", "MACKENZIE", "MADELEINE", "MADELINE", "MADELYN", "MADISON", "Madisyn", "MAE", "MAEVE", "Magaly", "MAGGIE", "Maia", "Maisie", "Maite", "MAKAYLA", "MAKENZIE", "MALAK", "Malaysia", "MALIA", "MALIYAH", "MALKA", "MALKY", "MANDY", "MANHA", "MARGARET", "Margaux", "MARGOT", "MARIA", "MARIAH", "MARIAM", "MARIAMA", "MARIANA", "Marielle", "MARILYN", "MARINA", "MARISOL", "Marjorie", "MARY", "MARYAM", "MATILDA", "MAYA", "MCKENZIE", "MEGAN", "MELANIE", "MELANY", "MELINA", "MELISSA", "MELODY", "Menucha", "MIA", "MIAH", "MICHAELA", "MICHAL", "MICHELLE", "MIKAELA", "MIKAYLA", "MILA", "MILAGROS", "MILAN", "Milana", "MILENA", "MILEY", "MINA", "MINDY", "Mira", "MIRACLE", "MIRANDA", "MIREL", "MIRI", "MIRIAM", "MOLLY", "Monica", "Monserrat", "Montserrat", "MORGAN", "Mushka", "MYA", "NADIA", "NAHLA", "NANA", "NANCY", "NAOMI", "Nashla", "Nashley", "NATALIA", "NATALIE", "NATALY", "NATASHA", "NATHALIA", "NATHALIE", "NATHALY", "NAYELI", "NECHAMA", "NEVAEH", "NIA", "NICOLE", "NICOLETTE", "NINA", "NOA", "Noelle", "NOEMI", "NOOR", "NORA", "Norah", "Nova", "Nyah", "NYLA", "NYLAH", "OLIVE", "OLIVIA", "OUMOU", "PAIGE", "Pamela", "PAOLA", "PARIS", "PARKER", "PAULA", "Payton", "PEARL", "PENELOPE", "PEREL", "PERL", "PERLA", "PERRY", "PESSY", "PEYTON", "PHOEBE", "PHOENIX", "PIPER", "POLINA", "Poppy", "Precious", "PRINCESS", "Priscilla", "Queena", "QUEENIE", "QUINN", "RACHEL", "RAINA", "RAIZEL", "RAIZY", "RAQUEL", "REAGAN", "REBECCA", "REEM", "REESE", "Reizy", "Rena", "RENEE", "RIFKA", "RIFKY", "RIHANNA", "RILEY", "RIVKA", "RIVKY", "Riya", "ROCHEL", "Roiza", "ROIZY", "Romy", "ROSA", "ROSE", "ROSELYN", "ROSIE", "Rowan", "RUBY", "Ruchel", "RUCHY", "RUTH", "RYAN", "Sabina", "SABRINA", "SADE", "SADIE", "Safa", "SAGE", "Saige", "SALMA", "SALOME", "SAMANTHA", "SAMARA", "Samira", "SAMIYA", "SAMIYAH", "SANAA", "SANAI", "SANDRA", "SANIYA", "SANIYAH", "SARA", "SARAH", "SARAI", "SARIAH", "Sarina", "SASHA", "SAVANNA", "SAVANNAH", "SCARLET", "SCARLETT", "SELENA", "SELINA", "SELMA", "SERENA", "SERENITY", "SHAINA", "SHAINDEL", "SHAINDY", "SHANIA", "SHANIYA", "SHARON", "SHAYLA", "Shayna", "SHERLYN", "SHERRY", "SHEVY", "SHIFRA", "SHILOH", "SHIRA", "SHIRLEY", "SHOSHANA", "Shreya", "SIENA", "SIENNA", "SIMA", "SIMI", "SIMONE", "SKYE", "SKYLA", "SKYLAH", "SKYLAR", "Skyler", "SLOANE", "SOFIA", "SONIA", "SOPHIA", "SOPHIE", "SORAYA", "STACEY", "STACY", "STELLA", "STEPHANIE", "STEPHANY", "SUMMER", "Sunny", "SURI", "SURY", "SYDNEY", "SYEDA", "SYLVIA", "SYLVIE", "SYMPHONY", "TABITHA", "TALIA", "TAMAR", "TAMIA", "TANISHA", "TARAJI", "TASNIM", "TATIANA", "TAYLOR", "TENZIN", "Tess", "TESSA", "Thea", "TIANA", "TIANNA", "TIFFANY", "TINA", "TOBY", "TORI", "Tova", "Trany", "TRINITY", "TZIPORA", "TZIPORAH", "TZIPPY", "TZIVIA", "VALENTINA", "VALERIA", "VALERIE", "Valery", "VANESSA", "VERA", "VERONICA", "VERONIKA", "VICKY", "VICTORIA", "VIOLET", "Violeta", "VIRGINIA", "VIVIAN", "VIVIANA", "VIVIENNE", "WENDY", "WILLA", "WINNIE", "Winter", "WYNTER", "XIMENA", "XIN", "YACHET", "YAEL", "YAMILET", "YAMILETH", "YARA", "YARETZI", "Yasmin", "Yasmina", "YASMINE", "YEHUDIS", "YESENIA", "YIDES", "YITTA", "YITTY", "YOCHEVED", "YU", "ZAHARA", "ZAHRA", "ZAINAB", "ZANIYAH", "ZARA", "ZARIA", "ZARIAH", "Zelda", "Zendaya", "ZISSY", "ZOE", "ZOEY", "Zoya", "ZURI"};
}
