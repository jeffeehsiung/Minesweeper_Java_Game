package project.demo;

import java.util.Random;

public class Field {

    private static int matrixSize;
    private int flagCount;
    private int minesLeftCount;
    private int minesInitial;
    private int actualMinesLeft;
    private Tile[][] matrix;
    private String difficulty;
    private boolean firstMove;
    private boolean gameOver;


    public Field(String difficulty){
        this.setDifficulty(difficulty);
        setFirstMove(true);
        setGameOver(false) ;
        switch (difficulty) {
            case ("easy") -> {
                matrixSize = 8;
                minesInitial = 10;
            }
            case ("medium") -> {
                matrixSize = 16;
                minesInitial = 40;
            }
            case ("difficult") -> {
                matrixSize = 30;
                minesInitial = 180;
            }
        }


        matrix = new Tile[matrixSize][matrixSize];
        //initialize game matrix with empty tiles
        for(int row = 0; row < matrixSize; row++){
            for(int col = 0; col < matrixSize; col++){
                matrix[row][col] = new UnMinedTile(row, col, 0, false, false);
            }
        }
    }

    public void minesGenerator(int firstClickPosx, int firstClickPosy) {

        //put mines randomly into the matrix
        for (int i=0; i<minesInitial; i++){
            int row = getRandomInt(matrixSize);
            int col = getRandomInt(matrixSize);
            if(!matrix[row][col].isMined() && row!=firstClickPosy && col!=firstClickPosx){
                // don't put mine on top or down of an existing one
                // also exclude the opening tile (first tile the player clicked)
                matrix[row][col] = new MinedTile(row, col, 0, false, false);
            }
            else i--;
        }
        //setting up minesLeftCount = minesInitial
        setMinesLeftCount(minesInitial);
        actualMinesLeft = minesInitial;

        //find the number of adjacent mines for each tile, checking all directions
        for(int row=0; row < matrixSize; row++){
            for(int col = 0; col<matrixSize; col++){
                //upper left
                if(row>0 && col>0 && matrix[row-1][col-1].isMined()){
                    matrix[row][col].setAdjacentBombs(matrix[row][col].getAdjacentBombs()+1);
                }
                //left
                if(col>0 && matrix[row][col-1].isMined()){
                    matrix[row][col].setAdjacentBombs(matrix[row][col].getAdjacentBombs()+1);
                }
                //lower left
                if(row<matrixSize-1 && col>0 && matrix[row+1][col-1].isMined()){
                    matrix[row][col].setAdjacentBombs(matrix[row][col].getAdjacentBombs()+1);
                }
                //downwards
                if (row<matrixSize-1 && matrix[row+1][col].isMined()){
                    matrix[row][col].setAdjacentBombs(matrix[row][col].getAdjacentBombs()+1);
                }
                //lower right
                if (row<matrixSize-1 && col<matrixSize-1 && matrix[row+1][col+1].isMined()){
                    matrix[row][col].setAdjacentBombs(matrix[row][col].getAdjacentBombs()+1);
                }
                //right
                if (col<matrixSize-1 && matrix[row][col+1].isMined()){
                    matrix[row][col].setAdjacentBombs(matrix[row][col].getAdjacentBombs()+1);
                }
                //upper right
                if (row>0 && col<matrixSize-1 && matrix[row-1][col+1].isMined()){
                    matrix[row][col].setAdjacentBombs(matrix[row][col].getAdjacentBombs()+1);
                }
                //upwards
                if (row>0 && matrix[row-1][col].isMined()){
                    matrix[row][col].setAdjacentBombs(matrix[row][col].getAdjacentBombs()+1);
                }
            }
        }
    }

    private static int getRandomInt(int max){
        Random random = new Random();
        return random.nextInt(max);
    }
    public boolean getGameOver()
    {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public int getMatrixSize() {return matrixSize;}

    public boolean isFirstMove() {
        return firstMove;
    }

    public void flag(int posx, int posy) {
        matrix[posy][posx].setFlagged(true);
        incrementFlagCount();
        minesLeftCount-=1;
    }

    public void unflag(int posx, int posy) {
        matrix[posy][posx].setFlagged(false);
        decrementFlagCount();
        minesLeftCount+=1;
    }

    public int getFlagCount() {return this.flagCount;}

    public void incrementFlagCount() {
        flagCount++;
    }

    public void decrementFlagCount(){
        flagCount--;
    }


    public void setFirstMove(boolean firstMove)
    {
        this.firstMove = firstMove;
    }

    public void clickOpen(int posx, int posy) {
        if(matrix[posy][posx].getRevealed()){return;}
        if(posx >= 0 && posy >= 0 && posx < matrixSize && posy < matrixSize && !matrix[posy][posx].getRevealed())
        {
            int row = posy;
            int col = posx;
            matrix[row][col].setRevealed(true);
            //if tile is flagged
            if(matrix[row][col].getFlagged()){
                matrix[row][col].setFlagged(false);
            }
            //bomb clicked
            else if (matrix[row][col].isMined())
            {
                setGameOver(true);
                revealAll();
                return;
            }
            else
            {	//recursive bomb check for revealing nearby blank tiles
                if(matrix[row][col].getAdjacentBombs() == 0){
                    //upper left
                    if(row>0 && col>0 && !matrix[row-1][col-1].isMined()){
                        clickOpen(col-1, row-1);
                    }
                    //left
                    if(col>0 && !matrix[row][col-1].isMined()){
                        clickOpen(col-1, row);
                    }
                    //lower left
                    if(row<matrixSize-1 && col>0 && !matrix[row+1][col-1].isMined()){
                        clickOpen(col-1, row+1);
                    }
                    //downwards
                    if (row<matrixSize-1 && !matrix[row+1][col].isMined()){
                        clickOpen(col, row+1);
                    }
                    //lower right
                    if (row<matrixSize-1 && col<matrixSize-1 && !matrix[row+1][col+1].isMined()){
                        clickOpen(col+1, row+1);
                    }
                    //right
                    if (col<matrixSize-1 && !matrix[row][col+1].isMined()){
                        clickOpen(col+1, row);
                    }
                    //upper right
                    if (row>0 && col<matrixSize-1 && !matrix[row-1][col+1].isMined()){
                        clickOpen(col+1, row-1);
                    }
                    //upwards
                    if (row>0 && !matrix[row-1][col].isMined()){
                        clickOpen(col, row-1);
                    }
                }
            }
        }
    }

    public Tile[][] getMatrix() {return matrix;}

    public int getMinesInitial() {
        return minesInitial;
    }

    public int getMinesLeftCount() {
        return this.minesLeftCount;
    }

    public void setMinesLeftCount(int minesLeftCount) {
        this.minesLeftCount = minesLeftCount;
    }

    public void actualMinesLeftCount()
    {
        //if the mined tile is flagged, minesLeftCount -1
        int counter = actualMinesLeft;
        for(int i = 0; i < matrixSize; i++) {
            for(int j = 0; j < matrixSize; j++)
            {
                if(matrix[i][j].isMined() && matrix[i][j].getFlagged()){
                    counter--;
                }
            }
        }
        actualMinesLeft = counter;
    }

    public int getActualMinesLeft() {
        return actualMinesLeft;
    }

    public String getDifficulty() {return difficulty;}

    public void setDifficulty(String difficulty) {this.difficulty = difficulty;}

    public void printField() {

        String fieldPrint = "";
        for (int row = 0; row < matrixSize+1; row++)
        {
            //System.out.println(fieldPrint);
            for(int col = 0; col < matrixSize+1; col++)
            {
                //gameBoard (0,0) blank
                if(row == 0 && col == 0){
                    fieldPrint += "\\";
                }
                //gameBoard (0,[1,8]) blank
                if(row == 0 && col != 0)
                {
                    fieldPrint += String.valueOf(col-1);
                }
                else if(row != 0 && col == 0)
                {
                    fieldPrint += String.valueOf(row-1);
                }
                else if(row != 0 && col != 0)
                {
                    fieldPrint += matrix[row-1][col-1].getVisual();
                }
                fieldPrint += "\t";
            }
            fieldPrint += "\n";
        }
        System.out.println(fieldPrint);

    }
    public void revealAll()
    {
        for(int i = 0; i < matrixSize; i++)
        {
            for(int j = 0; j < matrixSize; j++)
            {
                matrix[i][j].setRevealed(true);
            }
        }

    }


    public static void main(String[] args) {

        Field field = new Field("easy");
        field.clickOpen(0,0);
        field.printField();
        field.clickOpen(6,6);
        field.printField();
        field.revealAll();
        field.printField();
    }
}
