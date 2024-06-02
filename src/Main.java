public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        taskManager.newTask("Простая задача 1",
                            "Описание простой задачи 1", ProgressStage.NEW);
        taskManager.newTask("Простая задача 2",
                            "Описание простой задачи 2", ProgressStage.NEW);
        taskManager.newEpic("Сложная задача 1",
                            "Описание сложной задачи 1");
        taskManager.newSubtask("Подзадача 1 сложной задачи 1",
                            "Описание подзадачи 1 сложной задачи 1",
                            2, ProgressStage.NEW);
        taskManager.newSubtask("Подзадача 2 сложной задачи 1",
                            "Описание подзадачи 2 сложной задачи 1",
                            2, ProgressStage.NEW);
        taskManager.newEpic("Сложная задача 2",
                            "Описание сложной задачи 2");
        taskManager.newSubtask("Подзадача 1 сложной задачи 2",
                            "Описание подзадачи 1 сложной задачи 2",
                            5, ProgressStage.NEW);
        taskManager.newEpic("Сложная задача 3",
                            "Описание сложной задачи 3");

        System.out.println(taskManager.getTaskHashMap());
        System.out.println(taskManager.getEpicHashMap());
        System.out.println(taskManager.getSubtaskHashMap());

        taskManager.newSubtask("Подзадача 2 сложной задачи 2",
                "Описание подзадачи 2 сложной задачи 2",
                5, ProgressStage.IN_PROGRESS);

        taskManager.deleteTaskById(0);
        taskManager.deleteEpicById(7);

        System.out.println();
        System.out.println(taskManager.getTaskHashMap());
        System.out.println(taskManager.getEpicHashMap());
        System.out.println(taskManager.getSubtaskHashMap());
    }
}
