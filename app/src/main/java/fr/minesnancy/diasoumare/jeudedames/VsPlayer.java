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
    private boolean priseMultipleEnCours = false;
    private Square pionEnRafale = null;


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
    private boolean hasAnotherCapture(int row, int col) {
        return board.getAccessibleCaptures(row, col).size() > 0;
    }

    private void handleButtonClick(int row, int col) {
        Square clickedSquare = board.getSquare(row, col);

        if (tour == clickedSquare.color || clickedSquare.getColor() == Square.PieceColor.NONE) {
            if (selectedSquare == null) {
                if (priseMultipleEnCours) return; // On empêche de changer de pion pendant une rafale

                if (clickedSquare.getColor() != Square.PieceColor.NONE) {
                    selectedSquare = clickedSquare;
                    highlightAccessibleMoves(selectedSquare);
                    buttons[row][col].setBackgroundColor(Color.RED);
                }
            } else {
                if (selectedSquare == clickedSquare) {
                    if (priseMultipleEnCours) return; // Interdit de désélectionner pendant une rafale

                    clearHighlights();
                    selectedSquare = null;
                } else if (clickedSquare.getColor() == Square.PieceColor.NONE && isAccessibleMove(clickedSquare)) {
                    int fromRow = selectedSquare.getPosition()[0];
                    int fromCol = selectedSquare.getPosition()[1];

                    boolean isCapture = board.isCaptureMove(fromRow, fromCol, row, col);

                    board.movePiece(fromRow, fromCol, row, col);
                    updateUI();
                    clearHighlights();

                    if (isCapture && hasAnotherCapture(row, col)) {
                        // Rejouer avec le même pion
                        priseMultipleEnCours = true;
                        pionEnRafale = board.getSquare(row, col);
                        selectedSquare = pionEnRafale;
                        highlightAccessibleMoves(selectedSquare);
                        buttons[row][col].setBackgroundColor(Color.RED);
                    } else {
                        // Fin de rafale
                        priseMultipleEnCours = false;
                        pionEnRafale = null;
                        selectedSquare = null;
                        changeTurn();
                    }

                } else if (clickedSquare.getColor() == selectedSquare.getColor()) {
                    if (priseMultipleEnCours) return; // Interdit de changer de pion pendant une rafale

                    clearHighlights();
                    selectedSquare = clickedSquare;
                    highlightAccessibleMoves(selectedSquare);
                    buttons[row][col].setBackgroundColor(Color.RED);
                } else {
                    if (priseMultipleEnCours) return;

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
    private void checkGameOver() {
        if (board.isGameOver()) {
            String winner = getWinner();
            showGameOverDialog(winner);
        } else if (hasNoValidMoves(tour)) {
            // Vérifier si le joueur actuel a des mouvements possibles
            String winner = (tour == Square.PieceColor.WHITE) ? "Noirs" : "Blancs";
            showGameOverDialog(winner);
        }
    }

    /**
     * Détermine le gagnant basé sur les pièces restantes
     */
    private String getWinner() {
        boolean hasWhite = false;
        boolean hasBlack = false;

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Square.PieceColor color = board.getPieceColor(i, j);
                if (color == Square.PieceColor.WHITE) hasWhite = true;
                if (color == Square.PieceColor.BLACK) hasBlack = true;
            }
        }

        if (hasWhite && !hasBlack) return "Blancs";
        if (hasBlack && !hasWhite) return "Noirs";
        return "Égalité"; // Cas improbable mais possible
    }

    /**
     * Vérifie si un joueur a des mouvements valides
     */
    private boolean hasNoValidMoves(Square.PieceColor playerColor) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Square square = board.getSquare(i, j);
                if (square.getColor() == playerColor) {
                    List<Square> moves = board.getAccessibleMoves(i, j);
                    if (!moves.isEmpty()) {
                        return false; // Le joueur a au moins un mouvement
                    }
                }
            }
        }
        return true; // Aucun mouvement possible
    }

    /**
     * Affiche le dialog de fin de partie
     */
    private void showGameOverDialog(String winner) {
        ViewGroup layout = findViewById(R.id.main);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_game_over, null);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // Configurer le message de victoire
        TextView winnerText = popupView.findViewById(R.id.winnerText);
        winnerText.setText("Victoire des " + winner + " !");

        layout.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
            }
        });

        // Bouton nouvelle partie
        Button newGameButton = popupView.findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                restartGame(v);
            }
        });

        // Bouton retour au menu
        Button menuButton = popupView.findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Retour à MainActivity
            }
        });
    }
}