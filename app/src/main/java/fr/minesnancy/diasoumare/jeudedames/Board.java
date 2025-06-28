package fr.minesnancy.diasoumare.jeudedames;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private static final int SIZE = 10;
    private Square[][] board;

    public Board() {
        board = new Square[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = new Square(i, j);
            }
        }
    }

    public void setPiece(int row, int col, int buttonId, Square.PieceColor color) {
        Square sq = board[row][col];
        sq.setButtonId(buttonId);
        sq.setColor(color);
    }

    public Square.PieceColor getPieceColor(int row, int col) {
        return board[row][col].getColor();
    }

    public boolean isGameOver() {
        boolean hasWhite = false, hasBlack = false;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Square.PieceColor color = board[i][j].getColor();
                if (color == Square.PieceColor.WHITE) hasWhite = true;
                if (color == Square.PieceColor.BLACK) hasBlack = true;
            }
        }
        return !(hasWhite && hasBlack);
    }

    public int getButtonId(int row, int col) {
        return board[row][col].getButtonId();
    }

    public int[] findPositionByButtonId(int buttonId) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j].getButtonId() == buttonId) {
                    return new int[]{i, j};
                }
            }
        }
        return null; // Not found
    }

    public Square findSquareByButtonId(int buttonId) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (this.getButtonId(i, j) == buttonId) {
                    return board[i][j];
                }
            }
        }
        return null;
    }

    public Square getSquare(int row, int col) {
        return board[row][col];
    }

    public List<Square> getAccessibleMoves(int row, int col) {
        List<Square> moves = new ArrayList<>();
        Square square = this.getSquare(row, col);
        Square.PieceColor color = square.getColor();
        if (color == Square.PieceColor.NONE) return moves;
        if (!square.isDame()){
            int direction = (color == Square.PieceColor.WHITE) ? -1 : 1;
            // Les pièces noires vers le bas, les blanches vers le haut
            int[] dCols = {-1, 1};
            for (int dCol : dCols) {
                int newRow = row + direction;
                int newCol = col + dCol;
                if (isWithinBounds(newRow, newCol) && board[newRow][newCol].getColor() == Square.PieceColor.NONE) {
                    moves.add(board[newRow][newCol]);
                } else if (isWithinBounds(newRow + direction, newCol + dCol) && board[newRow + direction][newCol + dCol].getColor() == Square.PieceColor.NONE && board[newRow][newCol].getColor() != Square.PieceColor.NONE && board[newRow][newCol].getColor() != color ) {
                    moves.add(board[newRow+direction][newCol+dCol]);
                }
            }
        } else {
            // Pour les dames
            int[] dRows = {-1, 1};
            int[] dCols = {-1, 1};
            for (int dRow : dRows) {
                for (int dCol : dCols) {
                    int newRow = row + dRow;
                    int newCol = col + dCol;
                    while (isWithinBounds(newRow, newCol)) {
                        if (board[newRow][newCol].getColor() == Square.PieceColor.NONE) {
                            moves.add(board[newRow][newCol]);
                        } else if (board[newRow][newCol].getColor() != color) {
                            // Pièce adverse
                            int jumpRow = newRow + dRow;
                            int jumpCol = newCol + dCol;
                            if (isWithinBounds(jumpRow, jumpCol) && board[jumpRow][jumpCol].getColor() == Square.PieceColor.NONE) {
                                moves.add(board[jumpRow][jumpCol]);
                            } else {
                                break; // Deux pièces de couleurs différentes
                            }
                        } else {
                            break; // Pièce de la même couleur
                        }
                        newRow += dRow;
                        newCol += dCol;
                    }
                }
            }
        }
        return moves;
    }
    public static int generateButtonId(int row, int col) {
        return row * SIZE + col; // pour que ça soit unique
    }

    public boolean movePiece(int startRow, int startCol, int endRow, int endCol) {
        if (!isWithinBounds(startRow, startCol) || !isWithinBounds(endRow, endCol)) {
            return false;
        }

        Square startSquare = board[startRow][startCol];
        Square endSquare = board[endRow][endCol];
        Square midSquare = board[(endRow+startRow)/2][(endCol+startCol)/2];

        if (startSquare.getColor() == Square.PieceColor.NONE) {
            return false;
        }

        if (endSquare.getColor() != Square.PieceColor.NONE) {
            return false;
        }

        int rowDiff = endRow - startRow;
        int colDiff = Math.abs(endCol - startCol);

        endSquare.setColor(startSquare.getColor());
        startSquare.setColor(Square.PieceColor.NONE);

        if (colDiff == 2) {
            midSquare.setColor(Square.PieceColor.NONE);
        }

        if (endRow == 0 && endSquare.getColor() == Square.PieceColor.WHITE || startSquare.isDame()) {
            endSquare.setDame(true);
        } else if (endRow == SIZE - 1 && endSquare.getColor() == Square.PieceColor.BLACK || startSquare.isDame()) {
            endSquare.setDame(true);
        }
        if(colDiff >= 2){
            for(int i = 0; i < colDiff; i++){
                int midRow = startRow + (rowDiff > 0 ? 1 : -1) * i;
                int midCol = startCol + (endCol > startCol ? 1 : -1) * i;
                board[midRow][midCol].setColor(Square.PieceColor.NONE);
            }
        }
        return true;
    }

    private boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }
}