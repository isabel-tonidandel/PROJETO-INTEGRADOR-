package com.example.nutrimov;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btnAlimentacao = findViewById(R.id.btnAlimentacao);
        Button btnExercicios = findViewById(R.id.btnExercicios);
        Button btnTreinos = findViewById(R.id.btnTreinos);
        Button btnPerfil = findViewById(R.id.btnPerfil);
        Button btnEvolucao = findViewById(R.id.btnEvolucao);

        long idUsuario = getIntent().getLongExtra("id_usuario", -1);

        // Botão Alimentação
        btnAlimentacao.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    AlimentacaoActivity.class
            );

            intent.putExtra("id_usuario", idUsuario);
            startActivity(intent);
        });

        // Botão Exercícios
        btnExercicios.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    ExerciciosActivity.class
            );

            intent.putExtra("id_usuario", idUsuario);
            startActivity(intent);
        });

        // Botão Treinos
        btnTreinos.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    TreinosActivity.class
            );

            intent.putExtra("id_usuario", idUsuario);
            startActivity(intent);
        });

        // Botão Meu Perfil
        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    PerfilActivity.class
            );

            intent.putExtra("id_usuario", idUsuario);
            startActivity(intent);
        });

        // Botão Minha Evolução
        btnEvolucao.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    ConsultarEvolucaoActivity.class
            );

            intent.putExtra("id_usuario", idUsuario);
            startActivity(intent);
        });
    }
}