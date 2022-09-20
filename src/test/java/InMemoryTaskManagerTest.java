import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;


public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager();
        task = new Task("New_task", "Test", Status.NEW);
        epic = new Epic("New_epic", "test");
        subtaskFirst = new Subtask("SubtaskFirst", "Test", Status.NEW, epic.getIdTask(),
                Duration.ofMinutes(50), LocalDateTime.of(0, 1, 25, 10, 10, 10));
        subtaskSecond = new Subtask("SubtaskSecond", "Test", Status.IN_PROGRESS, epic.getIdTask(),
                Duration.ofMinutes(100), LocalDateTime.of(0, 1, 10, 18, 25, 36));
    }


}

