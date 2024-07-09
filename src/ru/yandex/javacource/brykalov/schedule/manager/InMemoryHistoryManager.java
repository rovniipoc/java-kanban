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
        remove(taskId);

        historyMap.put(task.getId(), historyList.linkLast(task));
    }

    @Override
    public void remove(int id) {
        final Node node = historyMap.remove(id);
        if (node == null) {
            return;
        }
        removeNode(node);
    }

    public void removeNode(Node<Task> node) {
        historyList.unlink(node);
    }

    @Override
    public List<Task> getHistory() {
        return historyList.getTasks();
    }

    public static class CustomLinkedList<Task> {
        private Node<Task> head;
        private Node<Task> tail;
        private int size = 0;

        public Task unlink(Node<Task> node) {
            final Task data = node.data;
            final Node<Task> next = node.next;
            final Node<Task> prev = node.prev;

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

        public Node<Task> linkLast(Task element) {
            final Node<Task> oldTail = tail;
            final Node<Task> newNode = new Node<>(oldTail, element, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
            size++;

            return newNode;
        }

        public List<Task> getTasks() {
            List<Task> result = new ArrayList<>();

            for (Node<Task> node = head; node != null; node = node.next) {
                result.add(node.data);
            }

            return result;
        }
    }
}
