-- Spring Boot crea estas tablas solo (spring.jpa.hibernate.ddl-auto=update).
-- Este script es una referencia por si prefieres crearlas a mano (pon ddl-auto=validate).
CREATE TABLE usuarios (
    id            NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username      VARCHAR2(50)  NOT NULL UNIQUE,
    nombre        VARCHAR2(100) NOT NULL,
    password_hash VARCHAR2(100) NOT NULL,
    rol           VARCHAR2(20)  NOT NULL CHECK (rol IN ('ADMIN','ENCARGADO','VENDEDOR'))
);

CREATE TABLE productos (
    id           NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo       VARCHAR2(60)  NOT NULL UNIQUE,
    nombre       VARCHAR2(120) NOT NULL,
    categoria    VARCHAR2(80)  NOT NULL,
    precio       BINARY_DOUBLE NOT NULL,
    stock        NUMBER(10)    NOT NULL,
    stock_minimo NUMBER(10)    NOT NULL,
    caducidad    DATE,
    descuento    NUMBER(10)    NOT NULL,
    creado       DATE          NOT NULL,
    foto_version NUMBER(19)    NOT NULL
);

CREATE TABLE producto_fotos (
    producto_id NUMBER PRIMARY KEY,
    datos       BLOB NOT NULL
);

CREATE TABLE mermas (
    id             NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    producto_id    NUMBER NOT NULL REFERENCES productos(id),
    cantidad       NUMBER(10)    NOT NULL,
    motivo         VARCHAR2(30)  NOT NULL,
    fecha          DATE          NOT NULL,
    observaciones  VARCHAR2(300),
    registrado_por VARCHAR2(100)
);
