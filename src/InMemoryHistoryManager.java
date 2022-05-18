import task.Task;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private LinkedList<Task> listHistory;

    public InMemoryHistoryManager() {
        listHistory = new LinkedList<>();
    }

    public LinkedList<Task> getListHistory() {
        return listHistory;
    }


    @Override
    public void add(Task task) {
        if (listHistory.size() < 10) {
            listHistory.add(task);
        } else {
            listHistory.removeFirst();
            listHistory.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return listHistory.isEmpty() ? Collections.emptyList() : getListHistory();
    }

}
