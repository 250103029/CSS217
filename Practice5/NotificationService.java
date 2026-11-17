import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationService {
    private final List<String> notifications = new ArrayList<>();

    public void record(String message) {
        notifications.add(message);
    }

    public int getCount() {
        return notifications.size();
    }

    public List<String> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(notifications));
    }
}
