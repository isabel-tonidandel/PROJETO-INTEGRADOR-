-- PROJETO INTEGRADOR - NUTRIMOV
-- SCRIPT DML - INSERÇAÕ DE DADOS

-- INSERIR USUÁRIOS

INSERT INTO USUARIO (nome,email, senha, data_cadastro)
VALUES
('Maria dos Santos', 'mariasantos@gmail.com', 'teste_01', '2026-11-11),
('João da Silva','silvajoao@gmail.com', 'teste_02', '2026-11-11),
('Luisa Pereira', 'luisapereira@gmail.com', 'teste_03', '2026-11-11);

  -- INSERIR PERFIS

  INSERT INTO PERFIL (id_usuario, idade, altura, peso, objetivo)
  VALUES 
  (1, 25, 1.65, 60.0, 'condicionamento físico'),
  (2, 19, 1.80, 82.0, 'Ganho de massa muscular'),
  (3, 33, 1.60, 70.0, 'Perda de peso');

  -- CONSULTAR PERFIS

  SELECT * FROM PERFIL;

  -- INSERIR REGISTRO DE ALIMENTAÇÃO

  INSERT INTO REGISTRO_ALIMENTACAO
  (id_usuario, alimento, quantidade, data_registro)
  VALUES
  (1, 'Arroz e frango', '300g', '2026-11-11'),
  (2, 'Ovos e pão integral', '200g', '2026-11-11'),
  (3, 'Salada e peixe', '300g', '2026-11-11'),
