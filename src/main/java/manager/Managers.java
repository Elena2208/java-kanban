package manager;

import http.HttpTaskManager;

public class Managers {


    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() {
        return new HttpTaskManager(8078);

    }

} 

 