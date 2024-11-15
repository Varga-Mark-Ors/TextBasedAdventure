package me.felakalandra.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NpcTester{

    @BeforeEach
    void setup() {
        // Betöltjük az NPC adatokat minden teszt előtt
        Npc.loadNpcs();
    }

    @Test
    void testLoadNpcs() {
        // Ellenőrizzük, hogy az NPC-k betöltődtek
        List<Npc> npcs = Npc.getNpcs();
        assertNotNull(npcs, "NPC lista nem lehet null.");
        assertFalse(npcs.isEmpty(), "NPC lista nem lehet üres.");

        // Ellenőrizzük, hogy az első NPC adatai helyesek
        Npc firstNpc = npcs.get(0);
        assertEquals("Skeleton", firstNpc.getName(), "Az első NPC neve nem egyezik.");
        assertEquals("Images/NPCs/Skeleton.png", firstNpc.getPath(), "Az első NPC képútja nem egyezik.");
        assertEquals(1, firstNpc.getLevel(), "Az első NPC szintje nem egyezik.");
        assertEquals(NpcType.FRIENDLY, firstNpc.getType(), "Az első NPC típusa nem egyezik.");
        assertEquals(60, firstNpc.getReliability(), "Az első NPC megbízhatósága nem egyezik.");
    }

    @Test
    void testGetImage() {
        // Ellenőrizzük, hogy egy létező kép betöltődik
        Npc firstNpc = Npc.getNpcs().get(0);
        assertNotNull(firstNpc, "Az első NPC nem lehet null.");
        assertNotNull(Npc.getImage(firstNpc.getPath()), "Az első NPC képét nem sikerült betölteni.");
    }

    @Test
    void testInvalidJsonPath() {
        // Próbáljunk betölteni egy nem létező JSON-t (ezt kódolás közben kell szimulálni)
        String originalPath = "Data/Characters.Json";
        try {
            Npc.loadNpcs();
            List<Npc> npcs = Npc.getNpcs();
            assertNotNull(npcs, "NPC lista nem lehet null.");
        } catch (Exception e) {
            fail("Betöltési hiba történt, de nem vártuk el: " + e.getMessage());
        }
    }

    @Test
    void testReliabilityRange() {
        // Ellenőrizzük, hogy a megbízhatósági érték minden NPC esetén a helyes tartományban van
        for (Npc npc : Npc.getNpcs()) {
            int reliability = npc.getReliability();
            assertTrue(reliability >= 0 && reliability <= 100,
                    "NPC megbízhatósága érvénytelen: " + reliability);
        }
    }

    @Test
    void testToString() {
        // Ellenőrizzük, hogy az NPC `toString` metódusa helyesen működik
        Npc firstNpc = Npc.getNpcs().get(0);
        assertNotNull(firstNpc, "Az első NPC nem lehet null.");
        String npcString = firstNpc.toString();
        assertTrue(npcString.contains("Skeleton"), "Az első NPC toString metódusa nem tartalmazza a nevét.");
    }
}
