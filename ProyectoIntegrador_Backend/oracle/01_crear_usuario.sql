-- Ejecutar como ADMIN en Database Actions > SQL (Oracle Cloud). Opcional pero recomendado.
CREATE USER stocksmart IDENTIFIED BY "CambiaEstaClave#2026";
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE VIEW TO stocksmart;
ALTER USER stocksmart QUOTA UNLIMITED ON DATA;
