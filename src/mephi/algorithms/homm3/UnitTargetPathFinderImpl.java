package mephi.algorithms.homm3;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.EdgeDistance;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {

    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    /**
     * Метод определяет кратчайший маршрут между атакующим и атакуемым юнитом.
     * Сложность: O((WIDTH * HEIGHT) * log(WIDTH * HEIGHT)), где WIDTH — ширина игрового поля, HEIGHT — высота игрового поля.
     *
     * @param attackUnit      юнит, который атакует
     * @param targetUnit      юнит, который подвергается атаке
     * @param existingUnitList список всех существующих юнитов
     * @return список объектов Edge, представляющих кратчайший путь, или пустой список, если путь не найден
     */
    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        int[][] distance = initializeDistanceArray();
        boolean[][] visited = new boolean[WIDTH][HEIGHT];
        Edge[][] previous = new Edge[WIDTH][HEIGHT];
        Set<String> occupiedCells = getOccupiedCells(existingUnitList, attackUnit, targetUnit);
        PriorityQueue<EdgeDistance> queue = new PriorityQueue<>(Comparator.comparingInt(EdgeDistance::getDistance));

        initializeStartPoint(attackUnit, distance, queue);

        // Выполняем алгоритм поиска пути (Dijkstra's algorithm)
        while (!queue.isEmpty()) {
            EdgeDistance current = queue.poll();
            if (visited[current.getX()][current.getY()]) continue;
            visited[current.getX()][current.getY()] = true;

            if (isTargetReached(current, targetUnit)) {
                break;
            }

            exploreNeighbors(current, occupiedCells, distance, previous, queue);
        }

        return constructPath(previous, attackUnit, targetUnit);
    }

    /**
     * Инициализация массива расстояний.
     * Сложность: O(WIDTH * HEIGHT).
     *
     * @return двумерный массив расстояний, заполненный значением Integer.MAX_VALUE
     */
    private int[][] initializeDistanceArray() {
        int[][] distance = new int[WIDTH][HEIGHT];
        for (int[] row : distance) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        return distance;
    }

    /**
     * Получение занятых клеток.
     * Сложность: O(k), где k — количество существующих юнитов.
     *
     * @param existingUnitList список всех существующих юнитов
     * @param attackUnit       атакующий юнит
     * @param targetUnit       атакуемый юнит
     * @return множество строковых представлений координат занятых клеток
     */
    private Set<String> getOccupiedCells(List<Unit> existingUnitList, Unit attackUnit, Unit targetUnit) {
        Set<String> occupiedCells = new HashSet<>();
        for (Unit unit : existingUnitList) {
            if (unit.isAlive() && unit != attackUnit && unit != targetUnit) {
                occupiedCells.add(unit.getxCoordinate() + "," + unit.getyCoordinate());
            }
        }
        return occupiedCells;
    }

    /**
     * Инициализация начальной точки.
     * Сложность: O(1).
     *
     * @param attackUnit атакующий юнит
     * @param distance   массив расстояний
     * @param queue      очередь приоритетов для алгоритма Дейкстры
     */
    private void initializeStartPoint(Unit attackUnit, int[][] distance, PriorityQueue<EdgeDistance> queue) {
        int startX = attackUnit.getxCoordinate();
        int startY = attackUnit.getyCoordinate();
        distance[startX][startY] = 0;
        queue.add(new EdgeDistance(startX, startY, 0));
    }

    /**
     * Проверка достижения цели.
     * Сложность: O(1).
     *
     * @param current    текущая клетка
     * @param targetUnit атакуемый юнит
     * @return true, если цель достигнута, иначе false
     */
    private boolean isTargetReached(EdgeDistance current, Unit targetUnit) {
        return current.getX() == targetUnit.getxCoordinate() && current.getY() == targetUnit.getyCoordinate();
    }

    /**
     * Исследование соседних клеток текущей клетки.
     * Сложность: O(d), где d — количество направлений (4 для этой задачи).
     *
     * @param current         текущая клетка
     * @param occupiedCells   множество занятых клеток
     * @param distance        массив расстояний
     * @param previous        массив предыдущих клеток для восстановления пути
     * @param queue          очередь приоритетов для алгоритма Дейкстры
     */
    private void exploreNeighbors(EdgeDistance current, Set<String> occupiedCells, int[][] distance, Edge[][] previous, PriorityQueue<EdgeDistance> queue) {
        for (int[] dir : DIRECTIONS) {
            int neighborX = current.getX() + dir[0];
            int neighborY = current.getY() + dir[1];

            if (isValid(neighborX, neighborY, occupiedCells)) {
                int newDistance = distance[current.getX()][current.getY()] + 1;
                if (newDistance < distance[neighborX][neighborY]) {
                    distance[neighborX][neighborY] = newDistance;
                    previous[neighborX][neighborY] = new Edge(current.getX(), current.getY());
                    queue.add(new EdgeDistance(neighborX, neighborY, newDistance));
                }
            }
        }
    }

    /**
     * Проверка валидности координат.
     * Сложность: O(1).
     *
     * @param x             координата x
     * @param y             координата y
     * @param occupiedCells множество занятых клеток
     * @return true, если координаты валидны и клетка не занята, иначе false
     */
    private boolean isValid(int x, int y, Set<String> occupiedCells) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT && !occupiedCells.contains(x + "," + y);
    }

    /**
     * Построение пути.
     * Сложность: O(p), где p — длина пути.
     *
     * @param previous    массив предыдущих клеток для восстановления пути
     * @param attackUnit  атакующий юнит
     * @param targetUnit  атакуемый юнит
     * @return список объектов Edge, представляющих кратчайший путь, или пустой список, если путь не найден
     */
    private List<Edge> constructPath(Edge[][] previous, Unit attackUnit, Unit targetUnit) {
        List<Edge> path = new ArrayList<>();
        int pathX = targetUnit.getxCoordinate();
        int pathY = targetUnit.getyCoordinate();

        while (pathX != attackUnit.getxCoordinate() || pathY != attackUnit.getyCoordinate()) {
            path.add(new Edge(pathX, pathY));
            Edge prev = previous[pathX][pathY];
            if (prev == null) return Collections.emptyList(); // Если путь не найден
            pathX = prev.getX();
            pathY = prev.getY();
        }

        path.add(new Edge(attackUnit.getxCoordinate(), attackUnit.getyCoordinate()));
        Collections.reverse(path);
        return path;
    }
}