package id.shoestore;

import id.shoestore.Models.CartLine;
import id.shoestore.Models.Shoe;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartTest {
  private Shoe shoe(long id, String price, int stock) {
    return new Shoe(
        id, "Contoh", "Contoh", "Hitam", new BigDecimal(price), stock, 40, "product1-1.jpg");
  }

  @Test
  void quantitiesAccumulateWithinStockWithoutMutatingOriginalSnapshotOrDatabaseStock() {
    Shoe shoe = shoe(1, "100000", 5);
    Map<Long, Integer> previous = Map.of(1L, 1);
    assertEquals(3, Cart.add(previous, shoe, 2).get(1L).intValue());
    assertEquals(1, previous.get(1L).intValue());
    assertEquals(5, shoe.stock());
  }

  @Test
  void unavailableMissingAndExcessiveQuantitiesCannotEnterCart() {
    assertThrows(IllegalArgumentException.class, () -> Cart.add(Map.of(), shoe(1, "1", 0), 1));
    assertThrows(IllegalArgumentException.class, () -> Cart.add(Map.of(1L, 2), shoe(1, "1", 2), 1));
    assertThrows(IllegalArgumentException.class, () -> Cart.add(Map.of(), shoe(1, "1", 100), 51));
    assertThrows(IllegalArgumentException.class, () -> Cart.add(Map.of(), null, 1));
  }

  @Test
  void moneyIsSummedAsDecimalWithoutFloatingPointRounding() {
    assertEquals(
        new BigDecimal("0.30"),
        Cart.total(
            List.of(new CartLine(shoe(1, "0.10", 5), 1), new CartLine(shoe(2, "0.20", 5), 1))));
  }

  @Test
  void checkoutRechecksEmptyAndChangedStock() {
    assertThrows(IllegalArgumentException.class, () -> Cart.validateCheckout(List.of()));
    assertThrows(
        IllegalArgumentException.class,
        () -> Cart.validateCheckout(List.of(new CartLine(shoe(1, "100", 0), 1))));
    assertDoesNotThrow(() -> Cart.validateCheckout(List.of(new CartLine(shoe(1, "100", 3), 2))));
  }
}
