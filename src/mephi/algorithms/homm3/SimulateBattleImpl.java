package mephi.algorithms.homm3;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.*;

public class SimulateBattleImpl implements SimulateBattle {

    private PrintBattleLog printBattleLog;

    /**
     * Метод осуществляет симуляцию боя между армией игрока и армией компьютера.
     * Сложность: O(n^2 * log n), где n — общее количество юнитов в армии.
     *
     * @param playerArmy    объект армии игрока, содержащий список её юнитов
     * @param computerArmy  объект армии компьютера, содержащий список её юнитов
     */
    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        Set<Unit> playerUnits = new HashSet<>(playerArmy.getUnits());
        Set<Unit> computerUnits = new HashSet<>(computerArmy.getUnits());

        while (!playerUnits.isEmpty() && !computerUnits.isEmpty()) {
            executeAttacks(playerUnits, computerUnits);
            executeAttacks(computerUnits, playerUnits);
        }
    }

    /**
     * Выполняет атаки для набора атакующих юнитов на набор защищающихся юнитов.
     * Сложность: O(n * log n), где n — количество юнитов в наборе.
     *
     * @param attackingUnits  набор атакующих юнитов
     * @param defendingUnits  набор защищающихся юнитов
     */
    private void executeAttacks(Set<Unit> attackingUnits, Set<Unit> defendingUnits) throws InterruptedException {
        List<Unit> sortedAttackingUnits = new ArrayList<>(attackingUnits);
        // Сортируем юниты по значению атаки (O(n log n))
        sortedAttackingUnits.sort(Comparator.comparingInt(Unit::getBaseAttack).reversed());

        for (Unit attackingUnit : sortedAttackingUnits) {
            if (!attackingUnit.isAlive()) {
                attackingUnits.remove(attackingUnit); // Удаляем мертвых юнитов
                continue;
            }

            Unit target = attackingUnit.getProgram().attack();
            if (target != null && defendingUnits.contains(target)) {
                printBattleLog.printBattleLog(attackingUnit, target);
                if (!target.isAlive()) {
                    defendingUnits.remove(target); // Удаляем мертвых юнитов из обороны
                }
            }
        }
    }
}
