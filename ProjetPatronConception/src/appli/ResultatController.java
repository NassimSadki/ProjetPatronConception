package appli;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ResultatController {
	
	@FXML
	private Text fichierCree;
		
	public void afficherResultat() {
    	Main m = new Main();
    	try {
			m.resultat();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
		
    @FXML
    public void initialize() {
    	fichierCree.setText(MainController.outputFile.getPath());
    }
    
    public void quitter() {
    	Main.getPrimaryStage().close();
    }
    
    @SuppressWarnings("unused")
	public void recommencer() {
    	// remove the existing people objects
    	var allpeople = MainController.teamsProcessor.get_allpeople();
        for (People people : allpeople) {
        	people = null;
        }
        
        // restart the GUI
        Main.getPrimaryStage().close();
        Main m = new Main();
        try {
			m.startApplication(new Stage());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
}
