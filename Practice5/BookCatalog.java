import java.util.HashMap;
import java.util.Map;

public class BookCatalog {
    private final Map<String, Integer> copies = new HashMap<>();

    public BookCatalog() {
        copies.put("B1", 2);
        copies.put("B2", 0);
    }

    public boolean exists(String bookId) {
        return copies.containsKey(bookId);
    }

    public int getCopies(String bookId) {
        Integer count = copies.get(bookId);
        if (count == null) {
            throw new IllegalArgumentException("Unknown book: " + bookId);
        }
        return count;
    }

    public boolean isAvailable(String bookId) {
        return exists(bookId) && copies.get(bookId) > 0;
    }

    public void decreaseCopies(String bookId) {
        if (getCopies(bookId) == 0) {
            throw new IllegalStateException("No copies left of " + bookId);
        }
        copies.put(bookId, copies.get(bookId) - 1);
    }

    public void increaseCopies(String bookId) {
        copies.put(bookId, getCopies(bookId) + 1);
    }
}
