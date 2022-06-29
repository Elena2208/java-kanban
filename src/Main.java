import manager.FileBackedTasksManager;
import manager.Managers;
import manager.TaskManager;
import task.*;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        File file = new File("\\Tasks.csv");
        TaskManager manager = Managers.getDefault();
        manager.createTask(new Task("Позвонить маме", "Уточнить инфу", Status.NEW));
        manager.createTask(new Task("Записаться к врачу", " На июнь ", Status.IN_PROGRESS));
        manager.createEpic(new Epic("ДР", "Подготовка", Status.NEW));
        manager.createSubtask(new Subtask("Подарок", "Почта", Status.IN_PROGRESS, 2));
        manager.createSubtask(new Subtask("Приглашения", "Рассылка", Status.IN_PROGRESS, 2));
        manager.createTask(new Task("Заплатить коммуналку", " срочно ", Status.DONE));
        manager.getEpiId(2);
        manager.getTaskId(0);
        manager.getTaskId(1);
        manager.getSubtaskId(3);
        manager.getSubtaskId(4);
        manager.getTaskId(5);
        manager.getHistory();


        FileBackedTasksManager newFile = FileBackedTasksManager.loadFromFile(file);

        System.out.println(newFile.getHistory());
        System.out.println();
        System.out.println(newFile.getListSubtask());
        System.out.println();
        System.out.println(newFile.getListEpic());
        System.out.println();
        System.out.println(newFile.getListTask());


    }
}
