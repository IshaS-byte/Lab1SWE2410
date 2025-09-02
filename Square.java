package checkers;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * Represents a place on the board where a piece can sit.
 */
public class Square {
    private final int x;
    private final int y;
    private static final int SQUARE_SIZE = BoardController.SQUARE_SIZE;

    /**
     * The piece currently sitting on this square.
     *
     * null if this square is empty.
     */
    private Piece piece = null;
    private final Rectangle rectangle;

    /**
     * Sets the position of this square on the board, where the top left square
     * is at (x,y) == (0,0),
     * the top right square is at (BoardController.BOARD_WIDTH-1,0)
     * and the bottom right square is at (BOARD_WIDTH-1,BOARD_WIDTH-1).
     *
     * @param x the horizontal position, increasing rightwards.
     * @param y the vertical position, increasing downwards.
     */
    public Square(int x, int y) {
        this.x = x;
        this.y = y;

        Paint paint;
        if((this.y + this.x)%2==0) {
            paint = Color.BLACK.brighter().brighter().brighter().brighter();
        } else {
            paint = Color.RED.darker().darker();
        }
        rectangle = new Rectangle(SQUARE_SIZE, SQUARE_SIZE, paint);
        rectangle.setX(this.x *SQUARE_SIZE);
        rectangle.setY(this.y *SQUARE_SIZE);
        rectangle.setOnMouseClicked(event -> tryMovePieceHere());
        BoardController.addChild(rectangle);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String toString() {
        return "Square at "+x+", "+y;
    }

    private void tryMovePieceHere() {
        BoardController.tryMovePiece(this);
    }

    /**
     * @return The piece currently sitting on this square.
     * null if this square is empty.
     */
    public Piece getPiece() {
        return piece;
    }

    public void removePiece() {
        if(piece == null) {
            throw new IllegalStateException("Cannot remove piece from an empty square.");
        }
        piece = null;
    }

    public void placePiece(Piece piece) {
        if(this.piece != null) {
            throw new UnsupportedOperationException("The place being moved to already has a piece on it.");
        }
        if(piece == null) {
            throw new IllegalArgumentException("No piece provided.");
        }
        this.piece = piece;
    }

}
