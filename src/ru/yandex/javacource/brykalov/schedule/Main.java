package ru.yandex.javacource.brykalov.schedule;

import ru.yandex.javacource.brykalov.schedule.manager.FileBackedTaskManager;

import ru.yandex.javacource.brykalov.schedule.task.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;


public class Main {

    public static void main(String[] args) {
        // Пользовательский сценарий

        // Создаем менеджер №1 и файл сохранения
        File file = new File("resources/file.csv");
        FileBackedTaskManager fileBackedTaskManager1 = new FileBackedTaskManager(file);

        // Создаем задачи разных типов в менеджере №1:
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
                LocalDateTime.of(2024, 8, 4, 2, 0),
                Duration.ofMinutes(300));
        int subtaskId3 = fileBackedTaskManager1.addNewSubtask(subtask3);

        Task task1 = new Task("Задача", "Описание задачи", Status.NEW);
        int taskId1 = fileBackedTaskManager1.addNewTask(task1);

        Task task2 = new Task("Задача2", "Описание задачи2", Status.NEW,
                LocalDateTime.of(2025, 1, 1, 12, 0),
                Duration.ofMinutes(60));
        int taskId2 = fileBackedTaskManager1.addNewTask(task2);

        // Создаем менеджер №2 из файла сохранения
        FileBackedTaskManager fileBackedTaskManager2 = new FileBackedTaskManager(file);

        // Проверяем, что все задачи из менеджера №1 есть в менеджере №2
        System.out.println(fileBackedTaskManager1.getTaskList());
        System.out.println(fileBackedTaskManager1.getEpicList());
        System.out.println(fileBackedTaskManager1.getSubtaskList());
        System.out.println();
        System.out.println(fileBackedTaskManager1.getPrioritizedTasks());
        System.out.println("----------------------------------------------");

        System.out.println(fileBackedTaskManager2.getTaskList());
        System.out.println(fileBackedTaskManager2.getEpicList());
        System.out.println(fileBackedTaskManager2.getSubtaskList());
        System.out.println();
        System.out.println(fileBackedTaskManager2.getPrioritizedTasks());
        System.out.println("----------------------------------------------");

        // Удаляем все задачи
        fileBackedTaskManager1.deleteAllTasks();
        fileBackedTaskManager1.deleteAllEpics();
        fileBackedTaskManager1.deleteAllSubtasks();

    }
}
