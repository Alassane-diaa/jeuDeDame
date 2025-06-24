package fr.minesnancy.diasoumare.jeudedames;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;

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
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int boutonSize = width / 11 ;
        int height = displayMetrics.heightPixels;
        for(int i = 1; i <= 10; i++){
            for (int j = 1; j <= 10; j++){
                Button bouton = new Button(getApplicationContext());
                if((i+j) % 2 == 0) {
                    bouton.setBackgroundColor(Color.WHITE);
                } else{
                    bouton.setBackgroundColor(Color.BLACK);
                }
                layout.addView(bouton);
                ViewGroup.LayoutParams boutonLayoutParams = bouton.getLayoutParams();
                boutonLayoutParams.width = boutonSize;
                boutonLayoutParams.height = boutonSize;
                bouton.setLayoutParams(boutonLayoutParams);
            }
        }
    }

}