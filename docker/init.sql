CREATE TABLE IF NOT EXISTS usuario (
   idUsuario char(36) primary key,
   nome      varchar(255),
   email     varchar(255) not null,
   telefone  varchar(20),
   senha     varchar(255) not null,
   perfil    enum ('ADMIN', 'PACIENTE', 'MEDICO', 'ENFERMEIRO') null,
   situacao  enum ('ATIVO', 'INATIVO', 'CANCELADO') null
);

CREATE TABLE IF NOT EXISTS consulta (
    idConsulta char(36) primary key ,
    dataConsulta datetime not null,
    idPaciente char(36) not null,
    idMedico char(36) not null,
    idUsuario char(36) not null,
    anotacao varchar(2000),
    situacao enum ('AGENDADA', 'REALIZADA', 'CANCELADA') not null,
    dataCriacao datetime,
    dataAtualizacao datetime,
    constraint consulta_usuario_paciente_fk
        foreign key (idPaciente) references usuario (idUsuario),
    constraint consulta_usuario_medico_fk
        foreign key (idMedico) references usuario (idUsuario),
    constraint consulta_usuario_operador_fk
        foreign key (idUsuario) references usuario (idUsuario)
);

CREATE TABLE IF NOT EXISTS notificacao (
   idNotificacao    char(36) primary key,
   idConsulta       char(36) not null,
   dataNotificacao  datetime,
   dataCriacao      datetime,
   dataAtualizacao  datetime,
   mensagem         varchar(8000),
   situacao         enum ('PENDENTE', 'ENVIADA', 'ERRO') null,
   constraint notificacao_consulta_idConsulta_fk
       foreign key (idConsulta) references consulta (idConsulta)
);