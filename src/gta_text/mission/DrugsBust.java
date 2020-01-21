/*
 * Created on 20-Jun-2006
 *
 */
package gta_text.mission;

import java.io.IOException;
import java.util.ArrayList;

public class DrugsBust extends Mission {

    public int stage=0;

    public DrugsBust(ArrayList missions) {
        super(missions);
    }

    public void setCompleted() throws IOException {
        this.completed = true;
    }

    public void reset() throws IOException {
        stage = 0;
    }


}
