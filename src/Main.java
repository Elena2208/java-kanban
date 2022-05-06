import task.*;

public class Main {

    public static void main(String[] args) {
       TaskManager taskManager= new TaskManager();
       taskManager.createEpic(new Epic(TaskManager.getIdInc(), "Переезд","В другой город",
               Status.NEW));
       taskManager.createTask(new Task(TaskManager.getIdInc(),"Поход в магазин",
               "Купить продукты на неделю", Status.NEW));
        taskManager.createTask(new Task(TaskManager.getIdInc(),"Прогулка",
                "Погулять с собакой ", Status.NEW));
       taskManager.createSubtask(new Subtask(TaskManager.getIdInc(),"Найти фирму","Перевозка вещей",
               Status.NEW,0));
       taskManager.createSubtask(new Subtask(TaskManager.getIdInc(),"Упаковка","Запаковать вещи по коробкам",
               Status.NEW,0));
       taskManager.createEpic(new Epic(TaskManager.getIdInc(), "Врач","Стоматолог",
               Status.NEW));
       taskManager.createSubtask(new Subtask(TaskManager.getIdInc(),"Записаться на прием",
               "Позвонить во торник после 9.00",Status.NEW,5));
        System.out.println(taskManager.getListEpic());
        System.out.println(taskManager.getListSubtask());
        taskManager.updateSubtask(4,new Subtask(TaskManager.getIdInc(),"Упаковка","Запаковать вещи по коробкам",
                Status.DONE,0));
        System.out.println(taskManager.getListSubtaskEpic(0));
        System.out.println(taskManager.getListEpic());
        taskManager.updateSubtask(3,new Subtask(TaskManager.getIdInc(),"Найти фирму","Перевозка вещей",
                Status.DONE,0));
        System.out.println(taskManager.getListEpic());





    }
}
