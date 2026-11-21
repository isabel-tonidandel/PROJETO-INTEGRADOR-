package com.example.nutrimov;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    private BancoDados bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        TextView txtNomeUsuario = findViewById(R.id.txtNomeUsuario);
        TextView txtEmailUsuario = findViewById(R.id.txtEmailUsuario);

        EditText edtIdade = findViewById(R.id.edtIdade);
        EditText edtAltura = findViewById(R.id.edtAltura);
        EditText edtPeso = findViewById(R.id.edtPeso);
        EditText edtObjetivo = findViewById(R.id.edtObjetivo);

        Button btnSalvarPerfil = findViewById(R.id.btnSalvarPerfil);

        Button btnRegistrarEvolucao =
                findViewById(R.id.btnRegistrarEvolucao);

        Button btnConsultarEvolucao =
                findViewById(R.id.btnConsultarEvolucao);

        Button btnExcluirConta =
                findViewById(R.id.btnExcluirConta);

        long idUsuario = getIntent().getLongExtra(
                "id_usuario", -1
        );

        if (idUsuario <= 0) {

            Toast.makeText(this,
                    "Usuário não identificado.",
                    Toast.LENGTH_SHORT).show();

            finish();
            return;
        }

        bancoDados = new BancoDados(this);

        // Carregar os dados do usuário

        try (Cursor cursor = bancoDados.buscarPerfil(idUsuario)) {

            if (cursor.moveToFirst()) {

                txtNomeUsuario.setText(cursor.getString(0));
                txtEmailUsuario.setText(cursor.getString(1));

                if (!cursor.isNull(2)) {
                    edtIdade.setText(cursor.getString(2));
                }

                if (!cursor.isNull(3)) {
                    edtAltura.setText(cursor.getString(3));
                }

                if (!cursor.isNull(4)) {
                    edtPeso.setText(cursor.getString(4));
                }

                if (!cursor.isNull(5)) {
                    edtObjetivo.setText(cursor.getString(5));
                }
            }
        }

        // Botão Salvar perfil

        btnSalvarPerfil.setOnClickListener(v -> {

            String idadeTexto =
                    edtIdade.getText().toString().trim();

            String alturaTexto =
                    edtAltura.getText().toString().trim();

            String pesoTexto =
                    edtPeso.getText().toString().trim();

            String objetivo =
                    edtObjetivo.getText().toString().trim();

            if (idadeTexto.isEmpty() ||
                    alturaTexto.isEmpty() ||
                    pesoTexto.isEmpty() ||
                    objetivo.isEmpty()) {

                Toast.makeText(this,
                        "Preencha todos os campos.",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            try {

                int idade = Integer.parseInt(idadeTexto);

                double altura = Double.parseDouble(
                        alturaTexto.replace(",", ".")
                );

                double peso = Double.parseDouble(
                        pesoTexto.replace(",", ".")
                );

                if (idade <= 0 || altura <= 0 || peso <= 0 ||
                        Double.isNaN(altura) ||
                        Double.isInfinite(altura) ||
                        Double.isNaN(peso) ||
                        Double.isInfinite(peso)) {

                    Toast.makeText(this,
                            "Informe valores válidos.",
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                boolean resultado = bancoDados.salvarPerfil(
                        idUsuario,
                        idade,
                        altura,
                        peso,
                        objetivo
                );

                if (resultado) {

                    Toast.makeText(this,
                            "Perfil salvo com sucesso!",
                            Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(this,
                            "Erro ao salvar perfil.",
                            Toast.LENGTH_SHORT).show();
                }

            } catch (NumberFormatException e) {

                Toast.makeText(this,
                        "Verifique os valores informados.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Botão Registrar evolução

        btnRegistrarEvolucao.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PerfilActivity.this,
                    RegistrarEvolucaoActivity.class
            );

            intent.putExtra("id_usuario", idUsuario);

            startActivity(intent);
        });

        // Botão Consultar evolução

        btnConsultarEvolucao.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PerfilActivity.this,
                    ConsultarEvolucaoActivity.class
            );

            intent.putExtra("id_usuario", idUsuario);

            startActivity(intent);
        });

        // Botão Excluir conta

        btnExcluirConta.setOnClickListener(v -> {

            confirmarExclusaoConta(idUsuario);
        });
    }

    // Janela de confirmação da exclusão

    private void confirmarExclusaoConta(long idUsuario) {

        LinearLayout layout = new LinearLayout(this);

        layout.setOrientation(LinearLayout.VERTICAL);

        layout.setPadding(
                dp(24),
                dp(16),
                dp(24),
                dp(8)
        );

        TextView txtAviso = new TextView(this);

        txtAviso.setText(
                "Todos os seus dados serão excluídos " +
                        "permanentemente, incluindo seu perfil, " +
                        "alimentação, exercícios, treinos e evolução corporal.\n\n" +
                        "Esta ação não poderá ser desfeita.\n\n" +
                        "Confirme sua senha para continuar."
        );

        txtAviso.setTextSize(16);

        txtAviso.setTextColor(
                getColor(R.color.texto_principal)
        );

        layout.addView(txtAviso);

        // Campo de confirmação da senha

        EditText edtSenha = new EditText(this);

        edtSenha.setHint("Digite sua senha");

        edtSenha.setTextSize(16);

        edtSenha.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        edtSenha.setSingleLine(true);
        edtSenha.setTransformationMethod(
                PasswordTransformationMethod.getInstance()
        );

        LinearLayout.LayoutParams parametros =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(56)
                );

        parametros.topMargin = dp(16);

        edtSenha.setLayoutParams(parametros);

        layout.addView(edtSenha);

        // Criar janela de confirmação

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Excluir conta?")
                .setView(layout)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Excluir conta", null)
                .create();

        dialog.setOnShowListener(d -> {

            Button btnConfirmar = dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
            );

            Button btnCancelar = dialog.getButton(
                    AlertDialog.BUTTON_NEGATIVE
            );

            btnConfirmar.setTextColor(
                    Color.parseColor("#B71C1C")
            );

            btnCancelar.setTextColor(
                    getColor(R.color.verde_principal)
            );

            btnConfirmar.setOnClickListener(v -> {

                String senha = edtSenha.getText().toString();

                if (senha.isEmpty()) {

                    edtSenha.setError("Informe sua senha.");

                    edtSenha.requestFocus();

                    return;
                }

                // Evitar cliques repetidos

                btnConfirmar.setEnabled(false);

                boolean resultado;

                try {

                    resultado = bancoDados.excluirConta(
                            idUsuario,
                            senha
                    );

                } catch (android.database.SQLException e) {

                    resultado = false;

                    android.util.Log.e(
                            "NutriMov",
                            "Erro ao excluir conta",
                            e
                    );
                }

                if (resultado) {

                    edtSenha.setText("");

                    dialog.dismiss();

                    Toast.makeText(
                            PerfilActivity.this,
                            "Conta excluída com sucesso!",
                            Toast.LENGTH_LONG
                    ).show();

                    // Retornar à tela de login

                    Intent intent = new Intent(
                            PerfilActivity.this,
                            MainActivity.class
                    );

                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );

                    startActivity(intent);

                    finish();

                } else {

                    btnConfirmar.setEnabled(true);

                    edtSenha.setText("");

                    edtSenha.requestFocus();

                    Toast.makeText(
                            PerfilActivity.this,
                            "Senha incorreta ou não foi possível excluir a conta.",
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        });

        dialog.show();
    }

    // Converter dp em pixels

    private int dp(int valor) {

        return Math.round(
                valor * getResources()
                        .getDisplayMetrics().density
        );
    }

    @Override
    protected void onDestroy() {

        if (bancoDados != null) {

            bancoDados.close();
        }

        super.onDestroy();
    }
}