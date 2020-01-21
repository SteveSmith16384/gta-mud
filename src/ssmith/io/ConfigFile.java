/*
 * Created on 11-Oct-2006
 *
 */
package ssmith.io;

import java.io.IOException;
import ssmith.util.CSVString;
import java.io.File;

public class ConfigFile {

    private static final String DELIM = "=";
    private String filename;
    private boolean b_warn_of_missing;

    public ConfigFile(String fname, boolean b_warn) throws IOException {
        super();
        this.filename = fname;
        this.b_warn_of_missing = b_warn;
        
        if (new File(filename).canRead() == false) {
            throw new IOException("Cannot read " + filename);
        }
    }

    public String getSetting(String setting, String def) {
        TextFile tf = new TextFile();
        try {
            tf.openFile(filename, TextFile.READ);
            String line;
            while (tf.isEOF() == false) {
                line = tf.readLine();
                if (line.startsWith(setting + "=")) {
                    tf.close();
                    CSVString s = new CSVString(line, DELIM);
                    return s.getSection(1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (this.b_warn_of_missing) {
            System.out.println("Setting " + setting + " missing.  Returning " + def);
        }
        return def;
    }

}
