package me.felakalandra.services;

import javafx.scene.image.Image;
import lombok.Getter;
import me.felakalandra.model.EnemyNpc;
import me.felakalandra.model.Npc;
import me.felakalandra.model.Protagonist;

import java.util.List;
import java.util.Random;

public class NpcService {
    @Getter
    private Npc currentNpc;
    @Getter
    private EnemyNpc currentEnemyNpc;

    private final Random random = new Random();

    /**
     * Selects a random NPC whose level is appropriate for the protagonist.
     */
    public Npc selectNpc(List<Npc> npcList, Protagonist protagonist) {
        if (npcList == null || npcList.isEmpty()) {
            return null; // No NPCs available
        }

        boolean correspondingLevel = false;

        while (!correspondingLevel) {
            int randomIndex = random.nextInt(npcList.size());
            Npc npc = npcList.get(randomIndex);

            if (npc.getLevel() <= protagonist.getLevel()) {
                correspondingLevel = true;
                currentNpc = npc;
            }
        }

        return currentNpc;
    }

    /**
     * Selects a random enemy NPC whose level is appropriate for the protagonist.
     */
    public EnemyNpc selectEnemyNpc(List<EnemyNpc> enemyList, Protagonist protagonist) {
        if (enemyList == null || enemyList.isEmpty()) {
            return null; // No enemies available
        }

        boolean correspondingLevel = false;

        while (!correspondingLevel) {
            int randomIndex = random.nextInt(enemyList.size());
            EnemyNpc enemyNpc = enemyList.get(randomIndex);

            if (enemyNpc.getLevel() <= protagonist.getLevel()) {
                correspondingLevel = true;
                currentEnemyNpc = enemyNpc;
            }
        }

        return currentEnemyNpc;
    }

    /**
     * Loads the image for an NPC.
     */
    public Image loadNpcImage(String imagePath) {
        return (imagePath != null) ? Npc.getImage(imagePath) : null;
    }

    /**
     * Loads the image for an enemy NPC.
     */
    public Image loadEnemyNpcImage(String imagePath) {
        return (imagePath != null) ? EnemyNpc.getImage(imagePath) : null;
    }
}
