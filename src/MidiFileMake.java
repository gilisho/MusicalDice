import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.sound.midi.*;

public class MidiFileMake { 

	public static void FileMaker(File newComposition) {

		try	{

			//****  Create a new MIDI sequence with 30 ticks per beat  ****
			Sequence sequence = new Sequence(Sequence.PPQ, 30);

			//****  Obtain a MIDI track from the sequence  ****
			Track track = sequence.createTrack();

			//****  General MIDI sysex -- turn on General MIDI sound set  ****
			byte[] b = {(byte)0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte)0xF7};
			SysexMessage sm = new SysexMessage();
			sm.setMessage(b, 6);
			MidiEvent me = new MidiEvent(sm,(long)0);
			track.add(me);

			//****  set tempo (meta event)  ****
			MetaMessage metaMessage = new MetaMessage();
			byte[] bt = {0x02, (byte)0x00, 0x00};
			metaMessage.setMessage(0x51 ,bt, 3);
			me = new MidiEvent(metaMessage,(long)0);
			track.add(me);

			//****  set omni on  ****
			ShortMessage mm = new ShortMessage();
			mm.setMessage(ShortMessage.CONTROL_CHANGE, 0x7D,0x00);
			me = new MidiEvent(mm,(long)0);
			track.add(me);

			//****  set poly on  ****
			mm = new ShortMessage();
			mm.setMessage(ShortMessage.CONTROL_CHANGE, 0x7F,0x00);
			me = new MidiEvent(mm,(long)0);
			track.add(me);

			//****  set instrument  ****
			mm = new ShortMessage();
			mm.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 0x00);
			me = new MidiEvent(mm,(long)0);
			track.add(me);

			BufferedReader bufferedReader = new BufferedReader(new FileReader(newComposition));
			String line;
			String[] words;
			ShortMessage shortMessage;
			MidiEvent midiEvent;
			long startTime=0;
			long endTime=0;
			int note;

			line = bufferedReader.readLine();

			while (line != null){
				words = line.split(" ");
				note = noteParser(words[0]);
				System.out.println(note);
				
				startTime = (long) (120*Float.parseFloat(words[1]));
				shortMessage = new ShortMessage();
				shortMessage.setMessage(ShortMessage.NOTE_ON, note, 0x60);
				midiEvent = new MidiEvent(shortMessage, startTime);
				track.add(midiEvent);
				
				endTime = (long) (startTime + 120*Float.parseFloat(words[2]));
				shortMessage = new ShortMessage();
				shortMessage.setMessage(ShortMessage.NOTE_OFF, note, 0x00);
				midiEvent = new MidiEvent(shortMessage, endTime);
				track.add(midiEvent);
								
				line = bufferedReader.readLine();
			}
						
			//****  set end of track (meta event) 24 ticks later  ****
			metaMessage = new MetaMessage();
			byte[] bet = {}; // empty array
			metaMessage.setMessage(0x2F,bet,0);
			me = new MidiEvent(metaMessage, (long) endTime+120);
			track.add(me);

			//****  write the MIDI sequence to a MIDI file  ****
			File file = new File("midifile.mid");
			MidiSystem.write(sequence, 1, file);

			bufferedReader.close();

		}

		catch(Exception e) {
			System.out.println("Exception caught " + e.toString());
			e.printStackTrace();
		}

	}

	public static int noteParser(String string){
		int notesInOctave = 12;
		int middleC = 60;
		int middleOctave = 4;
		char[] array = string.toCharArray();
		int octave = array[array.length-1]-(int)'0';
		int note;

		note = ((int)array[0] - (int)'C') + middleC;
		note += (octave-middleOctave) * notesInOctave;
		if (array[1] == '#')
			note++; //add the #

		return note;
	}

}