package com.example.nutrimov;

import android.os.Bundle;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_main);

        BancoDados bancoDados = new BancoDados(this);
        SQLiteDatabase db = bancoDados.getWritableDatabase();

        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtSenha = findViewById(R.id.edtSenha);
        Button btnEntrar = findViewById(R.id.btnEntrar);
        TextView txtCadastrar = findViewById(R.id.txtCadastrar);

        txtCadastrar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CadastroActivity.class);
            startActivity(intent);
        });

        btnEntrar.setOnClickListener(v -> {

            String email = edtEmail.getText().toString().trim();
            String senha = edtSenha.getText().toString();

            if (email.isEmpty() || senha.isEmpty()) {

                Toast.makeText(this, "Preencha todos os campos",
                        Toast.LENGTH_SHORT).show();

            } else if (bancoDados.verificarLogin(email, senha)) {

                Toast.makeText(this, "Login realizado com sucesso!",
                        Toast.LENGTH_SHORT).show();

                long idUsuario = bancoDados.buscarIdUsuario(email);

                if (idUsuario != -1) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("id_usuario", idUsuario);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Usuário não encontrado",
                            Toast.LENGTH_SHORT).show();
                }

            } else {

                Toast.makeText(this, "E-mail ou senha incorretos",
                        Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                });
    }
}