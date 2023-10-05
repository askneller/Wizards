package com.example.wizards;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Util {

    private static final Logger logger = LogUtils.getLogger();

    public static void printStackTrace(int lines) {
        logger.info("Here");
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        System.out.println("\nTrace");
        int len = lines;
        if (stackTrace.length < len)
            len = stackTrace.length;
        for (int i = 0; i < len; i++) {
            System.out.println(stackTrace[i]);
        }
        System.out.println("\n");
    }

    public static Entity findEntityAtPos(BlockPos pos, Level level, double distance, String uuid) {
        logger.info("findEntityAtPos pos {}, dist {}", pos, distance);
        Vec3 vec = new Vec3(pos.getX(), pos.getY(), pos.getZ());
        AABB aabb = AABB.ofSize(vec, distance, distance, distance);
        logger.info("aabb {}", aabb);
        List<Entity> entities =
                level.getEntities(null, aabb);
        logger.info("Entities: {}", entities);
        Entity entity = entities.stream()
//                .filter(e -> e.getStringUUID().equals(uuid))
                .findFirst()
                .orElse(null);
        logger.info("entity {}", entity);
        return entity;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);
        logger.info("list {}", list);

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

}
