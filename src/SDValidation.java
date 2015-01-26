import java.io.*;
import java.text.ParseException;
import java.util.*;

public class SDValidation {
    private Scanner s;
    private Integer slotLength, n, daysPerCycle;
	Integer[] x, y;

    
	public SDValidation(String fileLocation) throws IOException, ParseException{	
		OpenFile(fileLocation);
		ReadFile();
		CloseFile();
	}

	protected void OpenFile(String fileName){
		try {
			s = new Scanner(new File(fileName)); 
		}
		catch (Exception e){
			System.out.println("File could not find");
		}
	}

	protected void ReadFile() throws IOException, ParseException{
		String strLine;
		String[] temp, tempStart;		
		
		while (s.hasNextLine()) {			  
			strLine = s.nextLine();
			strLine.trim();
			if (strLine.contains("#MinuteInterval:")){
				strLine = s.nextLine();
				strLine.trim();	
				slotLength = Integer.parseInt(strLine);
				//System.out.print("SlotLength: " + slotLength + "  ");
			}
			
			if (strLine.contains("#DaysPerCycle:")){
				strLine = s.nextLine();
				strLine.trim();	
				daysPerCycle = Integer.parseInt(strLine);
				n = 24 * daysPerCycle * 60 / slotLength;
				x = new Integer[n];
				y = new Integer[n];

				//System.out.print("DaysPerCycle: " + daysPerCycle + "  " );
			}
			
		
			if (strLine.contains("#Requirements:")){
				strLine = s.nextLine();
				strLine.trim();	
				temp = strLine.split(" ");
				for (int i=0; i < temp.length; i++){
					x[i] = Integer.parseInt(temp[i]);
					y[i] = 0;
					//System.out.println("x["+ i + "]: " + x[i] );
				}
			}
			int nrOfShift =0;
			if (strLine.contains("#Shift Start Length Duties:")){
				while(s.hasNextLine()){
					strLine = s.nextLine();
					strLine.trim();	
					temp = strLine.split(" ");
					nrOfShift++;
					temp[2].trim();	
					tempStart = temp[3].split(":");

					for (int j = 0; j < daysPerCycle; j++){
						for (int i = 0; i< Integer.parseInt(temp[7]) / slotLength; i++){
							y[(j * 24 * 60 / slotLength + ((Integer.parseInt(tempStart[0]) * 60 + Integer.parseInt(tempStart[1])) / slotLength + i))%n] += Integer.parseInt(temp[14 + 2 * j]);
						}
					}
				}
				int overCover = 0, underCover = 0;
				for (int i = 0; i < n; i++ ){
					if(y[i] > x[i]){
						System.out.println("Overcover [" + i + "] = " +  (y[i] - x[i]) );
						overCover++;
					}
					if(y[i] < x[i]){
						System.out.println("Undercover [" + i + "] = " +  (- y[i] + x[i]) );
						underCover++;
					}
				}

				System.out.println("Overcover =" + overCover);
				System.out.println("Undercover =" + underCover);
				System.out.println("Shifts =" + nrOfShift) ;
			}
		}
	}
	
	public void CloseFile() throws IOException{
		s.close();		
	}

}
