public class Main {
    public static void main(String[] args) {
        StudentService students = new StudentService();
        BookCatalog catalog = new BookCatalog();
        LoanService loans = new LoanService();
        NotificationService notifications = new NotificationService();
        LibraryFacade facade = new LibraryFacade(students, catalog, loans, notifications);

        step(facade, catalog, notifications, true, "S1", "B1");
        step(facade, catalog, notifications, true, "S1", "B1");
        step(facade, catalog, notifications, true, "S3", "B1");
        step(facade, catalog, notifications, false, "S1", "B1");
        step(facade, catalog, notifications, false, "S1", "B1");
    }

    private static void step(LibraryFacade facade, BookCatalog catalog,
                             NotificationService notifications,
                             boolean borrow, String studentId, String bookId) {
        String action = borrow ? "borrow" : "return";
        System.out.println("Request: " + studentId + " " + action + "s " + bookId);
        try {
            String result = borrow
                    ? facade.borrowBook(studentId, bookId)
                    : facade.returnBook(studentId, bookId);
            System.out.println("  Result: " + result);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("  Rejected: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        System.out.println("  B1 copies: " + catalog.getCopies("B1")
                + " | Notices: " + notifications.getCount());
    }
}
