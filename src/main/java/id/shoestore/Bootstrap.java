package id.shoestore;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public final class Bootstrap implements ServletContextListener {
  @Override
  public void contextInitialized(ServletContextEvent event) {
    try {
      Repository repository = new Repository();
      if (!repository.hasAdmin())
        repository.createAccount(
            Validation.username(Database.required("ADMIN_USERNAME")),
            "admin@example.test",
            Validation.password(Database.required("ADMIN_PASSWORD")),
            "admin");
    } catch (Exception ex) {
      event
          .getServletContext()
          .log(
              "ShoeStore startup unavailable: check SQL Server/schema and admin configuration (details withheld).");
      throw new IllegalStateException("ShoeStore configuration unavailable");
    }
  }
}
