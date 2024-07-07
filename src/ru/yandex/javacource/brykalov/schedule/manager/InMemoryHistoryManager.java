package ru.yandex.javacource.brykalov.schedule.manager;

import ru.yandex.javacource.brykalov.schedule.task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> historyList = new CustomLinkedList<>();
    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        int taskId = task.getId();

        if (historyMap.containsKey(taskId)) {
            Node<Task> unlinkTaskNode = historyMap.get(taskId);
            removeNode(unlinkTaskNode);
        }

        historyMap.put(task.getId(), historyList.linkLast(task));

    }

    @Override
    public void remove(int id) {
        if (historyMap.get(id) == null) {
            return;
        }
        removeNode(historyMap.get(id));
        historyMap.remove(id);
    }

    public void removeNode(Node<Task> node) {
        historyList.unlink(node);
    }

    @Override
    public List<Task> getHistory() {
        return historyList.getTasks();
    }

    public static class CustomLinkedList<T> {
        private Node<T> head;
        private Node<T> tail;
        private int size = 0;

        public T unlink(Node<T> node) {
            final T data = node.data;
            final Node<T> next = node.next;
            final Node<T> prev = node.prev;

            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
                node.prev = null;
            }

            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
                node.next = null;
            }

            node.data = null;
            size--;

            return data;
        }

        public Node<T> linkLast(T element) {
            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(oldTail, element, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
            size++;

            return newNode;
        }

        public List<T> getTasks() {
            List<T> result = new ArrayList<>();

            for (Node<T> node = head; node != null; node = node.next) {
                result.add(node.data);
            }

            return result;
        }
    }
}
