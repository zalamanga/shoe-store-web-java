package id.shoestore;

import id.shoestore.Models.Account;
import id.shoestore.Models.CartLine;
import id.shoestore.Models.Shoe;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/")
public final class AppServlet extends HttpServlet {
  private final Repository repository = new Repository();

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    String path = req.getServletPath();
    if (path.equals("/favicon.ico")) {
      res.setStatus(204);
      return;
    }
    if (path.startsWith("/assets/")) {
      getServletContext().getNamedDispatcher("default").forward(req, res);
      return;
    }
    if (path.equals("/health")) {
      try {
        if (!repository.healthy()) res.setStatus(503);
      } catch (SQLException ex) {
        res.setStatus(503);
      }
      res.setContentType("text/plain;charset=UTF-8");
      res.getWriter().print(res.getStatus() == 200 ? "ok" : "unavailable");
      return;
    }
    try {
      Account user = current(req);
      req.setAttribute("user", user);
      if (path.startsWith("/admin") || path.startsWith("/cart") || path.equals("/checkout")) {
        if (user == null) {
          redirect(req, res, "/login");
          return;
        }
        if (path.startsWith("/admin") && !user.isAdmin()) {
          fail(req, res, 403, "Halaman ini hanya untuk pengelola.");
          return;
        }
      }
      super.service(req, res);
    } catch (SQLException ex) {
      getServletContext().log("Database request unavailable (query and parameters withheld).");
      fail(
          req,
          res,
          503,
          "Database belum dapat dihubungi. Coba lagi setelah layanan database siap.");
    } catch (IllegalArgumentException ex) {
      fail(req, res, 400, ex.getMessage());
    }
  }

  private Account current(HttpServletRequest req) throws SQLException {
    Object id = req.getSession().getAttribute("accountId");
    if (!(id instanceof Long)) return null;
    Account user = repository.findAccount((Long) id);
    if (user == null) req.getSession().removeAttribute("accountId");
    return user;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    try {
      Object flash = req.getSession().getAttribute("flash");
      if (flash != null) {
        req.setAttribute("message", flash);
        req.getSession().removeAttribute("flash");
      }
      switch (req.getServletPath()) {
        case "/" -> view(req, res, "home", "ShoeStore");
        case "/catalog" -> {
          req.setAttribute("shoes", repository.shoes());
          view(req, res, "catalog", "Katalog sepatu");
        }
        case "/login" -> view(req, res, "login", "Masuk");
        case "/register" -> view(req, res, "register", "Daftar akun");
        case "/about" -> view(req, res, "about", "Tentang dan penggunaan data");
        case "/cart", "/checkout" -> {
          List<CartLine> lines = lines(req.getSession());
          req.setAttribute("lines", lines);
          req.setAttribute("total", Models.money(Cart.total(lines)));
          req.setAttribute(
              "canCheckout", !lines.isEmpty() && lines.stream().allMatch(CartLine::isAvailable));
          view(
              req,
              res,
              req.getServletPath().equals("/cart") ? "cart" : "checkout",
              "Keranjang demo");
        }
        case "/admin" -> admin(req, res);
        case "/admin/edit", "/admin/remove" -> edit(req, res);
        default -> fail(req, res, 404, "Halaman yang kamu cari tidak ditemukan.");
      }
    } catch (SQLException ex) {
      getServletContext().log("Database read unavailable (query and parameters withheld).");
      fail(
          req,
          res,
          503,
          "Database belum dapat dihubungi. Coba lagi setelah layanan database siap.");
    }
  }

  private void admin(HttpServletRequest req, HttpServletResponse res)
      throws SQLException, ServletException, IOException {
    String kind =
        req.getParameter("kind") == null ? "shoes" : Validation.kind(req.getParameter("kind"));
    req.setAttribute("kind", kind);
    if (kind.equals("shoes")) req.setAttribute("shoes", repository.shoes());
    else req.setAttribute("accounts", repository.accounts());
    view(req, res, "admin", "Kelola " + label(kind));
  }

  private void edit(HttpServletRequest req, HttpServletResponse res)
      throws SQLException, ServletException, IOException {
    String kind = Validation.kind(req.getParameter("kind"));
    long id = optionalId(req);
    req.setAttribute("kind", kind);
    req.setAttribute("id", id);
    req.setAttribute("kindLabel", label(kind));
    if (id > 0) {
      Object item = kind.equals("shoes") ? repository.findShoe(id) : repository.findAccount(id);
      if (item == null) {
        fail(req, res, 404, "Data ini tidak ditemukan atau sudah dihapus.");
        return;
      }
      req.setAttribute(kind.equals("shoes") ? "shoe" : "account", item);
    }
    boolean removing = req.getServletPath().equals("/admin/remove");
    if (removing && id == 0) {
      fail(req, res, 400, "Pilih data yang ingin dihapus.");
      return;
    }
    view(
        req,
        res,
        removing ? "remove" : "edit",
        (removing ? "Hapus " : id == 0 ? "Tambah " : "Ubah ") + label(kind));
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    String path = req.getServletPath();
    try {
      switch (path) {
        case "/login" -> {
          String username = Validation.username(req.getParameter("username"));
          Account user = repository.authenticate(username, req.getParameter("password"));
          if (user == null)
            throw new IllegalArgumentException("Username atau kata sandi tidak cocok.");
          signIn(req, user.id());
          redirect(req, res, user.isAdmin() ? "/admin" : "/catalog");
        }
        case "/register" -> {
          long id =
              repository.createAccount(
                  Validation.username(req.getParameter("username")),
                  Validation.email(req.getParameter("email")),
                  Validation.password(req.getParameter("password")),
                  "user");
          signIn(req, id);
          success(req, res, "/catalog", "Akun dibuat. Kamu sudah bisa mencoba keranjang demo.");
        }
        case "/logout" -> {
          req.getSession().invalidate();
          redirect(req, res, "/");
        }
        case "/cart/add" -> {
          long id = Validation.id(req.getParameter("id"));
          int quantity = Validation.integer(req.getParameter("quantity"), "Jumlah", 1, 50);
          synchronized (req.getSession()) {
            Shoe shoe = repository.findShoe(id);
            req.getSession().setAttribute("cart", Cart.add(cart(req.getSession()), shoe, quantity));
          }
          success(
              req, res, "/cart", "Produk ditambahkan ke keranjang. Stok database belum dikurangi.");
        }
        case "/cart/remove" -> {
          long id = Validation.id(req.getParameter("id"));
          synchronized (req.getSession()) {
            Map<Long, Integer> cart = cart(req.getSession());
            if (cart.remove(id) == null)
              throw new IllegalArgumentException("Produk tidak ada di keranjang.");
            req.getSession().setAttribute("cart", cart);
          }
          success(req, res, "/cart", "Produk dihapus dari keranjang.");
        }
        case "/checkout" -> {
          synchronized (req.getSession()) {
            Cart.validateCheckout(lines(req.getSession()));
            req.getSession().setAttribute("cart", new LinkedHashMap<Long, Integer>());
          }
          success(
              req,
              res,
              "/cart",
              "Simulasi selesai. Keranjang dikosongkan; tidak ada pesanan, pembayaran, atau pengurangan stok database.");
        }
        case "/admin/save" -> {
          String kind = Validation.kind(req.getParameter("kind"));
          long id = optionalId(req);
          if (kind.equals("shoes"))
            repository.saveShoe(
                new Shoe(
                    id,
                    Validation.text(req.getParameter("name"), "Nama sepatu", 160),
                    Validation.text(req.getParameter("brand"), "Merek", 80),
                    Validation.text(req.getParameter("color"), "Warna", 80),
                    Validation.price(req.getParameter("price")),
                    Validation.integer(req.getParameter("stock"), "Stok", 0, 100000),
                    Validation.integer(req.getParameter("size"), "Ukuran", 20, 55),
                    Validation.image(req.getParameter("image"))));
          else {
            String password = value(req, "password");
            if (id == 0 || !password.isEmpty()) Validation.password(password);
            repository.saveAccount(
                id,
                Validation.username(req.getParameter("username")),
                Validation.email(req.getParameter("email")),
                password,
                Validation.role(req.getParameter("role")));
          }
          success(req, res, "/admin?kind=" + kind, "Data " + label(kind) + " disimpan.");
        }
        case "/admin/delete" -> {
          String kind = Validation.kind(req.getParameter("kind"));
          repository.delete(kind, Validation.id(req.getParameter("id")));
          success(req, res, "/admin?kind=" + kind, "Data " + label(kind) + " dihapus.");
        }
        default -> fail(req, res, 405, "Metode ini tidak tersedia untuk halaman tersebut.");
      }
    } catch (IllegalArgumentException ex) {
      formError(req, res, path, ex.getMessage());
    } catch (SQLException ex) {
      if (ex.getSQLState() != null && ex.getSQLState().startsWith("23"))
        formError(req, res, path, "Username atau email sudah terdaftar. Gunakan nilai lain.");
      else {
        getServletContext().log("Database write unavailable (query and parameters withheld).");
        fail(req, res, 503, "Perubahan belum dapat disimpan. Coba lagi setelah database siap.");
      }
    }
  }

  @SuppressWarnings("unchecked")
  private Map<Long, Integer> cart(HttpSession session) {
    Object value = session.getAttribute("cart");
    return value instanceof Map
        ? new LinkedHashMap<>((Map<Long, Integer>) value)
        : new LinkedHashMap<>();
  }

  private List<CartLine> lines(HttpSession session) throws SQLException {
    List<CartLine> lines = new ArrayList<>();
    Map<Long, Integer> snapshot;
    synchronized (session) {
      snapshot = cart(session);
    }
    for (Map.Entry<Long, Integer> entry : snapshot.entrySet()) {
      Shoe shoe = repository.findShoe(entry.getKey());
      if (shoe == null)
        shoe = new Shoe(entry.getKey(), "Produk sudah dihapus", "", "", BigDecimal.ZERO, 0, 0, "");
      lines.add(new CartLine(shoe, entry.getValue()));
    }
    return lines;
  }

  private void formError(
      HttpServletRequest req, HttpServletResponse res, String path, String message)
      throws ServletException, IOException {
    res.setStatus(400);
    req.setAttribute("error", message);
    if (path.equals("/login") || path.equals("/register"))
      view(req, res, path.substring(1), "Periksa kembali formulir");
    else if (path.equals("/admin/save")) {
      try {
        String kind = Validation.kind(req.getParameter("kind"));
        req.setAttribute("kind", kind);
        req.setAttribute("id", optionalId(req));
        req.setAttribute("kindLabel", label(kind));
        req.setAttribute("submitted", true);
        view(req, res, "edit", "Periksa kembali data");
      } catch (IllegalArgumentException ex) {
        view(req, res, "error", "Perubahan belum disimpan");
      }
    } else view(req, res, "error", "Permintaan belum berhasil");
  }

  private void signIn(HttpServletRequest req, long id) {
    req.getSession().invalidate();
    HttpSession session = req.getSession(true);
    session.setAttribute("accountId", id);
    session.setMaxInactiveInterval(1800);
  }

  private long optionalId(HttpServletRequest req) {
    String id = req.getParameter("id");
    return id == null || id.isBlank() || id.equals("0") ? 0 : Validation.id(id);
  }

  private String value(HttpServletRequest req, String key) {
    String value = req.getParameter(key);
    return value == null ? "" : value;
  }

  private String label(String kind) {
    return kind.equals("shoes") ? "sepatu" : "akun";
  }

  private void redirect(HttpServletRequest req, HttpServletResponse res, String path) {
    res.setStatus(303);
    res.setHeader("Location", req.getContextPath() + path);
  }

  private void success(
      HttpServletRequest req, HttpServletResponse res, String path, String message) {
    req.getSession().setAttribute("flash", message);
    redirect(req, res, path);
  }

  private void view(HttpServletRequest req, HttpServletResponse res, String page, String title)
      throws ServletException, IOException {
    req.setAttribute("title", title);
    req.getRequestDispatcher("/WEB-INF/views/" + page + ".jsp").forward(req, res);
  }

  private void fail(HttpServletRequest req, HttpServletResponse res, int status, String message)
      throws ServletException, IOException {
    res.setStatus(status);
    req.setAttribute("error", message);
    view(req, res, "error", "Permintaan belum berhasil");
  }
}
