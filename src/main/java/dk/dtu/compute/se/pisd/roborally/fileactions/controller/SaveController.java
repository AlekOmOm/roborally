package dk.dtu.compute.se.pisd.roborally.fileactions.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.Adapter;
import dk.dtu.compute.se.pisd.roborally.fileactions.model.FileSaver;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.BoardTemplate;

import java.io.FileWriter;
import java.io.IOException;

public class SaveController {

    public static void saveBoard(Board board) {

        try {

            FileSaver fs = new FileSaver();
            String filename = fs.save();

            GsonBuilder builder = new GsonBuilder().registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>()).
                    setPrettyPrinting();
            Gson gson = builder.create();

            FileWriter fw = new FileWriter(filename);
            JsonWriter writer = gson.newJsonWriter(fw);

            BoardTemplate bt = (new BoardTemplate()).fromBoard(board);

            gson.toJson(bt, bt.getClass(), writer);

            writer.close();


        } catch (IOException e) {
            // XXX We should probably do something here...
        }


    }
}
