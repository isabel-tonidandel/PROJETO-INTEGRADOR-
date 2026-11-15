
package com.example.nutrimov;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Patterns;
import androidx.appcompat.app.AppCompatActivity;

public class CadastroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        EditText edtNome = findViewById(R.id.edtNome);
        EditText edtEmail = findViewById(R.id.edtEmailCadastro);
        EditText edtSenha = findViewById(R.id.edtSenhaCadastro);
        Button btnCadastrar = findViewById(R.id.btnCadastrar);

        btnCadastrar.setOnClickListener(v -> {

            String nome = edtNome.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String senha = edtSenha.getText().toString();

            if (nome.isEmpty()) {
                edtNome.setError("Digite seu nome");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.setError("Digite um e-mail válido");
                return;
            }

            if (senha.length() < 8) {
                edtSenha.setError("A senha deve ter pelo menos 8 caracteres");
                return;
            }

            BancoDados banco = new BancoDados(this);

            try {
                boolean cadastrado = banco.cadastrarUsuario(nome, email, senha);

                if (cadastrado) {
                    Toast.makeText(this,
                            "Cadastro realizado com sucesso!",
                            Toast.LENGTH_LONG).show();

                    finish();

                } else {
                    edtEmail.setError("Não foi possível cadastrar. Verifique o e-mail.");
                }

            } catch (Exception e) {
                Toast.makeText(this,
                        "Erro ao realizar cadastro",
                        Toast.LENGTH_LONG).show();
            } finally {
                banco.close();
            }
        });
    }
}