import com.google.gson.Gson;
import http.HttpTaskServer;
import http.KVServer;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.time.LocalDateTime;


public class Main {

    public static void main(String[] args) throws IOException {
        new KVServer().start();

        TaskManager manager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();
        Gson gson = new Gson();


        manager.createTask(new Task("New1", "111", Status.NEW, 120));
        manager.createEpic(new Epic("newEpic","222"));
        manager.createSubtask(new Subtask("Приглашения", "Рассылка", Status.DONE, 2,20,

                LocalDateTime.of(0,2,1,12,10,10)));

    }
}
