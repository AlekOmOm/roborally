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
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import dk.dtu.compute.se.pisd.roborally.RoboRally;

import dk.dtu.compute.se.pisd.roborally.dal.Repository;
import dk.dtu.compute.se.pisd.roborally.dal.RepositoryAccess;
import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.fileactions.model.FileLoader;
import dk.dtu.compute.se.pisd.roborally.model.*;

import dk.dtu.compute.se.pisd.roborally.view.RoboRallyMenuBar;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard.saveBoard;
import static dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard.saveCommandCard;
import static dk.dtu.compute.se.pisd.roborally.view.SpaceView.SPACE_HEIGHT;
import static dk.dtu.compute.se.pisd.roborally.view.SpaceView.SPACE_WIDTH;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class AppController implements Observer {
    Board board;
    Command command;
    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    final private RoboRally roboRally;

    private GameController gameController;
    private BorderPane boardRoot;
    private Stage stage;

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }


    public void newGame() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }

            // XXX the board should eventually be created programmatically or loaded from a file
            //     here we just create an empty board with the required number of players.
            board = new Board(8,8);
            gameController = new GameController(board);
            int no = result.get();
            Space checkpoint1 = board.getSpace(5,6);
            checkpoint1.setCheckpoint(true);
            Space checkpoint2 = board.getSpace(2,3);
            checkpoint2.setCheckpoint(true);
            for (int i = 0; i < no; i++) {
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                board.addPlayer(player);
                player.setSpace(board.getSpace(i % board.width, i));
                player.addCheckpoint(checkpoint1);
                player.addCheckpoint(checkpoint2);
            }

            // V4 create walls
            Space space1 = board.getSpace(2,3);
            space1.addWall(Heading.WEST);
            Space space2 = board.getSpace(2,3);
            space2.addWall(Heading.NORTH);
            Space space3 = board.getSpace(5,6);
            space3.addWall(Heading.NORTH);
            Space space4 = board.getSpace(5,4);
            space4.addWall(Heading.NORTH);
            Space space5 = board.getSpace(3,4);
            space5.addWall(Heading.EAST);
            Space space6 = board.getSpace(2,6);
            space6.addWall(Heading.WEST);
            Space space7 = board.getSpace(2,5);
            space7.addWall(Heading.SOUTH);
            Space space8 = board.getSpace(6,2);
            space8.addWall(Heading.NORTH);

            // V4 create fields on the board
            Space sp1 = board.getSpace(1,0);
            sp1.addFelte(Heading.SOUTH);
            Space sp2 = board.getSpace(1,1);
            sp2.addFelte(Heading.NORTH);
            Space sp3 = board.getSpace(1,2);
            sp3.addFelte(Heading.NORTH);
            Space sp4 = board.getSpace(1,3);
            sp4.addFelte(Heading.NORTH);

            // XXX: V2
            // board.setCurrentPlayer(board.getPlayer(0));
            gameController.startProgrammingPhase();

            RepositoryAccess.getRepository().createGameInDB(board);

            roboRally.createBoardView(gameController);
        }
    }

    public void saveGame() {
        // XXX needs to be implemented eventually
        LoadBoard loadboard = new LoadBoard();
        //LoadBoard.saveCommandCard();
        try {
            //  Block of code to try
            loadboard.saveBoard(board, "game");
        } catch (Exception e) {
            //  Block of code to handle errors
        }
    }

    public void loadGame() {
        // XXX needs to be implememted eventually
        // for now, we just create a new game
        if (gameController == null) {
            newGame();
        }
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    public boolean isGameRunning() {
        return gameController != null;
    }


    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

    private String selectBoardLayout() {

        FileLoader fl = new FileLoader();
        String filename = fl.open();

        return filename;

    }
}
