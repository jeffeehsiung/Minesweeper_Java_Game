package project.demo;


import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import java.util.Objects;

public abstract class Tile extends Button {

    private int row;
    private int col;
    private boolean flagged;
    private boolean revealed;
    private boolean mined;
    private int adjacentBombs;

    private static final Rectangle2D vBounds = Screen.getPrimary().getVisualBounds();
    private static final double tileSize = vBounds.getHeight()>1000 ? 35.0 : 27.0;

    public Tile(int row, int col, int adjacentBombs, boolean mined, boolean flagged, boolean revealed){
        this.setRow(row);
        this.setCol(col);
        this.setAdjacentBombs(adjacentBombs);
        this.setMined(mined);
        this.setFlagged(flagged);
        this.revealed = revealed;
    }

    public void setImage(String image) {
        ImageView tileView = new ImageView(new Image(Objects.requireNonNull(Main.class.getResourceAsStream(image))));
        tileView.setFitHeight(tileSize);
        tileView.setFitWidth(tileSize);
        //this.setPadding(new Insets(0));
        this.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent; -fx-background-color: transparent");
        this.setGraphic(tileView);
    }

    public int getRow() {return row;}

    public abstract String getVisual();

    public void setRow(int row) {this.row = row;}

    public int getCol() {return col;}

    public void setCol(int col) {this.col = col;}

    public boolean getFlagged() {
        return this.flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public boolean getRevealed() {
        return this.revealed;
    }
    public void setRevealed(boolean revealed) {this.revealed = revealed;}

    public boolean isMined() {
        return this.mined;
    }

    public void setMined(boolean mined) {
        this.mined = mined;
    }

    public int getAdjacentBombs() {
        return this.adjacentBombs;
    }

    public void setAdjacentBombs(int adjacentBombs) {
        this.adjacentBombs = adjacentBombs;
    }

}
