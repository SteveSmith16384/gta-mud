package gta_text.locations;

import java.io.IOException;
import gta_text.npcs.SecurityGuard;

public class BankVault extends Location {

    public BankVault(String no) throws IOException {
        super(no);

        new SecurityGuard(this);
    }

    public void regenChars() throws IOException {
        // do nothing
    }

}
