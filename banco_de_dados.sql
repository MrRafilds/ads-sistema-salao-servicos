create database if not exists trabalhoPoo;
use trabalhoPoo;

create table if not exists tipoServico(
    idTipoServico integer primary key auto_increment,
    descricao varchar(80),
    preco double
);

create table if not exists cliente(
    idCliente integer primary key auto_increment,
    nome varchar(80) not null,
    cpf varchar(20) not null,
    telefone varchar(15)
);

create table if not exists servico(
    idServico integer primary key auto_increment,
    cliente integer not null,
    tipoServico integer not null,
    detalhe varchar(200),
    valor double,
    data date,
    foreign key (cliente) references cliente(idCliente),
    foreign key (tipoServico) references tipoServico(idTipoServico)
);

-- Dados de exemplo (opcional)
insert into tipoServico (descricao, preco) values
    ('Corte Masculino', 35.00),
    ('Corte Degradê', 45.00),
    ('Barba', 25.00),
    ('Corte + Barba', 55.00);

insert into cliente (nome, cpf, telefone) values
    ('João Silva', '123.456.789-00', '(14) 99001-1234'),
    ('Carlos Mendes', '987.654.321-00', '(14) 98765-4321');
