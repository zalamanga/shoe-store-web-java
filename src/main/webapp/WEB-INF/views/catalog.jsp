<%@ include file="head.jspf" %>
<div class="page-heading">
  <h1>Katalog sepatu</h1>
  <p>Pilih produk dengan ukuran yang sesuai. Harga, nama, dan stok awal merupakan data contoh.</p>
</div>
<c:choose>
  <c:when test="${empty shoes}">
    <div class="empty"><h2>Katalog belum terisi</h2><p>Pengelola belum menambahkan produk. Silakan kembali nanti.</p><a href="${pageContext.request.contextPath}/">Kembali ke beranda</a></div>
  </c:when>
  <c:otherwise>
    <div class="product-grid">
      <c:forEach items="${shoes}" var="shoe">
        <article class="product" data-product-id="${shoe.id}">
          <img src="${pageContext.request.contextPath}/assets/<c:out value='${shoe.image}'/>" alt="Foto <c:out value='${shoe.name}'/>" width="320" height="240">
          <div class="product-copy">
            <h2><c:out value="${shoe.name}"/></h2>
            <p><c:out value="${shoe.brand}"/> · <c:out value="${shoe.color}"/></p>
            <p class="price"><c:out value="${shoe.formattedPrice}"/></p>
            <p>Ukuran ${shoe.size} · Stok ${shoe.stock}</p>
            <c:choose>
              <c:when test="${shoe.stock eq 0}"><p class="stock-empty">Stok habis untuk ukuran ini.</p></c:when>
              <c:when test="${empty user}"><a href="${pageContext.request.contextPath}/login">Masuk untuk menambah ke keranjang</a></c:when>
              <c:otherwise>
                <form action="${pageContext.request.contextPath}/cart/add" method="post">
                  <input type="hidden" name="csrf" value="${csrf}">
                  <input type="hidden" name="id" value="${shoe.id}">
                  <label for="quantity-${shoe.id}">Jumlah <span class="sr-only"><c:out value="${shoe.name}"/></span></label>
                  <input id="quantity-${shoe.id}" name="quantity" type="number" min="1" max="${shoe.stock lt 50 ? shoe.stock : 50}" value="1" required>
                  <button type="submit">Tambah ke keranjang<span class="sr-only"> <c:out value="${shoe.name}"/></span></button>
                  <p class="loading" role="status" hidden>Sedang menambah produk…</p>
                </form>
              </c:otherwise>
            </c:choose>
          </div>
        </article>
      </c:forEach>
    </div>
  </c:otherwise>
</c:choose>
<%@ include file="foot.jspf" %>
