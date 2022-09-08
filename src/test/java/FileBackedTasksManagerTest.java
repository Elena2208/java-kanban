import manager.FileBackedTasksManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTasksManagerTest extends TaskManagerTest <FileBackedTasksManager> {
   private File file;
   private TaskManager manager;

   @BeforeEach
   public void beforeEach(){
       file = new File("\\Tasks.csv");
       manager = Managers.getDefault();
       task = new Task("New_task", "Test", Status.NEW);
       epic = new Epic("New_epic", "test");
       subtaskFirst = new Subtask("SubtaskFirst", "Test", Status.NEW, epic.getIdTask(),
               Duration.ofMinutes(50),  LocalDateTime.of(0,1,25,10,10,10));
       subtaskSecond = new Subtask("SubtaskSecond", "Test", Status.IN_PROGRESS, epic.getIdTask(),
               Duration.ofMinutes(100), LocalDateTime.of(0,1,10,18,25,36));
   }
    @Test
    void loadFromFileIsTaskListIsEmptyTest() {
        FileBackedTasksManager newFile = FileBackedTasksManager.loadFromFile(file);
        assertTrue(manager.getListEpic().isEmpty());
    }

    @Test
    void loadFromFileEpicByNotSubtaskTest(){

        Epic epic=new Epic("ДР", "Подготовка");
        manager.createEpic(epic);
        FileBackedTasksManager newFile = FileBackedTasksManager.loadFromFile(file);
        assertTrue(newFile.getEpicId(epic.getIdTask()).isPresent());
    }


    @Test
    void loadFromFileHistoryIsEmpty(){

        Epic epic=new Epic("ДР", "Подготовка");
        manager.createEpic(epic);
        FileBackedTasksManager newFile = FileBackedTasksManager.loadFromFile(file);
        assertTrue(newFile.getHistory().isEmpty());
    }

}

