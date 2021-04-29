package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import appli.ParcoursCSV;
import appli.TEAMSProcessor;

public class MainController {
	@FXML
	private Text nomFichier;
	@FXML
	private Text dateFichier;
	@FXML
	private Text heureMinFichier;
	@FXML
	private Text heureMaxFichier;
	@FXML
	private TextField libelle;
	@FXML
	private TextField heureDebut;
	@FXML
	private TextField heureFin;
	@FXML
	private ToggleGroup triSortie;
	@FXML
	private CheckBox sansId;
	@FXML
	private CheckBox sansNom;
	@FXML
	private CheckBox sansPlanning;

	static TEAMSProcessor teamsProcessor;
	private File selectedFile;
	private ParcoursCSV parcours;
	static File outputFile;

	public void infosFichier(ActionEvent actionEvent) {

		FileChooser fileChooser = new FileChooser();
		selectedFile = fileChooser.showOpenDialog(null);

		if (selectedFile != null) {
			nomFichier.setText("" + selectedFile.getName());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			dateFichier.setText("" + sdf.format(selectedFile.lastModified()));

			parcours = new ParcoursCSV(selectedFile);
			LocalDateTime heureMin = parcours.getHeureMin();
			LocalDateTime heureMax = parcours.getHeureMax();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
			String heureMin2 = heureMin.format(formatter);
			String heureMax2 = heureMax.format(formatter);

			heureMinFichier.setText("" + heureMin2);
			heureMaxFichier.setText("" + heureMax2);

		}
	}

	public void genererSortie() {

		// process the file, and limit periods to given time interval
		String heureMinEntree = heureDebut.getText();
		String heureMaxEntree = heureFin.getText();
		String test1 = parcours.getDate() + " à " + heureMinEntree;
		String test2 = parcours.getDate() + " à " + heureMaxEntree;
		int sortBy = 0;
		RadioButton selectedRadioButton = (RadioButton) triSortie.getSelectedToggle();
		if (selectedRadioButton != null) {
			if (selectedRadioButton.getText().equals("par ID")) {
				sortBy = 1;
			} else if (selectedRadioButton.getText().equals("par Nom")) {
				sortBy = 2;
			}
		}
		
		boolean visibilityId = true;
		boolean visibilityNom = true;
		boolean visibilityPlanning = true;
		if (sansId.isSelected()) {
			visibilityId = false;
		}
		if (sansNom.isSelected()) {
			visibilityNom = false;
		}
		if (sansPlanning.isSelected()) {
			visibilityPlanning = false;
		}

		FileChooser fileChooser = new FileChooser();

		// Set extension filter for text files
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("HTML File", "*.html");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		outputFile = fileChooser.showSaveDialog(Main.getPrimaryStage());

		teamsProcessor = new TEAMSProcessor(selectedFile, outputFile, test1, test2, libelle.getText(), sortBy);

		teamsProcessor.toHTMLFile(visibilityId, visibilityNom, visibilityPlanning);
		ResultatController r = new ResultatController();
		r.afficherResultat();
	}

}
