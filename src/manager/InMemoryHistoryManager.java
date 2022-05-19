package manager;

import manager.HistoryManager;
import task.Task;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final int LIST_SIZE = 10;
    private LinkedList<Task> listHistory;

    public InMemoryHistoryManager() {
        listHistory = new LinkedList<>();
    }


    @Override
    public void add(Task task) {
        if (listHistory.size() > LIST_SIZE) {
            listHistory.removeFirst();
        }
        listHistory.add(task);

    }

    @Override
    public List<Task> getHistory() {
        List<Task> copy = List.copyOf(listHistory);
        return listHistory.isEmpty() ? Collections.emptyList() : copy;
    }

}
