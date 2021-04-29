package controller;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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

	@FXML
	public void initialize() {

		libelle.setPromptText("Objet de la réunion");
		heureDebut.setPromptText("Heure de début au format HH:MM:SS");
		heureFin.setPromptText("Heure de fin au format HH:MM:SS");

		heureDebut.focusedProperty().addListener((arg0, oldValue, newValue) -> {
			if (!newValue) { // when focus lost
				if (!heureDebut.getText().matches("^(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)$")) {
					// when it not matches the pattern (HH:MM:SS)
					heureDebut.setText("");
				}
			}
		});

		heureFin.focusedProperty().addListener((arg0, oldValue, newValue) -> {
			if (!newValue) { // when focus lost
				if (!heureFin.getText().matches("^(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)$")) {
					// when it not matches the pattern (HH:MM:SS)
					heureFin.setText("");
				}
			}
		});

		heureDebut.textProperty().addListener(event -> {
			heureDebut.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), !heureDebut.getText().isEmpty()
					&& !heureDebut.getText().matches("^(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)$"));
		});

		heureFin.textProperty().addListener(event -> {
			heureFin.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), !heureFin.getText().isEmpty()
					&& !heureFin.getText().matches("^(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)$"));
		});
	}

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

		String heureMinEntree = heureDebut.getText();
		String heureMaxEntree = heureFin.getText();
		String dateDebutFichier = "";
		String dateFinFichier = "";
		
		// limit periods to given time interval
		if (selectedFile != null) {
			dateDebutFichier = parcours.getDate() + " à " + heureMinEntree;
			dateFinFichier = parcours.getDate() + " à " + heureMaxEntree;
		}

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

		// Start processing only if inputs are valid
		if (validation()) {
			teamsProcessor = new TEAMSProcessor(selectedFile, outputFile, dateDebutFichier, dateFinFichier,
					libelle.getText(), sortBy);
			teamsProcessor.toHTMLFile(visibilityId, visibilityNom, visibilityPlanning);
			
			// Display result
			ResultatController r = new ResultatController();
			r.afficherResultat();
		}

	}

	// display an error if a required input is empty
	public boolean validation() {
		if (selectedFile == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Saisie incorrecte");
			alert.setHeaderText(null);
			alert.setContentText("Veuillez sélectionner un fichier d'entrée");
			alert.showAndWait();
			return false;
		}
		if (outputFile == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Saisie incorrecte");
			alert.setHeaderText(null);
			alert.setContentText("Veuillez renseigner le fichier de sortie");
			alert.showAndWait();
			return false;
		}
		if (libelle.getText().isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Saisie incorrecte");
			alert.setHeaderText(null);
			alert.setContentText("Veuillez renseigner l'objet de la réunion");
			alert.showAndWait();
			return false;
		}
		if (heureDebut.getText().isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Saisie incorrecte");
			alert.setHeaderText(null);
			alert.setContentText("Veuillez renseigner l'heure de début");
			alert.showAndWait();
			return false;
		}
		if (heureFin.getText().isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Saisie incorrecte");
			alert.setHeaderText(null);
			alert.setContentText("Veuillez renseigner l'heure de fin");
			alert.showAndWait();
			return false;
		}
		return true;
	}

}
