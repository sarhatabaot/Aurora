package com.zenya.aurora.api;

import com.zenya.aurora.util.ext.ZParticleDisplay;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public interface ParticleFactory {

    /**
     * Creates a ZParticleDisplay object from a Bukkit particle to be shown to specified players.
     *
     * @param particle the bukkit particle.
     * @param players an array of players the particles will be shown to.
     * @return ZParticleDisplay object to be used in the API for creating particle shapes.
     * @since ZParticle 1.0.1
     */
    static ZParticleDisplay toDisplay(Particle particle, Player... players) {
        return ZParticleDisplay.simple(particle, null, players);
    }

    /**
     * Creates a ZParticleDisplay object from a Bukkit particle to be shown to all players.
     *
     * @param particle the bukkit particle.
     * @return ZParticleDisplay object to be used in the API for creating particle shapes.
     * @since ZParticle 1.0.1
     */
    static ZParticleDisplay toDisplay(Particle particle) {
        return toDisplay(particle, Bukkit.getOnlinePlayers().toArray(new Player[0]));
    }

    /**
     * Spawn a simple particle point.
     *
     * @param loc location to spawn the particle at.
     * @param update how often to refresh the particle.
     * @param duration how long to display the particle for.
     * @param display ZParticleDisplay to display.
     * @return BukkitTask for an particle point
     * @since ZParticle 1.0.0
     */
    BukkitTask createPoint(Location loc, int update, long duration, ZParticleDisplay display);

    /**
     * Spawns an instantaneous line from a location to another. Tutorial: https://www.spigotmc.org/threads/176695/ This method is a modified version to get the best performance.
     *
     * @param start the starting point of the line.
     * @param end the ending point of the line.
     * @param rate the rate of points of the line. Lower values will spawn more particles.
     * @param update how often to refresh the line.
     * @param duration how long to display the line for.
     * @param display ZParticleDisplay to display.
     * @return BukkitTask for an instantaneous line
     * @see #drawInstantLine(Player, double, double, int, long, ZParticleDisplay)
     * @since 1.0.0
     */
    BukkitTask createInstantLine(Location start, Location end, double rate, int update, long duration, ZParticleDisplay display);

    /**
     * Draws an instantaneous line from the player's looking direction.
     *
     * @param player the reference location of the line direction.
     * @param length the length of the line.
     * @param rate the rate of points of the line. Lower values will spawn more particles.
     * @param update how often to refresh the line.
     * @param duration how long to display the line for.
     * @param display ZParticleDisplay to display.
     * @return BukkitTask for an instantaneous line
     * @see #createInstantLine(Location, Location, double, int, long, ZParticleDisplay)
     * @since 1.0.0
     */
    BukkitTask drawInstantLine(Player player, double length, double rate, int update, long duration, ZParticleDisplay display);

    /**
     * Spawns a moving line from a location to another.
     *
     * @param start the starting point of the line.
     * @param end the ending point of the line.
     * @param rate the rate of points of the line. Lower values will spawn more particles.
     * @param update how often to extend the line.
     * @param duration how long to display each particle in the line for.
     * @param display ZParticleDisplay to display.
     * @return BukkitTask for a moving line
     * @see #drawLine(Player, double, double, int, long, ZParticleDisplay)
     * @since ZParticle 1.0.0
     */
    BukkitTask createLine(Location start, Location end, double rate, int update, long duration, ZParticleDisplay display);

    /**
     * Draws a moving line from the player's looking direction.
     *
     * @param player the reference location of the line direction.
     * @param length the length of the line.
     * @param rate the rate of points of the line. Lower values will spawn more particles.
     * @param update how often to refresh the line.
     * @param duration how long to display each particle in the line for.
     * @param display ZParticleDisplay to display.
     * @return BukkitTask for a moving line
     * @see #createLine(Location, Location, double, int, long, ZParticleDisplay)
     * @since ZParticle 1.0.0
     */
    BukkitTask drawLine(Player player, double length, double rate, int update, long duration, ZParticleDisplay display);

    /**
     * Spawn a cube with the inner space and walls empty, leaving only the edges visible.
     *
     * @param center location to spawn the cube at.
     * @param length length of the cube.
     * @param rate the rate of points of the lines forming the cube. Lower values will spawn more particles.
     * @param update how often to refresh the cube.
     * @param duration how long to display the cube for.
     * @param angle rotation of the cube in degrees.
     * @param axis the axis of rotation for the cube.
     * @param display ZParticleDisplay to display.
     * @return BukkitTask for an empty cube
     * @since ZParticle 1.0.0
     */
    BukkitTask createCube(Location center, double length, double rate, int update, long duration, double angle, char axis, ZParticleDisplay display);

    /**
     * Spawn a hollow ring
     *
     * @param center location to spawn the ring at.
     * @param radius radius of the ring.
     * @param rate the rate of points forming the ring. Lower values will spawn more particles.
     * @param update how often to refresh the ring.
     * @param duration how long to display the ring for.
     * @param angle rotation of the ring in degrees.
     * @param axis the axis of rotation for the ring.
     * @param display ZParticleDisplay to display.
     * @return BukkitTask for a hollow ring
     * @since ZParticle 1.0.0
     */
    BukkitTask createRing(Location center, double radius, double rate, int update, long duration, double angle, char axis, ZParticleDisplay display);

    /**
     * Spawn a filled circle
     *
     * @param center location to spawn the circle at.
     * @param radius radius of the circle.
     * @param rate the rate of points forming the circle. Lower values will spawn more particles.
     * @param update how often to refresh the circle.
     * @param duration how long to display the circle for.
     * @param angle rotation of the circle in degrees.
     * @param axis the axis of rotation for the circle.
     * @param display ZParticleDisplay to display.
     * @return BukkitTask for a filled circle
     * @since ZParticle 1.0.0
     */
    BukkitTask createCircle(Location center, double radius, double rate, int update, long duration, double angle, char axis, ZParticleDisplay display);

    /**
     * Spawn a hollow sphere
     *
     * @param center location to spawn the sphere at.
     * @param radius radius of the sphere.
     * @param rate the rate of points forming the sphere. Lower values will spawn more particles.
     * @param update how often to refresh the sphere.
     * @param duration how long to display the sphere for.
     * @param display ZParticleDisplay to display.
     * @return BukkitTask for a filled circle
     * @since ZParticle 1.0.0
     */
    BukkitTask createSphere(Location center, double radius, double rate, int update, long duration, ZParticleDisplay display);

    /**
     * Spawns a moving wave from a location to another.
     *
     * @param start the starting point of the wave.
     * @param end the ending point of the wave.
     * @param rate the rate of points of the wave. Lower values will spawn more particles.
     * @param update how often to extend the wave.
     * @param duration how long to display each particle in the wave for.
     * @param cycles how many full oscillations the wave should make from the start to end location
     * @param multiplier the amplitude of the wave
     * @param angle rotation of the wave in degrees.
     * @param axis the axis of rotation for the wave.
     * @param display ZParticleDisplay to display.
     * @return BukkitTask for a moving wave
     * @see #drawLine(Player, double, double, int, long, ZParticleDisplay)
     * @since ZParticle 1.0.0
     */
    BukkitTask createWave(Location start, Location end, double rate, int update, long duration, double cycles, double multiplier, double angle, char axis, ZParticleDisplay display);
}
