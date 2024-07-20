package ru.yandex.javacource.brykalov.schedule;

import ru.yandex.javacource.brykalov.schedule.manager.FileBackedTaskManager;
import ru.yandex.javacource.brykalov.schedule.task.*;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        File file = new File("src/ru/yandex/javacource/brykalov/schedule/save/file.csv");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        // Создаем задачи:
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        int epicId1 = fileBackedTaskManager.addNewEpic(epic1);

        Epic epic2 = new Epic("Эпик2", "Описание эпика2");
        int epicId2 = fileBackedTaskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", epicId2, Status.NEW);
        int subtaskId1 = fileBackedTaskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", epicId2, Status.NEW);
        int subtaskId2 = fileBackedTaskManager.addNewSubtask(subtask2);

        Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи3", epicId2, Status.NEW);
        int subtaskId3 = fileBackedTaskManager.addNewSubtask(subtask3);

        Task task1 = new Task("Задача", "Описание задачи", Status.NEW);
        int taskId1 = fileBackedTaskManager.addNewTask(task1);

        // Проверяем быстродействие
//        int numberOfTasks = 1_000_000;
//        Task[] array = new Task[numberOfTasks];
//        for (int i = 0; i < numberOfTasks; i++) {
//            array[i] = new Task("Задача", "Описание задачи", Status.NEW);
//            taskManager.addNewTask(array[i]);
//            taskManager.getTask(array[i].getId());
//        }
//        long t1 = System.nanoTime();
//        taskManager.getTask(numberOfTasks / 2);
//        taskManager.deleteTaskById(numberOfTasks / 2);
//        long t2 = System.nanoTime();
//        System.out.println("Затраченное время на просмотр и удаление одной из N задач (в т.ч. изменение истории), наносекунд: " + (t2 - t1));
//        System.out.println("Количество задач N = " + numberOfTasks);

        // Проверяем чтение из файла
//        fileBackedTaskManager.readSaveFile();
//        System.out.println(fileBackedTaskManager.getTaskList());
//        System.out.println(fileBackedTaskManager.getEpicList());
//        System.out.println(fileBackedTaskManager.getSubtaskList());
    }
}
