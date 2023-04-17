package dk.dtu.compute.se.pisd.roborally.fileactions.model;

import javafx.stage.FileChooser;

import java.io.File;

public class FileLoader {

    public String open() {

        FileChooser c = new FileChooser();
        c.getExtensionFilters().add(new FileChooser.ExtensionFilter("Json Files", "*.json"));
        File selectedFile = c.showOpenDialog(null);

        if (selectedFile == null) {
            return "";
        }

        return selectedFile.getAbsolutePath();
    }
}
