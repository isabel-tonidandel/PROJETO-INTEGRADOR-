package com.example.nutrimov;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class AlimentacaoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alimentacao);

        Spinner spinnerRefeicao = findViewById(R.id.spinnerRefeicao);
        EditText edtAlimento = findViewById(R.id.edtAlimento);
        EditText edtQuantidade = findViewById(R.id.edtQuantidade);
        Button btnSalvarRefeicao = findViewById(R.id.btnSalvarRefeicao);
        Button btnHistoricoAlimentacao =
                findViewById(R.id.btnHistoricoAlimentacao);

        long idUsuario = getIntent().getLongExtra("id_usuario", -1);
        btnHistoricoAlimentacao.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AlimentacaoActivity.this,
                    HistoricoAlimentarActivity.class
            );

            intent.putExtra("id_usuario", idUsuario);

            startActivity(intent);
        });

        String[] tiposRefeicao = {
                "Selecione a refeição",
                "Café da manhã",
                "Almoço",
                "Lanche",
                "Jantar",
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tiposRefeicao
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerRefeicao.setAdapter(adapter);

        btnSalvarRefeicao.setOnClickListener(v -> {

            String tipo = spinnerRefeicao.getSelectedItem().toString();
            String alimento = edtAlimento.getText().toString().trim();
            String quantidadeTexto = edtQuantidade.getText().toString().trim();

            if (idUsuario <= 0) {
                Toast.makeText(this, "Usuário não identificado",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (spinnerRefeicao.getSelectedItemPosition() == 0
                    || alimento.isEmpty() || quantidadeTexto.isEmpty()) {

                Toast.makeText(this, "Preencha todos os campos",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double quantidade = Double.parseDouble(
                        quantidadeTexto.replace(",", ".")
                );

                if (quantidade <= 0 || !Double.isFinite(quantidade)) {
                    Toast.makeText(this, "Informe uma quantidade válida",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                BancoDados bancoDados = new BancoDados(this);

                boolean sucesso = bancoDados.salvarRefeicao(
                        idUsuario, tipo, alimento, quantidade
                );

                if (sucesso) {
                    Toast.makeText(this, "Refeição salva com sucesso!",
                            Toast.LENGTH_SHORT).show();

                    edtAlimento.setText("");
                    edtQuantidade.setText("");
                    spinnerRefeicao.setSelection(0);

                } else {
                    Toast.makeText(this, "Erro ao salvar refeição",
                            Toast.LENGTH_SHORT).show();
                }

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Digite uma quantidade numérica",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}