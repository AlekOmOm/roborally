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

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space) {
        // TODO Assignment V1: method should be implemented by the students:
        //   - the current player should be moved to the given space
        //     (if it is free()
        //   - and the current player should be set to the player
        //     following the current player
        //   - the counter of moves in the game should be increased by one
        //     if and when the player is moved (the counter and the status line
        //     message needs to be implemented at another place)

        Player player = board.getCurrentPlayer();
        if (space.getPlayer() == null) {
            player.setSpace(space);
            if (board.getCurrentPlayer() == board.getPlayer(board.getPlayersNumber() - 1)) {
                board.setCurrentPlayer(board.getPlayer(0));
            } else {
                board.setCurrentPlayer(board.getPlayer(board.getPlayerNumber(player) + 1));
            }
        }
    }

    // XXX: V2
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    // XXX: V2
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX: V2
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX: V2
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX: V2
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX: V2
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    // XXX: V2
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX: V2
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    // XXX: V2
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    if (command.isInteractive()) {
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                    }
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    // --> execute action on fields!
                    // -> check checkpoint for alle players
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    public void executeCommandOptionAndContinue(@NotNull Command option) {
        Player currentPlayer = board.getCurrentPlayer();
        if (currentPlayer != null && board.getPhase() == Phase.PLAYER_INTERACTION && option != null) {
            board.setPhase(Phase.ACTIVATION);
            executeCommand(currentPlayer, option);
            int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
            if (nextPlayerNumber < board.getPlayersNumber()) {
                board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
            } else {
                int step = board.getStep() + 1;
                if (step < Player.NO_REGISTERS) {
                    makeProgramFieldsVisible(step);
                    board.setStep(step);
                    board.setCurrentPlayer(board.getPlayer(0));
                } else {
                    startProgrammingPhase();
                }
            }
        }
        continuePrograms();
    }

    // XXX: V2
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                case FASTER_FORWARD:
                    this.fasterForward(player);
                    break;
                case STEP_BACK:
                    this.stepBack(player);
                    break;
                case U_TURN:
                    this.uTurn(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    /**
     * This method is used to make the current player's movement
     * @param player the current player.
     */
    // TODO Assignment V2
    public void moveForward(@NotNull Player player) {
        if (player.board == board) {
            Space space = player.getSpace();
            Heading heading = player.getHeading();
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                moveToSpace(player, target, heading);

            }
        }
    }

    /**
     * This method is used to end the game when there is a winner.
     */
    // todo V4b
    public void endGame() {
        board.setPhase(Phase.ENDGAME);

    }

    /**
     * This method is used to check the new space is available for the current player moves.
     * to check the walls -so the player can't wall through the walls
     * to check if there is any player in the new space, if yes, move to make space available for current player.
     */
    private boolean moveToSpace(Player player, Space target, Heading heading) {
        if (!target.getWalls().isEmpty() && target.getWalls().contains(heading.next().next())) {
            return false;
        } else if (!player.getSpace().getWalls().isEmpty() && player.getSpace().getWalls().contains(heading)) {
            return false;
        }
        if (target.getPlayer() == null) {
            player.setSpace(target);
            return true;
        } else if (target.getPlayer() != null) {
            Space other = board.getNeighbour(target, heading);
            boolean result = moveToSpace(target.getPlayer(), other, heading);
            if (result != false) {
                player.setSpace(target);
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * This method is used to make the player move twice as fast as normal.
     * @param player the current player
     */
    // TODO Assignment V2
    public void fastForward(@NotNull Player player) {
        moveForward(player);
        moveForward(player);

    }

    /**
     * This method is used to change the player's direction 90 degrees to the right side.
     * @param player the current player
     */
    // TODO Assignment V2
    public void turnRight(@NotNull Player player) {
        Heading heading = player.getHeading();
        player.setHeading(heading.next());
    }

    /**
     * This method is used to change the player's direction 90 degrees to the left side.
     * @param player the current player
     */
    // TODO Assignment V2
    public void turnLeft(@NotNull Player player) {
        Heading heading = player.getHeading();
        player.setHeading(heading.prev());
    }


    /**
     * This method is used to change the player's direction 180 degrees
     * @param player the current player
     */

    // TODO Assignment A3
    public void uTurn(@NotNull Player player) {
        Heading heading = player.getHeading();
        player.setHeading(heading.next().next());
    }
    /**
     * This method is used to make the current player steps back
     * @param player the current player
     */
    // TODO Assignment A3
    public void stepBack(@NotNull Player player) {
           uTurn(player);
           moveForward(player);
           uTurn(player);
    }
    public void fasterForward(@NotNull Player player){
        moveForward(player);
        moveForward(player);
        moveForward(player);
    }

    /**
     * This method is used to change the programming card on the card field.
     * @param source the current card
     * @param target the new card
     * @return true or false for the method execution result.
     */
    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

}
