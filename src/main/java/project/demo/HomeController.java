package project.demo;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class HomeController implements Initializable {

    private Field field;
    private Timer timer = new Timer();
    private double timeLeft = 0;
    //detect the bounds of user's screen
    private static final Rectangle2D vBounds = Screen.getPrimary().getVisualBounds();
    //if the bounded height is > 1000, tile size: 35*27
    private static final double tileSize = vBounds.getHeight()>1000 ? 35.0 : 27.0;
    private FXMLLoader currentLoader;
    private Parent currentRoot;
    private AnchorPane currentPane;
    private Scene currentScene;
    private Stage currentStage;

    @FXML
    private Text title;
    @FXML
    private Button easyGameButton;
    @FXML
    private Button mediumGameButton;
    @FXML
    private Button difficultGameButton;

    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private Button endGameButton;
    @FXML
    private Button newGameButton;
    @FXML
    private GridPane gameGridPane;
    @FXML
    private Label lbFlag;
    @FXML
    private Label lbFlagValue;
    @FXML
    private Label lbMines;
    @FXML
    private Label lbMinesValue;
    @FXML
    private Label lbTime;
    @FXML
    private Label lbTimeValue;
    @FXML
    private Label lbMsg;
    @FXML
    private TextField txField;




    @FXML
    void difficultGame(ActionEvent event) {
        this.field = new Field("difficult");
        System.out.println("field matrix size: " + field.getMatrixSize()*field.getMatrixSize());
        generateEmptyGameGrid(field);
    }

    @FXML
    void easyGame(ActionEvent event) {
        this.field = new Field("easy");
        System.out.println("field matrix size: " + field.getMatrixSize()*field.getMatrixSize());
        generateEmptyGameGrid(field);

    }

    @FXML
    void mediumGame(ActionEvent event) {
        this.field = new Field("medium");
        System.out.println("field matrix size: " + field.getMatrixSize()*field.getMatrixSize());
        generateEmptyGameGrid(field);
    }


    //empty unMined grids generation & bind tile with action listener
    private void generateEmptyGameGrid(Field field){
        currentStage = (Stage) mainAnchorPane.getScene().getWindow();
        currentStage.setWidth(((tileSize+1)*field.getMatrixSize())+160);
        currentStage.setHeight((tileSize+1)*field.getMatrixSize()+340);

        easyGameButton.setVisible(false);
        mediumGameButton.setVisible(false);
        difficultGameButton.setVisible(false);
        title.setVisible(false);

        gameGridPane.getChildren().clear();
        newGameButton.setVisible(true);
        endGameButton.setVisible(true);
        lbTime.setLayoutX((currentStage.getWidth()/2)-30);
        lbTime.setVisible(true);
        lbMines.setLayoutX((currentStage.getWidth()/2)-(lbMines.getWidth()/2));
        lbMines.setVisible(true);
        lbFlag.setLayoutX((currentStage.getWidth()/2)+30);
        lbFlag.setVisible(true);
        lbTimeValue.setVisible(true);
        lbMinesValue.setLayoutX((currentStage.getWidth()/2)-(lbMinesValue.getWidth()/2));
        lbMinesValue.setVisible(true);
        lbFlagValue.setVisible(true);
        newGameButton.setLayoutX((currentStage.getWidth()/2)-(newGameButton.getWidth()/2));
        newGameButton.setVisible(true);
        endGameButton.setLayoutX((currentStage.getWidth()/2)-(endGameButton.getWidth()/2));
        endGameButton.setVisible(true);

        lbMsg.setLayoutX((currentStage.getWidth()/2)-(lbMsg.getWidth()/2));
        lbMsg.setVisible(false);
        txField.setLayoutX((currentStage.getWidth()/2)-(txField.getWidth()/2));
        txField.setLayoutY(lbMsg.getLayoutY() + lbMsg.getHeight() + (txField.getHeight()/2)+1);
        txField.setVisible(false);

        gameGridPane.setVisible(true);
        for (int row=0; row < field.getMatrixSize(); row++) {
            for (int col = 0; col <field.getMatrixSize(); col++) {
                Tile tile = new UnMinedTile(row, col, 0, false, false);
                gameGridPane.add(tile, col, row);
                tile.setImage("/images/tile.png");
                tile.setOnAction(event -> {
                    try {
                        System.out.println("start to generate game board with first click");
                        generateGameGrid(tile.getCol(),tile.getRow());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    //plant random bomb & bind clickOpen method to each tile
    private void generateGameGrid(int firstClickPosx, int firstClickPosy) throws IOException {
        field.minesGenerator(firstClickPosx,firstClickPosy);
        System.out.println("mines generated");
        gameGridPane.getChildren().clear();
        for (int row = 0; row < field.getMatrixSize(); row++) {
            for (int col = 0; col < field.getMatrixSize(); col++) {
                Tile tile = field.getMatrix()[row][col];
                gameGridPane.add(tile, col, row);
                tile.setImage("/images/tile.png");
                tile.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        try {
                            click(tile.getCol(),tile.getRow());
                            revealTile(tile); //set images
                            lbMinesValue.setText(String.valueOf(field.getMinesLeftCount()));
                            lbTimeValue.setText(String.valueOf(getTimeLeft()));
                            lbFlagValue.setText(String.valueOf(field.getFlagCount()));
                            System.out.println("game over: "+ field.getGameOver());
                            checkWin();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    else if (e.getButton() == MouseButton.SECONDARY)
                        flagTile(tile);
                });
            }
        }
        //click open the starting tile
        field.setFirstMove(false);
        click(firstClickPosx,firstClickPosy);
        revealTile(field.getMatrix()[firstClickPosy][firstClickPosx]);
        System.out.println("x: "+firstClickPosx+ " y: "+ firstClickPosy );
        System.out.println("first clicked opened");
        System.out.println("game over: "+ field.getGameOver());
        timeCountDown();
        lbMinesValue.setText(String.valueOf(field.getMinesInitial()));
        lbTimeValue.setText(String.valueOf(getTimeLeft()));
        lbFlagValue.setText(String.valueOf(field.getFlagCount()));
        System.out.println("timeCount down started");
    }

    public void timeCountDown() {
        //assign game duration
        switch (field.getDifficulty()) {
            case "easy" -> timeLeft = 8 * 60;//8minutes
            case "medium" -> timeLeft = 16 * 60;//12minutes
            case "difficult" -> timeLeft = 24 * 60;//24 minutes
        }
        //define what the timer should do
        TimerTask timerTask = new TimerTask(){
            public void run() {
                timeLeft--;
                if (timeLeft < 0) {
                    timer.cancel();
                    lbMsg.setVisible(true);
                    lbMsg.setText("Timer Over");
                    field.setGameOver(true);
                    revealBoard(field);
                }
            }
        };
        long period = Math.round(Duration.seconds(1).toMillis());
        timer.scheduleAtFixedRate(timerTask, 0, period);// timer-1 per second
    }

    public double getTimeLeft(){
        return timeLeft;
    }

    public void click(int clickPosx, int clickPosy) {
        field.clickOpen(clickPosx, clickPosy);
        field.actualMinesLeftCount();
    }

    private void revealTile(Tile tile) {
        tile.setRevealed(true);
        if (tile.isMined())
            tile.setImage("/images/flask.png");
        else if ((tile.getAdjacentBombs()==1))
            tile.setImage("/images/1.png");
        else if ((tile.getAdjacentBombs()==2))
            tile.setImage("/images/2.png");
        else if ((tile.getAdjacentBombs()==3))
            tile.setImage("/images/3.png");
        else if ((tile.getAdjacentBombs()==4))
            tile.setImage("/images/4.png");
        else if ((tile.getAdjacentBombs()==5))
            tile.setImage("/images/5.png");
        else if ((tile.getAdjacentBombs()==6))
            tile.setImage("/images/6.png");
        else if ((tile.getAdjacentBombs()==7))
            tile.setImage("/images/7.png");
        else if ((tile.getAdjacentBombs()==8))
            tile.setImage("/images/8.png");
        else if ((tile.getAdjacentBombs()==9))
            tile.setImage("/images/9.png");
        else if ((tile.getAdjacentBombs()==10))
            tile.setImage("/images/10.png");
        else if ((tile.getAdjacentBombs()==11))
            tile.setImage("/images/11.png");
        else if ((tile.getAdjacentBombs()==12))
            tile.setImage("/images/12.png");
        else if ((tile.getAdjacentBombs()==0))
        {tile.setImage("/images/opentile.png");
            int row = tile.getRow();
            int col = tile.getCol();
            for (int y = -1; y < 2; y++) {
                for (int x = -1; x < 2; x++) {
                    if (row+y >= 0 && row+y < field.getMatrixSize() && col+x >= 0 && col+x < field.getMatrixSize() && !field.getMatrix()[row+y][col+x].isMined() && field.getMatrix()[row+y][col+x].getRevealed() &&field.getMatrix()[row+y][col+x].getAdjacentBombs()==0)
                        field.getMatrix()[row+y][col+x].setImage("/images/opentile.png");
                }
            }
        }
    }

    private void revealBoard(Field field) {
        for (int row = 0; row < field.getMatrixSize(); row++) {
            for (int col = 0; col < field.getMatrixSize(); col++) {
                field.getMatrix()[row][col].setRevealed(true);
                revealTile(field.getMatrix()[row][col]);
            }
        }
    }

    //on game board page
    public void checkWin() throws IOException {
        //no mines left, win
        if(field.getActualMinesLeft()==0){
            lbMsg.setTextFill(Color.rgb(204, 88, 214));
            lbMsg.setText("Congratulations! you won!");
            lbMsg.setVisible(true);
            field.setGameOver(true);
            revealBoard(field);
        }
        //mines left and bomb clicked
        else if(field.getActualMinesLeft()>0 && field.getGameOver()){
            field.setGameOver(true);
            revealBoard(field);
            lbMsg.setTextFill(Color.HOTPINK);
            lbMsg.setText("Boo, you suck! Want to play again the game? (y/n) ");
            lbMsg.setVisible(true);
            txField.setVisible(true);
            EventHandler<ActionEvent> answered = event -> {
                if(txField.getText().getClass().equals("".getClass())){
                    if(txField.getText().toLowerCase().trim().equals("y")){
                        txField.clear();
                        field.setGameOver(true);
                        String difficulty = field.getDifficulty();
                        field = new Field(difficulty);
                        generateEmptyGameGrid(field);
                    }
                    else if (txField.getText().toLowerCase().trim().equals("n"))
                    {
                        lbMsg.setTextFill(Color.rgb(37, 150, 190));
                        lbMsg.setText("Ok, see ya");
                        txField.clear();
                        gameGridPane.setVisible(false);
                    }
                }else{lbMsg.setTextFill(Color.rgb(37, 150, 190));
                    lbMsg.setText("Let's try that again, repeat the game? (y/n)");
                    txField.clear(); return;}
            };
            txField.setOnAction(answered);
        }
    }

    public void endGame(ActionEvent event) {
        //set current scene gameover
        field.setGameOver(true);
        revealBoard(field);
        //initialize labels for gameover window
        Label lbfinalTimeValue = new Label();
        Label lbfinalMinesValue = new Label();
        Label lbfinalFlagValue = new Label();
        Label lbfinalRealMinesValue = new Label();
        Label lbfinalMsg = new Label();
        //create new stage/window
        Stage gameOverStage = new Stage();
        double width = 400.0;
        double height = 160.0;
        //defines the dialog window
        gameOverStage.initModality(Modality.APPLICATION_MODAL); //Defines a modal window that blocks events from being delivered to any other application window.
        gameOverStage.initStyle(StageStyle.UTILITY);
        //set the label values
        lbfinalTimeValue.setText("Your time was " + timeLeft + " seconds.");
        lbfinalMinesValue.setText("You started with " + field.getMinesLeftCount() + " bombs.");
        lbfinalFlagValue.setText("Your flagged " + field.getFlagCount() + " flags." );
        lbfinalRealMinesValue.setText("You have "+ (field.getMinesLeftCount()-field.getActualMinesLeft()) + " flags that were correct.");
        lbfinalMsg.setTextFill(Color.HOTPINK);
        lbfinalMsg.setStyle("-fx-font-size: 16; -fx-font-weight: extra bold");
        lbfinalMsg.setText("**Jeffee & Kamiel: WELL DONE!**");
        //create close button
        Button btnClose = new Button("Close");
        btnClose.setPrefWidth(120.0);
        btnClose.setOnMouseClicked(e -> gameOverStage.close());
        //set layout
        VBox layout = new VBox(5);
        HBox buttons = new HBox (10);
        //configure buttons and layout
        buttons.getChildren().addAll(btnClose);
        buttons.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(lbfinalMsg,lbfinalTimeValue,lbfinalMinesValue,lbfinalFlagValue,lbfinalRealMinesValue,buttons);
        layout.setAlignment(Pos.CENTER);
        //set the scene
        Scene scene = new Scene(layout,width,height);
        gameOverStage.setResizable(false);
        gameOverStage.setScene(scene);
        //set window position
        currentStage = (Stage) mainAnchorPane.getScene().getWindow();
        currentScene = mainAnchorPane.getScene();
        double xPos = currentStage.getX() + currentStage.getWidth()/2d;
        double yPos = currentStage.getY() + currentStage.getHeight()/2d;
        gameOverStage.setX(xPos - (width+(currentStage.getWidth()-currentScene.getWidth()))/2);
        gameOverStage.setY(yPos - (height+(currentStage.getHeight()-currentScene.getHeight()))/2);
        gameOverStage.showAndWait();
    }


    public void newGame(ActionEvent event) {
        field.setGameOver(true);
        currentStage = (Stage) mainAnchorPane.getScene().getWindow();
        currentStage.setWidth(350);
        currentStage.setHeight(230);

        easyGameButton.setVisible(true);
        mediumGameButton.setVisible(true);
        difficultGameButton.setVisible(true);
        title.setVisible(true);

        gameGridPane.getChildren().clear();
        newGameButton.setVisible(false);
        endGameButton.setVisible(false);
        lbTime.setVisible(false);
        lbMines.setVisible(false);
        lbFlag.setVisible(false);
        lbTimeValue.setVisible(false);
        lbMinesValue.setVisible(false);
        lbFlagValue.setVisible(false);
        newGameButton.setVisible(false);
        endGameButton.setVisible(false);

        lbMsg.setVisible(false);
        txField.setVisible(false);

        gameGridPane.setVisible(false);

    }

    private void flagTile(Tile tile) {
        if (tile.getRevealed()) return;
        if (!tile.getFlagged()) {
            tile.setImage("/images/flag.png");
            field.flag(tile.getCol(),tile.getRow());
            lbMinesValue.setText(String.valueOf(field.getMinesLeftCount()));
            lbFlagValue.setText(String.valueOf(field.getFlagCount()));
        }
        else if (tile.getFlagged()){
            tile.setImage("/images/tile.png");
            field.unflag(tile.getCol(),tile.getRow());
            lbMinesValue.setText(String.valueOf(field.getMinesLeftCount()));
            lbFlagValue.setText(String.valueOf(field.getFlagCount()));
        }
    }

    public void initialize (URL location, ResourceBundle resources){
        currentPane = mainAnchorPane;
    }

}