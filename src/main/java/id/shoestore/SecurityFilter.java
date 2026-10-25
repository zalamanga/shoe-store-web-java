package id.shoestore;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

@WebFilter("/*")
public final class SecurityFilter implements Filter {
  private static final SecureRandom RANDOM = new SecureRandom();

  public static String token(HttpSession session) {
    String token = (String) session.getAttribute("csrf");
    if (token == null) {
      byte[] bytes = new byte[32];
      RANDOM.nextBytes(bytes);
      token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
      session.setAttribute("csrf", token);
    }
    return token;
  }

  @Override
  public void doFilter(ServletRequest input, ServletResponse output, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) input;
    HttpServletResponse res = (HttpServletResponse) output;
    req.setCharacterEncoding("UTF-8");
    res.setCharacterEncoding("UTF-8");
    res.setHeader("X-Content-Type-Options", "nosniff");
    res.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
    res.setHeader(
        "Content-Security-Policy",
        "default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self'; object-src 'none'; base-uri 'self'; frame-ancestors 'none'; form-action 'self'");
    String path = req.getServletPath();
    if (!path.startsWith("/assets/") && !path.equals("/health")) {
      res.setHeader("Cache-Control", "no-store");
      String token = token(req.getSession());
      req.setAttribute("csrf", token);
      if (req.getMethod().equals("POST")) {
        String submitted = req.getParameter("csrf");
        if (submitted == null
            || !MessageDigest.isEqual(
                token.getBytes(StandardCharsets.UTF_8),
                submitted.getBytes(StandardCharsets.UTF_8))) {
          res.setStatus(403);
          req.setAttribute(
              "error", "Formulir sudah kedaluwarsa. Buka kembali halaman lalu coba lagi.");
          req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, res);
          return;
        }
      }
    }
    chain.doFilter(req, res);
  }
}
