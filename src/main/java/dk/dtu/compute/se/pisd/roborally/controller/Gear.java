package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;


public class Gear extends FieldAction {
        public final static int LEFT_TURN = 1;
        public final static int RIGHT_TURN = -1;

        private int directionOfTurn;
    
        public Gear(int directionOfTurn) {
            
            this.directionOfTurn = directionOfTurn;
        }

    /**
     * This method changes the players heading if a player lands on the field
     * @param gameController the gameController of the respective game
     * @param space the space this action should be executed for
     * @return
     */
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        Heading newHeading;
        if(directionOfTurn == LEFT_TURN){
            newHeading = space.getPlayer().getHeading().prev();
        }else {
            newHeading = space.getPlayer().getHeading().next();
        }
        space.getPlayer().setHeading(newHeading);
        return true;
    }

    public int getDirectionOfTurn() {
        return directionOfTurn;
    }
}
