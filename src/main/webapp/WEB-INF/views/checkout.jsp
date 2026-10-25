<%@ include file="head.jspf" %>
<section class="confirmation">
  <h1>Konfirmasi simulasi</h1>
  <p>Simulasi ini hanya mengosongkan keranjang. Tidak ada pesanan, pembayaran, pengiriman, atau pengurangan stok database.</p>
  <c:choose>
    <c:when test="${empty lines}"><div class="empty"><h2>Keranjang masih kosong</h2><a href="${pageContext.request.contextPath}/catalog">Pilih sepatu terlebih dahulu</a></div></c:when>
    <c:otherwise>
      <ul><c:forEach items="${lines}" var="line"><li><c:out value="${line.shoe.name}"/> · ${line.quantity} pasang · <c:out value="${line.formattedSubtotal}"/></li></c:forEach></ul>
      <p class="price">Total <c:out value="${total}"/></p>
      <c:choose>
        <c:when test="${canCheckout}">
          <form action="${pageContext.request.contextPath}/checkout" method="post">
            <input type="hidden" name="csrf" value="${csrf}"><button type="submit">Jalankan simulasi</button>
            <p class="loading" role="status" hidden>Sedang menyelesaikan simulasi…</p>
          </form>
        </c:when>
        <c:otherwise><p class="error" role="alert">Stok berubah atau produk sudah dihapus. Periksa kembali keranjang.</p></c:otherwise>
      </c:choose>
    </c:otherwise>
  </c:choose>
  <a href="${pageContext.request.contextPath}/cart">Kembali ke keranjang</a>
</section>
<%@ include file="foot.jspf" %>
