package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;

public class Checkpoint extends FieldAction {

    private int checkpointNumber;

    public Checkpoint(int checkpointNumber) {
        this.checkpointNumber = checkpointNumber;
    }


    @Override
    public boolean doAction(GameController gameController, Space space) {
        if (space != null) {
            Player player = space.getPlayer();
            if (player != null) {
                if (player.getCheckpoints() + 1 == checkpointNumber) {
                    player.setCheckpoints(player.getCheckpoints() + 1);
                }

            }
            return true;
        }
        return false;
    }
}
