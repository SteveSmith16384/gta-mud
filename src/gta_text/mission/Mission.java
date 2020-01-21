package gta_text.mission;

import java.io.IOException;
import java.util.ArrayList;

public abstract class Mission {

    public boolean completed = true; // Start off completed

    public Mission(ArrayList missions) {
        missions.add(this);
    }

    public abstract void setCompleted() throws IOException;

    public abstract void reset() throws IOException;

}
