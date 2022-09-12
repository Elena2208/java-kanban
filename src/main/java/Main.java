import manager.Managers;
import manager.TaskManager;
import task.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static java.lang.System.out;

public class Main {

    public static void main(String[] args) {
        File file = new File("\\Tasks.csv");
        TaskManager manager = Managers.getDefault();
        manager.createEpic(new Epic("ДР", "Подготовка"));
        manager.createSubtask(new Subtask("Подарок", "Почта", Status.DONE,0,
                Duration.of(30,ChronoUnit.MINUTES),
                LocalDateTime.of(0,2,1,10,00,00)));
        manager.createSubtask(new Subtask("Приглашения", "Рассылка", Status.DONE, 0,
                Duration.of(30,ChronoUnit.MINUTES),
                LocalDateTime.of(0,2,1,12,10,10)));
        manager.createSubtask(new Subtask("wwww", "fggggg", Status.IN_PROGRESS, 0,
                Duration.of(35,ChronoUnit.MINUTES),
                LocalDateTime.of(0,2,1,19,45,10)));
        manager.createTask(new Task("task","new",Status.IN_PROGRESS,Duration.ofMinutes(50)));
        manager.updateSubtask(3,new Subtask("wwww", "fggggg", Status.IN_PROGRESS, 0,
                Duration.of(35,ChronoUnit.MINUTES),
                LocalDateTime.of(0,2,1,11,45,10)));
        out.println(manager.getPrioritizedTasks());
    }
}
