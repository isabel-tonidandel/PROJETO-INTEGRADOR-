package com.example.nutrimov;

import android.database.Cursor;
import android.os.Bundle;
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

import java.text.NumberFormat;
import java.util.Locale;

public class VisualizarExerciciosTreinoActivity extends AppCompatActivity {

    private LinearLayout layoutExercicios;
    private BancoDados bancoDados;
    private long idUsuario;
    private long idTreino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_visualizar_exercicios_treino
        );

        TextView txtNomeTreino = findViewById(
                R.id.txtNomeTreino
        );

        layoutExercicios = findViewById(
                R.id.layoutExerciciosTreino
        );

        idUsuario = getIntent()
                .getLongExtra("id_usuario", -1);

        idTreino = getIntent()
                .getLongExtra("id_treino", -1);

        String nomeTreino = getIntent()
                .getStringExtra("nome_treino");

        if (nomeTreino != null) {

            txtNomeTreino.setText(nomeTreino);
        }

        if (idUsuario <= 0 || idTreino <= 0) {

            mostrarAviso(
                    "Treino ou usuário não identificado."
            );

            return;
        }

        bancoDados = new BancoDados(this);

        carregarExercicios();
    }

    private void carregarExercicios() {

        layoutExercicios.removeAllViews();

        try (Cursor cursor =
                     bancoDados.buscarExerciciosTreinoEdicao(
                             idUsuario, idTreino
                     )) {

            if (!cursor.moveToFirst()) {

                mostrarAviso(
                        "Nenhum exercício cadastrado neste treino."
                );

                return;
            }

            do {

                long idTreinoExercicio = cursor.getLong(0);

                String nome = cursor.getString(1);

                String grupoMuscular = cursor.getString(2);

                int series = cursor.getInt(3);

                int repeticoes = cursor.getInt(4);

                Double carga = cursor.isNull(5)
                        ? null : cursor.getDouble(5);

                criarCartao(
                        idTreinoExercicio,
                        nome,
                        grupoMuscular,
                        series,
                        repeticoes,
                        carga
                );

            } while (cursor.moveToNext());

        } catch (android.database.SQLException e) {

            android.util.Log.e(
                    "NutriMov",
                    "Erro ao consultar exercícios do treino",
                    e
            );

            mostrarAviso(
                    "Não foi possível consultar os exercícios."
            );
        }
    }

    private void criarCartao(
            long idTreinoExercicio,
            String nome,
            String grupoMuscular,
            int series,
            int repeticoes,
            Double carga) {

        // Formatação da carga

        String cargaTexto = "Não informada";

        if (carga != null) {

            NumberFormat formato =
                    NumberFormat.getNumberInstance(
                            new Locale("pt", "BR")
                    );

            formato.setMaximumFractionDigits(2);

            cargaTexto = formato.format(carga) + " kg";
        }

        // Cartão do exercício

        LinearLayout cartao = new LinearLayout(this);

        cartao.setOrientation(LinearLayout.VERTICAL);

        cartao.setPadding(
                dp(16), dp(16), dp(16), dp(16)
        );

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

        LinearLayout.LayoutParams paramsCartao =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        paramsCartao.setMargins(0, 0, 0, dp(16));

        // Nome do exercício

        TextView titulo = new TextView(this);

        titulo.setText(nome);

        titulo.setTextSize(20);

        titulo.setTypeface(null, Typeface.BOLD);

        titulo.setTextColor(
                ContextCompat.getColor(
                        this, R.color.texto_titulo
                )
        );

        titulo.setPadding(0, 0, 0, dp(8));

        cartao.addView(titulo);

        // Grupo muscular

        if (grupoMuscular != null &&
                !grupoMuscular.trim().isEmpty()) {

            TextView grupo = new TextView(this);

            grupo.setText(
                    "Grupo muscular: " + grupoMuscular
            );

            grupo.setTextSize(16);

            grupo.setTextColor(
                    ContextCompat.getColor(
                            this, R.color.texto_principal
                    )
            );

            grupo.setPadding(0, 0, 0, dp(12));

            cartao.addView(grupo);
        }

        adicionarDivisoria(cartao);

        // Linha de informações

        LinearLayout linha = new LinearLayout(this);

        linha.setOrientation(LinearLayout.HORIZONTAL);

        linha.addView(
                criarInformacao(
                        "Séries",
                        String.valueOf(series)
                )
        );

        linha.addView(
                criarInformacao(
                        "Repetições",
                        String.valueOf(repeticoes)
                )
        );

        linha.addView(
                criarInformacao(
                        "Carga",
                        cargaTexto
                )
        );

        cartao.addView(linha);

        adicionarDivisoria(cartao);

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

        LinearLayout.LayoutParams paramsEditar =
                new LinearLayout.LayoutParams(
                        0, dp(48), 1
                );

        paramsEditar.setMargins(
                0, 0, dp(8), 0
        );

        btnEditar.setOnClickListener(v ->

                abrirEdicao(
                        idTreinoExercicio,
                        nome,
                        grupoMuscular,
                        series,
                        repeticoes,
                        carga
                )
        );

        layoutBotoes.addView(
                btnEditar, paramsEditar
        );

        // Botão Excluir

        Button btnExcluir = criarBotao(
                "Excluir",
                Color.parseColor("#B71C1C")
        );

        LinearLayout.LayoutParams paramsExcluir =
                new LinearLayout.LayoutParams(
                        0, dp(48), 1
                );

        btnExcluir.setOnClickListener(v ->

                confirmarExclusao(
                        idTreinoExercicio,
                        nome
                )
        );

        layoutBotoes.addView(
                btnExcluir, paramsExcluir
        );

        cartao.addView(layoutBotoes);

        layoutExercicios.addView(
                cartao, paramsCartao
        );
    }

    private void abrirEdicao(
            long idTreinoExercicio,
            String nome,
            String grupoMuscular,
            int series,
            int repeticoes,
            Double carga) {

        LinearLayout formulario = new LinearLayout(this);

        formulario.setOrientation(
                LinearLayout.VERTICAL
        );

        formulario.setPadding(
                dp(24), dp(8), dp(24), dp(16)
        );

        // Nome do exercício

        EditText edtNome = criarCampo(
                formulario,
                "Nome do exercício",
                nome,
                InputType.TYPE_CLASS_TEXT
        );

        // Grupo muscular

        EditText edtGrupoMuscular = criarCampo(
                formulario,
                "Grupo muscular",
                grupoMuscular,
                InputType.TYPE_CLASS_TEXT
        );

        // Séries

        EditText edtSeries = criarCampo(
                formulario,
                "Séries",
                String.valueOf(series),
                InputType.TYPE_CLASS_NUMBER
        );

        // Repetições

        EditText edtRepeticoes = criarCampo(
                formulario,
                "Repetições",
                String.valueOf(repeticoes),
                InputType.TYPE_CLASS_NUMBER
        );

        // Carga

        EditText edtCarga = criarCampo(
                formulario,
                "Carga em kg (opcional)",
                carga == null
                        ? ""
                        : String.valueOf(carga)
                        .replace(".", ","),
                InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL
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

                String novoGrupo =
                        edtGrupoMuscular.getText()
                                .toString().trim();

                String textoSeries =
                        edtSeries.getText().toString().trim();

                String textoRepeticoes =
                        edtRepeticoes.getText()
                                .toString().trim();

                String textoCarga =
                        edtCarga.getText().toString().trim();

                // Validar nome

                if (novoNome.isEmpty()) {

                    edtNome.setError(
                            "Informe o nome do exercício"
                    );

                    return;
                }

                // Validar séries

                int novasSeries;

                try {

                    novasSeries =
                            Integer.parseInt(textoSeries);

                    if (novasSeries <= 0) {

                        edtSeries.setError(
                                "Informe um valor maior que zero"
                        );

                        return;
                    }

                } catch (NumberFormatException e) {

                    edtSeries.setError(
                            "Informe uma quantidade válida"
                    );

                    return;
                }

                // Validar repetições

                int novasRepeticoes;

                try {

                    novasRepeticoes =
                            Integer.parseInt(textoRepeticoes);

                    if (novasRepeticoes <= 0) {

                        edtRepeticoes.setError(
                                "Informe um valor maior que zero"
                        );

                        return;
                    }

                } catch (NumberFormatException e) {

                    edtRepeticoes.setError(
                            "Informe uma quantidade válida"
                    );

                    return;
                }

                // Validar carga

                Double novaCarga = null;

                if (!textoCarga.isEmpty()) {

                    try {

                        novaCarga = Double.parseDouble(
                                textoCarga.replace(",", ".")
                        );

                        if (novaCarga < 0 ||
                                Double.isNaN(novaCarga) ||
                                Double.isInfinite(novaCarga)) {

                            edtCarga.setError(
                                    "Informe uma carga válida"
                            );

                            return;
                        }

                    } catch (NumberFormatException e) {

                        edtCarga.setError(
                                "Informe uma carga válida"
                        );

                        return;
                    }
                }

                // Atualizar no banco de dados

                boolean resultado =
                        bancoDados.editarExercicioTreino(
                                idUsuario,
                                idTreino,
                                idTreinoExercicio,
                                novoNome,
                                novoGrupo,
                                novasSeries,
                                novasRepeticoes,
                                novaCarga
                        );

                if (resultado) {

                    Toast.makeText(
                            this,
                            "Exercício atualizado com sucesso!",
                            Toast.LENGTH_SHORT
                    ).show();

                    dialogo.dismiss();

                    carregarExercicios();

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

    private void confirmarExclusao(
            long idTreinoExercicio,
            String nome) {

        AlertDialog dialogo =
                new AlertDialog.Builder(this)

                        .setTitle("Excluir exercício?")

                        .setMessage(
                                "Deseja excluir o exercício " +
                                        nome + " deste treino?\n\n" +
                                        "O treino continuará cadastrado.\n\n" +
                                        "Esta ação não poderá ser desfeita."
                        )

                        .setNegativeButton("Cancelar", null)

                        .setPositiveButton(
                                "Excluir",
                                (d, which) -> {

                                    boolean resultado =
                                            bancoDados.excluirExercicioTreino(
                                                    idUsuario,
                                                    idTreino,
                                                    idTreinoExercicio
                                            );

                                    if (resultado) {

                                        Toast.makeText(
                                                this,
                                                "Exercício excluído com sucesso!",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        carregarExercicios();

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

        campo.setSingleLine(true);

        campo.setInputType(tipoEntrada);

        if ((tipoEntrada & InputType.TYPE_MASK_CLASS)
                == InputType.TYPE_CLASS_NUMBER &&
                (tipoEntrada & InputType.TYPE_NUMBER_FLAG_DECIMAL)
                        != 0) {

            campo.setKeyListener(
                    android.text.method.DigitsKeyListener
                            .getInstance("0123456789,.")
            );
        }

        campo.setText(valor == null ? "" : valor);

        campo.setTextSize(16);

        formulario.addView(
                campo,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(56)
                )
        );

        return campo;
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

    private void adicionarDivisoria(
            LinearLayout layout) {

        View divisoria = new View(this);

        divisoria.setBackgroundColor(
                ContextCompat.getColor(
                        this, R.color.verde_claro
                )
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(1)
                );

        params.setMargins(
                0, dp(12), 0, dp(12)
        );

        layout.addView(divisoria, params);
    }

    private LinearLayout criarInformacao(
            String rotulo, String valor) {

        LinearLayout coluna = new LinearLayout(this);

        coluna.setOrientation(LinearLayout.VERTICAL);

        coluna.setGravity(Gravity.CENTER_HORIZONTAL);

        coluna.setPadding(
                dp(2), 0, dp(2), 0
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        coluna.setLayoutParams(params);

        TextView txtRotulo = new TextView(this);

        txtRotulo.setText(rotulo);

        txtRotulo.setTextSize(14);

        txtRotulo.setGravity(Gravity.CENTER);

        txtRotulo.setTextColor(
                ContextCompat.getColor(
                        this, R.color.texto_secundario
                )
        );

        TextView txtValor = new TextView(this);

        txtValor.setText(valor);

        txtValor.setTextSize(18);

        txtValor.setTypeface(null, Typeface.BOLD);

        txtValor.setGravity(Gravity.CENTER);

        txtValor.setTextColor(
                ContextCompat.getColor(
                        this, R.color.texto_titulo
                )
        );

        txtValor.setPadding(
                0, dp(6), 0, 0
        );

        coluna.addView(txtRotulo);

        coluna.addView(txtValor);

        return coluna;
    }

    private void mostrarAviso(String mensagem) {

        layoutExercicios.removeAllViews();

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

        layoutExercicios.addView(aviso);
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