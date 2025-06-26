package fr.minesnancy.diasoumare.jeudedames;

public class Square {

    public enum PieceColor {NONE, WHITE, BLACK}
    int row, col;
    int buttonId;
    PieceColor color;
    public Square(int row, int col) {
        this.row = row;
        this.col = col;
        this.buttonId = 0;
        this.color = PieceColor.NONE;
    }

    public void setButtonId(int buttonId) {
        this.buttonId = buttonId;
    }

    public int getButtonId() {
        return buttonId;
    }

    public void setColor(PieceColor color) {
        this.color = color;
    }

    public PieceColor getColor() {
        return color;
    }

    public int[] getPosition() {
        return new int[]{row, col};
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
