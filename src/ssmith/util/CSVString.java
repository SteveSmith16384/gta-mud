/*
 * Prog for extracting values from a CSV string.
 */

package ssmith.util;
import java.util.*;

public class CSVString {

	private String str;
	private ArrayList elements;
    private String separator;

	public CSVString(String newStr) {
		this(newStr, ",");
	}

	public CSVString(String newStr, String sep) {
            if (sep.length() != 1) {
            	throw new RuntimeException("Tokenizer not one character long!");
            }
            if (newStr == null) {
                throw new RuntimeException("newStr is null!");
            }
		this.str = newStr;
		this.separator = sep;
		this.elements = new ArrayList();
		StringTokenizer strtkn = new StringTokenizer(newStr, sep);
		while (strtkn.hasMoreElements()) {
			elements.add(strtkn.nextToken());
		}
	}

	public int getNoOfSections() {
		return elements.size();
	}

	public String getString() {
		return this.str;
	}

	public String toString() {
		return this.str;
	}

	public String getFirstSection() {
		return getSection(0);
	}

    public String getSection(int section) {
        //System.out.println("getting section"+section);
        try {
            return (String) elements.get(section);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getSections(int start, int finish) {
        StringBuffer sb = new StringBuffer();
        for (int i=start ; i<finish ; i++) {
            sb.append(this.getSection(i));
            if (i<finish-1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    public String getLastSection() {
    	return this.getSection(this.getNoOfSections()-1);
    }

	public static void main(String args[]) {
		System.out.println(new ssmith.util.CSVString("thix#hello#me","#").getSection(2));
	}

}
