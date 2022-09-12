package manager;

import task.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;
    private int size;

    private HashMap<Integer, Node<Task>> mapNode = new HashMap<>();

    public void linkLast(Task task) {
        if (size == 0) {
            head = new Node<>(task, null, null);
            tail = head;
        } else {
            Node secondLast = tail;
            tail = new Node<>(task, null, secondLast);
            secondLast.next = tail;
        }
        size++;
    }

    public List<Task> getTasks() {
        List<Task> taskList = new ArrayList<>();
        Node node = head;
        while (node != null) {
            taskList.add((Task) node.item);
            node = node.next;
        }
        return taskList;
    }

    @Override
    public void add(Task task) {
        int taskId = task.getIdTask();
        if (mapNode.containsKey(taskId)) {
            removeNode(mapNode.get(taskId));
        }
        linkLast(task);
        mapNode.put(taskId, tail);
    }

    @Override
    public void remove(int id) {
        if (mapNode.containsKey(id)) {
            removeNode(mapNode.get(id));
            mapNode.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void removeNode(Node node) {
        if (head.equals(node)) {
            head = node.next;
            head.prev = null;
        } else if (tail.equals(node)) {
            tail = node.prev;
            tail.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(E item, Node<E> next, Node<E> prev) {
            this.item = item;
            this.next = next;
            this.prev = prev;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?> node = (Node<?>) o;
            return Objects.equals(item, node.item) && Objects.equals(next, node.next) && Objects.equals(prev, node.prev);
        }

        @Override
        public int hashCode() {
            return Objects.hash(item, next, prev);
        }

        @Override
        public String toString() {
            return "Node{" +
                    "item=" + item +
                    ", next=" + next +
                    ", prev=" + prev +
                    '}';
        }
    }
}

