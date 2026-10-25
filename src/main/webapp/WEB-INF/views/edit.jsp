<%@ include file="head.jspf" %>
<div class="page-heading"><a href="${pageContext.request.contextPath}/admin?kind=${kind}">Kembali ke daftar</a><h1>${id eq 0 ? 'Tambah' : 'Ubah'} <c:out value="${kindLabel}"/></h1></div>
<form action="${pageContext.request.contextPath}/admin/save" method="post" class="panel editor">
  <input type="hidden" name="csrf" value="${csrf}"><input type="hidden" name="kind" value="${kind}"><input type="hidden" name="id" value="${id}">
  <c:choose>
    <c:when test="${kind eq 'accounts'}">
      <label for="username">Username</label>
      <input id="username" name="username" minlength="3" maxlength="40" pattern="[A-Za-z0-9_.\-]{3,40}" required value="<c:out value='${submitted ? param.username : account.username}'/>">
      <label for="email">Email</label>
      <input id="email" name="email" type="email" maxlength="160" required value="<c:out value='${submitted ? param.email : account.email}'/>">
      <label for="password">Kata sandi ${id eq 0 ? '' : '(kosongkan agar tetap)'}</label>
      <input id="password" name="password" type="password" minlength="12" maxlength="128" autocomplete="new-password" ${id eq 0 ? 'required' : ''} aria-describedby="password-hint">
      <p id="password-hint" class="hint">12 sampai 128 karakter. Kata sandi tersimpan tidak ditampilkan.</p>
      <label for="role">Peran</label>
      <c:set var="selectedRole" value="${submitted ? param.role : account.role}"/>
      <select id="role" name="role"><option value="user" ${selectedRole eq 'user' ? 'selected' : ''}>User</option><option value="admin" ${selectedRole eq 'admin' ? 'selected' : ''}>Admin</option></select>
    </c:when>
    <c:otherwise>
      <label for="name">Nama sepatu</label>
      <input id="name" name="name" maxlength="160" required value="<c:out value='${submitted ? param.name : shoe.name}'/>">
      <label for="brand">Merek</label>
      <input id="brand" name="brand" maxlength="80" required value="<c:out value='${submitted ? param.brand : shoe.brand}'/>">
      <label for="color">Warna</label>
      <input id="color" name="color" maxlength="80" required value="<c:out value='${submitted ? param.color : shoe.color}'/>">
      <label for="price">Harga (rupiah)</label>
      <input id="price" name="price" type="number" min="0.01" max="9999999999.99" step="0.01" required value="<c:out value='${submitted ? param.price : shoe.price}'/>">
      <div class="field-pair">
        <div><label for="stock">Stok</label><input id="stock" name="stock" type="number" min="0" max="100000" required value="<c:out value='${submitted ? param.stock : shoe.stock}'/>"></div>
        <div><label for="size">Ukuran</label><input id="size" name="size" type="number" min="20" max="55" required value="<c:out value='${submitted ? param.size : shoe.size}'/>"></div>
      </div>
      <label for="image">Foto</label>
      <c:set var="selectedImage" value="${submitted ? param.image : shoe.image}"/>
      <select id="image" name="image">
        <option value="product1-1.jpg" ${selectedImage eq 'product1-1.jpg' ? 'selected' : ''}>Foto produk 1</option>
        <option value="product2-1.jpg" ${selectedImage eq 'product2-1.jpg' ? 'selected' : ''}>Foto produk 2</option>
        <option value="product3-1.jpg" ${selectedImage eq 'product3-1.jpg' ? 'selected' : ''}>Foto produk 3</option>
        <option value="product4-1.jpg" ${selectedImage eq 'product4-1.jpg' ? 'selected' : ''}>Foto produk 4</option>
      </select>
    </c:otherwise>
  </c:choose>
  <button type="submit">Simpan data</button><p class="loading" role="status" hidden>Sedang menyimpan data…</p>
</form>
<%@ include file="foot.jspf" %>
