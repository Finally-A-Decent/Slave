package info.preva1l.slave;

import info.preva1l.slave.util.Logger;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Main {
    public void main(String[] args) {
        Logger.info("Initializing...");
        boolean running = true;
        int tries = 0;
        do {
            tries++;
            try {
                new Slave().start();
                tries = 0;
            } catch (Exception e) {
                if (tries >= 3) break;
                // webhook
                e.printStackTrace();
                running = false;
            }
        } while (!running);
    }
}