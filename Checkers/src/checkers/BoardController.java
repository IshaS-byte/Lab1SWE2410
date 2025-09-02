package checkers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is both the controller for the main FXML window
 * and represents the entire board. It thus holds
 * all the squares on the board as well as the pieces and manages their interactions.
 */
public class BoardController {
    private static BoardController theController = null;

    public static final int SQUARE_SIZE = 60;
    public static final int BOARD_WIDTH = 6;

    @FXML
    private Label message; // red text below the squares.

    @FXML
    private Pane theBoard; // green space on which board squares and pieces are laid

    private Piece.Type whoseTurn = Piece.Type.BLACK;
    private final List<Square> squares = new ArrayList<>();
    private Piece activePiece = null;

    @FXML
    public void initialize() {
        // for fun, set up a gradient background; could probably do in SceneBuilder as well
        // note the label has a Z index of 2 so it is drawn above the panel, otherwise it may be displayed "under" the panel and not be visible
        theBoard.setStyle("-fx-background-color: linear-gradient(to bottom right, derive(forestgreen, 20%), derive(forestgreen, -40%));");
        // load image from a file; the file needs to be in the top folder of the project
        theController = this;

        createSquares();

        for(int i = 0; i < BOARD_WIDTH; i+=2) {
            createPiece(Piece.Type.RED,i,0);
        }
        for(int i = 1; i < BOARD_WIDTH; i+=2) {
            createPiece(Piece.Type.BLACK,i,BOARD_WIDTH-1);
        }
        theBoard.setFocusTraversable(true); // ensure garden pane will receive keypresses
    }

    private void createPiece(Piece.Type type, int x, int y) {
        Piece piece = new Piece(type, x, y);
    }

    private void createSquares() {
        for(int indRow = 0; indRow < BOARD_WIDTH; indRow++) {
            for(int indCol = 0; indCol < BOARD_WIDTH; indCol++) {
                Square square = new Square(indCol, indRow);
                squares.add(square);
            }
        }
    }

    public static BoardController getTheController() {
        if(theController == null) {
            throw new IllegalStateException("theController should not be accessed before the " +
                    "gameboard is initialized.");
        }
        return theController;
    }

    public static void addChild(Node node) {
        getTheController().instanceAddChild(node);
    }
    private void instanceAddChild(Node node) {
        theBoard.getChildren().add(node);
    }

    public static void removeChild(Node node) {
        getTheController().instanceRemoveChild(node);
    }
    private void instanceRemoveChild(Node node) {
        theBoard.getChildren().remove(node);
    }

    public static void setMessage(String text) {
        getTheController().instanceSetMessage(text);
    }
    private void instanceSetMessage(String text) {
        message.setText(text);
    }

    public static void trySetActive(Piece piece) {
        getTheController().instanceTrySetActive(piece);
    }

    private void instanceTrySetActive(Piece piece) {
        if(piece.getType().equals(whoseTurn)) {
            if(activePiece != null) {
                activePiece.setActive(false);
            }
            piece.setActive(true);
            activePiece = piece;
            setMessage("Click on the square to which that piece should move.");
        } else {
            setMessage("The piece you have selected cannot move because it is " +
                    "not\nthat player's turn.\n" +
                    "Please select a piece from the opposite player");
        }
    }

    public static void tryMovePiece(Square square) {
        getTheController().instanceTryMovePiece(square);
    }

    private void instanceTryMovePiece(Square square) {
        if(activePiece == null) {
            setMessage("Please click on a piece to move.");
        } else {
            activePiece.tryMove(square);
        }
    }

    public static void switchTurns() {
        getTheController().instanceSwitchTurns();
    }
    private void instanceSwitchTurns() {
        if(whoseTurn.equals(Piece.Type.BLACK)) {
            whoseTurn = Piece.Type.RED;
        } else if (whoseTurn.equals(Piece.Type.RED)) {
            whoseTurn = Piece.Type.BLACK;
        } else{
            throw new IllegalStateException("Unknown player has turn:"+whoseTurn);
        }
        activePiece = null;
        setMessage("Click on a piece from the opposite player to move it.");
    }

    public static Square getSquare(int x, int y) {
        return getTheController().instanceGetSquare(x, y);
    }
    private Square instanceGetSquare(int x, int y) {
        if(!isValidPosition(x, y)) {
            throw new IllegalArgumentException("This square does not exist: "+x+ " "+ y);
        }
        return squares.get(y*BOARD_WIDTH + x);
    }

    public static boolean isValidPosition(int x, int y) {
        return getTheController().instanceIsValidPosition(x,y);
    }
    private boolean instanceIsValidPosition(int x, int y) {
        return x >= 0 && y >= 0 && x < BOARD_WIDTH && y < BOARD_WIDTH;
    }
}
