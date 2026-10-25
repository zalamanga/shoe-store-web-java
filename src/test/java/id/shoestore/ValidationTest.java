package id.shoestore;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidationTest {
  @Test
  void monetaryInputRejectsNegativeZeroExcessivePrecisionAndOverflow() {
    for (String price : new String[] {"0", "-1", "1.234", "10000000000", "NaN", ""})
      assertThrows(IllegalArgumentException.class, () -> Validation.price(price));
    assertEquals(new BigDecimal("199000.50"), Validation.price("199000.50"));
  }

  @Test
  void stockSizeAndQuantityUseBoundedIntegers() {
    assertThrows(IllegalArgumentException.class, () -> Validation.integer("-1", "Stok", 0, 100000));
    assertThrows(
        IllegalArgumentException.class, () -> Validation.integer("40.5", "Ukuran", 20, 55));
    assertThrows(IllegalArgumentException.class, () -> Validation.integer("51", "Jumlah", 1, 50));
    assertEquals(0, Validation.integer("0", "Stok", 0, 100000));
  }

  @Test
  void arbitrarySqlTableAndRoleValuesAreRejected() {
    assertThrows(
        IllegalArgumentException.class, () -> Validation.kind("shoes; DROP TABLE accounts"));
    assertThrows(IllegalArgumentException.class, () -> Validation.role("owner"));
    assertThrows(IllegalArgumentException.class, () -> Validation.kind(null));
  }

  @Test
  void imagesCannotReferenceArbitraryFilesOrRemoteHosts() {
    assertThrows(IllegalArgumentException.class, () -> Validation.image("../WEB-INF/web.xml"));
    assertThrows(
        IllegalArgumentException.class, () -> Validation.image("https://example.test/a.jpg"));
    assertThrows(IllegalArgumentException.class, () -> Validation.image(null));
  }

  @Test
  void accountFieldsAreBoundedAndNormalized() {
    assertEquals("member.one", Validation.username(" Member.One "));
    assertEquals("member@example.test", Validation.email("Member@EXAMPLE.test"));
    assertThrows(IllegalArgumentException.class, () -> Validation.username("x' OR '1'='1"));
    assertThrows(IllegalArgumentException.class, () -> Validation.email("invalid"));
    assertThrows(
        IllegalArgumentException.class, () -> Validation.text("x".repeat(161), "Nama sepatu", 160));
  }

  @Test
  void passwordsAndIdsCannotUseEmptyShortOrInvalidValues() {
    assertThrows(IllegalArgumentException.class, () -> Validation.password("short"));
    assertThrows(IllegalArgumentException.class, () -> Validation.password("x".repeat(129)));
    assertThrows(IllegalArgumentException.class, () -> Validation.id("0"));
    assertThrows(IllegalArgumentException.class, () -> Validation.id("999999999999999999999"));
  }
}
