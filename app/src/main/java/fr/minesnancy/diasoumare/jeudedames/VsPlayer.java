package fr.minesnancy.diasoumare.jeudedames;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

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
        int layoutSize = 35 * 20;
        int screenSize = Resources.getSystem().getDisplayMetrics().widthPixels;
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
            }
        }
    }

}