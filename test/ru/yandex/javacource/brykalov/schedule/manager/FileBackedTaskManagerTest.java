package ru.yandex.javacource.brykalov.schedule.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.brykalov.schedule.task.Epic;
import ru.yandex.javacource.brykalov.schedule.task.Status;
import ru.yandex.javacource.brykalov.schedule.task.Subtask;
import ru.yandex.javacource.brykalov.schedule.task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    @Test
    void writeAndReadSaveFile() throws IOException {
        // Тест на сохранение и загрузку нескольких задач разных типов.

        // Создаем менеджер №1 и файл сохранения
        File tempFile = File.createTempFile("temp", ".csv");
        FileBackedTaskManager fileBackedTaskManager1 = new FileBackedTaskManager(tempFile);

        // Создаем задачи в менеджере №1:
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        int epicId1 = fileBackedTaskManager1.addNewEpic(epic1);

        Epic epic2 = new Epic("Эпик2", "Описание эпика2");
        int epicId2 = fileBackedTaskManager1.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", epicId2, Status.NEW,
                LocalDateTime.of(2024, 8, 4, 0, 0),
                Duration.ofMinutes(360));
        int subtaskId1 = fileBackedTaskManager1.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", epicId2, Status.NEW);
        int subtaskId2 = fileBackedTaskManager1.addNewSubtask(subtask2);

        Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи3", epicId2, Status.NEW,
                LocalDateTime.of(2025, 8, 4, 2, 0),
                Duration.ofMinutes(300));
        int subtaskId3 = fileBackedTaskManager1.addNewSubtask(subtask3);

        Task task1 = new Task("Задача", "Описание задачи", Status.NEW);
        int taskId1 = fileBackedTaskManager1.addNewTask(task1);

        Task task2 = new Task("Задача2", "Описание задачи2", Status.NEW,
                LocalDateTime.of(2026, 1, 1, 12, 0),
                Duration.ofMinutes(60));
        int taskId2 = fileBackedTaskManager1.addNewTask(task2);

        // Создаем менеджера №2 из файла сохранения
        FileBackedTaskManager fileBackedTaskManager2 = new FileBackedTaskManager(tempFile);

        // Сравниваем задачи менеджера №1 и №2
        assertArrayEquals(new List[]{fileBackedTaskManager1.getTaskList()}, new List[]{fileBackedTaskManager2.getTaskList()},
                "Задачи сохраняются/считываются неверно.");
        assertArrayEquals(new List[]{fileBackedTaskManager1.getEpicList()}, new List[]{fileBackedTaskManager2.getEpicList()},
                "Эпики сохраняются/считываются неверно.");
        assertArrayEquals(new List[]{fileBackedTaskManager1.getSubtaskList()}, new List[]{fileBackedTaskManager2.getSubtaskList()},
                "Подзадачи сохраняются/считываются неверно.");
    }

    @Test
    void tasksCounterTest() throws IOException {
        // Тест на корректную работу счётчика задач после чтения из файла сохранения.

        // Создаем менеджер №1 и файл сохранения
        File tempFile = File.createTempFile("temp", ".csv");
        FileBackedTaskManager fileBackedTaskManager1 = new FileBackedTaskManager(tempFile);

        // Создаем задачи в менеджере №1:
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        int epicId1 = fileBackedTaskManager1.addNewEpic(epic1);
        Epic epic2 = new Epic("Эпик2", "Описание эпика2");
        int epicId2 = fileBackedTaskManager1.addNewEpic(epic2);
        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", epicId2, Status.NEW);
        int subtaskId1 = fileBackedTaskManager1.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", epicId2, Status.NEW);
        int subtaskId2 = fileBackedTaskManager1.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи3", epicId2, Status.NEW);
        int subtaskId3 = fileBackedTaskManager1.addNewSubtask(subtask3);
        Task task1 = new Task("Задача", "Описание задачи", Status.NEW);
        int taskId1 = fileBackedTaskManager1.addNewTask(task1);

        // Удаляем часть первую, последнюю и промежуточную задачу
        fileBackedTaskManager1.deleteEpicById(1);
        fileBackedTaskManager1.deleteTaskById(6);
        fileBackedTaskManager1.deleteSubtaskById(3);

        // Создаем менеджера №2 из файла сохранения
        FileBackedTaskManager fileBackedTaskManager2 = new FileBackedTaskManager(tempFile);

        // Создаем новую задачу в менеджере №2, ее id должен быть равен 6
        Task task2 = new Task("Задача2", "Описание задачи2", Status.NEW);
        int taskId2 = fileBackedTaskManager2.addNewTask(task2);
        assertEquals(taskId2, 6);
    }


    @Test
    void writeAndReadSaveEmptyFile() throws IOException {
        // Тест на сохранение и загрузку пустого файла.

        // Создаем менеджер №1 и файл сохранения
        File tempFile = File.createTempFile("temp", ".csv");
        FileBackedTaskManager fileBackedTaskManager1 = new FileBackedTaskManager(tempFile);

        // Создаем менеджера №2 из файла сохранения
        FileBackedTaskManager fileBackedTaskManager2 = new FileBackedTaskManager(tempFile);

        // Сравниваем задачи менеджера №1 и №2
        assertArrayEquals(new List[]{fileBackedTaskManager1.getTaskList()}, new List[]{fileBackedTaskManager2.getTaskList()},
                "Задачи сохраняются/считываются неверно.");
        assertArrayEquals(new List[]{fileBackedTaskManager1.getEpicList()}, new List[]{fileBackedTaskManager2.getEpicList()},
                "Эпики сохраняются/считываются неверно.");
        assertArrayEquals(new List[]{fileBackedTaskManager1.getSubtaskList()}, new List[]{fileBackedTaskManager2.getSubtaskList()},
                "Подзадачи сохраняются/считываются неверно.");

    }

    @Test
    void exceptionsTest() throws IOException {
        // Тест на исключения

        // В теории тема тестов на исключения не раскрывалась.

        File tempFile = File.createTempFile("temp", ".csv");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);

        assertThrows(ManagerSaveException.class, () -> {
            File file = new File("resources/nonExistentFile.csv");
            fileBackedTaskManager.loadFromFile(file);
        }, "Попытка обращения к несуществующему файлу должна приводить к исключению");


        assertThrows(ManagerSaveException.class, () -> {
            Path path = Path.of("thisdirectorydoesnotexist/temp.csv");
            FileBackedTaskManager fileBackedTaskManager2 = new FileBackedTaskManager(path.toFile());
        }, "Попытка обращения к несуществующему пути должна приводить к исключению");

    }

}
