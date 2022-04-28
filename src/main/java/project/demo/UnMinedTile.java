package project.demo;
public class UnMinedTile extends Tile {
    private String symbol = "_";
    public UnMinedTile(int row, int col, int adjacentBombs, boolean flagged, boolean revealed){
        super(row, col, adjacentBombs, false, flagged, revealed);
    }

    @Override
    public String getVisual()
    {
        //if flagged, unmined change to flag
        if(getFlagged())
        {
            return "!";
        }
        //if it's set to be revealed
        else if(getRevealed())
        {
            if(getAdjacentBombs() == 0)
            {
                return symbol;
            }
            else
            {
                return Integer.toString(getAdjacentBombs());
            }
        }
        //if it's not yet set to be revealed, remain un-opened symbol
        return "°";
    }
}