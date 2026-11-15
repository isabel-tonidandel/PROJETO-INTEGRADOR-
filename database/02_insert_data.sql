-- PROJETO INTEGRADOR - NUTRIMOV
-- SCRIPT DML - INSERÇAÕ DE DADOS

-- INSERIR USUÁRIOS

INSERT INTO USUARIO (nome,email, senha, data_cadastro)
VALUES
('Maria dos Santos', 'mariasantos@gmail.com', 'teste_01', '2026-11-11'),
('João da Silva','silvajoao@gmail.com', 'teste_02', '2026-11-11'),
('Luisa Pereira', 'luisapereira@gmail.com', 'teste_03', '2026-11-11');

  -- INSERIR PERFIS

  INSERT INTO PERFIL (id_usuario, idade, altura, peso, objetivo)
  VALUES 
  (1, 25, 1.65, 60.0, 'condicionamento físico'),
  (2, 19, 1.80, 82.0, 'Ganho de massa muscular'),
  (3, 33, 1.60, 70.0, 'Perda de peso');

  -- CONSULTAR PERFIS

  SELECT * FROM PERFIL;

  -- INSERIR ALIMENTOS

  INSERT INTO ALIMENTO (nome, categoria, calorias)
  VALUES
    ('Arroz branco', 'Carboidrato', 128),
    ('Peito de frango','Proteina', 165),
    ('Ovo cozido', 'Proteina', 155),
    ('Banana', 'Fruta', 89),
    ('Batata doce', 'Carboidrato', 86);

-- CONSULTAR ALIMENTOS

SELECT * FROM ALIMENTO;

-- INSERIR REFEIÇÕES

INSERT INTO REFEICAO (id_usuario, data, horario, tipo_refeicao)
VALUES
(1, '2026-11-11', '08:00', 'Café da manhã'),
(2, '2026-11-11', '12:00', 'almoço'),
(3, '2026-11-11', '19:00', 'Jantar');

-- CONSULTAR REFEIÇÕES

SELECT * FROM REFEICAO;

-- INSERIR ITENS DAS REFEIÇÕES

INSERT INTO ITEM_REFEICAO (id_refeicao, id_alimento, quantidade)
  VALUES
  (1, 1, 100),
  (1, 2, 150),
  (2, 3, 2),
  (3, 4, 100);

-- CONSULTAR ITENS DAS REFEIÇÕES

SELECT * FROM ITEM_REFEICAO;

-- INSERIR EXERCÍCIOS

INSERT INTO EXERCICIO (nome, grupo_muscular, descricao)
VALUES
('Agachamento', 'Pernas', 'Exercício para membros inferiores'),
('Supino reto', 'Peito', 'Exercício para peitoral'),
('Puxada alta', 'Costas', 'Exercício para dorsais'),
('Rosca direta', 'Bíceps', 'Exercício para bíceps'),
('Abdominal', 'Abdômen', 'Exercício para região abdominal');

-- CONSULTAR EXERCÍCIOS

SELECT * FROM EXERCICIO;

-- INSERIR TREINOS

INSERT INTO TREINO (id_usuario, nome, descricao, dias_semana)
VALUES
(1, 'Treino de pernas', 'Treino focado em membros inferiores', 'Segunda e quinta'),
(2, 'Treino superior', 'Treino para membros superiores', 'Terça e sexta'),
(3, 'Treino Completo', 'Treino geral para o corpo', 'Segunda, quarta e sexta');


-- CONSULTAR TREINOS

SELECT * FROM TREINO;

-- INSERIR EXERCÍCIOS NOS TREINOS

INSERT INTO TREINO_EXERCICIO (id_treino, id_exercicio, series, repeticoes, ordem)
VALUES
(1, 1, 3, 12, 1),
(1, 5, 3, 15, 2),
(2, 2, 3, 10, 1),
(2, 3, 3, 12, 2),
(2, 4, 3, 12, 3), 
(3, 1, 3, 12, 1),
(3, 2, 3, 10 ,2);

-- CONSULTAR EXERCÍCIOS DOS TREINOS

SELECT * FROM TREINO_EXERCICIO;

-- INSERIR REGISTROS DE EXERCÍCIOS

INSERT INTO REGISTRO_EXERCICIO (id_usuario, id_exercicio, id_treino, data, series_realizadas, repeticoes, carga)
VALUES
(1, 1, 1, '2026-11-11', 3, 12, 20.0),
(2, 2, 2, '2026-11-11', 3, 10, 30.0),
(3, 4, 3, '2026-11-11', 3, 10, 10.0);

-- CONSULTAR REGISTROS EXERCÍCIOS

SELECT * FROM REGISTRO_EXERCICIO;

-- INSERIR AVALIAÇÕES CORPORAIS

INSERT INTO AVALIACAO_CORPORAL (id_usuario, data_avaliacao, peso, altura)
VALUES 
(1, '2026-11-11', 60.0, 1.65),
(2, '2026-11-11', 82.0, 1.80),
(3, '2026-11-11', 70.0, 1.60);

-- CONSULTAR AVALIAÇÕES CORPORAIS

SELECT * FROM AVALIACAO_CORPORAL;

-- ATUALIZAR DADOS PERFIL

UPDATE PERFIL
SET objetivo = 'Saúde e condicionamento físico'
WHERE id_usuario = 1;

-- CONSULTAR PERFIL ATUALIZADO

SELECT * FROM PERFIL
WHERE id_usuario = 1;

-- EXCLUIR UM REGISTRO DE AVALIAÇÃO

DELETE FROM AVALIACAO_CORPORAL
WHERE id_avaliacao = 3;

-- CONSULTAR AVALIAÇÕES RESTANTES

SELECT * FROM AVALIACAO_CORPORAL;
