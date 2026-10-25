<%@ include file="head.jspf" %>
<div class="page-heading"><h1>Kelola data</h1><p>Perbarui katalog, ukuran, stok, dan akun pengguna. Satu baris sepatu mewakili satu ukuran.</p></div>
<nav class="tabs" aria-label="Jenis data">
  <a href="${pageContext.request.contextPath}/admin?kind=shoes" aria-current="${kind eq 'shoes' ? 'page' : 'false'}">Sepatu</a>
  <a href="${pageContext.request.contextPath}/admin?kind=accounts" aria-current="${kind eq 'accounts' ? 'page' : 'false'}">Akun</a>
</nav>
<div class="list-heading"><h2>${kind eq 'shoes' ? 'Sepatu' : 'Akun'}</h2><a class="button" href="${pageContext.request.contextPath}/admin/edit?kind=${kind}">Tambah ${kind eq 'shoes' ? 'sepatu' : 'akun'}</a></div>
<c:choose>
  <c:when test="${kind eq 'accounts'}">
    <c:forEach items="${accounts}" var="account">
      <article class="admin-row" data-record-id="${account.id}">
        <div><h3><c:out value="${account.username}"/></h3><p><c:out value="${account.email}"/> · <c:out value="${account.role}"/></p></div>
        <div class="actions"><a href="${pageContext.request.contextPath}/admin/edit?kind=accounts&amp;id=${account.id}">Ubah<span class="sr-only"> <c:out value="${account.username}"/></span></a><a href="${pageContext.request.contextPath}/admin/remove?kind=accounts&amp;id=${account.id}">Hapus<span class="sr-only"> <c:out value="${account.username}"/></span></a></div>
      </article>
    </c:forEach>
  </c:when>
  <c:otherwise>
    <c:if test="${empty shoes}"><div class="empty"><h3>Belum ada data sepatu</h3><p>Gunakan tombol Tambah untuk membuat produk pertama.</p></div></c:if>
    <c:forEach items="${shoes}" var="shoe">
      <article class="admin-row" data-record-id="${shoe.id}">
        <div><h3><c:out value="${shoe.name}"/></h3><p><c:out value="${shoe.brand}"/> · <c:out value="${shoe.color}"/> · Ukuran ${shoe.size}</p><p><c:out value="${shoe.formattedPrice}"/> · Stok ${shoe.stock}</p></div>
        <div class="actions"><a href="${pageContext.request.contextPath}/admin/edit?kind=shoes&amp;id=${shoe.id}">Ubah<span class="sr-only"> <c:out value="${shoe.name}"/></span></a><a href="${pageContext.request.contextPath}/admin/remove?kind=shoes&amp;id=${shoe.id}">Hapus<span class="sr-only"> <c:out value="${shoe.name}"/></span></a></div>
      </article>
    </c:forEach>
  </c:otherwise>
</c:choose>
<%@ include file="foot.jspf" %>
