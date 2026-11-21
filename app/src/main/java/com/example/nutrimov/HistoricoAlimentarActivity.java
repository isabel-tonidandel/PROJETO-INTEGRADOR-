package com.example.nutrimov;

import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
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

public class HistoricoAlimentarActivity extends AppCompatActivity {

    private LinearLayout layoutHistoricoAlimentar;
    private TextView txtSemRegistros;
    private BancoDados bancoDados;
    private long idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_historico_alimentar);

        layoutHistoricoAlimentar =
                findViewById(R.id.layoutHistoricoAlimentar);

        txtSemRegistros =
                findViewById(R.id.txtSemRegistros);

        idUsuario = getIntent()
                .getLongExtra("id_usuario", -1);

        if (idUsuario <= 0) {

            txtSemRegistros.setText(
                    "Usuário não identificado."
            );

            txtSemRegistros.setVisibility(View.VISIBLE);

            return;
        }

        bancoDados = new BancoDados(this);

        carregarHistorico();
    }

    private void carregarHistorico() {

        layoutHistoricoAlimentar.removeAllViews();

        layoutHistoricoAlimentar.addView(txtSemRegistros);

        txtSemRegistros.setVisibility(View.GONE);

        try (Cursor cursor =
                     bancoDados.buscarRegistrosAlimentares(idUsuario)) {

            if (!cursor.moveToFirst()) {

                mostrarAviso("Nenhuma refeição registrada.");

                return;
            }

            do {

                long idItem = cursor.getLong(0);
                String data = cursor.getString(1);
                String horario = cursor.getString(2);
                String tipo = cursor.getString(3);
                String alimento = cursor.getString(4);
                double quantidade = cursor.getDouble(5);

                criarCartao(
                        idItem,
                        data,
                        horario,
                        tipo,
                        alimento,
                        quantidade
                );

            } while (cursor.moveToNext());

        } catch (android.database.SQLException e) {

            android.util.Log.e(
                    "NutriMov",
                    "Erro ao consultar histórico alimentar",
                    e
            );

            mostrarAviso("Não foi possível consultar o histórico.");
        }
    }

    private void criarCartao(
            long idItem,
            String data,
            String horario,
            String tipo,
            String alimento,
            double quantidade) {

        LinearLayout cartao = new LinearLayout(this);

        cartao.setOrientation(LinearLayout.VERTICAL);

        cartao.setPadding(
                dp(16), dp(16), dp(16), dp(16)
        );

        LinearLayout.LayoutParams parametrosCartao =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametrosCartao.setMargins(
                0, 0, 0, dp(12)
        );

        cartao.setLayoutParams(parametrosCartao);

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

        // Data e horário

        TextView txtData = new TextView(this);

        txtData.setText(
                formatarData(data) +
                        (horario == null ? "" : " - " + horario)
        );

        txtData.setTextSize(14);

        txtData.setTextColor(
                ContextCompat.getColor(
                        this, R.color.texto_secundario
                )
        );

        cartao.addView(txtData);

        // Tipo de refeição

        TextView txtTipo = new TextView(this);

        txtTipo.setText(tipo);

        txtTipo.setTextSize(20);

        txtTipo.setTypeface(
                null, Typeface.BOLD
        );

        txtTipo.setTextColor(
                ContextCompat.getColor(
                        this, R.color.texto_titulo
                )
        );

        txtTipo.setPadding(
                0, dp(8), 0, dp(8)
        );

        cartao.addView(txtTipo);

        // Alimento

        TextView txtAlimento = new TextView(this);

        txtAlimento.setText(
                "Alimento: " + alimento
        );

        txtAlimento.setTextSize(16);

        txtAlimento.setTextColor(
                ContextCompat.getColor(
                        this, R.color.texto_principal
                )
        );

        cartao.addView(txtAlimento);

        // Quantidade

        TextView txtQuantidade = new TextView(this);

        NumberFormat formato =
                NumberFormat.getNumberInstance(
                        new Locale("pt", "BR")
                );

        formato.setMaximumFractionDigits(2);

        txtQuantidade.setText(
                "Quantidade: " +
                        formato.format(quantidade) + " g"
        );

        txtQuantidade.setTextSize(16);

        txtQuantidade.setTextColor(
                ContextCompat.getColor(
                        this, R.color.texto_principal
                )
        );

        txtQuantidade.setPadding(
                0, dp(4), 0, dp(12)
        );

        cartao.addView(txtQuantidade);

        // Linha divisória

        View divisor = new View(this);

        divisor.setBackgroundColor(
                ContextCompat.getColor(
                        this, R.color.verde_claro
                )
        );

        cartao.addView(
                divisor,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(1)
                )
        );

        // Área dos botões

        LinearLayout layoutBotoes =
                new LinearLayout(this);

        layoutBotoes.setOrientation(
                LinearLayout.HORIZONTAL
        );

        layoutBotoes.setPadding(
                0, dp(12), 0, 0
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
                        idItem,
                        tipo,
                        alimento,
                        quantidade
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
                confirmarExclusao(idItem, alimento)
        );

        layoutBotoes.addView(btnEditar);
        layoutBotoes.addView(btnExcluir);

        cartao.addView(layoutBotoes);

        layoutHistoricoAlimentar.addView(cartao);
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
            long idItem,
            String tipo,
            String alimento,
            double quantidade) {

        LinearLayout formulario =
                new LinearLayout(this);

        formulario.setOrientation(
                LinearLayout.VERTICAL
        );

        formulario.setPadding(
                dp(24), dp(8), dp(24), dp(16)
        );

        EditText edtTipo = criarCampo(
                formulario,
                "Tipo de refeição",
                tipo,
                InputType.TYPE_CLASS_TEXT
        );

        EditText edtAlimento = criarCampo(
                formulario,
                "Alimento",
                alimento,
                InputType.TYPE_CLASS_TEXT
        );

        EditText edtQuantidade = criarCampo(
                formulario,
                "Quantidade (g)",
                String.valueOf(quantidade)
                        .replace('.', ','),
                InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        ScrollView scrollView = new ScrollView(this);

        scrollView.addView(formulario);

        AlertDialog dialogo =
                new AlertDialog.Builder(this)
                        .setTitle("Editar refeição")
                        .setView(scrollView)
                        .setNegativeButton("Cancelar", null)
                        .setPositiveButton("Salvar", null)
                        .create();

        dialogo.setOnShowListener(d -> {

            Button btnSalvar = dialogo.getButton(
                    AlertDialog.BUTTON_POSITIVE
            );

            btnSalvar.setOnClickListener(v -> {

                String novoTipo =
                        edtTipo.getText().toString().trim();

                String novoAlimento =
                        edtAlimento.getText().toString().trim();

                String textoQuantidade =
                        edtQuantidade.getText().toString()
                                .trim().replace(',', '.');

                if (novoTipo.isEmpty()) {

                    edtTipo.setError(
                            "Informe o tipo de refeição"
                    );

                    return;
                }

                if (novoAlimento.isEmpty()) {

                    edtAlimento.setError(
                            "Informe o alimento"
                    );

                    return;
                }

                double novaQuantidade;

                try {

                    novaQuantidade =
                            Double.parseDouble(textoQuantidade);

                } catch (NumberFormatException e) {

                    edtQuantidade.setError(
                            "Informe uma quantidade válida"
                    );

                    return;
                }

                if (novaQuantidade <= 0 ||
                        Double.isNaN(novaQuantidade) ||
                        Double.isInfinite(novaQuantidade)) {

                    edtQuantidade.setError(
                            "A quantidade deve ser maior que zero"
                    );

                    return;
                }

                boolean resultado =
                        bancoDados.editarRefeicao(
                                idUsuario,
                                idItem,
                                novoTipo,
                                novoAlimento,
                                novaQuantidade
                        );

                if (resultado) {

                    Toast.makeText(
                            this,
                            "Refeição atualizada com sucesso!",
                            Toast.LENGTH_SHORT
                    ).show();

                    dialogo.dismiss();

                    carregarHistorico();

                } else {

                    Toast.makeText(
                            this,
                            "Erro ao atualizar refeição.",
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
            long idItem, String alimento) {

        AlertDialog dialogo =
                new AlertDialog.Builder(this)

                        .setTitle("Excluir refeição?")

                        .setMessage(
                                "Deseja excluir o registro de " +
                                        alimento + "?\n\n" +
                                        "Esta ação não poderá ser desfeita."
                        )

                        .setNegativeButton(
                                "Cancelar", null
                        )

                        .setPositiveButton(
                                "Excluir",
                                (d, which) -> {

                                    boolean resultado =
                                            bancoDados.excluirRefeicao(
                                                    idUsuario,
                                                    idItem
                                            );

                                    if (resultado) {

                                        Toast.makeText(
                                                this,
                                                "Refeição excluída com sucesso!",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        carregarHistorico();

                                    } else {

                                        Toast.makeText(
                                                this,
                                                "Erro ao excluir refeição.",
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

        layoutHistoricoAlimentar.removeAllViews();

        txtSemRegistros.setText(mensagem);

        txtSemRegistros.setVisibility(View.VISIBLE);

        layoutHistoricoAlimentar.addView(txtSemRegistros);
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