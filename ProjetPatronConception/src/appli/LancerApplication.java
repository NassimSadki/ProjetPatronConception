package appli;

import controller.Main;
import javafx.application.Application;


// On obtient une erreur de runtime lorsqu'on essaye de lancer la classe Main directement, il faut passer par cette classe
// intermédiaire pour y remédier
public class LancerApplication {

	public static void main(String[] args) {
		Application.launch(Main.class);
	}

}
