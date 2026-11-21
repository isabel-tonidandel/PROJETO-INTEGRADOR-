package com.example.nutrimov;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class TreinosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treinos);

        EditText edtNomeTreino = findViewById(R.id.edtNomeTreino);
        EditText edtDescricaoTreino = findViewById(R.id.edtDescricaoTreino);
        EditText edtDiasSemana = findViewById(R.id.edtDiasSemana);

        Button btnSalvarTreino = findViewById(R.id.btnSalvarTreino);
        Button btnConsultarTreinos = findViewById(R.id.btnConsultarTreinos);

        long idUsuario = getIntent().getLongExtra("id_usuario", -1);

        BancoDados bancoDados = new BancoDados(this);

        btnSalvarTreino.setOnClickListener(v -> {

            String nome = edtNomeTreino.getText().toString().trim();
            String descricao = edtDescricaoTreino.getText().toString().trim();
            String diasSemana = edtDiasSemana.getText().toString().trim();

            if (idUsuario <= 0) {
                Toast.makeText(this, "Usuário não identificado",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (nome.isEmpty()) {
                Toast.makeText(this, "Informe o nome do treino",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            boolean sucesso = bancoDados.salvarTreino(
                    idUsuario, nome, descricao, diasSemana
            );

            if (sucesso) {

                Toast.makeText(this, "Treino salvo com sucesso!",
                        Toast.LENGTH_SHORT).show();

                edtNomeTreino.setText("");
                edtDescricaoTreino.setText("");
                edtDiasSemana.setText("");

            } else {

                Toast.makeText(this, "Erro ao salvar treino",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Botão Consultar treinos
        btnConsultarTreinos.setOnClickListener(v -> {

            Intent intent = new Intent(
                    TreinosActivity.this,
                    ConsultarTreinosActivity.class
            );

            intent.putExtra("id_usuario", idUsuario);

            startActivity(intent);
        });
    }
}