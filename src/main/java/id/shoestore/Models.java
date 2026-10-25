package id.shoestore;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class Models {
  private Models() {}

  public static String money(BigDecimal value) {
    return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).format(value);
  }

  public record Account(long id, String username, String email, String role) {
    public long getId() {
      return id;
    }

    public String getUsername() {
      return username;
    }

    public String getEmail() {
      return email;
    }

    public String getRole() {
      return role;
    }

    public boolean isAdmin() {
      return "admin".equals(role);
    }
  }

  public record Shoe(
      long id,
      String name,
      String brand,
      String color,
      BigDecimal price,
      int stock,
      int size,
      String image) {
    public long getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public String getBrand() {
      return brand;
    }

    public String getColor() {
      return color;
    }

    public BigDecimal getPrice() {
      return price;
    }

    public String getFormattedPrice() {
      return money(price);
    }

    public int getStock() {
      return stock;
    }

    public int getSize() {
      return size;
    }

    public String getImage() {
      return image;
    }
  }

  public record CartLine(Shoe shoe, int quantity) {
    public Shoe getShoe() {
      return shoe;
    }

    public int getQuantity() {
      return quantity;
    }

    public BigDecimal getSubtotal() {
      return shoe.price().multiply(BigDecimal.valueOf(quantity));
    }

    public String getFormattedSubtotal() {
      return money(getSubtotal());
    }

    public boolean isAvailable() {
      return shoe.stock() >= quantity;
    }
  }
}
