import task.Epic;
import task.Subtask;
import task.Task;

public class Main {

    public static void main(String[] args) {
       TaskManager taskManager= new TaskManager();
       taskManager.createEpic(new Epic(TaskManager.getIdInc(), "Переезд","В другой город",
               Task.TaskStatus.NEW));
       taskManager.createTask(new Task(TaskManager.getIdInc(),"Поход в магазин",
               "Купить продукты на неделю", Task.TaskStatus.NEW));
        taskManager.createTask(new Task(TaskManager.getIdInc(),"Прогулка",
                "Погулять с собакой ", Task.TaskStatus.NEW));
       taskManager.createSubtask(new Subtask(TaskManager.getIdInc(),"Найти фирму","Перевозка вещей",
               Task.TaskStatus.NEW,0));
       taskManager.createSubtask(new Subtask(TaskManager.getIdInc(),"Упаковка","Запаковать вещи по коробкам",
               Task.TaskStatus.NEW,0));
       taskManager.createEpic(new Epic(TaskManager.getIdInc(), "Врач","Стоматолог",
               Task.TaskStatus.NEW));
       taskManager.createSubtask(new Subtask(TaskManager.getIdInc(),"Записаться на прием",
               "Позвонить во торник после 9.00",Task.TaskStatus.NEW,5));
       System.out.println(taskManager.getListEpic());
       System.out.println(taskManager.getListTask());
       System.out.println(taskManager.getListSubtask());
       taskManager.updateTask(2,new Task(TaskManager.getIdInc(),"Прогулка",
        "Погулять с собакой ", Task.TaskStatus.DONE));
        System.out.println(taskManager.getListTask());
        taskManager.updateSubtask(4,new Subtask(TaskManager.getIdInc(),"Упаковка","Запаковать вещи по коробкам",
                Task.TaskStatus.DONE,0));
        taskManager.deleteSubtaskById(3);

        System.out.println(taskManager.getListSubtaskEpic(0));
        System.out.println(taskManager.getListEpic());
        taskManager.updateEpic(0,new Epic(TaskManager.getIdInc(), "Переезд","В другой город",
                Task.TaskStatus.DONE));













    }
}
