package com.example.nutrimov;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

public class ExerciciosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercicios);

        EditText edtExercicio = findViewById(R.id.edtExercicio);
        EditText edtDuracao = findViewById(R.id.edtDuracao);
        EditText edtObservacoes = findViewById(R.id.edtObservacoes);
        Button btnSalvarExercicio = findViewById(R.id.btnSalvarExercicio);
        Button btnHistoricoExercicios = findViewById(R.id.btnHistoricoExercicios);

        long idUsuario = getIntent().getLongExtra("id_usuario", -1);

        btnSalvarExercicio.setOnClickListener(v -> {

            String nome = edtExercicio.getText().toString().trim();
            String duracaoTexto = edtDuracao.getText().toString().trim();
            String observacoes = edtObservacoes.getText().toString().trim();

            if (idUsuario <= 0) {
                Toast.makeText(this, "Usuário não identificado",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (nome.isEmpty() || duracaoTexto.isEmpty()) {
                Toast.makeText(this, "Preencha os campos obrigatórios",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int duracao = Integer.parseInt(duracaoTexto);

                if (duracao <= 0) {
                    Toast.makeText(this, "Informe uma duração válida",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                BancoDados bancoDados = new BancoDados(this);

                boolean sucesso = bancoDados.salvarExercicio(
                        idUsuario, nome, duracao, observacoes
                );

                if (sucesso) {
                    Toast.makeText(this, "Exercício salvo com sucesso!",
                            Toast.LENGTH_SHORT).show();

                    edtExercicio.setText("");
                    edtDuracao.setText("");
                    edtObservacoes.setText("");

                } else {
                    Toast.makeText(this, "Erro ao salvar exercício",
                            Toast.LENGTH_SHORT).show();
                }

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Digite uma duração numérica válida",
                        Toast.LENGTH_SHORT).show();
            }
        });
        // Botão Histórico de Exercícios

        btnHistoricoExercicios.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ExerciciosActivity.this,
                    HistoricoExerciciosActivity.class
            );

            intent.putExtra("id_usuario", idUsuario);

            startActivity(intent);
        });
    }

}