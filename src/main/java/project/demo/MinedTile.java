package project.demo;

public class MinedTile extends Tile {
    private String symbol = "*";
    public MinedTile(int row, int col, int adjacentBombs, boolean flagged, boolean revealed){
        super(row, col, adjacentBombs, true, flagged, revealed);
    }

    @Override
    public String getVisual()
    {
        if(getFlagged())
        {
            return "!";
        }
        else if(getRevealed())
        {
            return symbol;
        }
        return "°";
    }
}