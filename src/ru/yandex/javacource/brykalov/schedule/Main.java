package ru.yandex.javacource.brykalov.schedule;

import ru.yandex.javacource.brykalov.schedule.manager.FileBackedTaskManager;
import ru.yandex.javacource.brykalov.schedule.task.*;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        // Пользовательский сценарий

        // Создаем менеджер №1 и файл сохранения
        File file = new File("src/ru/yandex/javacource/brykalov/schedule/save/file.csv");
        FileBackedTaskManager fileBackedTaskManager1 = new FileBackedTaskManager(file);

        // Создаем задачи разных типов в менеджере №1:
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

        // Создаем менеджер №2 из файла сохранения
        FileBackedTaskManager fileBackedTaskManager2 = new FileBackedTaskManager(file);

        // Проверяем, что все задачи из менеджера №1 есть в менеджере №2
        System.out.println(fileBackedTaskManager2.getTaskList());
        System.out.println(fileBackedTaskManager2.getEpicList());
        System.out.println(fileBackedTaskManager2.getSubtaskList());

        // Удаляем все задачи
        fileBackedTaskManager1.deleteAllTasks();
        fileBackedTaskManager1.deleteAllEpics();
        fileBackedTaskManager1.deleteAllSubtasks();
    }
}
