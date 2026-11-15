package com.example.nutrimov;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.ContentValues;
import java.security.SecureRandom;
import java.security.GeneralSecurityException;
import android.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
public class BancoDados extends SQLiteOpenHelper {
    private static final String NOME_BANCO = "nutrimov.db";
    private static final int VERSAO_BANCO = 1;

    public BancoDados(Context context){
        super (context, NOME_BANCO, null, VERSAO_BANCO);
    }
    @Override
    public void onCreate(SQLiteDatabase db ) {
        db.execSQL("CREATE TABLE IF NOT EXISTS USUARIO (" +
            "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "senha TEXT NOT NULL, " +
                "data_cadastro TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS PERFIL (" +
                "id_perfil INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_usuario INTEGER NOT NULL UNIQUE, " +
                "idade INTEGER, " +
                "altura REAL, " +
                "peso REAL, " +
                "objetivo TEXT, " +
                "FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario))");

        db.execSQL("CREATE TABLE IF NOT EXISTS REFEICAO (" +
                "id_refeicao INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_usuario INTEGER NOT NULL, " +
                "data TEXT NOT NULL, " +
                "horario TEXT, " +
                "tipo_refeicao TEXT, " +
                "FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario))");

        db.execSQL("CREATE TABLE IF NOT EXISTS ALIMENTO (" +
                "id_alimento INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "categoria TEXT, " +
                "calorias REAL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS ITEM_REFEICAO (" +
                "id_item INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_refeicao INTEGER NOT NULL, " +
                "id_alimento INTEGER NOT NULL, " +
                "quantidade REAL NOT NULL, " +
                " FOREIGN KEY (id_refeicao) REFERENCES REFEICAO(id_refeicao), " +
                "FOREIGN KEY (id_alimento) REFERENCES ALIMENTO(id_alimento))");

        db.execSQL("CREATE TABLE IF NOT EXISTS EXERCICIO (" +
                "id_exercicio INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "grupo_muscular TEXT, " +
                "descricao TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS TREINO (" +
                "id_treino INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_usuario INTEGER NOT NULL, " +
                "nome TEXT NOT NULL, " +
                "descricao TEXT, " +
                "dias_semana TEXT, " +
                "FOREIGN KEY (id_usuario) REFERENCES USUARIO (id_usuario))");

        db.execSQL("CREATE TABLE IF NOT EXISTS TREINO_EXERCICIO (" +
                "id_treino_exercicio INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_treino INTEGER NOT NULL, " +
                "id_exercicio INTEGER NOT NULL, " +
                "series INTEGER, " +
                "repeticoes INTEGER, " +
                "ordem INTEGER, " +
                "FOREIGN KEY (id_treino) REFERENCES TREINO(id_treino), " +
                "FOREIGN KEY (id_exercicio) REFERENCES EXERCICIO(id_exercicio))");

        db.execSQL("CREATE TABLE IF NOT EXISTS REGISTRO_EXERCICIO (" +
                "id_registro INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_usuario INTEGER NOT NULL, " +
                "id_exercicio INTEGER NOT NULL, " +
                "id_treino INTEGER, " +
                "data TEXT NOT NULL, " +
                "series_realizadas INTEGER, " +
                "repeticoes INTEGER, " +
                "carga REAL, " +
                "FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario), " +
                "FOREIGN KEY (id_exercicio) REFERENCES EXERCICIO(id_exercicio), " +
                "FOREIGN KEY (id_treino) REFERENCES TREINO(id_treino))");

        db.execSQL("CREATE TABLE IF NOT EXISTS AVALIACAO_CORPORAL (" +
                "id_avaliacao INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_usuario INTEGER NOT NULL, " +
                "data_avaliacao TEXT NOT NULL, " +
                "peso REAL, " +
                "altura REAL, " +
                "FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario))");
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Aqui serão realizadas futuras atualizações do banco.
    }

    public boolean verificarLogin(String email, String senha) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT senha FROM USUARIO WHERE email = ?",
                new String[]{email}
        );

        try {
            if (cursor.moveToFirst()) {
                String senhaArmazenada = cursor.getString(0);

                return conferirSenha(senha, senhaArmazenada);
            }

            return false;

        } finally {
            cursor.close();
        }
    }
    public boolean cadastrarUsuario(String nome, String email, String senha) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put("nome", nome);
        valores.put("email", email);
        valores.put("senha", protegerSenha(senha));
        valores.put("data_cadastro",
                new java.text.SimpleDateFormat(
                        "yyyy-MM-dd",
                        java.util.Locale.getDefault()
                ).format(new java.util.Date()));

        long resultado = db.insert("USUARIO", null, valores);

        return resultado != -1;
    }
    private String protegerSenha(String senha) {
        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(
                    senha.toCharArray(),
                    salt,
                    210000,
                    256
            );

            byte[] hash;
            try {
                SecretKeyFactory factory =
                        SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                hash = factory.generateSecret(spec).getEncoded();
            } finally {
                spec.clearPassword();
            }

            return Base64.encodeToString(salt, Base64.NO_WRAP)
                    + ":"
                    + Base64.encodeToString(hash, Base64.NO_WRAP);

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Erro ao proteger a senha", e
            );
        }
    }
    private boolean conferirSenha(String senha, String senhaArmazenada) {
        try {
            String[] partes = senhaArmazenada.split(":");

            if (partes.length != 2) {
                return false;
            }

            byte[] salt = Base64.decode(partes[0], Base64.NO_WRAP);
            byte[] hashArmazenado = Base64.decode(partes[1], Base64.NO_WRAP);

            PBEKeySpec spec = new PBEKeySpec(
                    senha.toCharArray(),
                    salt,
                    210000,
                    256
            );

            byte[] hashDigitado;

            try {
                SecretKeyFactory factory =
                        SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

                hashDigitado = factory.generateSecret(spec).getEncoded();
            } finally {
                spec.clearPassword();
            }

            return MessageDigest.isEqual(hashDigitado, hashArmazenado);

        } catch (GeneralSecurityException | IllegalArgumentException e) {
            return false;
        }
    }
}