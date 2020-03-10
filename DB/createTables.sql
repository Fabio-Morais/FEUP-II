CREATE SCHEMA IF NOT EXISTS fabrica;
SET search_path to fabrica;

CREATE TABLE IF NOT EXISTS Fabrica(
	id SERIAL PRIMARY KEY,
	nPecasFabrica int
);

CREATE TABLE IF NOT EXISTS Descarga(
	tipoDescarga VARCHAR(50) PRIMARY KEY,
	tipoPecaDescarregada VARCHAR(50),
    id_fabrica INTEGER NOT NULL,
	FOREIGN KEY (id_fabrica) REFERENCES Fabrica(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Ordem(
	numeroOrdem VARCHAR(50) PRIMARY KEY,
	estadoOrdem VARCHAR(50),
    pecasProduzidas int,
    pecasProducao int,
    pecasPendentes int,
	horaEntradaOrdem TIMESTAMP,
	horaInicioExecucao TIMESTAMP,
	horaFimExecucao TIMESTAMP,
    folgaExecucao int,
    id_fabrica INTEGER NOT NULL,
    FOREIGN KEY (id_fabrica) REFERENCES Fabrica(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS Maquina(
	tipoMaquina VARCHAR(50)  PRIMARY KEY,
    tipoPecaOperada VARCHAR(50),
    tempoTotal int,
    id_fabrica INTEGER NOT NULL,
    FOREIGN KEY (id_fabrica) REFERENCES Fabrica(id) ON DELETE CASCADE
    );

