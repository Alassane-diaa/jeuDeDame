package fr.minesnancy.diasoumare.jeudedames;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class VsPlayer extends AppCompatActivity {

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
        for(int i = 1; i <= 10; i++){
            for (int j = 1; j <= 10; j++){
                ImageButton bouton = new ImageButton(getApplicationContext());
                if((i+j) % 2 == 0) {
                    if (i <= 4) {
                        bouton.setImageResource(R.drawable.piece_noire);
                        bouton.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    } else if (i >= 7) {
                        bouton.setImageResource(R.drawable.piece_blanche);
                        bouton.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                }
                bouton.setBackgroundColor(Color.TRANSPARENT);
                layout.addView(bouton);
                ViewGroup.LayoutParams boutonLayoutParams = bouton.getLayoutParams();
                boutonLayoutParams.width = boutonSize;
                boutonLayoutParams.height = boutonSize;
                bouton.setLayoutParams(boutonLayoutParams);
                // Comme je t'avais dit, je crée juste les boutons, pas les listeners (c'est à dire ce qu'ils font quand on clique dessus)
                // Il serait peut être pertinent de les mettre dans un tableau pour pouvoir les manipuler plus facilement
                // Genre un tableau de 10x10, et chaque bouton serait à la position [i][j] dans le tableau
                // Et tu pourrais repérer un bouton par son id en faisant bouton.setID(*) avec * un truc distinctif qui renseigne sur la couleur et la position
                // Je me suis aussi rendu compte que je n'ai pas mis d'image pour le cas où une piece devient une dame
                // Je vais faire ça après en fonction de la logique que tu veux implémenter
            }
        }
    }
    public void changeTurn(){
        TextView textView = findViewById(R.id.turn);
        if(textView.getText().toString().equals("Tour des blancs")){
            textView.setText("Tour des noirs");
        } else {
            textView.setText("Tour des blancs");
        }
        // Là je change juste le texte, je te laisse activer et désactiver les boutons convenablement
    }

}