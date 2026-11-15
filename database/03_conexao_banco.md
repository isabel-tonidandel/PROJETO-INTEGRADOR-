# Conexão com o Banco de Dados – NutriMov

## 1. Banco de dados utilizado

O aplicativo NutriMov – Saúde e Bem-Estar utiliza o SQLite como sistema de gerenciamento de banco de dados.

O SQLite é um banco de dados relacional que permite armazenar informações localmente no dispositivo Android, sem necessidade de conexão com a internet.

## 2. Tecnologias utilizadas

- Linguagem de programação: Java
- Banco de dados: SQLite
- Ambiente de desenvolvimento: Android Studio
- Interface do aplicativo: XML

## 3. Configuração da conexão

A conexão com o banco de dados será realizada no aplicativo Android utilizando a classe SQLiteOpenHelper.

Nome previsto do banco de dados: `nutrimov.db`

O banco de dados armazenará informações de usuários, perfis, alimentos, refeições, exercícios, treinos e avaliações corporais.

## 4. Integridade dos dados

O banco utiliza chaves primárias e estrangeiras para estabelecer os relacionamentos entre as tabelas.

O comando `PRAGMA foreign_keys = ON;` habilita a verificação das chaves estrangeiras nas conexões SQLite em que for executado.

## 5. Conexões remotas

O SQLite será utilizado localmente no dispositivo Android. Portanto, nesta versão do projeto, não será necessária uma conexão remota com servidor de banco de dados.

## 6. Implementação

A implementação da classe de conexão com o banco de dados no Android Studio está prevista para a etapa de desenvolvimento do aplicativo.
