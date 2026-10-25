<%@ include file="head.jspf" %>
<div class="page-heading"><h1>Keranjang demo</h1><p>Isi keranjang tersimpan dalam sesi akun ini. Harga dan stok dibaca ulang dari katalog; menambah barang belum mengurangi stok.</p></div>
<c:choose>
  <c:when test="${empty lines}"><div class="empty"><h2>Keranjang masih kosong</h2><p>Pilih ukuran produk dari katalog terlebih dahulu.</p><a class="button" href="${pageContext.request.contextPath}/catalog">Pilih sepatu</a></div></c:when>
  <c:otherwise>
    <div class="cart-layout">
      <div>
        <c:forEach items="${lines}" var="line">
          <article class="cart-row" data-product-id="${line.shoe.id}">
            <c:if test="${not empty line.shoe.image}"><img src="${pageContext.request.contextPath}/assets/<c:out value='${line.shoe.image}'/>" alt="Foto <c:out value='${line.shoe.name}'/>" width="120" height="100"></c:if>
            <div>
              <h2><c:out value="${line.shoe.name}"/></h2>
              <p>Ukuran ${line.shoe.size} · Jumlah ${line.quantity}</p>
              <p><c:out value="${line.formattedSubtotal}"/></p>
              <c:if test="${not line.available}"><p class="stock-empty">Stok berubah atau produk sudah dihapus. Hapus item ini lalu pilih kembali dari katalog.</p></c:if>
              <form action="${pageContext.request.contextPath}/cart/remove" method="post">
                <input type="hidden" name="csrf" value="${csrf}"><input type="hidden" name="id" value="${line.shoe.id}">
                <button type="submit" class="quiet">Hapus dari keranjang<span class="sr-only"> <c:out value="${line.shoe.name}"/></span></button>
                <p class="loading" role="status" hidden>Sedang menghapus produk…</p>
              </form>
            </div>
          </article>
        </c:forEach>
      </div>
      <aside class="summary"><h2>Ringkasan</h2><p>Total saat ini</p><p class="price" id="cart-total"><c:out value="${total}"/></p><p>Belum ada pesanan atau pembayaran yang dibuat.</p>
        <c:choose><c:when test="${canCheckout}"><a class="button" href="${pageContext.request.contextPath}/checkout">Lanjut ke simulasi</a></c:when><c:otherwise><p class="stock-empty">Periksa item yang tidak tersedia sebelum melanjutkan.</p></c:otherwise></c:choose>
        <a href="${pageContext.request.contextPath}/catalog">Tambah produk lain</a>
      </aside>
    </div>
  </c:otherwise>
</c:choose>
<%@ include file="foot.jspf" %>
