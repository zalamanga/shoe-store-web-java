<%@ include file="head.jspf" %>
<section class="form-layout">
  <div><h1>Buat akun demo.</h1><p>Akun member dapat menambah produk ke keranjang dan mencoba checkout simulasi.</p><p>Gunakan data contoh. <a href="${pageContext.request.contextPath}/about">Baca penggunaan data</a>.</p><p>Sudah punya akun? <a href="${pageContext.request.contextPath}/login">Masuk</a>.</p></div>
  <form action="${pageContext.request.contextPath}/register" method="post" class="panel">
    <input type="hidden" name="csrf" value="${csrf}">
    <label for="username">Username</label>
    <input id="username" name="username" minlength="3" maxlength="40" pattern="[A-Za-z0-9_.\-]{3,40}" autocomplete="username" aria-describedby="username-hint" required value="<c:out value='${param.username}'/>">
    <p id="username-hint" class="hint">3-40 huruf, angka, titik, garis bawah, atau tanda hubung. Huruf disimpan sebagai huruf kecil.</p>
    <label for="email">Email</label>
    <input id="email" name="email" type="email" maxlength="160" autocomplete="email" required value="<c:out value='${param.email}'/>">
    <label for="password">Kata sandi</label>
    <input id="password" name="password" type="password" minlength="12" maxlength="128" autocomplete="new-password" aria-describedby="password-hint" required>
    <p id="password-hint" class="hint">Gunakan 12 sampai 128 karakter.</p>
    <button type="submit">Buat akun</button><p class="loading" role="status" hidden>Sedang membuat akun…</p>
  </form>
</section>
<%@ include file="foot.jspf" %>
