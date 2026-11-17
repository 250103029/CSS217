import java.util.HashMap;
import java.util.Map;

public class StudentService {
    private final Map<String, Boolean> students = new HashMap<>();

    public StudentService() {
        students.put("S1", true);
        students.put("S2", false);
        students.put("S3", true);
    }

    public boolean exists(String studentId) {
        return students.containsKey(studentId);
    }

    public boolean isActive(String studentId) {
        return Boolean.TRUE.equals(students.get(studentId));
    }
}
