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
    private static final int VERSAO_BANCO = 3;

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
                "carga REAL, " +
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
                "duracao_min iNTEGER, " +
                "observacoes Text," +
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

        if (oldVersion < 2) {
            db.execSQL(
                    "ALTER TABLE REGISTRO_EXERCICIO ADD COLUMN duracao_min INTEGER"
            );

            db.execSQL(
                    "ALTER TABLE REGISTRO_EXERCICIO ADD COLUMN observacoes TEXT"
            );
        }

        if (oldVersion < 3) {
            db.execSQL(
                    "ALTER TABLE TREINO_EXERCICIO ADD COLUMN carga REAL"
            );
        }
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

        public long buscarIdUsuario(String email) {

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery(
                    "SELECT id_usuario FROM USUARIO WHERE email = ?",
                    new String[]{email}
            );

            try {
                if (cursor.moveToFirst()) {
                    return cursor.getLong(0);
                }

                return -1;

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

    public boolean salvarRefeicao(long idUsuario, String tipo,
                                  String alimento, double quantidade) {

        if (idUsuario <= 0 || tipo == null || tipo.trim().isEmpty()
                || alimento == null || alimento.trim().isEmpty()
                || quantidade <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {
            String data = new java.text.SimpleDateFormat(
                    "yyyy-MM-dd", java.util.Locale.getDefault()
            ).format(new java.util.Date());

            String horario = new java.text.SimpleDateFormat(
                    "HH:mm", java.util.Locale.getDefault()
            ).format(new java.util.Date());

            ContentValues valoresRefeicao = new ContentValues();
            valoresRefeicao.put("id_usuario", idUsuario);
            valoresRefeicao.put("data", data);
            valoresRefeicao.put("horario", horario);
            valoresRefeicao.put("tipo_refeicao", tipo);

            long idRefeicao = db.insertOrThrow(
                    "REFEICAO", null, valoresRefeicao
            );

            ContentValues valoresAlimento = new ContentValues();
            valoresAlimento.put("nome", alimento.trim());

            long idAlimento = db.insertOrThrow(
                    "ALIMENTO", null, valoresAlimento
            );

            ContentValues valoresItem = new ContentValues();
            valoresItem.put("id_refeicao", idRefeicao);
            valoresItem.put("id_alimento", idAlimento);
            valoresItem.put("quantidade", quantidade);

            db.insertOrThrow(
                    "ITEM_REFEICAO", null, valoresItem
            );

            db.setTransactionSuccessful();
            return true;

        } catch (android.database.SQLException e) {
            android.util.Log.e(
                    "NutriMov",
                    "Erro ao salvar refeição",
                    e
            );
            return false;

        } finally {
            db.endTransaction();
        }
    }

    public java.util.ArrayList<String> buscarHistoricoAlimentar(long idUsuario) {

        java.util.ArrayList<String> historico = new java.util.ArrayList<>();

        if (idUsuario <= 0) {
            return historico;
        }

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT r.data, r.horario, r.tipo_refeicao, " +
                        "a.nome, i.quantidade " +
                        "FROM REFEICAO r " +
                        "INNER JOIN ITEM_REFEICAO i ON r.id_refeicao = i.id_refeicao " +
                        "INNER JOIN ALIMENTO a ON i.id_alimento = a.id_alimento " +
                        "WHERE r.id_usuario = ? " +
                        "ORDER BY r.data DESC, r.horario DESC, r.id_refeicao DESC",
                new String[]{String.valueOf(idUsuario)}
        );

        try {
            while (cursor.moveToNext()) {

                String data = cursor.getString(0);
                String horario = cursor.getString(1);
                String tipo = cursor.getString(2);
                String alimento = cursor.getString(3);
                double quantidade = cursor.getDouble(4);

                String registro = data + " - " + horario +
                        "\n" + tipo +
                        "\n" + alimento + " - " + quantidade + " g";

                historico.add(registro);
            }
        } finally {
            cursor.close();
        }

        return historico;
    }
    public boolean salvarExercicio(long idUsuario, String nome,
                                   int duracao, String observacoes) {

        if (idUsuario <= 0 || nome == null || nome.trim().isEmpty()
                || duracao <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {
            ContentValues valoresExercicio = new ContentValues();
            valoresExercicio.put("nome", nome.trim());

            long idExercicio = db.insertOrThrow(
                    "EXERCICIO", null, valoresExercicio
            );

            String data = new java.text.SimpleDateFormat(
                    "yyyy-MM-dd", java.util.Locale.getDefault()
            ).format(new java.util.Date());

            ContentValues valoresRegistro = new ContentValues();
            valoresRegistro.put("id_usuario", idUsuario);
            valoresRegistro.put("id_exercicio", idExercicio);
            valoresRegistro.put("data", data);
            valoresRegistro.put("duracao_min", duracao);
            valoresRegistro.put("observacoes", observacoes);

            db.insertOrThrow(
                    "REGISTRO_EXERCICIO", null, valoresRegistro
            );

            db.setTransactionSuccessful();
            return true;

        } catch (android.database.SQLException e) {
            android.util.Log.e(
                    "NutriMov",
                    "Erro ao salvar exercício",
                    e
            );
            return false;

        } finally {
            db.endTransaction();
        }
    }
    public Cursor buscarHistoricoExercicios(long idUsuario) {

        SQLiteDatabase db = getReadableDatabase();

        String consulta =
                "SELECT EXERCICIO.nome, " +
                        "REGISTRO_EXERCICIO.data, " +
                        "REGISTRO_EXERCICIO.duracao_min, " +
                        "REGISTRO_EXERCICIO.observacoes " +
                        "FROM REGISTRO_EXERCICIO " +
                        "INNER JOIN EXERCICIO ON " +
                        "REGISTRO_EXERCICIO.id_exercicio = EXERCICIO.id_exercicio " +
                        "WHERE REGISTRO_EXERCICIO.id_usuario = ? " +
                        "ORDER BY REGISTRO_EXERCICIO.data DESC, " +
                        "REGISTRO_EXERCICIO.id_registro DESC";

        return db.rawQuery(
                consulta,
                new String[]{String.valueOf(idUsuario)}
        );
    }
    public boolean salvarTreino(long idUsuario, String nome,
                                String descricao, String diasSemana) {

        if (idUsuario <= 0 || nome == null || nome.trim().isEmpty()) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put("id_usuario", idUsuario);
        valores.put("nome", nome.trim());
        valores.put("descricao", descricao);
        valores.put("dias_semana", diasSemana);

        long resultado = db.insert("TREINO", null, valores);

        return resultado != -1;
    }
    public Cursor buscarTreinos(long idUsuario) {

        SQLiteDatabase db = getReadableDatabase();

        String consulta =
                "SELECT id_treino, nome, descricao, dias_semana " +
                        "FROM TREINO " +
                        "WHERE id_usuario = ? " +
                        "ORDER BY id_treino DESC";

        return db.rawQuery(
                consulta,
                new String[]{String.valueOf(idUsuario)}
        );
    }
    public boolean adicionarExercicioTreino(
            long idUsuario, long idTreino, String nome,
            String grupoMuscular, int series,
            int repeticoes, Double carga) {

        if (idUsuario <= 0 || idTreino <= 0 ||
                nome == null || nome.trim().isEmpty() ||
                series <= 0 || repeticoes <= 0 ||
                (carga != null && (carga < 0 ||
                        carga.isNaN() || carga.isInfinite()))) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {

            // Verificar se o treino pertence ao usuário
            try (Cursor cursor = db.rawQuery(
                    "SELECT id_treino FROM TREINO " +
                            "WHERE id_treino = ? AND id_usuario = ?",
                    new String[]{
                            String.valueOf(idTreino),
                            String.valueOf(idUsuario)
                    })) {

                if (!cursor.moveToFirst()) {
                    return false;
                }
            }

            // Cadastrar o exercício
            ContentValues valoresExercicio = new ContentValues();

            valoresExercicio.put("nome", nome.trim());
            valoresExercicio.put("grupo_muscular", grupoMuscular);

            long idExercicio = db.insertOrThrow(
                    "EXERCICIO", null, valoresExercicio
            );

            // Associar o exercício ao treino
            ContentValues valoresTreino = new ContentValues();

            valoresTreino.put("id_treino", idTreino);
            valoresTreino.put("id_exercicio", idExercicio);
            valoresTreino.put("series", series);
            valoresTreino.put("repeticoes", repeticoes);

            if (carga != null) {
                valoresTreino.put("carga", carga);
            } else {
                valoresTreino.putNull("carga");
            }

            db.insertOrThrow(
                    "TREINO_EXERCICIO", null, valoresTreino
            );

            db.setTransactionSuccessful();

            return true;

        } catch (android.database.SQLException e) {

            android.util.Log.e(
                    "NutriMov",
                    "Erro ao adicionar exercício ao treino",
                    e
            );

            return false;

        } finally {
            db.endTransaction();
        }
    }
    public Cursor buscarExerciciosTreino(long idUsuario, long idTreino) {

        SQLiteDatabase db = getReadableDatabase();

        String consulta =
                "SELECT EXERCICIO.nome, " +
                        "EXERCICIO.grupo_muscular, " +
                        "TREINO_EXERCICIO.series, " +
                        "TREINO_EXERCICIO.repeticoes, " +
                        "TREINO_EXERCICIO.carga " +
                        "FROM TREINO_EXERCICIO " +
                        "INNER JOIN EXERCICIO ON " +
                        "TREINO_EXERCICIO.id_exercicio = EXERCICIO.id_exercicio " +
                        "INNER JOIN TREINO ON " +
                        "TREINO_EXERCICIO.id_treino = TREINO.id_treino " +
                        "WHERE TREINO.id_usuario = ? " +
                        "AND TREINO.id_treino = ? " +
                        "ORDER BY TREINO_EXERCICIO.id_treino_exercicio ASC";

        return db.rawQuery(
                consulta,
                new String[]{
                        String.valueOf(idUsuario),
                        String.valueOf(idTreino)
                }
        );
    }
    public Cursor buscarPerfil(long idUsuario) {

        SQLiteDatabase db = getReadableDatabase();

        String consulta =
                "SELECT USUARIO.nome, " +
                        "USUARIO.email, " +
                        "PERFIL.idade, " +
                        "PERFIL.altura, " +
                        "PERFIL.peso, " +
                        "PERFIL.objetivo " +
                        "FROM USUARIO " +
                        "LEFT JOIN PERFIL ON " +
                        "USUARIO.id_usuario = PERFIL.id_usuario " +
                        "WHERE USUARIO.id_usuario = ?";

        return db.rawQuery(
                consulta,
                new String[]{String.valueOf(idUsuario)}
        );
    }
    public boolean salvarPerfil(long idUsuario, int idade,
                                double altura, double peso,
                                String objetivo) {

        if (idUsuario <= 0 || idade <= 0 ||
                altura <= 0 || peso <= 0 ||
                Double.isNaN(altura) || Double.isInfinite(altura) ||
                Double.isNaN(peso) || Double.isInfinite(peso)) {

            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put("id_usuario", idUsuario);
        valores.put("idade", idade);
        valores.put("altura", altura);
        valores.put("peso", peso);
        valores.put("objetivo", objetivo);

        db.beginTransaction();

        try {

            int resultado = db.update(
                    "PERFIL",
                    valores,
                    "id_usuario = ?",
                    new String[]{String.valueOf(idUsuario)}
            );

            if (resultado == 0) {

                db.insertOrThrow("PERFIL", null, valores);
            }

            db.setTransactionSuccessful();

            return true;

        } catch (android.database.SQLException e) {

            android.util.Log.e(
                    "NutriMov",
                    "Erro ao salvar perfil",
                    e
            );

            return false;

        } finally {

            db.endTransaction();
        }
    }
    public boolean salvarEvolucao(long idUsuario,
                                  double peso, double altura) {

        if (idUsuario <= 0 || peso <= 0 || altura <= 0 ||
                Double.isNaN(peso) || Double.isInfinite(peso) ||
                Double.isNaN(altura) || Double.isInfinite(altura)) {

            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        String data = new java.text.SimpleDateFormat(
                "yyyy-MM-dd",
                java.util.Locale.getDefault()
        ).format(new java.util.Date());

        ContentValues valores = new ContentValues();

        valores.put("id_usuario", idUsuario);
        valores.put("data_avaliacao", data);
        valores.put("peso", peso);
        valores.put("altura", altura);

        long resultado = db.insert(
                "AVALIACAO_CORPORAL",
                null,
                valores
        );

        return resultado != -1;
    }
    public Cursor buscarHistoricoEvolucao(long idUsuario) {

        SQLiteDatabase db = getReadableDatabase();

        String consulta =
                "SELECT data_avaliacao, peso, altura " +
                        "FROM AVALIACAO_CORPORAL " +
                        "WHERE id_usuario = ? " +
                        "ORDER BY data_avaliacao DESC, id_avaliacao DESC";

        return db.rawQuery(
                consulta,
                new String[]{String.valueOf(idUsuario)}
        );
    }
    public Cursor buscarRegistrosAlimentares(long idUsuario) {

        SQLiteDatabase db = getReadableDatabase();

        String consulta =
                "SELECT i.id_item, r.data, r.horario, " +
                        "r.tipo_refeicao, a.nome, i.quantidade " +
                        "FROM REFEICAO r " +
                        "INNER JOIN ITEM_REFEICAO i " +
                        "ON r.id_refeicao = i.id_refeicao " +
                        "INNER JOIN ALIMENTO a " +
                        "ON i.id_alimento = a.id_alimento " +
                        "WHERE r.id_usuario = ? " +
                        "ORDER BY r.data DESC, " +
                        "r.horario DESC, r.id_refeicao DESC";

        return db.rawQuery(
                consulta,
                new String[]{String.valueOf(idUsuario)}
        );
    }
    public boolean editarRefeicao(
            long idUsuario, long idItem,
            String tipo, String alimento,
            double quantidade) {

        if (idUsuario <= 0 || idItem <= 0 ||
                tipo == null || tipo.trim().isEmpty() ||
                alimento == null || alimento.trim().isEmpty() ||
                quantidade <= 0 ||
                Double.isNaN(quantidade) ||
                Double.isInfinite(quantidade)) {

            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {

            long idRefeicao;
            long idAlimentoAntigo;

            try (Cursor cursor = db.rawQuery(
                    "SELECT i.id_refeicao, i.id_alimento " +
                            "FROM ITEM_REFEICAO i " +
                            "INNER JOIN REFEICAO r " +
                            "ON i.id_refeicao = r.id_refeicao " +
                            "WHERE i.id_item = ? " +
                            "AND r.id_usuario = ?",
                    new String[]{
                            String.valueOf(idItem),
                            String.valueOf(idUsuario)
                    })) {

                if (!cursor.moveToFirst()) {
                    return false;
                }

                idRefeicao = cursor.getLong(0);
                idAlimentoAntigo = cursor.getLong(1);
            }

            ContentValues valoresRefeicao = new ContentValues();

            valoresRefeicao.put("tipo_refeicao", tipo.trim());

            db.update(
                    "REFEICAO",
                    valoresRefeicao,
                    "id_refeicao = ? AND id_usuario = ?",
                    new String[]{
                            String.valueOf(idRefeicao),
                            String.valueOf(idUsuario)
                    }
            );

            ContentValues valoresAlimento = new ContentValues();

            valoresAlimento.put("nome", alimento.trim());

            long idNovoAlimento = db.insertOrThrow(
                    "ALIMENTO", null, valoresAlimento
            );

            ContentValues valoresItem = new ContentValues();

            valoresItem.put("id_alimento", idNovoAlimento);
            valoresItem.put("quantidade", quantidade);

            db.update(
                    "ITEM_REFEICAO",
                    valoresItem,
                    "id_item = ?",
                    new String[]{String.valueOf(idItem)}
            );

            db.delete(
                    "ALIMENTO",
                    "id_alimento = ? AND NOT EXISTS (" +
                            "SELECT 1 FROM ITEM_REFEICAO " +
                            "WHERE id_alimento = ?)",
                    new String[]{
                            String.valueOf(idAlimentoAntigo),
                            String.valueOf(idAlimentoAntigo)
                    }
            );

            db.setTransactionSuccessful();

            return true;

        } catch (android.database.SQLException e) {

            android.util.Log.e(
                    "NutriMov",
                    "Erro ao editar refeição",
                    e
            );

            return false;

        } finally {

            db.endTransaction();
        }
    }
    public boolean excluirRefeicao(
            long idUsuario, long idItem) {

        if (idUsuario <= 0 || idItem <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {

            long idRefeicao;
            long idAlimento;

            try (Cursor cursor = db.rawQuery(
                    "SELECT i.id_refeicao, i.id_alimento " +
                            "FROM ITEM_REFEICAO i " +
                            "INNER JOIN REFEICAO r " +
                            "ON i.id_refeicao = r.id_refeicao " +
                            "WHERE i.id_item = ? " +
                            "AND r.id_usuario = ?",
                    new String[]{
                            String.valueOf(idItem),
                            String.valueOf(idUsuario)
                    })) {

                if (!cursor.moveToFirst()) {
                    return false;
                }

                idRefeicao = cursor.getLong(0);
                idAlimento = cursor.getLong(1);
            }

            int resultado = db.delete(
                    "ITEM_REFEICAO",
                    "id_item = ?",
                    new String[]{String.valueOf(idItem)}
            );

            if (resultado == 0) {
                return false;
            }

            db.delete(
                    "REFEICAO",
                    "id_refeicao = ? AND id_usuario = ? " +
                            "AND NOT EXISTS (" +
                            "SELECT 1 FROM ITEM_REFEICAO " +
                            "WHERE id_refeicao = ?)",
                    new String[]{
                            String.valueOf(idRefeicao),
                            String.valueOf(idUsuario),
                            String.valueOf(idRefeicao)
                    }
            );

            db.delete(
                    "ALIMENTO",
                    "id_alimento = ? AND NOT EXISTS (" +
                            "SELECT 1 FROM ITEM_REFEICAO " +
                            "WHERE id_alimento = ?)",
                    new String[]{
                            String.valueOf(idAlimento),
                            String.valueOf(idAlimento)
                    }
            );

            db.setTransactionSuccessful();

            return true;

        } catch (android.database.SQLException e) {

            android.util.Log.e(
                    "NutriMov",
                    "Erro ao excluir refeição",
                    e
            );

            return false;

        } finally {

            db.endTransaction();
        }
    }
    public Cursor buscarRegistrosExercicios(long idUsuario) {

        SQLiteDatabase db = getReadableDatabase();

        String consulta =
                "SELECT r.id_registro, e.nome, r.data, " +
                        "r.duracao_min, r.observacoes " +
                        "FROM REGISTRO_EXERCICIO r " +
                        "INNER JOIN EXERCICIO e " +
                        "ON r.id_exercicio = e.id_exercicio " +
                        "WHERE r.id_usuario = ? " +
                        "ORDER BY r.data DESC, " +
                        "r.id_registro DESC";

        return db.rawQuery(
                consulta,
                new String[]{String.valueOf(idUsuario)}
        );
    }
    public boolean editarExercicio(
            long idUsuario, long idRegistro,
            String nome, int duracao,
            String observacoes) {

        if (idUsuario <= 0 || idRegistro <= 0 ||
                nome == null || nome.trim().isEmpty() ||
                duracao <= 0) {

            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {

            long idExercicioAntigo;

            try (Cursor cursor = db.rawQuery(
                    "SELECT id_exercicio " +
                            "FROM REGISTRO_EXERCICIO " +
                            "WHERE id_registro = ? " +
                            "AND id_usuario = ?",
                    new String[]{
                            String.valueOf(idRegistro),
                            String.valueOf(idUsuario)
                    })) {

                if (!cursor.moveToFirst()) {
                    return false;
                }

                idExercicioAntigo = cursor.getLong(0);
            }

            ContentValues valoresExercicio =
                    new ContentValues();

            valoresExercicio.put("nome", nome.trim());

            long idNovoExercicio = db.insertOrThrow(
                    "EXERCICIO",
                    null,
                    valoresExercicio
            );

            ContentValues valoresRegistro =
                    new ContentValues();

            valoresRegistro.put(
                    "id_exercicio", idNovoExercicio
            );

            valoresRegistro.put(
                    "duracao_min", duracao
            );

            valoresRegistro.put(
                    "observacoes", observacoes
            );

            int resultado = db.update(
                    "REGISTRO_EXERCICIO",
                    valoresRegistro,
                    "id_registro = ? AND id_usuario = ?",
                    new String[]{
                            String.valueOf(idRegistro),
                            String.valueOf(idUsuario)
                    }
            );

            if (resultado == 0) {
                return false;
            }

            db.delete(
                    "EXERCICIO",
                    "id_exercicio = ? " +
                            "AND NOT EXISTS (" +
                            "SELECT 1 FROM REGISTRO_EXERCICIO " +
                            "WHERE id_exercicio = ?) " +
                            "AND NOT EXISTS (" +
                            "SELECT 1 FROM TREINO_EXERCICIO " +
                            "WHERE id_exercicio = ?)",
                    new String[]{
                            String.valueOf(idExercicioAntigo),
                            String.valueOf(idExercicioAntigo),
                            String.valueOf(idExercicioAntigo)
                    }
            );

            db.setTransactionSuccessful();

            return true;

        } catch (android.database.SQLException e) {

            android.util.Log.e(
                    "NutriMov",
                    "Erro ao editar exercício",
                    e
            );

            return false;

        } finally {

            db.endTransaction();
        }
    }
    public boolean excluirExercicio(
            long idUsuario, long idRegistro) {

        if (idUsuario <= 0 || idRegistro <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {

            long idExercicio;

            try (Cursor cursor = db.rawQuery(
                    "SELECT id_exercicio " +
                            "FROM REGISTRO_EXERCICIO " +
                            "WHERE id_registro = ? " +
                            "AND id_usuario = ?",
                    new String[]{
                            String.valueOf(idRegistro),
                            String.valueOf(idUsuario)
                    })) {

                if (!cursor.moveToFirst()) {
                    return false;
                }

                idExercicio = cursor.getLong(0);
            }

            int resultado = db.delete(
                    "REGISTRO_EXERCICIO",
                    "id_registro = ? AND id_usuario = ?",
                    new String[]{
                            String.valueOf(idRegistro),
                            String.valueOf(idUsuario)
                    }
            );

            if (resultado == 0) {
                return false;
            }

            db.delete(
                    "EXERCICIO",
                    "id_exercicio = ? " +
                            "AND NOT EXISTS (" +
                            "SELECT 1 FROM REGISTRO_EXERCICIO " +
                            "WHERE id_exercicio = ?) " +
                            "AND NOT EXISTS (" +
                            "SELECT 1 FROM TREINO_EXERCICIO " +
                            "WHERE id_exercicio = ?)",
                    new String[]{
                            String.valueOf(idExercicio),
                            String.valueOf(idExercicio),
                            String.valueOf(idExercicio)
                    }
            );

            db.setTransactionSuccessful();

            return true;

        } catch (android.database.SQLException e) {

            android.util.Log.e(
                    "NutriMov",
                    "Erro ao excluir exercício",
                    e
            );

            return false;

        } finally {

            db.endTransaction();
        }
    }
    public boolean editarTreino(
            long idUsuario, long idTreino,
            String nome, String descricao,
            String diasSemana) {

        if (idUsuario <= 0 || idTreino <= 0 ||
                nome == null || nome.trim().isEmpty()) {

            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put("nome", nome.trim());
        valores.put("descricao", descricao);
        valores.put("dias_semana", diasSemana);

        try {

            int resultado = db.update(
                    "TREINO",
                    valores,
                    "id_treino = ? AND id_usuario = ?",
                    new String[]{
                            String.valueOf(idTreino),
                            String.valueOf(idUsuario)
                    }
            );

            return resultado > 0;

        } catch (android.database.SQLException e) {

            android.util.Log.e(
                    "NutriMov",
                    "Erro ao editar treino",
                    e
            );

            return false;
        }
    }
    public boolean excluirTreino(
            long idUsuario, long idTreino) {

        if (idUsuario <= 0 || idTreino <= 0) {

            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {

            // Verificar se o treino pertence ao usuário

            try (Cursor cursor = db.rawQuery(
                    "SELECT id_treino FROM TREINO " +
                            "WHERE id_treino = ? " +
                            "AND id_usuario = ?",
                    new String[]{
                            String.valueOf(idTreino),
                            String.valueOf(idUsuario)
                    })) {

                if (!cursor.moveToFirst()) {

                    return false;
                }
            }

            // Identificar os exercícios associados

            java.util.ArrayList<Long> idsExercicios =
                    new java.util.ArrayList<>();

            try (Cursor cursor = db.rawQuery(
                    "SELECT id_exercicio " +
                            "FROM TREINO_EXERCICIO " +
                            "WHERE id_treino = ?",
                    new String[]{
                            String.valueOf(idTreino)
                    })) {

                while (cursor.moveToNext()) {

                    idsExercicios.add(cursor.getLong(0));
                }
            }

            // Preservar o histórico de exercícios

            ContentValues valores = new ContentValues();

            valores.putNull("id_treino");

            db.update(
                    "REGISTRO_EXERCICIO",
                    valores,
                    "id_treino = ?",
                    new String[]{
                            String.valueOf(idTreino)
                    }
            );

            // Excluir as associações do treino

            db.delete(
                    "TREINO_EXERCICIO",
                    "id_treino = ?",
                    new String[]{
                            String.valueOf(idTreino)
                    }
            );

            // Excluir o treino

            int resultado = db.delete(
                    "TREINO",
                    "id_treino = ? AND id_usuario = ?",
                    new String[]{
                            String.valueOf(idTreino),
                            String.valueOf(idUsuario)
                    }
            );

            if (resultado == 0) {

                return false;
            }

            // Excluir somente exercícios sem vínculos

            for (long idExercicio : idsExercicios) {

                db.delete(
                        "EXERCICIO",
                        "id_exercicio = ? " +
                                "AND NOT EXISTS (" +
                                "SELECT 1 FROM TREINO_EXERCICIO " +
                                "WHERE id_exercicio = ?) " +
                                "AND NOT EXISTS (" +
                                "SELECT 1 FROM REGISTRO_EXERCICIO " +
                                "WHERE id_exercicio = ?)",
                        new String[]{
                                String.valueOf(idExercicio),
                                String.valueOf(idExercicio),
                                String.valueOf(idExercicio)
                        }
                );
            }

            db.setTransactionSuccessful();

            return true;

        } catch (android.database.SQLException e) {

            android.util.Log.e(
                    "NutriMov",
                    "Erro ao excluir treino",
                    e
            );

            return false;

        } finally {

            db.endTransaction();
        }
    }
    public Cursor buscarExerciciosTreinoEdicao(
            long idUsuario, long idTreino) {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT te.id_treino_exercicio, " +
                        "e.nome, e.grupo_muscular, " +
                        "te.series, te.repeticoes, te.carga " +
                        "FROM TREINO_EXERCICIO te " +
                        "INNER JOIN EXERCICIO e " +
                        "ON e.id_exercicio = te.id_exercicio " +
                        "INNER JOIN TREINO t " +
                        "ON t.id_treino = te.id_treino " +
                        "WHERE t.id_usuario = ? " +
                        "AND te.id_treino = ? " +
                        "ORDER BY te.id_treino_exercicio ASC",
                new String[]{
                        String.valueOf(idUsuario),
                        String.valueOf(idTreino)
                }
        );
    }
    public boolean editarExercicioTreino(
            long idUsuario,
            long idTreino,
            long idTreinoExercicio,
            String nome,
            String grupoMuscular,
            int series,
            int repeticoes,
            Double carga) {

        if (idUsuario <= 0 ||
                idTreino <= 0 ||
                idTreinoExercicio <= 0 ||
                nome == null ||
                nome.trim().isEmpty() ||
                series <= 0 ||
                repeticoes <= 0) {

            return false;
        }

        if (carga != null &&
                (Double.isNaN(carga) ||
                        Double.isInfinite(carga) ||
                        carga < 0)) {

            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {

            long idExercicioAntigo;

            // Verificar se o exercício pertence ao treino do usuário

            try (Cursor cursor = db.rawQuery(
                    "SELECT te.id_exercicio " +
                            "FROM TREINO_EXERCICIO te " +
                            "INNER JOIN TREINO t " +
                            "ON t.id_treino = te.id_treino " +
                            "WHERE te.id_treino_exercicio = ? " +
                            "AND te.id_treino = ? " +
                            "AND t.id_usuario = ?",
                    new String[]{
                            String.valueOf(idTreinoExercicio),
                            String.valueOf(idTreino),
                            String.valueOf(idUsuario)
                    })) {

                if (!cursor.moveToFirst()) {

                    return false;
                }

                idExercicioAntigo = cursor.getLong(0);
            }

            // Criar exercício com os dados atualizados

            ContentValues valoresExercicio =
                    new ContentValues();

            valoresExercicio.put("nome", nome.trim());

            valoresExercicio.put(
                    "grupo_muscular",
                    grupoMuscular
            );

            long novoIdExercicio = db.insertOrThrow(
                    "EXERCICIO",
                    null,
                    valoresExercicio
            );

            // Atualizar o exercício dentro do treino

            ContentValues valoresTreino =
                    new ContentValues();

            valoresTreino.put(
                    "id_exercicio",
                    novoIdExercicio
            );

            valoresTreino.put("series", series);

            valoresTreino.put("repeticoes", repeticoes);

            if (carga == null) {

                valoresTreino.putNull("carga");

            } else {

                valoresTreino.put("carga", carga);
            }

            int resultado = db.update(
                    "TREINO_EXERCICIO",
                    valoresTreino,
                    "id_treino_exercicio = ? AND id_treino = ?",
                    new String[]{
                            String.valueOf(idTreinoExercicio),
                            String.valueOf(idTreino)
                    }
            );

            if (resultado == 0) {

                return false;
            }

            // Remover exercício antigo somente se não possuir vínculos

            db.delete(
                    "EXERCICIO",
                    "id_exercicio = ? " +
                            "AND NOT EXISTS (" +
                            "SELECT 1 FROM TREINO_EXERCICIO " +
                            "WHERE id_exercicio = ?) " +
                            "AND NOT EXISTS (" +
                            "SELECT 1 FROM REGISTRO_EXERCICIO " +
                            "WHERE id_exercicio = ?)",
                    new String[]{
                            String.valueOf(idExercicioAntigo),
                            String.valueOf(idExercicioAntigo),
                            String.valueOf(idExercicioAntigo)
                    }
            );

            db.setTransactionSuccessful();

            return true;

        } catch (android.database.SQLException e) {

            android.util.Log.e(
                    "NutriMov",
                    "Erro ao editar exercício do treino",
                    e
            );

            return false;

        } finally {

            db.endTransaction();
        }
    }
    public boolean excluirExercicioTreino(
            long idUsuario,
            long idTreino,
            long idTreinoExercicio) {

        if (idUsuario <= 0 ||
                idTreino <= 0 ||
                idTreinoExercicio <= 0) {

            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {

            long idExercicio;

            // Verificar se o exercício pertence ao usuário

            try (Cursor cursor = db.rawQuery(
                    "SELECT te.id_exercicio " +
                            "FROM TREINO_EXERCICIO te " +
                            "INNER JOIN TREINO t " +
                            "ON t.id_treino = te.id_treino " +
                            "WHERE te.id_treino_exercicio = ? " +
                            "AND te.id_treino = ? " +
                            "AND t.id_usuario = ?",
                    new String[]{
                            String.valueOf(idTreinoExercicio),
                            String.valueOf(idTreino),
                            String.valueOf(idUsuario)
                    })) {

                if (!cursor.moveToFirst()) {

                    return false;
                }

                idExercicio = cursor.getLong(0);
            }

            // Excluir exercício da lista do treino

            int resultado = db.delete(
                    "TREINO_EXERCICIO",
                    "id_treino_exercicio = ? AND id_treino = ?",
                    new String[]{
                            String.valueOf(idTreinoExercicio),
                            String.valueOf(idTreino)
                    }
            );

            if (resultado == 0) {

                return false;
            }

            // Excluir exercício apenas se não possuir outros vínculos

            db.delete(
                    "EXERCICIO",
                    "id_exercicio = ? " +
                            "AND NOT EXISTS (" +
                            "SELECT 1 FROM TREINO_EXERCICIO " +
                            "WHERE id_exercicio = ?) " +
                            "AND NOT EXISTS (" +
                            "SELECT 1 FROM REGISTRO_EXERCICIO " +
                            "WHERE id_exercicio = ?)",
                    new String[]{
                            String.valueOf(idExercicio),
                            String.valueOf(idExercicio),
                            String.valueOf(idExercicio)
                    }
            );

            db.setTransactionSuccessful();

            return true;

        } catch (android.database.SQLException e) {

            android.util.Log.e(
                    "NutriMov",
                    "Erro ao excluir exercício do treino",
                    e
            );

            return false;

        } finally {

            db.endTransaction();
        }
    }
    public Cursor buscarRegistrosEvolucao(long idUsuario) {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT id_avaliacao, data_avaliacao, " +
                        "peso, altura " +
                        "FROM AVALIACAO_CORPORAL " +
                        "WHERE id_usuario = ? " +
                        "ORDER BY data_avaliacao DESC, " +
                        "id_avaliacao DESC",
                new String[]{
                        String.valueOf(idUsuario)
                }
        );
    }
    public boolean editarEvolucao(
            long idUsuario,
            long idAvaliacao,
            double peso,
            double altura) {

        if (idUsuario <= 0 ||
                idAvaliacao <= 0 ||
                peso <= 0 ||
                altura <= 0 ||
                Double.isNaN(peso) ||
                Double.isNaN(altura) ||
                Double.isInfinite(peso) ||
                Double.isInfinite(altura)) {

            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put("peso", peso);

        valores.put("altura", altura);

        try {

            int resultado = db.update(
                    "AVALIACAO_CORPORAL",
                    valores,
                    "id_avaliacao = ? AND id_usuario = ?",
                    new String[]{
                            String.valueOf(idAvaliacao),
                            String.valueOf(idUsuario)
                    }
            );

            return resultado > 0;

        } catch (android.database.SQLException e) {

            android.util.Log.e(
                    "NutriMov",
                    "Erro ao editar avaliação corporal",
                    e
            );

            return false;
        }
    }
    public boolean excluirEvolucao(
            long idUsuario,
            long idAvaliacao) {

        if (idUsuario <= 0 || idAvaliacao <= 0) {

            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        try {

            int resultado = db.delete(
                    "AVALIACAO_CORPORAL",
                    "id_avaliacao = ? AND id_usuario = ?",
                    new String[]{
                            String.valueOf(idAvaliacao),
                            String.valueOf(idUsuario)
                    }
            );

            return resultado > 0;

        } catch (android.database.SQLException e) {

            android.util.Log.e(
                    "NutriMov",
                    "Erro ao excluir avaliação corporal",
                    e
            );

            return false;
        }
    }
    public boolean excluirConta(long idUsuario, String senha) {

        if (idUsuario <= 0 ||
                senha == null ||
                senha.isEmpty()) {

            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        String[] argumentos = {
                String.valueOf(idUsuario)
        };

        db.beginTransaction();

        try {

            // 1. Confirmar a senha do usuário

            try (Cursor cursor = db.rawQuery(
                    "SELECT senha FROM USUARIO " +
                            "WHERE id_usuario = ?",
                    argumentos)) {

                if (!cursor.moveToFirst()) {

                    return false;
                }

                String senhaArmazenada = cursor.getString(0);

                if (!conferirSenha(senha, senhaArmazenada)) {

                    return false;
                }
            }

            // 2. Identificar os alimentos vinculados à conta

            java.util.HashSet<Long> idsAlimentos =
                    new java.util.HashSet<>();

            try (Cursor cursor = db.rawQuery(
                    "SELECT DISTINCT i.id_alimento " +
                            "FROM ITEM_REFEICAO i " +
                            "INNER JOIN REFEICAO r " +
                            "ON i.id_refeicao = r.id_refeicao " +
                            "WHERE r.id_usuario = ?",
                    argumentos)) {

                while (cursor.moveToNext()) {

                    idsAlimentos.add(cursor.getLong(0));
                }
            }

            // 3. Identificar os exercícios registrados

            java.util.HashSet<Long> idsExercicios =
                    new java.util.HashSet<>();

            try (Cursor cursor = db.rawQuery(
                    "SELECT DISTINCT id_exercicio " +
                            "FROM REGISTRO_EXERCICIO " +
                            "WHERE id_usuario = ?",
                    argumentos)) {

                while (cursor.moveToNext()) {

                    idsExercicios.add(cursor.getLong(0));
                }
            }

            // 4. Identificar os exercícios vinculados aos treinos

            try (Cursor cursor = db.rawQuery(
                    "SELECT DISTINCT te.id_exercicio " +
                            "FROM TREINO_EXERCICIO te " +
                            "INNER JOIN TREINO t " +
                            "ON te.id_treino = t.id_treino " +
                            "WHERE t.id_usuario = ?",
                    argumentos)) {

                while (cursor.moveToNext()) {

                    idsExercicios.add(cursor.getLong(0));
                }
            }

            // 5. Excluir os itens das refeições

            db.delete(
                    "ITEM_REFEICAO",
                    "id_refeicao IN (" +
                            "SELECT id_refeicao FROM REFEICAO " +
                            "WHERE id_usuario = ?)",
                    argumentos
            );

            // 6. Excluir as refeições

            db.delete(
                    "REFEICAO",
                    "id_usuario = ?",
                    argumentos
            );

            // 7. Excluir os registros de exercícios

            db.delete(
                    "REGISTRO_EXERCICIO",
                    "id_usuario = ?",
                    argumentos
            );

            // 8. Excluir as associações dos exercícios aos treinos

            db.delete(
                    "TREINO_EXERCICIO",
                    "id_treino IN (" +
                            "SELECT id_treino FROM TREINO " +
                            "WHERE id_usuario = ?)",
                    argumentos
            );

            // 9. Excluir os treinos do usuário

            db.delete(
                    "TREINO",
                    "id_usuario = ?",
                    argumentos
            );

            // 10. Excluir somente alimentos sem outros vínculos

            for (long idAlimento : idsAlimentos) {

                db.delete(
                        "ALIMENTO",
                        "id_alimento = ? " +
                                "AND NOT EXISTS (" +
                                "SELECT 1 FROM ITEM_REFEICAO " +
                                "WHERE id_alimento = ?)",
                        new String[]{
                                String.valueOf(idAlimento),
                                String.valueOf(idAlimento)
                        }
                );
            }

            // 11. Excluir somente exercícios sem outros vínculos

            for (long idExercicio : idsExercicios) {

                db.delete(
                        "EXERCICIO",
                        "id_exercicio = ? " +
                                "AND NOT EXISTS (" +
                                "SELECT 1 FROM REGISTRO_EXERCICIO " +
                                "WHERE id_exercicio = ?) " +
                                "AND NOT EXISTS (" +
                                "SELECT 1 FROM TREINO_EXERCICIO " +
                                "WHERE id_exercicio = ?)",
                        new String[]{
                                String.valueOf(idExercicio),
                                String.valueOf(idExercicio),
                                String.valueOf(idExercicio)
                        }
                );
            }

            // 12. Excluir o histórico de evolução corporal

            db.delete(
                    "AVALIACAO_CORPORAL",
                    "id_usuario = ?",
                    argumentos
            );

            // 13. Excluir o perfil

            db.delete(
                    "PERFIL",
                    "id_usuario = ?",
                    argumentos
            );

            // 14. Excluir a conta do usuário

            int resultado = db.delete(
                    "USUARIO",
                    "id_usuario = ?",
                    argumentos
            );

            if (resultado != 1) {

                return false;
            }

            // Confirmar todas as exclusões

            db.setTransactionSuccessful();

            return true;

        } catch (android.database.SQLException e) {

            android.util.Log.e(
                    "NutriMov",
                    "Erro ao excluir conta",
                    e
            );

            return false;

        } finally {

            db.endTransaction();
        }
    }
}