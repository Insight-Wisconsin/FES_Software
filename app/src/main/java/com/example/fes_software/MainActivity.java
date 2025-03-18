package com.example.fes_software;

import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView HeightNumber = (TextView) findViewById(R.id.textViewHeight);
        TextView WeightNumber = (TextView) findViewById(R.id.textViewWeight);
        Button Heightbutton = findViewById(R.id.HeightButton);
        Button Weightbutton = findViewById(R.id.WeightButton);
        EditText HeighteditText = findViewById(R.id.Height);
        EditText WeighteditText = findViewById(R.id.Weight);
        Heightbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = HeighteditText.getText().toString();
                HeightNumber.setText(text);
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();

            }
        });
        Weightbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = WeighteditText.getText().toString();
                WeightNumber.setText(text);
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();

            }
        });

    }
}