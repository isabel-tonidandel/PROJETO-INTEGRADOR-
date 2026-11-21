package com.example.nutrimov;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdicionarExercicioTreinoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_exercicio_treino);

        TextView txtNomeTreino = findViewById(R.id.txtNomeTreino);

        EditText edtNomeExercicio = findViewById(R.id.edtNomeExercicio);
        EditText edtGrupoMuscular = findViewById(R.id.edtGrupoMuscular);
        EditText edtSeries = findViewById(R.id.edtSeries);
        EditText edtRepeticoes = findViewById(R.id.edtRepeticoes);
        EditText edtCarga = findViewById(R.id.edtCarga);

        Button btnAdicionarExercicio = findViewById(R.id.btnAdicionarExercicio);

        long idUsuario = getIntent().getLongExtra("id_usuario", -1);
        long idTreino = getIntent().getLongExtra("id_treino", -1);

        String nomeTreino = getIntent().getStringExtra("nome_treino");

        if (nomeTreino != null) {
            txtNomeTreino.setText(nomeTreino);
        }

        BancoDados bancoDados = new BancoDados(this);

        btnAdicionarExercicio.setOnClickListener(v -> {

            String nome = edtNomeExercicio.getText().toString().trim();
            String grupoMuscular = edtGrupoMuscular.getText().toString().trim();
            String seriesTexto = edtSeries.getText().toString().trim();
            String repeticoesTexto = edtRepeticoes.getText().toString().trim();
            String cargaTexto = edtCarga.getText().toString().trim();

            if (idUsuario <= 0 || idTreino <= 0) {
                Toast.makeText(this, "Treino ou usuário não identificado",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (nome.isEmpty() || seriesTexto.isEmpty()
                    || repeticoesTexto.isEmpty()) {

                Toast.makeText(this, "Preencha os campos obrigatórios",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try {

                int series = Integer.parseInt(seriesTexto);
                int repeticoes = Integer.parseInt(repeticoesTexto);

                Double carga = null;

                if (!cargaTexto.isEmpty()) {
                    carga = Double.parseDouble(
                            cargaTexto.replace(",", ".")
                    );
                }

                if (series <= 0 || repeticoes <= 0 ||
                        (carga != null && (carga < 0 ||
                                carga.isNaN() || carga.isInfinite()))) {

                    Toast.makeText(this, "Informe valores válidos",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean sucesso = bancoDados.adicionarExercicioTreino(
                        idUsuario,
                        idTreino,
                        nome,
                        grupoMuscular,
                        series,
                        repeticoes,
                        carga
                );

                if (sucesso) {

                    Toast.makeText(this, "Exercício adicionado com sucesso!",
                            Toast.LENGTH_SHORT).show();

                    edtNomeExercicio.setText("");
                    edtGrupoMuscular.setText("");
                    edtSeries.setText("");
                    edtRepeticoes.setText("");
                    edtCarga.setText("");

                } else {

                    Toast.makeText(this, "Erro ao adicionar exercício",
                            Toast.LENGTH_SHORT).show();
                }

            } catch (NumberFormatException e) {

                Toast.makeText(this, "Informe valores numéricos válidos",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}