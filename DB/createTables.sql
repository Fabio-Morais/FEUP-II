CREATE SCHEMA IF NOT EXISTS fabrica;
SET search_path to fabrica;

CREATE TABLE IF NOT EXISTS Maquina(
	id SERIAL PRIMARY KEY,
	tipoMaquina VARCHAR(10),
    tipoPecaOperada VARCHAR(10),
    tempo int
);

CREATE TABLE IF NOT EXISTS ZonaDescarga(
    id SERIAL PRIMARY KEY,
	tipoDescarga VARCHAR(10),
	tipoPecaDescarregada VARCHAR(5)
   
);


CREATE TABLE IF NOT EXISTS Ordem(
	numeroOrdem VARCHAR(50) PRIMARY KEY,
	estadoOrdem VARCHAR(50) default 0,
    pecasProduzidas int default 0,
    pecasProducao int default 0,
    pecasPendentes int default 0,
	horaEntradaOrdem TIMESTAMP,
	horaInicioExecucao TIMESTAMP,
	horaFimExecucao TIMESTAMP,
    folgaExecucao int default 0
    );

    CREATE TABLE IF NOT EXISTS Producao(
    numeroOrdem VARCHAR(10) PRIMARY KEY,
    FOREIGN KEY (numeroOrdem) REFERENCES Ordem(numeroOrdem) ON DELETE CASCADE,
	pecaOrigem VARCHAR(10) ,
    pecaFinal VARCHAR(10),
    quantidadeProduzir int,
    atrasoMaximo int
    );

CREATE TABLE IF NOT EXISTS Descarga(
    numeroOrdem VARCHAR(10) PRIMARY KEY,
    FOREIGN KEY (numeroOrdem) REFERENCES Ordem(numeroOrdem) ON DELETE CASCADE,	
    pecaDescarga VARCHAR(10) ,
    destino VARCHAR(10),
    quantidadePecasDescarregar int
    );
    
