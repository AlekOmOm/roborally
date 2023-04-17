package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

public class GameTemplate {
    CommandCardFieldTemplate commandTemplate = new CommandCardFieldTemplate();
    BoardTemplate boardTemplate = new BoardTemplate();
    public CommandCardFieldTemplate getCommandTemplate(CommandCardFieldTemplate commandTemplate){
        return commandTemplate;
    }
    public void setCommandTemplate(CommandCardFieldTemplate commandTemplate){
        this.commandTemplate= commandTemplate;
    }
    public BoardTemplate getBoardTemplate(BoardTemplate boardTemplate){
        return boardTemplate;
    }
    public void setBoardTemplate(BoardTemplate boardTemplate){
        this.boardTemplate= boardTemplate;
    }
}
