<%@ include file="head.jspf" %>
<section class="form-layout">
  <div><h1>Masuk ke ShoeStore.</h1><p>Gunakan username untuk membuka keranjang atau mengelola katalog.</p><p>Belum punya akun? <a href="${pageContext.request.contextPath}/register">Daftar sebagai member</a>.</p></div>
  <form action="${pageContext.request.contextPath}/login" method="post" class="panel">
    <input type="hidden" name="csrf" value="${csrf}">
    <label for="username">Username</label>
    <input id="username" name="username" maxlength="40" autocomplete="username" required value="<c:out value='${param.username}'/>">
    <label for="password">Kata sandi</label>
    <input id="password" name="password" type="password" maxlength="128" autocomplete="current-password" required>
    <button type="submit">Masuk</button><p class="loading" role="status" hidden>Sedang masuk…</p>
  </form>
</section>
<%@ include file="foot.jspf" %>
