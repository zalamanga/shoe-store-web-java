<%@ include file="head.jspf" %>
<article class="prose">
  <h1>Tentang ShoeStore</h1>
  <p>ShoeStore adalah aplikasi latihan untuk menampilkan katalog sepatu, mencoba keranjang, dan mengelola produk serta akun.</p>
  <h2>Batas demo</h2>
  <p>Nama, harga, dan stok awal merupakan data contoh. Setiap baris produk mewakili satu ukuran. Keranjang memakai data katalog saat halaman dibuka. Simulasi checkout hanya mengosongkan keranjang, tanpa membuat pesanan, memproses pembayaran, mengirim barang, atau mengurangi stok database.</p>
  <h2>Penggunaan data</h2>
  <p>Aplikasi menyimpan username, email, peran, dan hash kata sandi dalam database lokal. Admin dapat mengubah atau menghapus akun melalui Kelola data. Isi keranjang disimpan pada sesi pengguna; keluar atau berakhirnya sesi dapat mengosongkannya. Batas tidak aktif sesi adalah 30 menit.</p>
  <p>Gunakan data contoh dan hindari informasi pribadi atau sensitif. Tidak ada pembayaran atau pengiriman email pemulihan kata sandi dalam demo ini.</p>
  <h2>Asal proyek</h2>
  <p>Versi ini melanjutkan ShoeStoreWeb Java Servlet/JSP. Footer sumber menyebut Kelompok 1. Foto sepatu berasal dari folder aset sumber tersebut. Merek dalam foto tetap milik pemiliknya; demo ini tidak menyatakan afiliasi dengan merek tersebut. Rincian atribusi terdapat pada README dan NOTICE repositori.</p>
</article>
<%@ include file="foot.jspf" %>
