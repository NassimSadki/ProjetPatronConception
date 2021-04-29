package appli;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class ParcoursCSV {
	
    private LinkedList<String> _attlist = null;
    private ArrayList<LocalDateTime> listeDate;
    private LocalDateTime heureMin = null;
    private LocalDateTime heureMax = null;
    private String date = null;

	public ParcoursCSV(File _file) {
		// load CSV file
        var teamsFile = new TEAMSAttendanceList(_file);
        
        // filter to extract data
        var lines = teamsFile.get_attlist();
        if (lines != null) {
            // convert lines in periods
        	_attlist = teamsFile.get_attlist();
            processList();
        }
	}
	
	private void processList() {
        if (this._attlist != null) {
        	
        	this.listeDate = new ArrayList<LocalDateTime>();
  
            // DESIGN PATTERN : ITERATOR
            Iterator<String> element = this._attlist.iterator();
            // first line unused
            element.next();
            // process all lines
            while (element.hasNext()) {
                String input = element.next();

                String[] infos = input.split("\t");
         
                if (infos.length==1) {
                	String[] liste = infos[0].split(",");
              
                    String instant = liste[2];
                    this.listeDate.add(TEAMSDateTimeConverter.StringToLocalDateTime(instant));
                }
            }
            
            // get min and max from saved hours
            // DESIGN PATTERN : ITERATOR
            Iterator<LocalDateTime> parcoursListeDate = this.listeDate.iterator();
            while(parcoursListeDate.hasNext()) {
            	LocalDateTime current = parcoursListeDate.next();
            	if(heureMin == null) {
                	heureMin = current;
                }
                else {
                	if(heureMin.isAfter(current)) {
                		heureMin = current;
                	}
                }
            	if(heureMax == null) {
                	heureMax = current;
                }
                else {
                	if(heureMax.isBefore(current)) {
                		heureMax = current;
                	}
                }
            }
           
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            date = heureMin.format(formatter);
        }
    }
	
	public LocalDateTime getHeureMin() {
		return this.heureMin;
	}
	
	public LocalDateTime getHeureMax() {
		return this.heureMax;
	}
	
	public String getDate() {
		return this.date;
	}

}
