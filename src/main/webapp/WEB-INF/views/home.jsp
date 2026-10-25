<%@ include file="head.jspf" %>
<section class="hero">
  <div>
    <h1>Pilih sepatu.<br>Periksa ukurannya.</h1>
    <p>Lihat foto, ukuran, dan stok pada katalog. Buat akun untuk mencoba keranjang tanpa melakukan pembayaran.</p>
    <a class="button" href="${pageContext.request.contextPath}/catalog">Lihat katalog sepatu</a>
  </div>
  <img src="${pageContext.request.contextPath}/assets/hero.png" alt="Sepatu hitam putih dari koleksi foto ShoeStore" width="600" height="500">
</section>
<section class="intro">
  <h2>Katalog yang bisa dikelola.</h2>
  <div>
    <p>Pengelola menambahkan setiap ukuran sebagai baris produk tersendiri. Harga dan stok di keranjang mengikuti isi katalog saat halaman dibuka.</p>
    <p>Ini aplikasi demo. Simulasi checkout mengosongkan keranjang, tanpa menyimpan pesanan, memproses pembayaran, atau mengurangi stok.</p>
  </div>
</section>
<%@ include file="foot.jspf" %>
