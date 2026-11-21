package com.example.nutrimov;

import android.database.Cursor;
import android.os.Bundle;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
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

public class ConsultarTreinosActivity extends AppCompatActivity {

    private LinearLayout layoutListaTreinos;
    private BancoDados bancoDados;
    private long idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_consultar_treinos);

        layoutListaTreinos = findViewById(
                R.id.layoutListaTreinos
        );

        idUsuario = getIntent()
                .getLongExtra("id_usuario", -1);

        if (idUsuario <= 0) {

            mostrarAviso("Usuário não identificado.");

            return;
        }

        bancoDados = new BancoDados(this);

        carregarTreinos();
    }

    private void carregarTreinos() {

        layoutListaTreinos.removeAllViews();

        try (Cursor cursor =
                     bancoDados.buscarTreinos(idUsuario)) {

            if (!cursor.moveToFirst()) {

                mostrarAviso("Nenhum treino cadastrado.");

                return;
            }

            do {

                long idTreino = cursor.getLong(0);

                String nome = cursor.getString(1);

                String descricao = cursor.getString(2);

                String diasSemana = cursor.getString(3);

                criarCartao(
                        idTreino,
                        nome,
                        descricao,
                        diasSemana
                );

            } while (cursor.moveToNext());

        } catch (android.database.SQLException e) {

            android.util.Log.e(
                    "NutriMov",
                    "Erro ao consultar treinos",
                    e
            );

            mostrarAviso(
                    "Não foi possível consultar os treinos."
            );
        }
    }

    private void criarCartao(
            long idTreino,
            String nome,
            String descricao,
            String diasSemana) {

        // Cartão do treino

        LinearLayout cartao = new LinearLayout(this);

        cartao.setOrientation(LinearLayout.VERTICAL);

        cartao.setPadding(
                dp(16), dp(16), dp(16), dp(16)
        );

        GradientDrawable fundo = new GradientDrawable();

        fundo.setColor(ContextCompat.getColor(
                this, R.color.fundo_campo
        ));

        fundo.setCornerRadius(dp(12));

        fundo.setStroke(
                dp(1),
                ContextCompat.getColor(
                        this, R.color.verde_claro
                )
        );

        cartao.setBackground(fundo);

        LinearLayout.LayoutParams paramsCartao =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        paramsCartao.setMargins(0, 0, 0, dp(16));

        // Nome do treino

        TextView titulo = new TextView(this);

        titulo.setText(nome);

        titulo.setTextSize(20);

        titulo.setTypeface(null, Typeface.BOLD);

        titulo.setTextColor(ContextCompat.getColor(
                this, R.color.texto_titulo
        ));

        titulo.setPadding(0, 0, 0, dp(12));

        cartao.addView(titulo);

        // Descrição e dias da semana

        String texto = "";

        if (descricao != null &&
                !descricao.trim().isEmpty()) {

            texto += "Descrição: " + descricao;
        }

        if (diasSemana != null &&
                !diasSemana.trim().isEmpty()) {

            if (!texto.isEmpty()) {

                texto += "\n";
            }

            texto += "Dias: " + diasSemana;
        }

        if (!texto.isEmpty()) {

            TextView registro = new TextView(this);

            registro.setText(texto);

            registro.setTextSize(16);

            registro.setTextColor(
                    ContextCompat.getColor(
                            this, R.color.texto_principal
                    )
            );

            registro.setLineSpacing(dp(4), 1.0f);

            registro.setPadding(0, 0, 0, dp(16));

            cartao.addView(registro);
        }

        // Botão adicionar exercícios

        Button btnAdicionar = criarBotao(
                "Adicionar exercícios"
        );

        btnAdicionar.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ConsultarTreinosActivity.this,
                    AdicionarExercicioTreinoActivity.class
            );

            intent.putExtra("id_usuario", idUsuario);

            intent.putExtra("id_treino", idTreino);

            intent.putExtra("nome_treino", nome);

            startActivity(intent);
        });

        LinearLayout.LayoutParams paramsAdicionar =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(60)
                );

        paramsAdicionar.setMargins(0, 0, 0, dp(8));

        cartao.addView(btnAdicionar, paramsAdicionar);

        // Botão visualizar exercícios

        Button btnVisualizar = criarBotao(
                "Visualizar exercícios"
        );

        btnVisualizar.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ConsultarTreinosActivity.this,
                    VisualizarExerciciosTreinoActivity.class
            );

            intent.putExtra("id_usuario", idUsuario);

            intent.putExtra("id_treino", idTreino);

            intent.putExtra("nome_treino", nome);

            startActivity(intent);
        });

        LinearLayout.LayoutParams paramsVisualizar =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(60)
                );

        cartao.addView(btnVisualizar, paramsVisualizar);

        // Linha divisória

        View divisor = new View(this);

        divisor.setBackgroundColor(
                ContextCompat.getColor(
                        this, R.color.verde_claro
                )
        );

        LinearLayout.LayoutParams paramsDivisor =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(1)
                );

        paramsDivisor.setMargins(
                0, dp(12), 0, dp(12)
        );

        cartao.addView(divisor, paramsDivisor);

        // Área dos botões de edição e exclusão

        LinearLayout layoutBotoes = new LinearLayout(this);

        layoutBotoes.setOrientation(
                LinearLayout.HORIZONTAL
        );

        // Botão Editar

        Button btnEditar = criarBotao("Editar");

        btnEditar.setTextSize(14);

        LinearLayout.LayoutParams paramsEditar =
                new LinearLayout.LayoutParams(
                        0, dp(48), 1
                );

        paramsEditar.setMargins(0, 0, dp(8), 0);

        btnEditar.setOnClickListener(v ->

                abrirEdicao(
                        idTreino,
                        nome,
                        descricao,
                        diasSemana
                )
        );

        layoutBotoes.addView(btnEditar, paramsEditar);

        // Botão Excluir

        Button btnExcluir = criarBotao("Excluir");

        btnExcluir.setTextSize(14);

        btnExcluir.setBackgroundTintList(
                ColorStateList.valueOf(
                        Color.parseColor("#B71C1C")
                )
        );

        LinearLayout.LayoutParams paramsExcluir =
                new LinearLayout.LayoutParams(
                        0, dp(48), 1
                );

        btnExcluir.setOnClickListener(v ->

                confirmarExclusao(idTreino, nome)
        );

        layoutBotoes.addView(btnExcluir, paramsExcluir);

        cartao.addView(layoutBotoes);

        layoutListaTreinos.addView(cartao, paramsCartao);
    }

    private void abrirEdicao(
            long idTreino,
            String nome,
            String descricao,
            String diasSemana) {

        LinearLayout formulario = new LinearLayout(this);

        formulario.setOrientation(
                LinearLayout.VERTICAL
        );

        formulario.setPadding(
                dp(24), dp(8), dp(24), dp(16)
        );

        // Campo nome

        EditText edtNome = criarCampo(
                formulario,
                "Nome do treino",
                nome,
                false
        );

        // Campo descrição

        EditText edtDescricao = criarCampo(
                formulario,
                "Descrição (opcional)",
                descricao,
                true
        );

        // Campo dias da semana

        EditText edtDiasSemana = criarCampo(
                formulario,
                "Dias da semana (opcional)",
                diasSemana,
                false
        );

        ScrollView scrollView = new ScrollView(this);

        scrollView.addView(formulario);

        AlertDialog dialogo =
                new AlertDialog.Builder(this)

                        .setTitle("Editar treino")

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

                String novaDescricao =
                        edtDescricao.getText().toString().trim();

                String novosDias =
                        edtDiasSemana.getText().toString().trim();

                if (novoNome.isEmpty()) {

                    edtNome.setError(
                            "Informe o nome do treino"
                    );

                    return;
                }

                boolean resultado =
                        bancoDados.editarTreino(
                                idUsuario,
                                idTreino,
                                novoNome,
                                novaDescricao,
                                novosDias
                        );

                if (resultado) {

                    Toast.makeText(
                            this,
                            "Treino atualizado com sucesso!",
                            Toast.LENGTH_SHORT
                    ).show();

                    dialogo.dismiss();

                    carregarTreinos();

                } else {

                    Toast.makeText(
                            this,
                            "Erro ao atualizar treino.",
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
            boolean multilinha) {

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

        campo.setText(valor == null ? "" : valor);

        campo.setTextSize(16);

        if (multilinha) {

            campo.setInputType(
                    InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_FLAG_MULTI_LINE
            );

            campo.setSingleLine(false);

            campo.setMinLines(3);

            campo.setGravity(
                    Gravity.TOP | Gravity.START
            );

        } else {

            campo.setInputType(
                    InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            );

            campo.setSingleLine(true);
        }

        formulario.addView(
                campo,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        multilinha
                                ? LinearLayout.LayoutParams.WRAP_CONTENT
                                : dp(56)
                )
        );

        return campo;
    }

    private void confirmarExclusao(
            long idTreino,
            String nome) {

        AlertDialog dialogo =
                new AlertDialog.Builder(this)

                        .setTitle("Excluir treino?")

                        .setMessage(
                                "Deseja excluir o treino " +
                                        nome + "?\n\n" +
                                        "Os exercícios associados ao treino " +
                                        "também serão removidos da lista.\n\n" +
                                        "O histórico de atividades realizadas " +
                                        "será preservado.\n\n" +
                                        "Esta ação não poderá ser desfeita."
                        )

                        .setNegativeButton("Cancelar", null)

                        .setPositiveButton(
                                "Excluir",
                                (d, which) -> {

                                    boolean resultado =
                                            bancoDados.excluirTreino(
                                                    idUsuario,
                                                    idTreino
                                            );

                                    if (resultado) {

                                        Toast.makeText(
                                                this,
                                                "Treino excluído com sucesso!",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        carregarTreinos();

                                    } else {

                                        Toast.makeText(
                                                this,
                                                "Erro ao excluir treino.",
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

    private Button criarBotao(String texto) {

        Button botao = new Button(this);

        botao.setText(texto);

        botao.setTextSize(16);

        botao.setAllCaps(false);

        botao.setTypeface(null, Typeface.BOLD);

        botao.setTextColor(
                ContextCompat.getColor(
                        this, R.color.texto_botao
                )
        );

        botao.setBackgroundTintList(
                ColorStateList.valueOf(
                        ContextCompat.getColor(
                                this, R.color.fundo_botao
                        )
                )
        );

        return botao;
    }

    private void mostrarAviso(String mensagem) {

        layoutListaTreinos.removeAllViews();

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

        layoutListaTreinos.addView(aviso);
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