/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import javafx.scene.control.TextInputDialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * The board class used to define the game board is the interface
 * that shows the game in progress.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    public final String boardName;

    private Integer gameId;

    private final Space[][] spaces;
    private Antenna antenna;
    private List<Checkpoint> checkpoints = new ArrayList<Checkpoint>();

    private final List<Player> players = new ArrayList<>();

    private Player current;
    private String name;
    private int gameID;
    private Phase phase = INITIALISATION;

    private int step = 0;

    private boolean stepMode;

    public Board(int width, int height, @NotNull String boardName) {
        this.boardName = boardName;
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        this.stepMode = false;
    }

    public Board(int width, int height) {
        this(width, height, "defaultboard");
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    public Space[][] getSpaces() {
        return spaces;
    }

    /**
     * Returns the number of players in the array of players.
     * @return number of players
     */
    public int getPlayersNumber() {
        return players.size();
    }

    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    /**
     * Returns the player at position "i" in the array of players
     * @param i is the sequence number of players in the array
     * @return player at position "i"
     */
    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    /**
     * Returns the current player of the game. This is the player who's
     * programming card will be executed next.
     * @return the current player of the game
     */
    public Player getCurrentPlayer() {
        return current;
    }

    /**
     * This method is used to modify the current player.
     * Takes a parameter "player" and assigns it to the current player.
     * @param player the player whose turn to play
     */
    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }
    public int getGameID() {
        return gameID;
    }
    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    public List<Checkpoint> getCheckpoints() {
        return this.checkpoints;
    }

    public void setCheckpoint(Checkpoint checkpoint) {
        this.checkpoints.add(checkpoint);
    }


    public Antenna getAntenna() {
        return this.antenna;
    }

    public void setAntenna(Antenna antenna) {
        this.antenna = antenna;
        for (Space[] spaces : this.spaces) {
            for (Space space : spaces) {
                // Very hack, we just need to trigger an update on all spaces.
                space.playerChanged();
            }
        }
    }
    public boolean isStepMode() {
        return stepMode;
    }

    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    /**
     * Returns the position of player in the array.
     * @param player the player is considered position
     * @return the number of the player's position
     */
    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }
    public void setName(@NotNull String name) {
        this.name = name;
    }

    public String getName() {

        if (this.name != null) {

            return name;

        } else {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Navn");
            dialog.setContentText("Indtast dit navn for spillet");
            dialog.showAndWait();

            if (dialog.getResult() != null) {
                this.name = dialog.getResult();
                return this.name;
            }
        }
        return null;
    }
    public Player getPlayerByDB(int i) {
        if (i >= 0 && i < players.size()) {
            for (Player player: players) {
              //  if (player.getDbNo() == i)
               //     return player;
            }
        }

        return null;
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y = (y + 1) % height;

                break;
            case WEST:
                x = (x + width - 1) % width;
                break;
            case NORTH:
                y = (y + height - 1) % height;
                break;
            case EAST:
                x = (x + 1) % width;
                break;
        }

        return getSpace(x, y);
    }

    public String getStatusMessage() {
        // this is actually a view aspect, but for making assignment V1 easy for
        // the students, this method gives a string representation of the current
        // status of the game

        // XXX: V1 add the move count to the status message
        // XXX: V2 changed the status so that it shows the phase, the current player and the number of steps
        return "Phase: " + getPhase().name() +
                ", Player = " + getCurrentPlayer().getName() +", Step = " +getCurrentPlayer().board.getStep();


    }


}
