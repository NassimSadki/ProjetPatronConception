package appli;

import controller.Main;
import javafx.application.Application;


// On obtient une erreur de runtime lorsqu'on essaye de lancer la classe Main directement, il faut passer par cette classe
// interm�diaire pour y rem�dier
public class LancerApplication {

	public static void main(String[] args) {
		Application.launch(Main.class);
	}

}
