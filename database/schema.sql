IF DB_ID(N'shoestore') IS NULL CREATE DATABASE shoestore;
GO
IF SUSER_ID(N'shoestore_app') IS NULL CREATE LOGIN shoestore_app WITH PASSWORD=N'$(APP_PASSWORD)';
GO
USE shoestore;
GO
IF USER_ID(N'shoestore_app') IS NULL CREATE USER shoestore_app FOR LOGIN shoestore_app;
IF IS_ROLEMEMBER(N'db_datareader',N'shoestore_app')=0 ALTER ROLE db_datareader ADD MEMBER shoestore_app;
IF IS_ROLEMEMBER(N'db_datawriter',N'shoestore_app')=0 ALTER ROLE db_datawriter ADD MEMBER shoestore_app;
GO
IF OBJECT_ID(N'dbo.accounts',N'U') IS NULL
CREATE TABLE dbo.accounts (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  username NVARCHAR(40) NOT NULL UNIQUE,
  email NVARCHAR(160) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  [role] VARCHAR(10) NOT NULL CHECK([role] IN ('user','admin'))
);
GO
IF OBJECT_ID(N'dbo.shoes',N'U') IS NULL
BEGIN
  CREATE TABLE dbo.shoes (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(160) NOT NULL,
    brand NVARCHAR(80) NOT NULL,
    color NVARCHAR(80) NOT NULL,
    price DECIMAL(12,2) NOT NULL CHECK(price>0),
    stock INT NOT NULL CHECK(stock>=0),
    [size] INT NOT NULL CHECK([size] BETWEEN 20 AND 55),
    image VARCHAR(80) NOT NULL
  );
  -- Contoh baru untuk demo, bukan data/dump toko asli.
  INSERT INTO dbo.shoes(name,brand,color,price,stock,[size],image) VALUES
    (N'Sepatu latihan ukuran 40',N'Contoh',N'Hitam',399000,12,40,'product1-1.jpg'),
    (N'Sepatu harian ukuran 41',N'Contoh',N'Putih',349000,8,41,'product2-1.jpg'),
    (N'Sepatu santai ukuran 39',N'Contoh',N'Abu-abu',299000,6,39,'product3-1.jpg'),
    (N'Sepatu klasik ukuran 42',N'Contoh',N'Hitam putih',449000,10,42,'product4-1.jpg');
END;
GO
