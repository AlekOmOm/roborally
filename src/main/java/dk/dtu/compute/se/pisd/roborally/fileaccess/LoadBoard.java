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
package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.BoardTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.CommandCardFieldTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.GameTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.SpaceTemplate;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.scene.control.Label;

import java.awt.*;
import java.io.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class LoadBoard {

    private static final String BOARDSFOLDER = "boards";
    private static final String DEFAULTBOARD = "defaultboard";
    private static final String JSON_EXT = "json";
  static Label statusLabel;
    public static Board loadBoard(String boardname) {
        if (boardname == null) {
            boardname = DEFAULTBOARD;
        }

        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        InputStream inputStream =
                classLoader.getResourceAsStream(BOARDSFOLDER + "/" + boardname + "." + JSON_EXT);
        if (inputStream == null) {
            // TODO these constants should be defined somewhere
            return new Board(8,8);
        }

        // In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();

        Board result;
        // FileReader fileReader = null;
        JsonReader reader = null;
        try {
            // fileReader = new FileReader(filename);
            reader = gson.newJsonReader(new InputStreamReader(inputStream));
            BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);

            result = new Board(template.width, template.height);
            for (SpaceTemplate spaceTemplate: template.spaces) {
                Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
                if (space != null) {
                    space.getActions().addAll(spaceTemplate.actions);
                    space.getWalls().addAll(spaceTemplate.walls);
                }
            }
            reader.close();
            return result;
        } catch (IOException e1) {
            if (reader != null) {
                try {
                    reader.close();
                    inputStream = null;
                } catch (IOException e2) {}
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e2) {}
            }
        }
        return null;
    }
    public static void saveGame(Command command, Board board, String name) {
        GameTemplate template = new GameTemplate();
       // save CommandCardField
        CommandCardFieldTemplate commandCardFieldTemplate = new CommandCardFieldTemplate();
        template.setCommandTemplate(commandCardFieldTemplate);



        // save board
        BoardTemplate boardTemplate = new BoardTemplate();
        template.setBoardTemplate(boardTemplate);
        boardTemplate.width = board.width;
        boardTemplate.height = board.height;
        for (int i = 0; i < board.width; i++) {
            for (int j = 0; j < board.height; j++) {
                Space space = board.getSpace(i, j);
                if (!space.getWalls().isEmpty() || !space.getActions().isEmpty() || space.getCheckpoint()!=0) {
                    SpaceTemplate spaceTemplate = new SpaceTemplate();
                    spaceTemplate.x = space.x;
                    spaceTemplate.y = space.y;
                    spaceTemplate.actions.addAll(space.getActions());
                    spaceTemplate.walls.addAll(space.getWalls());
                    spaceTemplate.felter.addAll(space.getFelter());

                    spaceTemplate.CheckPoint=space.getCheckpoint();
                    boardTemplate.spaces.add(spaceTemplate);
                }
            }
        }
        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        // TODO: this is not very defensive, and will result in a NullPointerException
        //       when the folder "resources" does not exist! But, it does not need
        //       the file "simpleCards.json" to exist!
        String filename ="/Users/lynguyenhansen/Documents/Idea projekt/roborally 2/src/main/resources/boards/simpleCards.json";
        //    classLoader.getResource(BOARDSFOLDER).getPath() + "/" + name + "." + JSON_EXT;

        // In simple cases, we can create a Gson object with new:
        //
        //   Gson gson = new Gson();
        //
        // But, if you need to configure it, it is better to create it from
        // a builder (here, we want to configure the JSON serialisation with
        // a pretty printer):
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>()).
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {
            fileWriter = new FileWriter(filename);
            writer = gson.newJsonWriter(fileWriter);
            gson.toJson(template, template.getClass(), writer);
            writer.close();
        } catch (IOException e1) {
            if (writer != null) {
                try {
                    writer.close();
                    fileWriter = null;
                } catch (IOException e2) {}
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {}
            }
        }
    }


public static void saveCommandCard(Command command){
    CommandCardFieldTemplate template = new CommandCardFieldTemplate();



}

    public static Board createBoard(String name) {
        Board board = new Board(8,8);
        Space checkpoint1 = board.getSpace(5,6);
        checkpoint1.setCheckpoint(1);
        //statusLabel = new Label("No.1");

        Space checkpoint2 = board.getSpace(2,3);
        checkpoint2.setCheckpoint(2);

        // V4 create walls on the board
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

        // V4 create ConveyorBelts on the board
        Space sp1 = board.getSpace(1,0);
        sp1.addConveyorBelt();
        Space sp2 = board.getSpace(1,1);
        sp2.addConveyorBelt();
        Space sp3 = board.getSpace(1,2);
        sp3.addConveyorBelt();
        Space sp4 = board.getSpace(1,3);
        sp4.addConveyorBelt();

        // V4 create Gears on the board
        Space space =board.getSpace(5,7);
        space.addGear();

        return board;

    }
}
