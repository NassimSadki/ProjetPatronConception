package appli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class TEAMSProcessor {

    private Collection<People> _allpeople = null;
    private String _fileName;
    private String _startTime;
    private String _endTime;
    private String libelle;
    private File outputFile;

    public TEAMSProcessor(File _file, File outputFile, String _start, String _stop, String libelle, int sortBy) {
        /*
         csv file to read
         output file
         start time of the course
         end time of the course
         course name
         sort option
        */
    	
        this._startTime = _start;
        this._endTime = _stop;
        this.libelle = libelle;
        this.outputFile = outputFile;

        // load CSV file
        this._fileName = _file.getName();
        var teamsFile = new TEAMSAttendanceList(_file);
        
        // filter to extract data for each people
        var lines = teamsFile.get_attlist();
        if (lines != null) {
            // convert lines in data structure with people & periods
            var filter = new TEAMSAttendanceListAnalyzer(lines);
            // cut periods before start time and after end time
            filter.setStartAndStop(_start, _stop);
            
            List<People> peopleByDuration = new ArrayList<>(filter.get_peopleList().values());
            // sort by duration
            if(sortBy == 0) {
            	peopleByDuration.get(0).setSort(0);
            }
            // sort by id
            else if(sortBy == 1) {
            	peopleByDuration.get(0).setSort(1);
            }
            // sort by name
            else if(sortBy == 2) {
            	peopleByDuration.get(0).setSort(2);
            }
            
            // DESIGN PATTERN STRATEGY
            Collections.sort(peopleByDuration);
            // init the people collection
            this._allpeople = peopleByDuration;//filter.get_peopleList().values();
            
            
        }
    }

    public Collection<People> get_allpeople() {
        return _allpeople;
    }

    // DESIGN PATTERN STRATEGY
    public String toHTMLCode(boolean visibilityId, boolean visibilityNom, boolean visibilityPlanning) {
    	String htmlVisibilityPlanning = 
    			"\n" +
		        "<h2> Durées de connexion</h2>\n" +
		        "\n" +
		        "<p> Pour chaque personne ci-dessous, on retrouve son temps total de connexion sur la plage déclarée du cours, ainsi qu'un graphe qui indique les périodes de connexion (en vert) et d'absence de connexion (en blanc). En pointant la souris sur une zone, une bulle affiche les instants de début et de fin de période. \n" +
		        "</p>";
    	if(visibilityPlanning == false) {
    		htmlVisibilityPlanning = 
    			"\n" +
    			"<h2> Liste des connectés :</h2>\n";
    	}
    	
        String html = "<!DOCTYPE html> \n <html lang=\"fr\"> \n <head> \n <meta charset=\"utf-8\"> ";
        html += "<title> Attendance Report </title> \n <link rel=\"stylesheet\" media=\"all\" href=\""+System.getProperty("user.dir")+"\\visu.css\"> \n";
        html += "</head> \n <body> \n ";
        html += "<h1> Rapport de connexion </h1>\n" +
                "\n" +
                "<div id=\"blockid\">\n" +
                "<table>\n" +
                "\t<tr>\n" +
                "\t\t<th> Date : </th>\n" +
                "\t\t<td> " + this._allpeople.iterator().next().getDate() + " </td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<th> Heure début : </th>\n" +
                "\t\t<td> " + this._startTime + " </td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<th> Heure fin : </th>\n" +
                "\t\t<td> " + this._endTime + " </td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<th> Cours : </th>\n" +
                "\t\t<td> "+ this.libelle +" </td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<th> Fichier analysé : </th>\n" +
                "\t\t<td> " + this._fileName + " </td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<th> Nombre de connectés : </th>\n" +
                "\t\t<td> " + this._allpeople.size() + "  </td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "</div>\n" +
                htmlVisibilityPlanning;
        html += "<div id=\"blockpeople\"> ";

        // DESIGN PATTERN : ITERATOR
        Iterator<People> parcoursPeople = this._allpeople.iterator();
        while(parcoursPeople.hasNext()) {
        	People people = parcoursPeople.next();
        	people.setVisibility(visibilityId, visibilityNom, visibilityPlanning);
        	html += people.getHTMLCode();
        }

	    html += "</div> \n </body> \n </html>";
        return html;
    }
    
    public void toHTMLFile(boolean visibilityId, boolean visibilityNom, boolean visibilityPlanning) {
    	// first, we empty the file (useful if it already exists)
    	PrintWriter pw;
		try {
			pw = new PrintWriter(outputFile.getPath());
			pw.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
    	
		// then, we write the new content
    	try (var fr = new FileWriter(outputFile, StandardCharsets.UTF_8, true)) {
    		System.out.println("File created: " + outputFile.getName());
            fr.write(toHTMLCode(visibilityId, visibilityNom, visibilityPlanning));
        } catch (IOException e) {
        	e.printStackTrace();
		}
    }
}
