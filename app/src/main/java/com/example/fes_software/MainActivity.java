package com.example.fes_software;

import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView GyroNumber = (TextView) findViewById(R.id.textView2);

        Button button = findViewById(R.id.button1);
        EditText editText1 = findViewById(R.id.editText1);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String text = editText1.getText().toString();
                GyroNumber.setText(text);
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();

            }
        });

    }
}