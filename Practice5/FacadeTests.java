import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class FacadeTests {
    private static int passed = 0;
    private static int failed = 0;

    private static class Env {
        final StudentService students = new StudentService();
        final BookCatalog catalog = new BookCatalog();
        final LoanService loans = new LoanService();
        final NotificationService notifications = new NotificationService();
        final LibraryFacade facade = new LibraryFacade(students, catalog, loans, notifications);

        String state() {
            return catalog.getCopies("B1") + "|" + catalog.getCopies("B2") + "|"
                    + loans.getLoanCount() + "|" + notifications.getCount();
        }
    }

    public static void main(String[] args) {
        t1Borrow();
        t2Duplicate();
        t3Inactive();
        t4Unavailable();
        t5Return();
        t6WrongStudent();
        t7SharedStock();
        t8BadInput();
        t9Normalization();

        System.out.println();
        System.out.println("Summary: " + passed + " passed, " + failed + " failed");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("PASS " + name);
        } else {
            failed++;
            System.out.println("FAIL " + name);
        }
    }

    private static boolean throwsType(Class<? extends RuntimeException> type, Runnable action) {
        try {
            action.run();
            return false;
        } catch (RuntimeException e) {
            return type.isInstance(e);
        }
    }

    private static void t1Borrow() {
        Env e = new Env();
        String result = e.facade.borrowBook("S1", "B1");
        check("T1 result message", "BORROWED S1 B1".equals(result));
        check("T1 B1 has 1 copy", e.catalog.getCopies("B1") == 1);
        check("T1 loan exists", e.loans.hasLoan("S1", "B1"));
        check("T1 one matching notification",
                e.notifications.getAll().equals(Arrays.asList("BORROWED S1 B1")));
    }

    private static void t2Duplicate() {
        Env e = new Env();
        e.facade.borrowBook("S1", "B1");
        check("T2 second borrow fails",
                throwsType(IllegalStateException.class, () -> e.facade.borrowBook("S1", "B1")));
        check("T2 copies stay at 1", e.catalog.getCopies("B1") == 1);
        check("T2 only one loan", e.loans.getLoanCount() == 1);
        check("T2 only one notification", e.notifications.getCount() == 1);
    }

    private static void t3Inactive() {
        Env e = new Env();
        check("T3 inactive student fails",
                throwsType(IllegalStateException.class, () -> e.facade.borrowBook("S2", "B1")));
        check("T3 B1 stays at 2", e.catalog.getCopies("B1") == 2);
        check("T3 no loan", e.loans.getLoanCount() == 0 && !e.loans.hasLoan("S2", "B1"));
        check("T3 no notification", e.notifications.getCount() == 0);
    }

    private static void t4Unavailable() {
        Env e = new Env();
        check("T4 unavailable book fails",
                throwsType(IllegalStateException.class, () -> e.facade.borrowBook("S1", "B2")));
        check("T4 B2 stays at 0", e.catalog.getCopies("B2") == 0);
        check("T4 no loan", e.loans.getLoanCount() == 0 && !e.loans.hasLoan("S1", "B2"));
        check("T4 no notification", e.notifications.getCount() == 0);
    }

    private static void t5Return() {
        Env e = new Env();
        e.facade.borrowBook("S1", "B1");
        String result = e.facade.returnBook("S1", "B1");
        check("T5 return message", "RETURNED S1 B1".equals(result));
        check("T5 copies restored to 2", e.catalog.getCopies("B1") == 2);
        check("T5 loan removed", !e.loans.hasLoan("S1", "B1"));
        check("T5 two notifications in order",
                e.notifications.getAll().equals(Arrays.asList("BORROWED S1 B1", "RETURNED S1 B1")));
        String before = e.state();
        check("T5 second return fails",
                throwsType(IllegalStateException.class, () -> e.facade.returnBook("S1", "B1")));
        check("T5 second return changes nothing", before.equals(e.state()));
    }

    private static void t6WrongStudent() {
        Env e = new Env();
        e.facade.borrowBook("S1", "B1");
        String before = e.state();
        check("T6 wrong student return fails",
                throwsType(IllegalStateException.class, () -> e.facade.returnBook("S3", "B1")));
        check("T6 S1 still holds loan", e.loans.hasLoan("S1", "B1"));
        check("T6 state unchanged", before.equals(e.state()));
    }

    private static void t7SharedStock() {
        Env e = new Env();
        e.facade.borrowBook("S1", "B1");
        e.facade.borrowBook("S3", "B1");
        check("T7 stock is 0 after two borrows", e.catalog.getCopies("B1") == 0);
        e.facade.returnBook("S1", "B1");
        e.facade.borrowBook("S1", "B1");
        check("T7 final count is 0", e.catalog.getCopies("B1") == 0);
        check("T7 both students hold a loan",
                e.loans.hasLoan("S1", "B1") && e.loans.hasLoan("S3", "B1"));
        check("T7 four notifications", e.notifications.getCount() == 4);
    }

    private static void t8BadInput() {
        String[][] inputs = {
                {null, "B1"}, {"S1", null},
                {"", "B1"}, {"S1", ""},
                {"  ", "B1"}, {"S1", "  "},
                {"S9", "B1"}, {"S1", "B9"},
                {"s1", "B1"}, {"S1", "b1"}
        };
        for (int mode = 0; mode < 2; mode++) {
            boolean borrow = mode == 0;
            String label = borrow ? "borrowBook" : "returnBook";
            for (String[] pair : inputs) {
                Env e = new Env();
                e.facade.borrowBook("S1", "B1");
                BiFunction<String, String, String> method =
                        borrow ? e.facade::borrowBook : e.facade::returnBook;
                String before = e.state();
                boolean thrown = throwsType(IllegalArgumentException.class,
                        () -> method.apply(pair[0], pair[1]));
                check("T8 " + label + " (" + pair[0] + ", " + pair[1] + ") throws IAE", thrown);
                check("T8 " + label + " (" + pair[0] + ", " + pair[1] + ") no state change",
                        before.equals(e.state()));
            }
        }

        StudentService s = new StudentService();
        BookCatalog c = new BookCatalog();
        LoanService l = new LoanService();
        NotificationService n = new NotificationService();
        check("T8 null StudentService rejected",
                throwsType(IllegalArgumentException.class, () -> new LibraryFacade(null, c, l, n)));
        check("T8 null BookCatalog rejected",
                throwsType(IllegalArgumentException.class, () -> new LibraryFacade(s, null, l, n)));
        check("T8 null LoanService rejected",
                throwsType(IllegalArgumentException.class, () -> new LibraryFacade(s, c, null, n)));
        check("T8 null NotificationService rejected",
                throwsType(IllegalArgumentException.class, () -> new LibraryFacade(s, c, l, null)));
    }

    private static void t9Normalization() {
        Env e = new Env();
        String borrowed = e.facade.borrowBook(" S1 ", " B1 ");
        check("T9 borrow message normalized", "BORROWED S1 B1".equals(borrowed));
        check("T9 loan stored under normalized IDs", e.loans.hasLoan("S1", "B1"));
        String returned = e.facade.returnBook("S1", "B1");
        check("T9 return message normalized", "RETURNED S1 B1".equals(returned));
        check("T9 copies back to 2", e.catalog.getCopies("B1") == 2);
        List<String> expected = Arrays.asList("BORROWED S1 B1", "RETURNED S1 B1");
        check("T9 normalized notifications", e.notifications.getAll().equals(expected));
    }
}
