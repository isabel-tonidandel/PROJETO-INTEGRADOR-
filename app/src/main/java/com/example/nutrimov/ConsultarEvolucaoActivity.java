package com.example.nutrimov;

import android.database.Cursor;
import android.os.Bundle;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class ConsultarEvolucaoActivity extends AppCompatActivity {

    private TextView txtPesoInicial;
    private TextView txtPesoAtual;
    private TextView txtVariacaoPeso;

    private LinearLayout layoutHistorico;

    private BancoDados bancoDados;

    private long idUsuario;

    private final Locale locale = new Locale("pt", "BR");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_consultar_evolucao);

        txtPesoInicial = findViewById(R.id.txtPesoInicial);

        txtPesoAtual = findViewById(R.id.txtPesoAtual);

        txtVariacaoPeso = findViewById(R.id.txtVariacaoPeso);

        layoutHistorico = findViewById(
                R.id.layoutHistoricoEvolucao
        );

        // Fundo dos indicadores

        txtPesoInicial.setBackground(
                criarFundo(R.color.fundo_campo)
        );

        txtPesoAtual.setBackground(
                criarFundo(R.color.fundo_campo)
        );

        txtVariacaoPeso.setBackground(
                criarFundo(R.color.verde_claro)
        );

        idUsuario = getIntent()
                .getLongExtra("id_usuario", -1);

        if (idUsuario <= 0) {

            Toast.makeText(
                    this,
                    "Usuário não identificado.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        bancoDados = new BancoDados(this);

        carregarEvolucao();
    }

    private void carregarEvolucao() {

        layoutHistorico.removeAllViews();

        limparIndicadores();

        try (Cursor cursor =
                     bancoDados.buscarRegistrosEvolucao(
                             idUsuario
                     )) {

            if (!cursor.moveToFirst()) {

                mostrarAviso(
                        "Nenhuma avaliação registrada."
                );

                return;
            }

            // Último peso registrado

            double pesoAtual = cursor.getDouble(2);

            // Primeiro peso registrado

            cursor.moveToLast();

            double pesoInicial = cursor.getDouble(2);

            // Calcular variação de peso

            double variacao = pesoAtual - pesoInicial;

            txtPesoInicial.setText(String.format(
                    locale,
                    "Peso inicial: %.1f kg",
                    pesoInicial
            ));

            txtPesoAtual.setText(String.format(
                    locale,
                    "Último peso registrado: %.1f kg",
                    pesoAtual
            ));

            txtVariacaoPeso.setText(String.format(
                    locale,
                    "Variação de peso\n%+.1f kg",
                    variacao
            ));

            // Consultar avaliações

            cursor.moveToPosition(-1);

            while (cursor.moveToNext()) {

                long idAvaliacao = cursor.getLong(0);

                String data = cursor.getString(1);

                double peso = cursor.getDouble(2);

                double altura = cursor.getDouble(3);

                criarCartao(
                        idAvaliacao,
                        data,
                        peso,
                        altura
                );
            }

        } catch (android.database.SQLException e) {

            android.util.Log.e(
                    "NutriMov",
                    "Erro ao consultar evolução corporal",
                    e
            );

            limparIndicadores();

            layoutHistorico.removeAllViews();

            mostrarAviso(
                    "Não foi possível consultar as avaliações."
            );
        }
    }

    private void limparIndicadores() {

        txtPesoInicial.setText("Peso inicial: --");

        txtPesoAtual.setText(
                "Último peso registrado: --"
        );

        txtVariacaoPeso.setText(
                "Variação de peso\n--"
        );
    }

    private void criarCartao(
            long idAvaliacao,
            String data,
            double peso,
            double altura) {

        String dataFormatada = formatarData(data);

        // Cartão da avaliação

        LinearLayout cartao = new LinearLayout(this);

        cartao.setOrientation(LinearLayout.VERTICAL);

        cartao.setPadding(
                dp(16), dp(16), dp(16), dp(16)
        );

        cartao.setBackground(
                criarFundo(R.color.fundo_campo)
        );

        LinearLayout.LayoutParams paramsCartao =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        paramsCartao.setMargins(0, 0, 0, dp(16));

        // Data da avaliação

        TextView txtData = new TextView(this);

        txtData.setText("Data: " + dataFormatada);

        txtData.setTextSize(18);

        txtData.setTypeface(null, Typeface.BOLD);

        txtData.setTextColor(
                ContextCompat.getColor(
                        this, R.color.texto_titulo
                )
        );

        cartao.addView(txtData);

        adicionarDivisoria(cartao);

        // Informações de peso e altura

        LinearLayout linha = new LinearLayout(this);

        linha.setOrientation(LinearLayout.HORIZONTAL);

        linha.addView(criarInformacao(
                "Peso",
                String.format(locale, "%.1f kg", peso)
        ));

        linha.addView(criarInformacao(
                "Altura",
                String.format(locale, "%.1f cm", altura)
        ));

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
                        idAvaliacao,
                        peso,
                        altura
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
                        idAvaliacao,
                        dataFormatada
                )
        );

        layoutBotoes.addView(
                btnExcluir, paramsExcluir
        );

        cartao.addView(layoutBotoes);

        layoutHistorico.addView(
                cartao, paramsCartao
        );
    }

    private void abrirEdicao(
            long idAvaliacao,
            double peso,
            double altura) {

        LinearLayout formulario = new LinearLayout(this);

        formulario.setOrientation(
                LinearLayout.VERTICAL
        );

        formulario.setPadding(
                dp(24), dp(8), dp(24), dp(16)
        );

        // Campo de peso

        EditText edtPeso = criarCampo(
                formulario,
                "Peso (kg)",
                String.valueOf(peso).replace(".", ",")
        );

        // Campo de altura

        EditText edtAltura = criarCampo(
                formulario,
                "Altura (cm)",
                String.valueOf(altura).replace(".", ",")
        );

        AlertDialog dialogo =
                new AlertDialog.Builder(this)

                        .setTitle("Editar avaliação")

                        .setView(formulario)

                        .setNegativeButton("Cancelar", null)

                        .setPositiveButton("Salvar", null)

                        .create();

        dialogo.setOnShowListener(d -> {

            Button btnSalvar = dialogo.getButton(
                    AlertDialog.BUTTON_POSITIVE
            );

            btnSalvar.setOnClickListener(v -> {

                String textoPeso =
                        edtPeso.getText()
                                .toString().trim();

                String textoAltura =
                        edtAltura.getText()
                                .toString().trim();

                // Validar peso

                double novoPeso;

                try {

                    novoPeso = Double.parseDouble(
                            textoPeso.replace(",", ".")
                    );

                    if (novoPeso <= 0 ||
                            Double.isNaN(novoPeso) ||
                            Double.isInfinite(novoPeso)) {

                        edtPeso.setError(
                                "Informe um peso válido"
                        );

                        return;
                    }

                } catch (NumberFormatException e) {

                    edtPeso.setError(
                            "Informe um peso válido"
                    );

                    return;
                }

                // Validar altura

                double novaAltura;

                try {

                    novaAltura = Double.parseDouble(
                            textoAltura.replace(",", ".")
                    );

                    if (novaAltura <= 0 ||
                            Double.isNaN(novaAltura) ||
                            Double.isInfinite(novaAltura)) {

                        edtAltura.setError(
                                "Informe uma altura válida"
                        );

                        return;
                    }

                } catch (NumberFormatException e) {

                    edtAltura.setError(
                            "Informe uma altura válida"
                    );

                    return;
                }

                // Atualizar avaliação no banco de dados

                boolean resultado =
                        bancoDados.editarEvolucao(
                                idUsuario,
                                idAvaliacao,
                                novoPeso,
                                novaAltura
                        );

                if (resultado) {

                    Toast.makeText(
                            this,
                            "Avaliação atualizada com sucesso!",
                            Toast.LENGTH_SHORT
                    ).show();

                    dialogo.dismiss();

                    carregarEvolucao();

                } else {

                    Toast.makeText(
                            this,
                            "Erro ao atualizar avaliação.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        });

        dialogo.show();
    }

    private void confirmarExclusao(
            long idAvaliacao,
            String data) {

        AlertDialog dialogo =
                new AlertDialog.Builder(this)

                        .setTitle("Excluir avaliação?")

                        .setMessage(
                                "Deseja excluir a avaliação " +
                                        "registrada em " + data +
                                        "?\n\n" +
                                        "O resumo da evolução será " +
                                        "atualizado automaticamente.\n\n" +
                                        "Esta ação não poderá ser desfeita."
                        )

                        .setNegativeButton("Cancelar", null)

                        .setPositiveButton(
                                "Excluir",
                                (d, which) -> {

                                    boolean resultado =
                                            bancoDados.excluirEvolucao(
                                                    idUsuario,
                                                    idAvaliacao
                                            );

                                    if (resultado) {

                                        Toast.makeText(
                                                this,
                                                "Avaliação excluída com sucesso!",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        carregarEvolucao();

                                    } else {

                                        Toast.makeText(
                                                this,
                                                "Erro ao excluir avaliação.",
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
            String valor) {

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

        campo.setInputType(
                InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        campo.setKeyListener(
                DigitsKeyListener.getInstance(
                        "0123456789,."
                )
        );

        campo.setText(valor);

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

    private String formatarData(String data) {

        if (data == null || data.trim().isEmpty()) {

            return "Não informada";
        }

        String[] partes = data.split("-");

        if (partes.length == 3) {

            return partes[2] + "/" +
                    partes[1] + "/" +
                    partes[0];
        }

        return data;
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

    private GradientDrawable criarFundo(int cor) {

        GradientDrawable fundo = new GradientDrawable();

        fundo.setColor(
                ContextCompat.getColor(this, cor)
        );

        fundo.setCornerRadius(dp(12));

        fundo.setStroke(
                dp(1),
                ContextCompat.getColor(
                        this, R.color.verde_claro
                )
        );

        return fundo;
    }

    private LinearLayout criarInformacao(
            String rotulo, String valor) {

        LinearLayout coluna = new LinearLayout(this);

        coluna.setOrientation(LinearLayout.VERTICAL);

        coluna.setGravity(Gravity.CENTER_HORIZONTAL);

        coluna.setPadding(
                dp(4), 0, dp(4), 0
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

        txtValor.setTextSize(20);

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