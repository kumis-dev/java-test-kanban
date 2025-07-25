import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TaskManager {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Task Manager Menu ---");
            System.out.println("1. Добавить обычную задачу");
            System.out.println("2. Посмотреть все обычные задачи");
            System.out.println("3. Удалить обычную задачу по ID");
            System.out.println("4. Добавить эпик");
            System.out.println("5. Удалить эпик по ID");
            System.out.println("6. Добавить подзадачу к эпику");
            System.out.println("7. Удалить подзадачу по ID");
            System.out.println("8. Посмотреть все эпики и подзадачи");
            System.out.println("0. Выход");
            System.out.print("Выбор: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> {
                    System.out.print("Введите название задачи: ");
                    String title = scanner.nextLine();
                    System.out.print("Введите описание: ");
                    String desc = scanner.nextLine();
                    Task task = new Task(title, desc, manager.generateId(), TasksStatus.NEW);
                    manager.addTask(task);
                    System.out.println("Задача добавлена с ID: " + task.getId());
                }
                case "2" -> {
                    for (Task task : manager.getAllTasks()) {
                        System.out.println(task);
                    }
                }
                case "3" -> {
                    System.out.print("Введите ID задачи для удаления: ");
                    int id = Integer.parseInt(scanner.nextLine());
                    manager.removeTask(id);
                    System.out.println("Удалено (если существовало).");
                }
                case "4" -> {
                    System.out.print("Название эпика: ");
                    String title = scanner.nextLine();
                    System.out.print("Описание: ");
                    String desc = scanner.nextLine();
                    Epic epic = new Epic(title, desc, manager.generateId(), TasksStatus.NEW);
                    manager.addEpicTask(epic);
                    System.out.println("Эпик добавлен с ID: " + epic.getId());
                }
                case "5" -> {
                    System.out.print("Введите ID эпика для удаления: ");
                    int id = Integer.parseInt(scanner.nextLine());
                    manager.removeEpicTask(id);
                    System.out.println("Удалено (если существовало).");
                }
                case "6" -> {
                    System.out.print("ID эпика: ");
                    int epicId = Integer.parseInt(scanner.nextLine());
                    if (manager.getEpicTask(epicId) == null) {
                        System.out.println("❌ Эпик с таким ID не найден. Сначала добавьте эпик.");
                        break;
                    }
                    System.out.print("Название подзадачи: ");
                    String title = scanner.nextLine();
                    System.out.print("Описание: ");
                    String desc = scanner.nextLine();
                    SubTask subTask = new SubTask(title, desc, manager.generateId(), TasksStatus.NEW, epicId);
                    manager.addSubTask(subTask);
                    System.out.println("Подзадача добавлена с ID: " + subTask.getId());
                }
                case "7" -> {
                    System.out.print("Введите ID подзадачи для удаления: ");
                    int id = Integer.parseInt(scanner.nextLine());
                    manager.removeSubTask(id);
                    System.out.println("Удалено (если существовало).");
                }
                case "8" -> {
                    System.out.println("--- Эпики и их подзадачи ---");
                    for (Epic epic : manager.getAllEpics()) {
                        System.out.println(epic);
                        System.out.println("  Подзадачи:");
                        for (SubTask sub : manager.getEpicSubtasks(epic.getId())) {
                            System.out.println("    " + sub);
                        }
                    }
                }
                case "0" -> {
                    System.out.println("Выход");
                    return;
                }
                default -> System.out.println("Неверный выбор.");
            }
        }
    }
    // переменные не должны быть статическими

    // общие задачи
    private HashMap<Integer, Task> tasks = new HashMap<>();
    // эпики - главные задачи
    private HashMap<Integer, Epic> epics = new HashMap<>();
    // саб таски - подзадачи
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();

    // переменная счетчика будет только одна
    private int id = 1; // для всех задач общая

    public void addTask(Task task) {
        // устанавливаем только что сгенерированный айди
        task.setId(generateId());
        // кладем его и саму задачу в хеш мап по обычным таскам
        tasks.put(task.getId(), task);
    }

    public Task getTask(int taskId) {
        return tasks.get(taskId);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values()); // передаем в ArrayList коллекцию значений HashMap tasks
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values()); // передаем в ArrayList коллекцию значений HashMap epics
    }

    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values()); // передаем в ArrayList коллекцию значений HashMap subTasks
    }

    // получение всех саб тасков определенного эпика
    public ArrayList<SubTask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        // здесь будем хранить сами саб таски эпика что получили
        ArrayList<SubTask> subTasks = new ArrayList<>(); // затеняем переменную класса на уровне выше - локальной
        if (epic != null) {
            ArrayList<Integer> subTasksIds = epic.getSubTasks();
            for (Integer subTaskId : subTasksIds) {
                subTasks.add(getSubTask(subTaskId));
                // вытаскиваем из общего пула саб тасков и пуляем в список саб тасков эпика
            }
        }
        return subTasks;
    }

    public void removeTask(int taskId) {
        if (tasks.containsKey(taskId))
            tasks.remove(taskId);
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void updateTask(Task task) {
        if (task != null)
            tasks.put(task.getId(), task);
    }

    // сама логика внесения задач
    // методы не должны передаваться статически и туда передается именно сам обьект по тз
    public void addEpicTask(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    public void removeEpicTask(int epicTaskId) {
        Epic epic = epics.get(epicTaskId); // получаем кокретный эпик, чтобы удалить и все его саб таски
        if (epic != null) {
            ArrayList<Integer> subTaskIds = epic.getSubTasks(); // получаем айди саб тасков эпика
            // проходимся циклом и удаляем каждый саб таск из хеш мапа саб тасков эпика
            for (Integer subTaskId : subTaskIds) {
                subTasks.remove(subTaskId);
            }
            epics.remove(epicTaskId);
        }
    }

    public void removeAllEpics() {
        subTasks.clear();
        epics.clear();
    }

    public void removeAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear(); // получаем айди подзадач и очищаем их
            epic.setTasksStatus(TasksStatus.NEW);
        }
    }

    public void addSubTask(SubTask subTask) {
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        // получаем айди эпика, к которому привязан саб таск
        int epicId = subTask.getEpicId();
        // получаем сам эпик саб таска
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        ArrayList<Integer> subTasks = epic.getSubTasks();
        subTasks.add(subTask.getId()); // здесь уже добавим в список саб тасков нужный айди саб таска
    }

    public void removeSubTask(int subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        if (subTask != null) {
            // удаляем и из эпика тоже
            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                epic.getSubTasks().remove(Integer.valueOf(subTaskId));
                epic.setTasksStatus(changeStatus(subTask.getEpicId()));
            }
            subTasks.remove(subTaskId);
        }
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        epic.setTasksStatus(changeStatus(epic.getId())); // присваиваем статус эпику
    }

    public void updateSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        // исправлено: пересчитываем статус эпика
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            epic.setTasksStatus(changeStatus(subTask.getEpicId()));
        }
    }

    public SubTask getSubTask(int subTaskId) {
        return subTasks.get(subTaskId);
    }

    public Epic getEpicTask(int epicTaskId) {
        return epics.get(epicTaskId); // возвращаем обьект эпика по его id
    }

    // по сути не должно касаться пользователя напрямую, если сделать метод приватным
    // статус эпика зависит от всех подзадач сразу
    private TasksStatus changeStatus(int epicTaskId) {
        int newCount = 0;
        int inProgressCount = 0;
        int doneCount = 0;

        Epic epic = epics.get(epicTaskId); // получаем обьект эпика
        if (epic == null) {
            return TasksStatus.NEW;
        }
        ArrayList<Integer> subTasksIds = epic.getSubTasks();
        if (subTasksIds.isEmpty())
            return TasksStatus.NEW;
        for (Integer subTaskId : subTasksIds) {
            SubTask currentSubTask = subTasks.get(subTaskId); // берем подзадачу под ее айди
            if (currentSubTask == null) continue;
            TasksStatus status = currentSubTask.getTasksStatus();
            switch (status) {
                case NEW -> newCount++;
                case IN_PROGRESS -> inProgressCount++;
                case DONE -> doneCount++;
            }
        }
        if (subTasksIds.size() == newCount) {
            return TasksStatus.NEW;
        } else if (subTasksIds.size() == doneCount) {
            return TasksStatus.DONE;
        } else return TasksStatus.IN_PROGRESS;
    }

    private int generateId() {
        return id++;
    }
}
