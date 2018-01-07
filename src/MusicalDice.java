import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * @author Gili Sh
 * The program arguments are:
 * 1) the starting composition file path
 * 2) the measures table file path
 * 3) the new composition to be made file path
 *
 */

public class MusicalDice {

	final static int diceRange = 11;
	final static int beatsInMeasure = 3;
	static int measuresNum = 16; // total number of measures in new composition

	public static void main(String[] args) throws IOException{
		File startingComposition = new File(args[0]);
		File measuresTable = new File(args[1]);
		File newComposition = new File(args[2]);

		int[] measuresFromTable = new int[16];

		for (int currMeasure = 0; currMeasure<measuresNum; currMeasure++){
			// for each measure to be composed
			int measureFromDice = ((int)(Math.random() * diceRange + 1)) - 1 ; //measureFromDice is between 0 to 10
			int measureFromTable = getMeasureFromTable(currMeasure, measureFromDice, measuresTable);
			measuresFromTable[currMeasure] = measureFromTable;
		}

		writeMeasuresToFile(newComposition, startingComposition, measuresFromTable);
		
		MidiFileMake.FileMaker(newComposition);
		
	}

	/**
	 * Measure selection according to Mozart's system for selecting the measures.
	 * 
	 * For each of the 16 measures, there are 11 possible selections from the starting 
	 * composition, and you choose each one by "rolling" two six-sided dice and taking
	 * their total.
	 * 
	 * @param tableLine 		the table line that the function needs to read from,
	 * 							aka the measure number in the new composition (range 0-15)
	 * @param measureFromDice	the measure selected from the dice (range 0-10) 
	 * @param MeasuresTable		the measures table text file according to Mozart's system
	 * @return the measure number from table (range 0-175)
	 * @throws IOException
	 */
	public static int getMeasureFromTable(int tableLine, int measureFromDice, File MeasuresTable) throws IOException{
		BufferedReader bufferedReader = new BufferedReader(new FileReader(MeasuresTable));
		String line = bufferedReader.readLine();
		for (int lineNum=0; lineNum<tableLine; lineNum++)
			line = bufferedReader.readLine();
		
		int measureFromTable = Integer.parseInt((line.split("\\s+"))[measureFromDice]) - 1; // deducion of 1 for the 0-index based.

		bufferedReader.close();
		return measureFromTable;
	}

	public static void writeMeasuresToFile(File newComposition, File startingComposition, int[] measuresFromTable) throws IOException{
		BufferedReader bufferedReader;
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(newComposition));
		String line;
		String[] note;

		for (int currMeasure=0; currMeasure<measuresNum; currMeasure++){
			bufferedReader = new BufferedReader(new FileReader(startingComposition));
			int measureFromTable = measuresFromTable[currMeasure];
			
			do {
				line = bufferedReader.readLine();
				if (line == null)
					break;
				note = line.split("\\s+");
				double orgSrtTime = Double.parseDouble(note[1]);
				int orgMeasureOfNote = (int) Math.floor(orgSrtTime/beatsInMeasure);
				String newTime;

				if (orgMeasureOfNote == measureFromTable){

					if (Integer.compare(measureFromTable, currMeasure) > 0)
						newTime = Double.toString(orgSrtTime - ((measureFromTable - currMeasure)*3));
					else
						newTime = Double.toString(orgSrtTime + ((currMeasure - measureFromTable)*3));
					
					bufferedWriter.write(note[0] + " " + newTime + " " + note[2]);
					bufferedWriter.newLine();
				}
				else if (orgMeasureOfNote > measureFromTable)
					break;

			} while (line != null);
			
			bufferedReader.close();
		}
		bufferedWriter.close();
	}

}
