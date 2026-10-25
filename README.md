# ShoeStore Web Java

Aplikasi katalog sepatu dengan Java Servlet/JSP, JDBC, dan SQL Server. Pengunjung melihat produk beserta ukuran, harga, dan stok. Member dapat mencoba keranjang sesi. Admin mengelola data sepatu dan akun melalui database.

Proyek ini melanjutkan **ShoeStoreWeb** versi NetBeans. Ini berbeda dari repo `shoe-store-crud-` yang memakai Java Swing dan `shoestore3` yang berupa website statis.

## Mulai dengan Docker

Siapkan Docker Desktop dengan Linux containers dan Docker Compose. SQL Server memerlukan image yang cukup besar dan alokasi memori container 2 GiB. Jalankan demo ini bergantian dengan aplikasi database lain pada laptop dengan memori terbatas.

```powershell
git clone https://github.com/zalamanga/shoe-store-web-java.git
cd shoe-store-web-java
Copy-Item .env.example .env
```

Buka `.env`. Isi tiga kata sandi berbeda pada `MSSQL_SA_PASSWORD`, `DB_PASSWORD`, dan `ADMIN_PASSWORD`. Gunakan setidaknya 16 karakter dengan huruf besar, huruf kecil, angka, serta simbol seperti `_` atau `!`. Untuk `DB_PASSWORD`, karakter yang diterima adalah huruf, angka, dan `_!@#%^+=.-`; jangan memakai spasi atau tanda kutip. Password SQL Server juga harus memenuhi kebijakan kompleksitas SQL Server.

`ADMIN_USERNAME` adalah username admin pertama. Jangan commit `.env` atau memakai data pribadi untuk demo.

```powershell
docker compose up --build -d
docker compose ps -a
```

Setelah `db` dan `app` sehat, buka **http://127.0.0.1:18097**. Layanan `db-init` harus selesai dengan kode 0; container tersebut memang tidak terus berjalan. Masuk menggunakan `ADMIN_USERNAME` dan `ADMIN_PASSWORD` yang kamu isi. Untuk mencoba member, buka Masuk lalu Daftar sebagai member.

Compose menjalankan SQL Server 2022 Developer dan menerima EULA image Microsoft melalui `ACCEPT_EULA`. Gunakan edisi Developer untuk pengembangan/pengujian sesuai ketentuan Microsoft, bukan toko produksi. [Panduan container SQL Server](https://learn.microsoft.com/en-us/sql/linux/quickstart-install-connect-docker?view=sql-server-ver17).

Port default aplikasi adalah 18097 dan SQL Server 14344; keduanya hanya dipublikasikan pada `127.0.0.1`. Ubah `APP_PORT` atau `SQL_PORT` jika sudah dipakai. Akun aplikasi `shoestore_app` hanya memperoleh akses baca/tulis pada database `shoestore`; aplikasi web tidak memakai akun `sa`.

```powershell
docker compose logs --tail 80 app
docker compose logs --tail 80 db-init
docker compose stop
docker compose up -d
```

Data disimpan dalam volume `database`. `docker compose down` mempertahankan volume. Schema dan data contoh hanya dibuat jika tabel belum ada. Mengubah `.env` tidak otomatis mengganti password login database/admin yang sudah terbentuk; perubahan password SQL Server yang sudah berjalan harus dilakukan di SQL Server dan disesuaikan dengan konfigurasi aplikasi.

## Apa yang dapat dicoba

| Alur | Perilaku |
| --- | --- |
| Katalog | Membaca nama, merek, warna, ukuran, harga rupiah, stok, dan foto dari database |
| Akun member | Daftar, masuk dengan username, dan keluar |
| Keranjang | Tambah jumlah yang tersedia, bertahan saat halaman dimuat ulang, hapus item, lihat total desimal |
| Simulasi checkout | Memeriksa kembali stok lalu mengosongkan keranjang |
| Admin | Tambah, baca, ubah, dan hapus sepatu serta akun; konfirmasi sebelum menghapus |

Satu baris sepatu mewakili satu ukuran. Untuk ukuran lain, admin menambahkan baris produk baru. Keranjang dibatasi 20 jenis produk dan 50 pasang per produk, serta tidak dapat melebihi stok katalog.

Checkout tetap merupakan **simulasi**, seperti cakupan keranjang contoh sumber. Tidak ada pesanan tersimpan, pembayaran, pengiriman, atau pengurangan stok database. Harga, ukuran, dan stok dibaca ulang di server; perubahan stok atau penghapusan produk oleh admin dapat membatalkan kelanjutan simulasi. Isi keranjang berakhir saat sesi berakhir atau pengguna keluar.

Admin terakhir tidak dapat dihapus atau dijadikan user. Peran akun dibaca ulang pada setiap permintaan aplikasi. Admin dapat mengubah username, email, peran, dan kata sandi akun; kata sandi tersimpan tidak ditampilkan.

## Build tanpa Docker

Siapkan JDK 17 dan Maven 3.9:

```powershell
mvn -B -ntp clean verify
```

Tes JUnit menguji hashing, validasi data, jumlah/stock keranjang, dan total harga desimal tanpa mengakses database. Hasil build adalah `target/shoe-store-web-java.war`. Dockerfile juga menjalankan langkah verifikasi ini sebelum membuat image runtime.

Untuk deploy manual, gunakan Tomcat 9 dan SQL Server dengan schema `database/schema.sql`. File schema mempunyai variabel SQLCMD `APP_PASSWORD` untuk pembuatan login aplikasi; `database/init.sh` menjalankannya dalam Compose. Schema ini bukan dump atau migrasi database lama.

| Environment pada proses Tomcat | Isi |
| --- | --- |
| `DB_HOST`, `DB_PORT` | Host/port SQL Server; default `127.0.0.1:1433`. Jika memakai SQL Server Compose dari host, portnya 14344 |
| `DB_NAME`, `DB_USER` | Default `shoestore`, `shoestore_app` |
| `DB_PASSWORD` | Password login database aplikasi, wajib |
| `DB_TRUST_CERTIFICATE` | `true` untuk sertifikat lokal demo; default `false` memvalidasi sertifikat server |
| `ADMIN_USERNAME`, `ADMIN_PASSWORD` | Akun admin awal jika belum ada admin |

Tomcat tidak membaca `.env` otomatis. Deploy sebagai `ROOT.war` untuk URL tanpa context path. Tomcat 10/11 memakai Jakarta dan bukan target namespace `javax.servlet` ini. Driver Maven memakai `mssql-jdbc:13.6.0.jre11`, yang mendukung Java 17. [Dokumentasi driver Microsoft](https://learn.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server?view=sql-server-ver17).

## Struktur dan perubahan dari sumber

```text
database/                 bootstrap SQL Server dan data contoh sintetis
src/main/java/id/shoestore/ servlet, repository, sesi, cart, validasi, hashing
src/main/webapp/WEB-INF/   JSP di balik controller
src/main/webapp/assets/    CSS, JavaScript, lima foto sumber
src/test/java/            tes tanpa database
```

Bypass admin hardcoded, tampilan password mentah, SQL di JSP, penghapusan lewat GET, dan referensi handler yang tidak ada sudah diganti. Alur baru memakai prepared statement, resource JDBC tertutup, session cookie `SHOESTORE_WEB_SESSION`, token CSRF pada POST, pemeriksaan peran, dan PBKDF2-HMAC-SHA256 dengan salt acak serta 600.000 iterasi. Tidak ada migrasi otomatis password atau data lama.

Foto produk dan arah putih/hitam dengan aksen oranye dipertahankan. Slider, review contoh yang tidak terverifikasi, dan tautan kosong pada sumber diganti dengan navigasi ke katalog yang dikelola. Tampilan memuat state kosong, pengiriman, dan kesalahan; menu serta formulir dapat dijangkau dengan keyboard.

Ini demo lokal. Sebelum penggunaan publik diperlukan HTTPS, cookie Secure, validasi sertifikat database yang benar, pembatasan percobaan login, pemantauan, backup, dan kebijakan penggunaan data yang sesuai penerapan.

## Atribusi

Footer ShoeStoreWeb sumber menyebut **Kelompok 1**. Tidak ditemukan Git remote atau LICENSE pada folder sumber yang diperiksa. Foto `hero.png` berasal dari `img/slide/1.png`; empat foto `product1-1.jpg` sampai `product4-1.jpg` berasal dari `img/product1/1.jpg` sampai `img/product4/1.jpg`, semuanya tanpa perubahan isi. Merek dalam foto milik pemilik masing-masing; proyek demo ini tidak mengklaim afiliasi.

Nama, harga, dan stok seed adalah data contoh baru. Perapihan ini tidak menambahkan klaim lisensi atau kepemilikan seluruh aset. Lihat [NOTICE](NOTICE).
