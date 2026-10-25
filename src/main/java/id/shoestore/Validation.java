package id.shoestore;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Set;

public final class Validation {
  public static final Set<String> IMAGES =
      Set.of("product1-1.jpg", "product2-1.jpg", "product3-1.jpg", "product4-1.jpg");

  private Validation() {}

  public static String text(String value, String label, int max) {
    String clean = value == null ? "" : value.strip();
    if (clean.isEmpty() || clean.length() > max || clean.indexOf('\0') >= 0)
      throw new IllegalArgumentException(label + " wajib diisi, maksimal " + max + " karakter.");
    return clean;
  }

  public static String username(String value) {
    String clean = text(value, "Username", 40).toLowerCase(Locale.ROOT);
    if (!clean.matches("[a-z0-9_.-]{3,40}"))
      throw new IllegalArgumentException(
          "Username harus 3-40 huruf, angka, titik, garis bawah, atau tanda hubung.");
    return clean;
  }

  public static String email(String value) {
    String clean = text(value, "Email", 160).toLowerCase(Locale.ROOT);
    if (!clean.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
      throw new IllegalArgumentException("Format email belum benar.");
    return clean;
  }

  public static String password(String value) {
    if (value == null || value.length() < 12 || value.length() > 128)
      throw new IllegalArgumentException("Kata sandi harus berisi 12 sampai 128 karakter.");
    return value;
  }

  public static long id(String value) {
    try {
      long id = Long.parseLong(value);
      if (id <= 0) throw new NumberFormatException();
      return id;
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException("ID data tidak valid.");
    }
  }

  public static int integer(String value, String label, int minimum, int maximum) {
    try {
      int number = Integer.parseInt(value);
      if (number < minimum || number > maximum) throw new NumberFormatException();
      return number;
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException(
          label + " harus bilangan bulat " + minimum + " sampai " + maximum + ".");
    }
  }

  public static BigDecimal price(String value) {
    try {
      BigDecimal price = new BigDecimal(value == null ? "" : value);
      if (price.scale() > 2
          || price.compareTo(new BigDecimal("0.01")) < 0
          || price.compareTo(new BigDecimal("9999999999.99")) > 0)
        throw new NumberFormatException();
      return price;
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException("Harga harus lebih dari nol, maksimal dua angka desimal.");
    }
  }

  public static String image(String value) {
    if (value == null || !IMAGES.contains(value))
      throw new IllegalArgumentException("Pilih foto dari daftar yang tersedia.");
    return value;
  }

  public static String kind(String value) {
    if (!"shoes".equals(value) && !"accounts".equals(value))
      throw new IllegalArgumentException("Jenis data tidak dikenal.");
    return value;
  }

  public static String role(String value) {
    if (!"user".equals(value) && !"admin".equals(value))
      throw new IllegalArgumentException("Peran akun tidak valid.");
    return value;
  }
}
