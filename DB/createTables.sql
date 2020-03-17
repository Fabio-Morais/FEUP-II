CREATE SCHEMA IF NOT EXISTS fabrica;
SET search_path to fabrica;

CREATE TABLE IF NOT EXISTS Fabrica(
	id SERIAL PRIMARY KEY,
	nPecasFabrica int
);

CREATE TABLE IF NOT EXISTS ZonaDescarga(
    id SERIAL PRIMARY KEY,
	tipoDescarga VARCHAR(10),
	tipoPecaDescarregada VARCHAR(5)
   
);


CREATE TABLE IF NOT EXISTS Ordem(
	numeroOrdem VARCHAR(10) PRIMARY KEY,
	estadoOrdem int,
    pecasProduzidas int,
    pecasProducao int,
    pecasPendentes int,
	horaEntradaOrdem TIMESTAMP,
	horaInicioExecucao TIMESTAMP,
	horaFimExecucao TIMESTAMP,
    folgaExecucao int
    );

    CREATE TABLE IF NOT EXISTS Producao(
    numeroOrdem VARCHAR(10) REFERENCES Ordem(numeroOrdem) PRIMARY KEY,    
	pecaOrigem VARCHAR(10) ,
    pecaFinal VARCHAR(10),
    quantidadeProduzir int,
    atrasoMaximo int
    );

CREATE TABLE IF NOT EXISTS Maquina(
    numeroOrdem VARCHAR(10) REFERENCES Ordem(numeroOrdem) PRIMARY KEY,    
    pecaDescarga VARCHAR(10) ,
    destino VARCHAR(10),
    quantidadePecasDescarregar int
    );
    
