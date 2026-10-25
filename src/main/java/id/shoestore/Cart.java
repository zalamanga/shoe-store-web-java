package id.shoestore;

import id.shoestore.Models.CartLine;
import id.shoestore.Models.Shoe;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Cart {
  private Cart() {}

  public static Map<Long, Integer> add(Map<Long, Integer> current, Shoe shoe, int quantity) {
    if (shoe == null) throw new IllegalArgumentException("Produk sudah tidak tersedia.");
    if (quantity < 1 || quantity > 50)
      throw new IllegalArgumentException("Jumlah harus 1 sampai 50 pasang.");
    Map<Long, Integer> next = new LinkedHashMap<>(current);
    int total = next.getOrDefault(shoe.id(), 0) + quantity;
    if (total > 50 || total > shoe.stock())
      throw new IllegalArgumentException("Jumlah melebihi stok atau batas 50 pasang per produk.");
    if (!next.containsKey(shoe.id()) && next.size() >= 20)
      throw new IllegalArgumentException("Keranjang maksimal 20 jenis produk.");
    next.put(shoe.id(), total);
    return next;
  }

  public static BigDecimal total(List<CartLine> lines) {
    return lines.stream().map(CartLine::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public static void validateCheckout(List<CartLine> lines) {
    if (lines.isEmpty()) throw new IllegalArgumentException("Keranjang masih kosong.");
    if (lines.stream()
        .anyMatch(line -> line.quantity() < 1 || line.quantity() > 50 || !line.isAvailable()))
      throw new IllegalArgumentException(
          "Stok berubah atau produk dihapus. Periksa kembali keranjang sebelum melanjutkan.");
  }
}
