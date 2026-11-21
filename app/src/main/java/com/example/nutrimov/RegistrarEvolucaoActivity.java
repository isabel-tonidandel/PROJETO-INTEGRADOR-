package com.example.nutrimov;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistrarEvolucaoActivity extends AppCompatActivity {

    private BancoDados bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_evolucao);

        TextView txtDataAvaliacao = findViewById(R.id.txtDataAvaliacao);

        EditText edtPeso = findViewById(R.id.edtPeso);
        EditText edtAltura = findViewById(R.id.edtAltura);

        Button btnSalvarEvolucao = findViewById(
                R.id.btnSalvarEvolucao
        );

        long idUsuario = getIntent().getLongExtra("id_usuario", -1);

        if (idUsuario <= 0) {

            Toast.makeText(this,
                    "Usuário não identificado.",
                    Toast.LENGTH_SHORT).show();

            finish();
            return;
        }

        bancoDados = new BancoDados(this);

        // Exibir a data atual

        String dataAtual = new SimpleDateFormat(
                "dd/MM/yyyy",
                new Locale("pt", "BR")
        ).format(new Date());

        txtDataAvaliacao.setText("Data da avaliação: " + dataAtual);

        // Botão Salvar evolução

        btnSalvarEvolucao.setOnClickListener(v -> {

            String pesoTexto = edtPeso.getText().toString().trim();
            String alturaTexto = edtAltura.getText().toString().trim();

            if (pesoTexto.isEmpty() || alturaTexto.isEmpty()) {

                Toast.makeText(this,
                        "Preencha o peso e a altura.",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            try {

                double peso = Double.parseDouble(
                        pesoTexto.replace(",", ".")
                );

                double altura = Double.parseDouble(
                        alturaTexto.replace(",", ".")
                );

                if (peso <= 0 || altura <= 0 ||
                        Double.isNaN(peso) || Double.isInfinite(peso) ||
                        Double.isNaN(altura) || Double.isInfinite(altura)) {

                    Toast.makeText(this,
                            "Informe valores válidos.",
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                boolean resultado = bancoDados.salvarEvolucao(
                        idUsuario, peso, altura
                );

                if (resultado) {

                    Toast.makeText(this,
                            "Evolução registrada com sucesso!",
                            Toast.LENGTH_SHORT).show();

                    finish();

                } else {

                    Toast.makeText(this,
                            "Erro ao registrar evolução.",
                            Toast.LENGTH_SHORT).show();
                }

            } catch (NumberFormatException e) {

                Toast.makeText(this,
                        "Verifique os valores informados.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {

        if (bancoDados != null) {
            bancoDados.close();
        }

        super.onDestroy();
    }
}