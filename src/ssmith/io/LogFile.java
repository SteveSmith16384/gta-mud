/*
 * LogFile.java
 *
 * Created on 26 September 2001, 08:57
 */

package ssmith.io;

import java.io.*;
/**
 *
 * @author  steve smith
 * @version
 */

public class LogFile extends TextFile {
	
    private BufferedWriter bw;
    private String filename;

    public LogFile(String filename) {
    	this.filename = filename;
    }

    /** Creates new TextFile */
    private boolean openFile() {
        try {
            bw = new BufferedWriter(new FileWriter(filename, true));
            return true;
        } catch (FileNotFoundException e) {
            System.err.println("LogFile(): " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("LogFile(): " + e.getMessage());
            return false;
        }
    }

    public synchronized void append(String text) {
    	try {
    		if (openFile() == true) {
    			bw.write(text);
    			bw.newLine();
    			close();
    		}
    	} catch (IOException e) {
    		System.err.println("Error writing to file.  "+e.getMessage());
    	}
    }
    
    public void close() {
        try {
            bw.close();
        } catch (IOException e) {
            System.err.println("Error closing file.  "+e.getMessage());
        }
    }

}
