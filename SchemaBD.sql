CREATE TABLE ContaCorrente (
    numero VARCHAR(20) PRIMARY KEY,
    saldo DECIMAL(15, 2) NOT NULL CHECK(saldo >= 0),
    limite_credito DECIMAL(15, 2) NOT NULL CHECK (limite_credito >= 0),
    data_criacao DATE NOT NULL,
    limite_Maximo DECIMAL(15,2) NOT NULL
);

CREATE TABLE Transacao (
    id UUID PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL CHECK( tipo IN('DEPOSITO', 'SAQUE', 'TRANSFERENCIA')),
    valor DECIMAL(15, 2) NOT NULL CHECK(valor >= 0),
    data DATE NOT NULL,
    descricao VARCHAR(255),
    conta_corrente_numero VARCHAR(20),
    FOREIGN KEY (conta_corrente_numero) REFERENCES ContaCorrente(numero) ON DELETE CASCADE
                       INDEX idx_conta_corrente_numero(conta_corrente_numero)
);

CREATE TABLE Auditoria (
    id UUID PRIMARY KEY,
    mensagem VARCHAR(255) NOT NULL,
    transacao_id UUID,
    FOREIGN KEY (transacao_id) REFERENCES Transacao(id) ON DELETE CASCADE
);