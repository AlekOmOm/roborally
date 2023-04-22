package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;


public class Gear extends FieldAction {

    /**
     * This method changes the players heading if a player lands on the field
     * @param gameController the gameController of the respective game
     * @param space the space this action should be executed for
     * @return
     */
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
       space.getPlayer().getHeading().prev();
        return true;
    }

}
