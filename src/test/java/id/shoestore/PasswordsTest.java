package id.shoestore;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordsTest {
  @Test
  void saltChangesStoredHashAndOnlyCorrectPasswordAuthenticates() {
    String password = "sepatu-contoh-aman-42";
    String first = Passwords.hash(password), second = Passwords.hash(password);
    assertNotEquals(first, second);
    assertFalse(first.contains(password));
    assertTrue(Passwords.verify(password, first));
    assertFalse(Passwords.verify("kata-sandi-salah", first));
  }

  @Test
  void malformedAndUnboundedHashFormatsFailClosed() {
    for (String hash :
        new String[] {
          "plaintext",
          "pbkdf2$x$bad$bad",
          "pbkdf2$999999999$AA==$AA==",
          "pbkdf2$0$AA==$AA==",
          "pbkdf2$600000$AA==$AA=="
        }) assertFalse(Passwords.verify("input", hash));
    assertFalse(Passwords.verify(null, null));
  }

  @Test
  void unicodePasswordIsPreserved() {
    String password = "Shoe-\u978b\u5b50-\u00e9-test-42";
    assertTrue(Passwords.verify(password, Passwords.hash(password)));
  }
}
