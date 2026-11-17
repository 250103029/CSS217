public class LibraryFacade {
    private final StudentService studentService;
    private final BookCatalog bookCatalog;
    private final LoanService loanService;
    private final NotificationService notificationService;

    public LibraryFacade(StudentService studentService,
                         BookCatalog bookCatalog,
                         LoanService loanService,
                         NotificationService notificationService) {
        if (studentService == null || bookCatalog == null
                || loanService == null || notificationService == null) {
            throw new IllegalArgumentException("Dependencies must not be null");
        }
        this.studentService = studentService;
        this.bookCatalog = bookCatalog;
        this.loanService = loanService;
        this.notificationService = notificationService;
    }

    public String borrowBook(String studentId, String bookId) {
        String student = normalize(studentId);
        String book = normalize(bookId);
        requireKnown(student, book);

        if (!studentService.isActive(student)) {
            throw new IllegalStateException("Student " + student + " is inactive");
        }
        if (loanService.hasLoan(student, book)) {
            throw new IllegalStateException("Student " + student + " already holds " + book);
        }
        if (!bookCatalog.isAvailable(book)) {
            throw new IllegalStateException("No available copies of " + book);
        }

        bookCatalog.decreaseCopies(book);
        loanService.addLoan(student, book);
        String message = "BORROWED " + student + " " + book;
        notificationService.record(message);
        return message;
    }

    public String returnBook(String studentId, String bookId) {
        String student = normalize(studentId);
        String book = normalize(bookId);
        requireKnown(student, book);

        if (!loanService.hasLoan(student, book)) {
            throw new IllegalStateException("Student " + student + " has no loan of " + book);
        }

        loanService.removeLoan(student, book);
        bookCatalog.increaseCopies(book);
        String message = "RETURNED " + student + " " + book;
        notificationService.record(message);
        return message;
    }

    private String normalize(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID must not be null or blank");
        }
        return id.trim();
    }

    private void requireKnown(String student, String book) {
        if (!studentService.exists(student)) {
            throw new IllegalArgumentException("Unknown student: " + student);
        }
        if (!bookCatalog.exists(book)) {
            throw new IllegalArgumentException("Unknown book: " + book);
        }
    }
}
