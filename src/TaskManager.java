import java.util.HashMap;

public class TaskManager {
    private HashMap <Integer, Task> taskHashMap = new HashMap<>();
    private HashMap <Integer, Epic> epicHashMap = new HashMap<>();
    private HashMap <Integer, Subtask> subtaskHashMap = new HashMap<>();
    private int tasksCounter = 0;

    public HashMap<Integer, Task> getTaskHashMap() {
        return taskHashMap;
    }

    public HashMap<Integer, Epic> getEpicHashMap() {
        return epicHashMap;
    }

    public HashMap<Integer, Subtask> getSubtaskHashMap() {
        return subtaskHashMap;
    }

    public void deleteAllTasks() {
        taskHashMap.clear();
    }

    public void deleteAllEpics() {
        epicHashMap.clear();
    }

    public void deleteAllSubtasks() {
        subtaskHashMap.clear();
    }

    public Task getTask(int id) {
        return taskHashMap.get(id);
    }

    public Task getEpic(int id) {
        return epicHashMap.get(id);
    }

    public Task getSubtask(int id) {
        return subtaskHashMap.get(id);
    }

    public void newTask(String name, String description, ProgressStage progressStage) {
        int id = tasksCounter;
        tasksCounter++;
        Task task = new Task(name, description, id, progressStage);
        taskHashMap.put(id, task);
    }

    public void newEpic(String name, String description) {
        int id = tasksCounter;
        tasksCounter++;
        Epic epic = new Epic(name, description, id);
        epicHashMap.put(id, epic);
    }

    public void newSubtask(String name, String description, int epicId, ProgressStage progressStage) {
        int id = tasksCounter;
        tasksCounter++;
        Subtask subtask = new Subtask(name, description, id, epicId, progressStage);
        subtaskHashMap.put(id, subtask);
        epicHashMap.get(epicId).updateSubtaskHashMap(subtask);
        updateEpic(epicHashMap.get(epicId));
    }

    public void updateTask(Task task) {
        taskHashMap.replace(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        String newEpicName = epic.getName();
        String newEpicDescription = epic.getDescription();
        int newEpicId = epic.getId();
        Epic newEpic = new Epic(newEpicName, newEpicDescription, newEpicId, calculateEpicStatus(epic.getId()));
        newEpic.setSubtaskHashMap(epic.getSubtaskHashMap());
        epicHashMap.replace(epic.getId(), newEpic);

//      На этом моменте долго спорил с наставником, как именно должно происходить обновление статуса Epic.
//      Я, прочитав ТЗ n-ное количество раз, понял его так, что объекты Task (и наследники) не могут иметь
//      методов, которые изменяют статус (например .setStatus(newStatus)).
//      Таким образом поле "статус" инициализируется только при создании объекта и не изменяется, а только
//      заменяется новым объектом с новым статусом. Что я и реализовал в этом методе.
//      Наставник, в свою очередь, уверен, что необходимо вызывать .setStatus(newStatus) из TaskManager.
//      Оставил для первого ревью свою точку зрения на Ваш "суд".
//      Ниже цитата из ТЗ:
//      "Фраза «информация приходит вместе с информацией по задаче» означает,
//      что не существует отдельного метода, который занимался бы только обновлением статуса задачи.
//      Вместо этого статус задачи обновляется вместе с полным обновлением задачи."
    }

    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        subtaskHashMap.replace(id, subtask);
        Subtask SubtaskToBeReplaced = subtaskHashMap.get(id);
        epicHashMap.get(SubtaskToBeReplaced.getEpicId()).replaceSubtask(id, subtask);
    }

    public void deleteTaskById(int id) {
        taskHashMap.remove(id);
    }

    public void deleteEpicById(int id) {
        epicHashMap.remove(id);
    }

    public void deleteSubtaskById(int id) {
        Subtask SubtaskToBeDeleted = subtaskHashMap.get(id);
        subtaskHashMap.remove(id);
        epicHashMap.get(SubtaskToBeDeleted.getEpicId()).removeSubtask(id);
    }

    public HashMap<Integer, Subtask> getSubtasksFromEpicById(int id) {
        return epicHashMap.get(id).getSubtaskHashMap();
    }

    public ProgressStage calculateEpicStatus(int epicId) {
        HashMap<Integer, Subtask> subtaskHashMap = epicHashMap.get(epicId).getSubtaskHashMap();
        int numberOfNewTask = 0;
        int numberOfCompletedTask = 0;

        for (Subtask subtask : subtaskHashMap.values()) {
            if (subtask.getProgressStage() == ProgressStage.NEW) {
                numberOfNewTask++;
            }
            if (subtask.getProgressStage() == ProgressStage.DONE) {
                numberOfCompletedTask++;
            }
        }

        if (!subtaskHashMap.isEmpty() && numberOfCompletedTask == subtaskHashMap.size()) {
            return ProgressStage.DONE;
        } else if (subtaskHashMap.isEmpty() || (numberOfNewTask == subtaskHashMap.size())) {
            return ProgressStage.NEW;
        } else {
            return ProgressStage.IN_PROGRESS;
        }
    }



}
