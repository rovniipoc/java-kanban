package ru.yandex.javacource.brykalov.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.brykalov.schedule.task.Status;
import ru.yandex.javacource.brykalov.schedule.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class InMemoryHistoryManagerTest {

    @Test
    void addAndReadTaskHistory() {
        // Проверка добавления задач в историю, чтения истории и удаления из истории
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

        // Проверка получения пустой истории задач
        List<Task> tasks = new ArrayList<>();
        assertArrayEquals(new List[]{tasks}, new List[]{inMemoryHistoryManager.getHistory()}, "Ошибка при получении пустой истории");

        // Проверка записи задач без дублирования и считывание истории
        Task task1 = new Task("Задача1", "Описание1", Status.NEW);
        task1.setId(1);

        Task task2 = new Task("Задача2", "Описание2", Status.NEW,
                LocalDateTime.of(2024, 1, 1, 12, 0),
                Duration.ofMinutes(300));
        task2.setId(2);

        tasks.add(task1);
        tasks.add(task2);

        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);

        assertArrayEquals(new List[]{tasks}, new List[]{inMemoryHistoryManager.getHistory()}, "История сохраняется неверно.");

        // Проверка на отсутствие записи дубликатов в истории
        inMemoryHistoryManager.add(task2);
        assertArrayEquals(new List[]{tasks}, new List[]{inMemoryHistoryManager.getHistory()}, "История сохраняется неверно (есть дубликаты).");

        // Проверка удаления из истории
        inMemoryHistoryManager.remove(1);
        tasks.removeFirst();
        assertArrayEquals(new List[]{tasks}, new List[]{inMemoryHistoryManager.getHistory()}, "Удаление из истории выполняется неверно.");
        inMemoryHistoryManager.remove(2);
        tasks.removeFirst();
        assertArrayEquals(new List[]{tasks}, new List[]{inMemoryHistoryManager.getHistory()}, "Удаление из истории выполняется неверно.");
    }
}
