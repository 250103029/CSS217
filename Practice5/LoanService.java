import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LoanService {
    private final Map<String, Set<String>> loans = new HashMap<>();

    public boolean hasLoan(String studentId, String bookId) {
        Set<String> books = loans.get(studentId);
        return books != null && books.contains(bookId);
    }

    public void addLoan(String studentId, String bookId) {
        if (hasLoan(studentId, bookId)) {
            throw new IllegalStateException("Loan already exists: " + studentId + " " + bookId);
        }
        loans.computeIfAbsent(studentId, k -> new HashSet<>()).add(bookId);
    }

    public void removeLoan(String studentId, String bookId) {
        if (!hasLoan(studentId, bookId)) {
            throw new IllegalStateException("No loan to remove: " + studentId + " " + bookId);
        }
        Set<String> books = loans.get(studentId);
        books.remove(bookId);
        if (books.isEmpty()) {
            loans.remove(studentId);
        }
    }

    public int getLoanCount() {
        int total = 0;
        for (Set<String> books : loans.values()) {
            total += books.size();
        }
        return total;
    }
}
