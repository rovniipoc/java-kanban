package ru.yandex.javacource.brykalov.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.brykalov.schedule.task.Epic;
import ru.yandex.javacource.brykalov.schedule.task.Status;
import ru.yandex.javacource.brykalov.schedule.task.Subtask;
import ru.yandex.javacource.brykalov.schedule.task.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.javacource.brykalov.schedule.task.Status.NEW;

public class InMemoryTaskManagerTest {

    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

    @BeforeEach
    void clearAllTasks() {
        inMemoryTaskManager.deleteAllTasks();
        inMemoryTaskManager.deleteAllEpics();
        inMemoryTaskManager.deleteAllSubtasks();
    }

    @Test
    void addNewAllTypeTasks() {
        Task task = new Task("Задача",
                         "Описание задачи", Status.NEW);
        int taskId = inMemoryTaskManager.addNewTask(task);

        Epic epic = new Epic("Эпик",
                         "Описание эпика");
        int epicId = inMemoryTaskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Подзадача",
                                  "Описание подзадачи",
                                             epicId, Status.NEW);
        int subtaskId = inMemoryTaskManager.addNewSubtask(subtask);

        Task savedTask = inMemoryTaskManager.getTask(taskId);
        Task savedEpic = inMemoryTaskManager.getEpic(epicId);
        Task savedSubtask = inMemoryTaskManager.getSubtask(subtaskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertNotNull(savedEpic, "Эпик не найден.");
        assertNotNull(savedSubtask, "Подзадача не найдена.");

        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");
    }

    @Test
    void addAndReadHistory() {
        Task task = new Task("Задача",
                         "Описание задачи", Status.NEW);
        int taskId = inMemoryTaskManager.addNewTask(task);

        Epic epic = new Epic("Эпик",
                         "Описание эпика");
        int epicId = inMemoryTaskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Подзадача",
                                  "Описание подзадачи",
                                             epicId, Status.NEW);
        int subtaskId = inMemoryTaskManager.addNewSubtask(subtask);

        inMemoryTaskManager.getTask(taskId);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(inMemoryTaskManager.getSubtask(subtaskId));
        tasks.add(inMemoryTaskManager.getSubtask(subtaskId));
        tasks.add(inMemoryTaskManager.getEpic(epicId));
        tasks.add(inMemoryTaskManager.getEpic(epicId));
        tasks.add(inMemoryTaskManager.getEpic(epicId));
        tasks.add(inMemoryTaskManager.getSubtask(subtaskId));
        tasks.add(inMemoryTaskManager.getTask(taskId));
        tasks.add(inMemoryTaskManager.getEpic(epicId));
        tasks.add(inMemoryTaskManager.getTask(taskId));
        tasks.add(inMemoryTaskManager.getSubtask(subtaskId));

        subtask.setName("Новое название подзадачи");

        ArrayList<Task> history = inMemoryTaskManager.getHistory();
        assertArrayEquals(new ArrayList[]{tasks}, new ArrayList[]{history}, "История сохраняется неверно.");
        assertNotEquals(history.getLast(), subtask.getName(), "В истории должны храниться задачи" +
                                                                        " в том состоянии, в котором они были в момент" +
                                                                         " добавления в историю");
    }

    @Test
    void cannotBeSubtasksAsEpic() {
        Epic epic = new Epic("Эпик",
                         "Описание эпика");
        int epicId = inMemoryTaskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача1",
                                   "Описание подзадачи1",
                                              epicId, Status.NEW);
        int subtaskId1 = inMemoryTaskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача2",
                                   "Описание подзадачи2",
                                              subtaskId1, Status.NEW);
        Integer subtaskId2 = inMemoryTaskManager.addNewSubtask(subtask2);

        assertNull(subtaskId2, "Для поздадачи другая подзадача не может служить эпиком");
    }
}
