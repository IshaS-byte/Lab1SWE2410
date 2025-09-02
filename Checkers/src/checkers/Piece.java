package checkers;

import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

/**
 * Represents a movable piece on the board.
 */
public class Piece {
    public enum Type {
        RED, BLACK
    }
    private final Type type;

    /**
     * The position of the piece on the board, where (0,0) is the top left corner
     * position, (BoardController.BOARD_WIDTH-1,0) is the top right corner
     * and (BOARD_WIDTH-1,BOARD_WIDTH-1) is the bottom right corner.
     */
    private int x, y;
    private final Ellipse ellipse;

    public Piece(Type type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        ellipse = createEllipse();
        setActive(false);
        reposition();
        BoardController.getSquare(x,y).placePiece(this);
    }

    private Ellipse createEllipse() {
        final Ellipse ellipse;
        ellipse = new Ellipse();
        ellipse.setRadiusX(25.0f);
        ellipse.setRadiusY(12.0f);
        ellipse.setStroke(Color.WHITE);
        if(this.type == Type.RED) {
            ellipse.setFill(Color.RED);
        } else if(this.type == Type.BLACK) {
            ellipse.setFill(Color.BLACK);
        } else {
            throw new IllegalArgumentException("Unknown type:"+type);
        }
        ellipse.setOnMouseClicked(event -> trySetActive());
        BoardController.addChild(ellipse);
        return ellipse;
    }

    public String toString() {
        return "Piece at "+x+", "+y;
    }

    public Type getType() {
        return type;
    }

    private void reposition() {
        ellipse.setLayoutX(x* BoardController.SQUARE_SIZE + BoardController.SQUARE_SIZE/2);
        ellipse.setLayoutY(y* BoardController.SQUARE_SIZE + BoardController.SQUARE_SIZE/2);
    }

    private void trySetActive() {
        BoardController.trySetActive(this);
    }

    public void setActive(boolean isActive) {
        if(isActive) {
            ellipse.setStrokeWidth(3);
        } else {
            ellipse.setStrokeWidth(1);
        }
    }

    /**
     * Try to move a piece to a given square, including capturing a piece
     *    if appropriate. If the move is not legal, reports the problem
     *    to the user.
     * @param target_square where moving to
     */
    public void tryMove(Square target_square) {
        if(target_square.getPiece() != null) {
            BoardController.setMessage("That location is already occupied!\n" +
                    "Please select a different location or piece.");
        } else {
            if (isValidOrdinaryMove(target_square)) {
                move(target_square);
            } else if (isValidCapture(target_square)) {
                captureMoveTo(target_square);
            } else {
                BoardController.setMessage("The piece can neither move nor capture to that position.\n"
                                           + "Please try a different square.");
            }
        }
    }

    /**
     * Make a game-level move, removing the piece from the old position
     * and placing it on the new position. Switches turns to the other player.
     *
     * Precondition:
     * The move must be valid -- a valid, unoccupied square must be provided.
     *
     * @param square the position to which this piece will be moved.
     */
    private void move(Square target_square) {
        BoardController.getSquare(x,y).removePiece();
        placeOnSquare(target_square);
        BoardController.switchTurns();
        setActive(false);

        if(type.equals(Type.BLACK) && y == 0) {
            BoardController.setMessage("Kings are not yet implemented. Sorry!");
        }
    }

    /**
     * Actually place the piece on the given square and force a redisplay.
     */
    private void placeOnSquare(Square square) {
        this.x = square.getX();
        this.y = square.getY();
        BoardController.getSquare(x,y).placePiece(this);
        reposition();
    }

    /**
     * Make a game-level move when that move captures another piece.
     * This identifies the piece to be captured, removes that piece from
     * the board, and then moves the current piece to the new position.
     *
     * Preconditions:
     * The move must be valid -- the place moved to must exist and there must be a piece to capture.
     *
     * @param square A square to which this piece is able to move and capture at the same time.
     * @throws  IllegalArgumentException If no capture is made by moving to the square.
     */
    private void captureMoveTo(Square square) {
        Piece captured = getCapturedPiece(square);
        if(captured == null) {
            throw new IllegalArgumentException("Cannot capture by moving to "+square);
        }
        captured.removeSelf();
        move(square);
    }

    /**
     * Removes this piece from the board.
     */
    public void removeSelf() {
        BoardController.getSquare(x,y).removePiece();
        BoardController.removeChild(ellipse);
    }

    /**
     * Check if the current piece can move to a new position without
     * capturing another piece.
     *
     * @param square The square to which this piece will move
     * @return true if this piece can move to that square 
     */
    public boolean isValidOrdinaryMove(Square square) {
        if(type.equals(Type.BLACK)) {
            return (square.getY() == y - 1 &&
                    Math.abs(square.getX()-x) == 1);
        } else if(type.equals(Type.RED)){
            return (square.getY() == y + 1 &&
                    Math.abs(square.getX()-x) == 1);
        } else {
            throw new IllegalStateException("This piece has an unknown type:"+type);
        }
    }

    /**
     * Check if the current piece can capture another piece when moving
     * to the given target square.
     *
     * @param square The square to which this piece will move
     * @return true if this piece can move to that square and capture another
     *    piece at the same time.
     */
    private boolean isValidCapture(Square square) {
        return getCapturedPiece(square) != null;
    }

    /**
     * Find the piece that would be captured by moving this piece to a given square.
     *
     * The piece is not actually captured when calling this method.
     * It is simply identified by calling this method.
     *
     * @param square The square to which a move will be mode
     * @return null if the move cannot be made.
     *      Otherwise, return the piece that would be removed by moving to that square.
     */
    public Piece getCapturedPiece(Square square) {
        if(type.equals(Type.BLACK)) {
            if (!((square.getY() == y - 2 &&
                    Math.abs(square.getX()-x) == 2))) {
                return null;
            } else {
                return getMiddlePiece(square);
            }
        } else if(type.equals(Type.RED)){
            if (!((square.getY() == y + 2 &&
                    Math.abs(square.getX()-x) == 2))) {
                return null;
            } else {
                return getMiddlePiece(square);
            }
        } else {
            throw new IllegalStateException("This piece has an unknown type:"+type);
        }
    }

    /**
     * Assuming the given square represents a valid move, returns any piece
     * that would be captured during the move. If there is no piece, returns
     * null.
     * @param square Target square for the move
     * @return null the piece that would be captured or null if there is none
   */
    private Piece getMiddlePiece(Square square) {
        int middleX = (square.getX() + x) / 2;
        int middleY = (square.getY() + y) / 2;
        return BoardController.getSquare(middleX, middleY).getPiece();
    }
}

