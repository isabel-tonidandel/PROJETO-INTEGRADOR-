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
    ('Batata doce', 'Carboidrato', 86),

-- CONSULTAR ALIMENTOS

SELECT * FROM ALIMENTO;

-- INSERIR REFEIÇÕES

INSERT INTO REFEICAO (id_usuario, data, horario, tipo_refeicao)
VALUES
(1, '2026-11-11', '08:00', 'Café da manhã'),
(2, '2026-11-11', '12:00', 'almoço'),
(3, '2026-11-11', '19:00', 'Jantar');

-- Consultar Refeições 

SELECT * FROM REFEICAO;


