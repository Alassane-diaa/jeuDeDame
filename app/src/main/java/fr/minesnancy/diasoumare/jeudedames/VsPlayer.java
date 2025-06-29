package fr.minesnancy.diasoumare.jeudedames;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.List;

public class VsPlayer extends AppCompatActivity {

    private ImageButton[][] buttons = new ImageButton[10][10];
    private Board board = new Board();
    private Square selectedSquare = null;

    private Square.PieceColor tour = Square.PieceColor.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vs_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        createGrid();
    }

    public void createGrid(){
        GridLayout layout = findViewById(R.id.grid);
        int layoutSize = 35 * 20; //350dp que j'ai mis en taille de la grille dans le fichier XML
        int boutonSize = layoutSize / 10;
        for(int i = 0; i < 10; i++){
            for (int j = 0; j < 10; j++){
                ImageButton bouton = new ImageButton(getApplicationContext());
                if((i+j) % 2 == 0) {
                    if (i <= 3) {
                        bouton.setImageResource(R.drawable.piece_noire);
                        bouton.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        board.setPiece(i, j, Board.generateButtonId(i, j), Square.PieceColor.BLACK);
                    } else if (i >= 6) {
                        bouton.setImageResource(R.drawable.piece_blanche);
                        bouton.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        board.setPiece(i, j, Board.generateButtonId(i, j), Square.PieceColor.WHITE);
                    } else {
                        board.setPiece(i, j, Board.generateButtonId(i, j), Square.PieceColor.NONE);
                    }
                }

                buttons[i][j] = bouton;
                bouton.setBackgroundColor(Color.TRANSPARENT);
                bouton.setId(Board.generateButtonId(i, j));
                layout.addView(bouton);
                ViewGroup.LayoutParams boutonLayoutParams = bouton.getLayoutParams();
                boutonLayoutParams.width = boutonSize;
                boutonLayoutParams.height = boutonSize;
                bouton.setLayoutParams(boutonLayoutParams);
                bouton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int[] position = board.findPositionByButtonId(v.getId());
                        if (position != null) { // Pour pas que ça soit sur une case blanche sinon ça crashe
                            handleButtonClick(position[0], position[1]);
                        }
                    }
                });
            }
        }
    }

    private void handleButtonClick(int row, int col) {
        Square clickedSquare = board.getSquare(row, col);

        if (tour == clickedSquare.color || clickedSquare.getColor() == Square.PieceColor.NONE){
            if (selectedSquare == null) {
                // Si aucune pièce n'est sélectionnée
                if (clickedSquare.getColor() != Square.PieceColor.NONE) {
                    selectedSquare = clickedSquare;
                    highlightAccessibleMoves(selectedSquare);
                    buttons[row][col].setBackgroundColor(Color.RED);
                }
            } else {
                if (selectedSquare == clickedSquare) {
                    // Si la même pièce est cliquée deux fois de suite, on désélectionne
                    clearHighlights();
                    selectedSquare = null;
                } else if (clickedSquare.getColor() == Square.PieceColor.NONE && isAccessibleMove(clickedSquare)) {
                    // Si on clique sur une case vide accessible, on y go
                    board.movePiece(selectedSquare.getPosition()[0], selectedSquare.getPosition()[1], row, col);
                    updateUI();
                    clearHighlights();
                    selectedSquare = null;
                    changeTurn();
                } else if (clickedSquare.getColor() == selectedSquare.getColor()) {
                    // Changement de sélection, si la pièce est de la même couleur
                    clearHighlights();
                    selectedSquare = clickedSquare;
                    highlightAccessibleMoves(selectedSquare);
                    buttons[row][col].setBackgroundColor(Color.RED);
                } else {
                    clearHighlights();
                    selectedSquare = null;
                }
            }
        }
    }

    private void highlightAccessibleMoves(Square square) {
        List<Square> moves = board.getAccessibleMoves(square.getPosition()[0], square.getPosition()[1]);
        for (Square move : moves) {
            int[] pos = move.getPosition();
            buttons[pos[0]][pos[1]].setBackgroundColor(Color.RED);
        }
    }

    private boolean isAccessibleMove(Square square) {
        List<Square> moves = board.getAccessibleMoves(selectedSquare.getPosition()[0], selectedSquare.getPosition()[1]);
        return moves.contains(square);
    }

    private void clearHighlights() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                buttons[i][j].setBackgroundColor(Color.TRANSPARENT);
            }
        }
        // C'est peut êter pas nécessaire de parcourir toute la grille mais flemme
    }

    private void updateUI() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Square square = board.getSquare(i, j);
                if (square.getColor() == Square.PieceColor.WHITE) {
                    if(square.isDame()){
                        buttons[i][j].setImageResource(R.drawable.piece_dame_blanche);
                    } else {
                        buttons[i][j].setImageResource(R.drawable.piece_blanche);
                    }
                } else if (square.getColor() == Square.PieceColor.BLACK) {
                    if (square.isDame()) {
                        buttons[i][j].setImageResource(R.drawable.piece_dame_noire);
                    } else {
                        buttons[i][j].setImageResource(R.drawable.piece_noire);
                    }
                } else {
                    buttons[i][j].setImageResource(0); // Clear image
                }
                buttons[i][j].setBackgroundColor(Color.TRANSPARENT);
                buttons[i][j].setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }
    }

    public void changeTurn() {
        TextView textView = findViewById(R.id.turn);
        if (textView.getText().toString().equals("Tour des blancs")) {
            tour = Square.PieceColor.BLACK;
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundColor(Color.BLACK);
            textView.setText("Tour des noirs");
        } else {
            tour = Square.PieceColor.WHITE;
            textView.setText("Tour des blancs");
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
        }
    }

    public void pauseGame(View v) {
        ViewGroup layout = findViewById(R.id.main);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_pause, null);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        layout.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
            }
        });
        Button resume = popupView.findViewById(R.id.resumeButton);
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        Button restart = popupView.findViewById(R.id.restartButton);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                restartGame(v);
            }
        });
        Button exit = popupView.findViewById(R.id.exitButton);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void restartGame(View v) {
        board = new Board();
        selectedSquare = null;
        tour = Square.PieceColor.WHITE;
        ViewGroup layout = findViewById(R.id.grid);
        layout.removeAllViews();
        createGrid();
        TextView textView = findViewById(R.id.turn);
        textView.setText("Tour des blancs");
        textView.setBackgroundColor(Color.WHITE);
        textView.setTextColor(Color.BLACK);
    }
}