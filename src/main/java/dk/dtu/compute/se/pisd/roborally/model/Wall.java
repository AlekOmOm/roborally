package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import org.jetbrains.annotations.NotNull;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;
import static dk.dtu.compute.se.pisd.roborally.view.SpaceView.SPACE_HEIGHT;
import static dk.dtu.compute.se.pisd.roborally.view.SpaceView.SPACE_WIDTH;

public class Wall extends Subject {
    final public Board board;
    private Space space;
    private Heading heading = SOUTH;

    public Wall(@NotNull Board board) {
        this.board = board;
        this.space = null;
    }
    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
        space.setWall(this);
    }
    public Heading getHeading() {
        return heading;
    }

    public void setHeading(@NotNull Heading heading) {

            this.heading = heading;

    }


}
