package com.zenya.aurora.util.ext;

import com.google.common.base.Enums;
import com.zenya.aurora.Aurora;
import com.zenya.aurora.api.ParticleFactory;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <b>ZParticle</b> - The most unique particle animation, text and image renderer.<br>
 * This utility uses {@link ZParticleDisplay} for cleaner code. This class adds the ability to define the optional values for spawning particles.
 * <p>
 * While this class provides many methods with options to spawn unique shapes, it's recommended to make your own shapes by copying the code from these methods.<br>
 * There are some shapes such as the magic circles, illuminati and the explosion method that mainly focus on using the other methods to create a new shape.
 * <p>
 * Note that some of the values for some methods are extremely sensitive and can change the shape significantly by adding small numbers such as 0.5 Yes, Chaos theory.<br>
 * Most of the method parameters have a recommended value set to start with. Note that these values are there to show how the intended normal shape looks like before you start changing the values.<br>
 * All the parameters and return types are not null.
 * <p>
 * It's recommended to use low particle counts. In most cases, increasing the rate is better than increasing the particle count. Most of the methods provide an option called "rate" that you can get more particles by decreasing the distance between each point the particle spawns. Rates for methods act in two ways. They're either for straight lines like the polygon method which lower rate means more points (usually 0.1 is used) and shapes that are curved such as the circle method, which higher rate means more points (these types of rates usually start from 30).<br>
 * Most of the {@link ZParticleDisplay} used in this class are intended to have 1 particle count and 0 xyz offset and speed.
 * <p>
 * Particles are rendered as front-facing 2D sprites, meaning they always face the player. Minecraft clients will automatically clear previous particles if you reach the limit. Particle range is 32 blocks. Particle count limit is 16,384. Particles are not entities.
 * <p>
 * All the methods and operations used in this class are thread-safe. Most of the methods do not run asynchronous by default. If you're doing a resource intensive operation it's recommended to either use {@link CompletableFuture#runAsync(Runnable)} or {@link BukkitRunnable#runTaskTimerAsynchronously(Plugin, long, long)} for smoothly animated shapes. For huge animations you can use splittable tasks. https://www.spigotmc.org/threads/409003/ By "huge", the algorithm used to generate locations is considered. You should not spawn a lot of particles at once. This will cause FPS drops for most of the clients, unless they have a powerful PC.
 * <p>
 * You can test your 2D shapes at <a href="https://www.desmos.com/calculator">Desmos</a><br>
 * Stuff you can do with with
 * <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html">Java {@link Math}</a><br>
 * Getting started with <a href="https://www.spigotmc.org/wiki/vector-programming-for-beginners/">Vectors</a><br>
 * Extra stuff if you want to read more: https://www.spigotmc.org/threads/418399/<br>
 * Particles: https://minecraft.gamepedia.com/Particles<br>
 *
 * @author Crypto Morin, Zenya4 (modded)
 * @version 4.1.2 (ZParticle 1.0.1)
 * @see ZParticleDisplay
 * @see Particle
 * @see Location
 * @see Vector
 */
public class ZParticle implements ParticleFactory {

    /**
     * An optimized and stable way of getting particles for cross-version support.
     *
     * @param particle the particle name.
     * @return a particle that matches the specified name.
     * @since 1.0.0
     */
    public static Particle getParticle(String particle) {
        return Enums.getIfPresent(Particle.class, particle).orNull();
    }

    /**
     * Get a random particle from a list of particle names.
     *
     * @param particles the particles name.
     * @return a random particle from the list.
     * @since 1.0.0
     */
    public static Particle randomParticle(String... particles) {
        int rand = randInt(0, particles.length - 1);
        return getParticle(particles[rand]);
    }

    /**
     * A thread safe way to get a random double in a range.
     *
     * @param min the minimum number.
     * @param max the maximum number.
     * @return a random number.
     * @see #randInt(int, int)
     * @since 1.0.0
     */
    public static double random(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    /**
     * A thread safe way to get a random integer in a range.
     *
     * @param min the minimum number.
     * @param max the maximum number.
     * @return a random number.
     * @see #random(double, double)
     * @since 1.0.0
     */
    public static int randInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /**
     * Generate a random RGB color for particles.
     *
     * @return a random color.
     * @since 1.0.0
     */
    public static Color randomColor() {
        ThreadLocalRandom gen = ThreadLocalRandom.current();
        int randR = gen.nextInt(0, 256);
        int randG = gen.nextInt(0, 256);
        int randB = gen.nextInt(0, 256);

        return Color.fromRGB(randR, randG, randB);
    }

    /**
     * Generate a random colorized dust with a random size.
     *
     * @return a REDSTONE colored dust.
     * @since 1.0.0
     */
    public static Particle.DustOptions randomDust() {
        float size = randInt(5, 10) / 10f;
        return new Particle.DustOptions(randomColor(), size);
    }

    /**
     * Generate a random rotation axis
     *
     * @return a random char - x or y or z
     * @since ZParticle 1.0.1
     */
    private static char randomAxis(char c) {
        if (c == 'x' || c == 'y' || c == 'z') {
            return c;
        }

        char[] selection = new char[]{'x', 'y', 'z'};
        return selection[randInt(0, 2)];
    }

    /**
     * Rotates the given vector with the given rotation radians
     *
     * @param vec the vector to rotate.
     * @param ref the reference vector to rotate about.
     * @param angle angle in radians to rotate vector by.
     * @param axis axis to rotate vector about.
     * @return a rotated vector.
     * @since ZParticle 1.0.0
     */
    public static Vector rotateAbout(Vector vec, Vector ref, double angle, char axis) {
        if (angle == 0) {
            return vec;
        }

        final double sin = Math.sin(angle);
        final double cos = Math.cos(angle);
        final double[] coords = {
            vec.getX() - ref.getX(), vec.getY() - ref.getY(), vec.getZ() - ref.getZ()
        };
        double[][] rotMatrix;
        switch (axis) {
            case 'x' ->
                rotMatrix = new double[][]{
                    {1, 0, 0},
                    {0, cos, -sin},
                    {0, sin, cos},};
            case 'y' ->
                rotMatrix = new double[][]{
                    {cos, 0, sin},
                    {0, 0, -sin},
                    {-sin, 0, cos},};
            case 'z' ->
                rotMatrix = new double[][]{
                    {cos, -sin, 0},
                    {sin, cos, 0},
                    {0, 0, 0}
                };
            default -> {
                char[] selection = new char[]{'x', 'y', 'z'};
                return rotateAbout(vec, ref, angle, selection[randInt(0, 2)]);
            }
        }
        double[] result = new double[3];

        for (int i = 0; i < 3; i++) {
            result[i] = 0;
            for (int j = 0; j < 3; j++) {
                result[i] += rotMatrix[i][j] * coords[j];
            }
        }
        return new Vector(result[0] + ref.getX(), result[1] + ref.getY(), result[2] + ref.getZ());
    }

    //Begin actual particles
    //Documentation can be found in com.zenya.aurora.api.ParticleFactory
    @Override
    public BukkitTask createPoint(Location loc, int update, long duration, ZParticleDisplay display) {
        return new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks > duration) {
                    this.cancel();
                    return;
                }
                ticks += update;
                display.withLocation(loc).spawn();
            }
        }.runTaskTimerAsynchronously(Aurora.getInstance(), 0, update);
    }

    @Override
    public BukkitTask createInstantLine(Location start, Location end, double rate, int update, long duration, ZParticleDisplay display) {
        double x = end.getX() - start.getX();
        double y = end.getY() - start.getY();
        double z = end.getZ() - start.getZ();
        double length = Math.sqrt(NumberConversions.square(x) + NumberConversions.square(y) + NumberConversions.square(z));

        x /= length;
        y /= length;
        z /= length;
        double finalX = x;
        double finalY = y;
        double finalZ = z;
        return new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks > duration) {
                    this.cancel();
                    return;
                }
                ticks += update;

                for (double i = 0; i < length; i += rate) {
                    if (i > length) {
                        i = length;
                    }

                    display.withLocation(start).spawn(finalX * i, finalY * i, finalZ * i);
                }
            }
        }.runTaskTimerAsynchronously(Aurora.getInstance(), 0, update);
    }

    @Override
    public BukkitTask drawInstantLine(Player player, double length, double rate, int update, long duration, ZParticleDisplay display) {
        Location eye = player.getEyeLocation();
        return createInstantLine(eye, eye.clone().add(eye.getDirection().multiply(length)), rate, update, duration, display);
    }

    @Override
    public BukkitTask createLine(Location start, Location end, double rate, int update, long duration, ZParticleDisplay display) {
        double x = end.getX() - start.getX();
        double y = end.getY() - start.getY();
        double z = end.getZ() - start.getZ();
        double length = Math.sqrt(NumberConversions.square(x) + NumberConversions.square(y) + NumberConversions.square(z));

        x /= length;
        y /= length;
        z /= length;

        List<double[]> locs = new ArrayList<>();
        for (double i = 0; i < length; i += rate) {
            // Since the rate can be any number it's possible to get a higher number than
            // the length in the last loop.
            if (i > length) {
                i = length;
            }
            locs.add(new double[]{x * i, y * i, z * i});
        }
        return new BukkitRunnable() {
            int progress = 0;

            @Override
            public void run() {
                if (progress >= locs.size() - 1) {
                    this.cancel();
                    return;
                }

                //Leave particle point for <duration> ticks
                new BukkitRunnable() {
                    final int finalProgress = progress;
                    int ticks = 0;

                    @Override
                    public void run() {
                        if (ticks > duration) {
                            this.cancel();
                            return;
                        }
                        ticks += update;

                        display.withLocation(start).spawn(locs.get(finalProgress)[0], locs.get(finalProgress)[1], locs.get(finalProgress)[2]);
                    }
                }.runTaskTimerAsynchronously(Aurora.getInstance(), 0, update);

                progress += 1;
            }
        }.runTaskTimerAsynchronously(Aurora.getInstance(), 0, update);
    }

    @Override
    public BukkitTask drawLine(Player player, double length, double rate, int update, long duration, ZParticleDisplay display) {
        Location eye = player.getEyeLocation();
        return createLine(eye, eye.clone().add(eye.getDirection().multiply(length)), rate, update, duration, display);
    }

    @Override
    public BukkitTask createCube(Location center, double length, double rate, int update, long duration, double angle, char axis, ZParticleDisplay display) {
        final double size = length / 2;
        final double x = center.getX();
        final double y = center.getY();
        final double z = center.getZ();

        //Initialise positions
        final double[][] cubePos = {
            {x - size, y - size, z - size}, {x + size, y - size, z - size},
            {x + size, y + size, z - size}, {x - size, y + size, z - size},
            {x - size, y - size, z + size}, {x + size, y - size, z + size},
            {x + size, y + size, z + size}, {x - size, y + size, z + size}
        };

        //Rotate positions
        List<Location> locs = new ArrayList<>();
        angle = Math.toRadians(angle);
        axis = randomAxis(axis);
        for (double[] locPos : cubePos) {
            Vector rotated = rotateAbout(new Vector(locPos[0], locPos[1], locPos[2]), center.toVector(), angle, axis);
            locs.add(rotated.toLocation(center.getWorld()));
        }

        return new BukkitRunnable() {
            int ticks = 0;
            boolean isDone = false;

            @Override
            public void run() {
                if (ticks > duration) {
                    this.cancel();
                    return;
                }
                ticks += update;

                if (!isDone) {
                    for (int i = 0; i < locs.size(); i++) {
                        Location pos1 = locs.get(i);
                        Location pos2;
                        Location pos3;
                        if (i != 0 && (i + 1) % 4 == 0) {
                            //4->1, 8->5
                            pos2 = locs.get(i - 3);
                        } else {
                            //1->2->3->4, 5->6->7->8
                            pos2 = locs.get(i + 1);
                        }
                        if (i < 4) {
                            //1->5, 2->6, 3->7, 4->8
                            pos3 = locs.get(i + 4);
                        } else {
                            pos3 = null;
                        }
                        createInstantLine(pos1, pos2, rate, update, duration, display);
                        if (pos3 != null) {
                            createInstantLine(pos1, pos3, rate, update, duration, display);
                        }
                    }
                    isDone = true;
                }
            }
        }.runTaskTimerAsynchronously(Aurora.getInstance(), 0, update);
    }

    @Override
    public BukkitTask createRing(Location center, double radius, double rate, int update, long duration, double angle, char axis, ZParticleDisplay display) {
        final double x = center.getX();
        final double y = center.getY();
        final double z = center.getZ();

        //Initialise positions
        List<double[]> outline = new ArrayList<>();
        for (float i = 0f; i <= 360f; i += (rate * 45)) { //To standardise rate, a rate of 1 means 8 points
            double relX = radius * Math.sin(i);
            double relZ = radius * Math.cos(i);
            outline.add(new double[]{x + relX, y, z + relZ});
        }

        //Rotate positions
        List<Location> locs = new ArrayList<>();
        angle = Math.toRadians(angle);
        axis = randomAxis(axis);
        for (double[] locPos : outline) {
            Vector rotated = rotateAbout(new Vector(locPos[0], locPos[1], locPos[2]), center.toVector(), angle, axis);
            locs.add(rotated.toLocation(center.getWorld()));
        }

        return new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks > duration) {
                    this.cancel();
                    return;
                }
                ticks += update;

                for (Location loc : locs) {
                    display.withLocation(loc).spawn();
                }
            }
        }.runTaskTimerAsynchronously(Aurora.getInstance(), 0, update);
    }

    @Override
    public BukkitTask createCircle(Location center, double radius, double rate, int update, long duration, double angle, char axis, ZParticleDisplay display) {
        final double x = center.getX();
        final double y = center.getY();
        final double z = center.getZ();

        //Initialise positions
        List<double[]> circle = new ArrayList<>();
        for (double relX = -radius; relX < radius; relX += rate) {
            for (double relZ = -radius; relZ < radius; relZ += rate) {
                //Add only if point lies within circle
                if (NumberConversions.square(relX) + NumberConversions.square(relZ) <= NumberConversions.square(radius)) {
                    circle.add(new double[]{x + relX, y, z + relZ});
                }
            }
        }

        //Rotate positions
        List<Location> locs = new ArrayList<>();
        angle = Math.toRadians(angle);
        axis = randomAxis(axis);
        for (double[] locPos : circle) {
            Vector rotated = rotateAbout(new Vector(locPos[0], locPos[1], locPos[2]), center.toVector(), angle, axis);
            locs.add(rotated.toLocation(center.getWorld()));
        }

        return new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks > duration) {
                    this.cancel();
                    return;
                }
                ticks += update;

                for (Location loc : locs) {
                    display.withLocation(loc).spawn();
                }
            }
        }.runTaskTimerAsynchronously(Aurora.getInstance(), 0, update);
    }

    @Override
    public BukkitTask createSphere(Location center, double radius, double rate, int update, long duration, ZParticleDisplay display) {
        final double x = center.getX();
        final double y = center.getY();
        final double z = center.getZ();

        //Initialise positions
        List<double[]> sphere = new ArrayList<>();
        for (double relX = -radius; relX < radius; relX += rate) {
            for (double relY = -radius; relY < radius; relY += rate) {
                for (double relZ = -radius; relZ < radius; relZ += rate) {
                    //Add only if point lies within sphere
                    if (NumberConversions.square(relX) + NumberConversions.square(relY) + NumberConversions.square(relZ) <= NumberConversions.square(radius)) {
                        sphere.add(new double[]{x + relX, y + relY, z + relZ});
                    }
                }
            }
        }

        //Only add edges of the sphere
        List<Location> locs = new ArrayList<>();
        for (double[] locPos : sphere) {
            Location point = new Location(center.getWorld(), locPos[0], locPos[1], locPos[2]);
            if (point.distance(center) > radius - rate && point.distance(center) < radius + rate) {
                locs.add(point);
            }
        }

        return new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks > duration) {
                    this.cancel();
                    return;
                }
                ticks += update;

                for (Location loc : locs) {
                    display.withLocation(loc).spawn();
                }
            }
        }.runTaskTimerAsynchronously(Aurora.getInstance(), 0, update);
    }

    @Override
    public BukkitTask createWave(Location start, Location end, double rate, int update, long duration, double cycles, double multiplier, double angle, char axis, ZParticleDisplay display) {
        double x = end.getX() - start.getX();
        double y = end.getY() - start.getY();
        double z = end.getZ() - start.getZ();
        double length = Math.sqrt(NumberConversions.square(x) + NumberConversions.square(y) + NumberConversions.square(z));

        x /= length;
        y /= length;
        z /= length;

        //Initialise positions
        List<double[]> line = new ArrayList<>(); //Rotate about this line
        List<double[]> wave = new ArrayList<>();
        double theta = 0;
        for (double i = 0; i < length; i += rate) {
            line.add(new double[]{x * i, y * i, z * i});

            if (theta > 360) {
                theta = 0;
            }
            double tilt = multiplier * Math.sin(Math.toRadians(theta));
            wave.add(new double[]{
                x * i + tilt * Math.sin(NumberConversions.square(y) * Math.PI / 2),
                y * i + tilt * Math.cos(NumberConversions.square(y) * Math.PI / 2),
                z * i + tilt * Math.sin(NumberConversions.square(y) * Math.PI / 2)});

            theta += (cycles * 360) / (length / rate);
        }

        //Rotate positions
//    List<Location> locs = new ArrayList<>();
        angle = Math.toRadians(angle);
        axis = randomAxis(axis);
        for (int i = 0; i < wave.size(); i++) {
            /*Vector rotated = */
            rotateAbout(new Vector(wave.get(i)[0], wave.get(i)[1], wave.get(i)[2]), new Vector(line.get(i)[0],
                    line.get(i)[1], line.get(i)[2]), angle, axis);
//      locs.add(rotated.toLocation(start.getWorld()));
        }

        return new BukkitRunnable() {
            int progress = 0;

            @Override
            public void run() {
                if (progress >= wave.size() - 1) {
                    this.cancel();
                    return;
                }

                //Leave particle point for <duration> ticks
                new BukkitRunnable() {
                    final int finalProgress = progress;
                    int ticks = 0;

                    @Override
                    public void run() {
                        if (ticks > duration) {
                            this.cancel();
                            return;
                        }
                        ticks += update;

                        display.withLocation(start).spawn(wave.get(finalProgress)[0], wave.get(finalProgress)[1], wave.get(finalProgress)[2]);
                    }
                }.runTaskTimerAsynchronously(Aurora.getInstance(), 0, update);

                progress += 1;
            }
        }.runTaskTimerAsynchronously(Aurora.getInstance(), 0, update);
    }
}
