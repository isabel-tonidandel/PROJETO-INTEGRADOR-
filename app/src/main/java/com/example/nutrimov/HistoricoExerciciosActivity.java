package com.example.nutrimov;

import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class HistoricoExerciciosActivity extends AppCompatActivity {

    private LinearLayout layoutHistorico;
    private BancoDados bancoDados;
    private long idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_historico_exercicios);

        layoutHistorico = findViewById(
                R.id.layoutHistoricoExercicios
        );

        idUsuario = getIntent()
                .getLongExtra("id_usuario", -1);

        if (idUsuario <= 0) {

            mostrarAviso("Usuário não identificado.");

            return;
        }

        bancoDados = new BancoDados(this);

        carregarHistorico();
    }

    private void carregarHistorico() {

        layoutHistorico.removeAllViews();

        try (Cursor cursor =
                     bancoDados.buscarRegistrosExercicios(idUsuario)) {

            if (!cursor.moveToFirst()) {

                mostrarAviso("Nenhum exercício registrado.");

                return;
            }

            do {

                long idRegistro = cursor.getLong(0);

                String nome = cursor.getString(1);

                String data = cursor.getString(2);

                int duracao = cursor.isNull(3)
                        ? 0 : cursor.getInt(3);

                String observacoes = cursor.getString(4);

                criarCartao(
                        idRegistro,
                        nome,
                        data,
                        duracao,
                        observacoes
                );

            } while (cursor.moveToNext());

        } catch (android.database.SQLException e) {

            android.util.Log.e(
                    "NutriMov",
                    "Erro ao consultar exercícios",
                    e
            );

            mostrarAviso(
                    "Não foi possível consultar o histórico."
            );
        }
    }

    private void criarCartao(
            long idRegistro,
            String nome,
            String data,
            int duracao,
            String observacoes) {

        LinearLayout cartao = new LinearLayout(this);

        cartao.setOrientation(LinearLayout.VERTICAL);

        cartao.setPadding(
                dp(16), dp(16), dp(16), dp(16)
        );

        LinearLayout.LayoutParams parametros =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametros.setMargins(0, 0, 0, dp(12));

        cartao.setLayoutParams(parametros);

        GradientDrawable fundo = new GradientDrawable();

        fundo.setColor(
                ContextCompat.getColor(
                        this, R.color.fundo_campo
                )
        );

        fundo.setCornerRadius(dp(12));

        fundo.setStroke(
                dp(1),
                ContextCompat.getColor(
                        this, R.color.verde_claro
                )
        );

        cartao.setBackground(fundo);

        // Data do exercício

        TextView txtData = new TextView(this);

        txtData.setText(formatarData(data));

        txtData.setTextSize(14);

        txtData.setTextColor(
                ContextCompat.getColor(
                        this, R.color.texto_secundario
                )
        );

        cartao.addView(txtData);

        // Nome do exercício

        TextView txtNome = new TextView(this);

        txtNome.setText(nome);

        txtNome.setTextSize(20);

        txtNome.setTypeface(null, Typeface.BOLD);

        txtNome.setTextColor(
                ContextCompat.getColor(
                        this, R.color.texto_titulo
                )
        );

        txtNome.setPadding(
                0, dp(8), 0, dp(8)
        );

        cartao.addView(txtNome);

        // Duração do exercício

        TextView txtDuracao = new TextView(this);

        txtDuracao.setText(
                duracao > 0
                        ? "Duração: " + duracao + " minutos"
                        : "Duração: não informada"
        );

        txtDuracao.setTextSize(16);

        txtDuracao.setTextColor(
                ContextCompat.getColor(
                        this, R.color.texto_principal
                )
        );

        cartao.addView(txtDuracao);

        // Observações

        if (observacoes != null &&
                !observacoes.trim().isEmpty()) {

            TextView txtObservacoes = new TextView(this);

            txtObservacoes.setText(
                    "Observações: " + observacoes
            );

            txtObservacoes.setTextSize(16);

            txtObservacoes.setTextColor(
                    ContextCompat.getColor(
                            this, R.color.texto_principal
                    )
            );

            txtObservacoes.setPadding(
                    0, dp(4), 0, 0
            );

            cartao.addView(txtObservacoes);
        }

        // Linha divisória

        View divisor = new View(this);

        divisor.setBackgroundColor(
                ContextCompat.getColor(
                        this, R.color.verde_claro
                )
        );

        LinearLayout.LayoutParams parametrosDivisor =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(1)
                );

        parametrosDivisor.setMargins(
                0, dp(12), 0, dp(12)
        );

        cartao.addView(divisor, parametrosDivisor);

        // Área dos botões

        LinearLayout layoutBotoes = new LinearLayout(this);

        layoutBotoes.setOrientation(
                LinearLayout.HORIZONTAL
        );

        // Botão Editar

        Button btnEditar = criarBotao(
                "Editar",
                ContextCompat.getColor(
                        this, R.color.verde_principal
                )
        );

        LinearLayout.LayoutParams parametrosEditar =
                new LinearLayout.LayoutParams(
                        0, dp(48), 1
                );

        parametrosEditar.setMargins(
                0, 0, dp(8), 0
        );

        btnEditar.setLayoutParams(parametrosEditar);

        btnEditar.setOnClickListener(v ->
                abrirEdicao(
                        idRegistro,
                        nome,
                        duracao,
                        observacoes
                )
        );

        // Botão Excluir

        Button btnExcluir = criarBotao(
                "Excluir",
                Color.parseColor("#B71C1C")
        );

        LinearLayout.LayoutParams parametrosExcluir =
                new LinearLayout.LayoutParams(
                        0, dp(48), 1
                );

        btnExcluir.setLayoutParams(parametrosExcluir);

        btnExcluir.setOnClickListener(v ->
                confirmarExclusao(idRegistro, nome)
        );

        layoutBotoes.addView(btnEditar);

        layoutBotoes.addView(btnExcluir);

        cartao.addView(layoutBotoes);

        layoutHistorico.addView(cartao);
    }

    private Button criarBotao(
            String texto, int cor) {

        Button botao = new Button(this);

        botao.setText(texto);
        botao.setTextSize(14);
        botao.setAllCaps(false);
        botao.setTextColor(Color.WHITE);
        botao.setTypeface(null, Typeface.BOLD);

        botao.setBackgroundTintList(
                ColorStateList.valueOf(cor)
        );

        return botao;
    }

    private void abrirEdicao(
            long idRegistro,
            String nome,
            int duracao,
            String observacoes) {

        LinearLayout formulario = new LinearLayout(this);

        formulario.setOrientation(
                LinearLayout.VERTICAL
        );

        formulario.setPadding(
                dp(24), dp(8), dp(24), dp(16)
        );

        EditText edtNome = criarCampo(
                formulario,
                "Nome do exercício",
                nome,
                InputType.TYPE_CLASS_TEXT
        );

        EditText edtDuracao = criarCampo(
                formulario,
                "Duração (minutos)",
                duracao > 0 ? String.valueOf(duracao) : "",
                InputType.TYPE_CLASS_NUMBER
        );

        EditText edtObservacoes = criarCampo(
                formulario,
                "Observações (opcional)",
                observacoes == null ? "" : observacoes,
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE
        );

        edtObservacoes.setSingleLine(false);

        edtObservacoes.setMinLines(3);

        edtObservacoes.setGravity(
                Gravity.TOP | Gravity.START
        );

        edtObservacoes.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        ScrollView scrollView = new ScrollView(this);

        scrollView.addView(formulario);

        AlertDialog dialogo =
                new AlertDialog.Builder(this)

                        .setTitle("Editar exercício")

                        .setView(scrollView)

                        .setNegativeButton("Cancelar", null)

                        .setPositiveButton("Salvar", null)

                        .create();

        dialogo.setOnShowListener(d -> {

            Button btnSalvar = dialogo.getButton(
                    AlertDialog.BUTTON_POSITIVE
            );

            btnSalvar.setOnClickListener(v -> {

                String novoNome =
                        edtNome.getText().toString().trim();

                String textoDuracao =
                        edtDuracao.getText().toString().trim();

                String novasObservacoes =
                        edtObservacoes.getText().toString().trim();

                if (novoNome.isEmpty()) {

                    edtNome.setError(
                            "Informe o nome do exercício"
                    );

                    return;
                }

                int novaDuracao;

                try {

                    novaDuracao =
                            Integer.parseInt(textoDuracao);

                } catch (NumberFormatException e) {

                    edtDuracao.setError(
                            "Informe uma duração válida"
                    );

                    return;
                }

                if (novaDuracao <= 0) {

                    edtDuracao.setError(
                            "A duração deve ser maior que zero"
                    );

                    return;
                }

                boolean resultado =
                        bancoDados.editarExercicio(
                                idUsuario,
                                idRegistro,
                                novoNome,
                                novaDuracao,
                                novasObservacoes
                        );

                if (resultado) {

                    Toast.makeText(
                            this,
                            "Exercício atualizado com sucesso!",
                            Toast.LENGTH_SHORT
                    ).show();

                    dialogo.dismiss();

                    carregarHistorico();

                } else {

                    Toast.makeText(
                            this,
                            "Erro ao atualizar exercício.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        });

        dialogo.show();
    }

    private EditText criarCampo(
            LinearLayout formulario,
            String titulo,
            String valor,
            int tipoEntrada) {

        TextView rotulo = new TextView(this);

        rotulo.setText(titulo);

        rotulo.setTextSize(16);

        rotulo.setTypeface(null, Typeface.BOLD);

        rotulo.setTextColor(
                ContextCompat.getColor(
                        this, R.color.texto_principal
                )
        );

        rotulo.setPadding(
                0, dp(12), 0, dp(4)
        );

        formulario.addView(rotulo);

        EditText campo = new EditText(this);

        campo.setInputType(tipoEntrada);

        campo.setText(valor);

        campo.setTextSize(16);

        campo.setSingleLine(true);

        formulario.addView(
                campo,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(56)
                )
        );

        return campo;
    }

    private void confirmarExclusao(
            long idRegistro,
            String nome) {

        AlertDialog dialogo =
                new AlertDialog.Builder(this)

                        .setTitle("Excluir exercício?")

                        .setMessage(
                                "Deseja excluir o registro de " +
                                        nome + "?\n\n" +
                                        "Esta ação não poderá ser desfeita."
                        )

                        .setNegativeButton("Cancelar", null)

                        .setPositiveButton(
                                "Excluir",
                                (d, which) -> {

                                    boolean resultado =
                                            bancoDados.excluirExercicio(
                                                    idUsuario,
                                                    idRegistro
                                            );

                                    if (resultado) {

                                        Toast.makeText(
                                                this,
                                                "Exercício excluído com sucesso!",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        carregarHistorico();

                                    } else {

                                        Toast.makeText(
                                                this,
                                                "Erro ao excluir exercício.",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                }
                        )

                        .create();

        dialogo.show();

        dialogo.getButton(
                AlertDialog.BUTTON_POSITIVE
        ).setTextColor(
                Color.parseColor("#B71C1C")
        );
    }

    private String formatarData(String data) {

        if (data == null) {
            return "";
        }

        String[] partes = data.split("-");

        if (partes.length == 3) {

            return partes[2] + "/" +
                    partes[1] + "/" + partes[0];
        }

        return data;
    }

    private void mostrarAviso(String mensagem) {

        layoutHistorico.removeAllViews();

        TextView aviso = new TextView(this);

        aviso.setText(mensagem);

        aviso.setTextSize(16);

        aviso.setTextColor(
                ContextCompat.getColor(
                        this, R.color.texto_secundario
                )
        );

        aviso.setGravity(Gravity.CENTER);

        aviso.setPadding(
                dp(16), dp(16), dp(16), dp(16)
        );

        layoutHistorico.addView(aviso);
    }

    private int dp(float valor) {

        return Math.round(
                valor *
                        getResources()
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