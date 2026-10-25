package id.shoestore;

import id.shoestore.Models.Account;
import id.shoestore.Models.Shoe;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class Repository {
  private Account account(ResultSet rs) throws SQLException {
    return new Account(
        rs.getLong("id"), rs.getString("username"), rs.getString("email"), rs.getString("role"));
  }

  private Shoe shoe(ResultSet rs) throws SQLException {
    return new Shoe(
        rs.getLong("id"),
        rs.getString("name"),
        rs.getString("brand"),
        rs.getString("color"),
        rs.getBigDecimal("price"),
        rs.getInt("stock"),
        rs.getInt("size"),
        rs.getString("image"));
  }

  public Account findAccount(long id) throws SQLException {
    try (Connection c = Database.open();
        PreparedStatement p =
            c.prepareStatement("SELECT id,username,email,[role] FROM dbo.accounts WHERE id=?")) {
      p.setLong(1, id);
      try (ResultSet rs = p.executeQuery()) {
        return rs.next() ? account(rs) : null;
      }
    }
  }

  public Account authenticate(String username, String password) throws SQLException {
    try (Connection c = Database.open();
        PreparedStatement p = c.prepareStatement("SELECT * FROM dbo.accounts WHERE username=?")) {
      p.setString(1, username);
      try (ResultSet rs = p.executeQuery()) {
        return rs.next() && Passwords.verify(password, rs.getString("password_hash"))
            ? account(rs)
            : null;
      }
    }
  }

  public long createAccount(String username, String email, String password, String role)
      throws SQLException {
    String hash = Passwords.hash(password);
    try (Connection c = Database.open();
        PreparedStatement p =
            c.prepareStatement(
                "INSERT INTO dbo.accounts(username,email,password_hash,[role]) VALUES(?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
      p.setString(1, username);
      p.setString(2, email);
      p.setString(3, hash);
      p.setString(4, role);
      p.executeUpdate();
      try (ResultSet rs = p.getGeneratedKeys()) {
        if (rs.next()) return rs.getLong(1);
      }
    }
    throw new SQLException("Account key unavailable");
  }

  public List<Account> accounts() throws SQLException {
    List<Account> rows = new ArrayList<>();
    try (Connection c = Database.open();
        PreparedStatement p =
            c.prepareStatement(
                "SELECT id,username,email,[role] FROM dbo.accounts ORDER BY id DESC");
        ResultSet rs = p.executeQuery()) {
      while (rs.next()) rows.add(account(rs));
    }
    return rows;
  }

  public List<Shoe> shoes() throws SQLException {
    List<Shoe> rows = new ArrayList<>();
    try (Connection c = Database.open();
        PreparedStatement p = c.prepareStatement("SELECT * FROM dbo.shoes ORDER BY id");
        ResultSet rs = p.executeQuery()) {
      while (rs.next()) rows.add(shoe(rs));
    }
    return rows;
  }

  public Shoe findShoe(long id) throws SQLException {
    try (Connection c = Database.open();
        PreparedStatement p = c.prepareStatement("SELECT * FROM dbo.shoes WHERE id=?")) {
      p.setLong(1, id);
      try (ResultSet rs = p.executeQuery()) {
        return rs.next() ? shoe(rs) : null;
      }
    }
  }

  public void saveShoe(Shoe shoe) throws SQLException {
    String columns = "name=?,brand=?,color=?,price=?,stock=?,[size]=?,image=?";
    String sql =
        shoe.id() == 0
            ? "INSERT INTO dbo.shoes(name,brand,color,price,stock,[size],image) VALUES(?,?,?,?,?,?,?)"
            : "UPDATE dbo.shoes SET " + columns + " WHERE id=?";
    try (Connection c = Database.open();
        PreparedStatement p = c.prepareStatement(sql)) {
      p.setString(1, shoe.name());
      p.setString(2, shoe.brand());
      p.setString(3, shoe.color());
      p.setBigDecimal(4, shoe.price());
      p.setInt(5, shoe.stock());
      p.setInt(6, shoe.size());
      p.setString(7, shoe.image());
      if (shoe.id() != 0) p.setLong(8, shoe.id());
      changed(p.executeUpdate());
    }
  }

  public void saveAccount(long id, String username, String email, String password, String role)
      throws SQLException {
    if (id == 0) {
      createAccount(username, email, password, role);
      return;
    }
    try (Connection c = Database.open()) {
      c.setAutoCommit(false);
      try {
        guardLastAdmin(c, id, "admin".equals(role));
        String sql =
            password.isEmpty()
                ? "UPDATE dbo.accounts SET username=?,email=?,[role]=? WHERE id=?"
                : "UPDATE dbo.accounts SET username=?,email=?,[role]=?,password_hash=? WHERE id=?";
        try (PreparedStatement p = c.prepareStatement(sql)) {
          p.setString(1, username);
          p.setString(2, email);
          p.setString(3, role);
          if (password.isEmpty()) p.setLong(4, id);
          else {
            p.setString(4, Passwords.hash(password));
            p.setLong(5, id);
          }
          changed(p.executeUpdate());
        }
        c.commit();
      } catch (SQLException | RuntimeException ex) {
        c.rollback();
        throw ex;
      }
    }
  }

  public void delete(String kind, long id) throws SQLException {
    String table = Validation.kind(kind);
    try (Connection c = Database.open()) {
      c.setAutoCommit(false);
      try {
        if (table.equals("accounts")) guardLastAdmin(c, id, false);
        try (PreparedStatement p = c.prepareStatement("DELETE FROM dbo." + table + " WHERE id=?")) {
          p.setLong(1, id);
          changed(p.executeUpdate());
        }
        c.commit();
      } catch (SQLException | RuntimeException ex) {
        c.rollback();
        throw ex;
      }
    }
  }

  private void guardLastAdmin(Connection c, long id, boolean remainsAdmin) throws SQLException {
    if (remainsAdmin) return;
    List<Long> admins = new ArrayList<>();
    try (PreparedStatement p =
            c.prepareStatement(
                "SELECT id FROM dbo.accounts WITH (UPDLOCK,HOLDLOCK) WHERE [role]='admin' ORDER BY id");
        ResultSet rs = p.executeQuery()) {
      while (rs.next()) admins.add(rs.getLong(1));
    }
    if (admins.size() == 1 && admins.contains(id))
      throw new IllegalArgumentException(
          "Admin terakhir tidak dapat dihapus atau diubah menjadi user.");
  }

  public boolean hasAdmin() throws SQLException {
    try (Connection c = Database.open();
        PreparedStatement p =
            c.prepareStatement("SELECT TOP 1 id FROM dbo.accounts WHERE [role]='admin'");
        ResultSet rs = p.executeQuery()) {
      return rs.next();
    }
  }

  public boolean healthy() throws SQLException {
    try (Connection c = Database.open()) {
      return c.isValid(2);
    }
  }

  private void changed(int count) {
    if (count == 0) throw new IllegalArgumentException("Data tidak ditemukan atau sudah dihapus.");
  }
}
