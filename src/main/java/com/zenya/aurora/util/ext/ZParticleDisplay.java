package com.zenya.aurora.util.ext;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * By default the particle xyz offsets and speed aren't 0, but everything will be 0 by default in this class. Particles are spawned to a location. So all the nearby players can see it.
 * <p>
 * The fields of this class are publicly accessible for ease of use. All the fields can be null except the particle type.
 * <p>
 * For cross-version compatibility, instead of Bukkit's {@link org.bukkit.Color} the java awt {@link Color} class is used.
 * <p>
 * the data field is used to store special particle data, such as colored particles. For colored particles a float list is used since the particle size is a float. The format of float list data for a colored particle is: <code>[r, g, b, size]</code>
 *
 * @author Crypto Morin, Zenya4 (modded)
 * @version 5.0.0 (ZParticleDisplay 1.0.1)
 * @see ZParticle
 */
public class ZParticleDisplay {

    private static final boolean ISFLAT = ZParticle.getParticle("FOOTSTEP") == null;

    private Particle particle;
    private Location location;
    private Callable<Location> locationCaller;
    private int count;
    private double offsetx, offsety, offsetz;
    private double extra;
    private Object data;
    private Player[] players;

    /**
     * Make a new instance of particle display. The position of each particle will be randomized positively and negatively by the offset parameters on each axis.
     *
     * @param particle the particle to spawn.
     * @param location the location to spawn the particle at.
     * @param count the count of particles to spawn.
     * @param offsetx the x offset.
     * @param offsety the y offset.
     * @param offsetz the z offset.
     * @param extra in most cases extra is the speed of the particles.
     * @param players the list of players particles are displayed to.
     * a ZParticleDisplay object.
     */
    private ZParticleDisplay(@Nonnull Particle particle, @Nullable Callable<Location> locationCaller, @Nullable Location location, int count, double offsetx, double offsety, double offsetz, double extra, Player... players) {
        this.particle = particle;
        this.locationCaller = locationCaller;
        this.location = location;
        this.count = count;
        this.offsetx = offsetx;
        this.offsety = offsety;
        this.offsetz = offsetz;
        this.extra = extra;
        this.players = players;
    }

    private ZParticleDisplay(@Nonnull Particle particle, @Nullable Location location, int count, double offsetx, double offsety, double offsetz, Player... players) {
        this(particle, null, location, count, offsetx, offsety, offsetz, 0, players);
    }

    private ZParticleDisplay(@Nonnull Particle particle, @Nullable Location location, int count, Player... players) {
        this(particle, location, count, 0, 0, 0, players);
    }

    private ZParticleDisplay(@Nonnull Particle particle, @Nullable Location location, Player... players) {
        this(particle, location, 0, players);
    }

    /**
     * Builds a simple ZParticleDisplay object with cross-version compatible {@link org.bukkit.Particle.DustOptions} properties.
     *
     * @param location the location of the display.
     * @param r the red component of the particle color
     * @param g the green component of the particle color
     * @param b the blue component of the particle color
     * @param size the size of the dust.
     * @param players the list of players particles are displayed to.
     * @return a redstone colored dust.
     * @see #simple(Particle, Location, Player...)
     * @since 1.0.0
     */
    @Nonnull
    public static ZParticleDisplay colored(@Nullable Location location, int r, int g, int b, float size, Player... players) {
        ZParticleDisplay dust = new ZParticleDisplay(Particle.REDSTONE, null, location, 1, 0, 0, 0, 0, players);
        dust.data = new float[]{r, g, b, size};
        return dust;
    }

    /**
     * Builds a simple ZParticleDisplay object with cross-version compatible {@link org.bukkit.Particle.DustOptions} properties.
     *
     * @param location the location of the display.
     * @param color the color of the particle.
     * @param size the size of the dust.
     * @param players the list of players particles are displayed to.
     * @return a redstone colored dust.
     * @see #colored(Location, int, int, int, float, Player...)
     * @since 3.0.0
     */
    @Nonnull
    public static ZParticleDisplay colored(@Nullable Location location, @Nonnull Color color, float size, Player... players) {
        return colored(location, color.getRed(), color.getGreen(), color.getBlue(), size, players);
    }

    /**
     * Builds a directional ZParticleDisplay object.
     *
     * @param particle the particle of the display.
     * @param location the location of the display.
     * @param offsetx the x offset.
     * @param offsety the y offset.
     * @param offsetz the z offset.
     * @param players the list of players particles are displayed to.
     * @return a directional ZParticleDisplay.
     * @since ZParticleDisplay 1.0.1
     */
    @Nonnull
    public static ZParticleDisplay directional(@Nonnull Particle particle, @Nullable Location location, double offsetx, double offsety, double offsetz, @Nonnull Player... players) {
        Objects.requireNonNull(particle, "Cannot build ZParticleDisplay with null particle");
        return new ZParticleDisplay(particle, null, location, 0, offsetx, offsety, offsetz, 0, players);
    }

    /**
     * Builds a simple ZParticleDisplay object. An invocation of this method yields exactly the same result as the expression:
     * <p>
     * <blockquote>
     * new ZParticleDisplay(particle, location, 1, 0, 0, 0, 0);
     * </blockquote>
     *
     * @param particle the particle of the display.
     * @param location the location of the display.
     * @param players the list of players particles are displayed to.
     * @return a simple ZParticleDisplay.
     * @since 1.0.0
     */
    @Nonnull
    public static ZParticleDisplay simple(@Nonnull Particle particle, @Nullable Location location, @Nonnull Player... players) {
        Objects.requireNonNull(particle, "Cannot build ZParticleDisplay with null particle");
        return new ZParticleDisplay(particle, null, location, 1, 0, 0, 0, 0, players);
    }

    /**
     * A quick access method to display a simple particle. An invocation of this method yields the same result as the expression:
     * <p>
     * <blockquote>
     * ZParticleDisplay.simple(particle, location, player...).spawn();
     * </blockquote>
     *
     * @param particle the particle to show.
     * @param location the location of the particle.
     * @param players the list of players particles are displayed to.
     * @return a simple ZParticleDisplay.
     * @since 1.0.0
     */
    @Nonnull
    public static ZParticleDisplay display(@Nonnull Particle particle, @Nonnull Location location, @Nonnull Player... players) {
        Objects.requireNonNull(location, "Cannot display particle in null location");
        ZParticleDisplay display = simple(particle, location, players);
        display.spawn();
        return display;
    }

    /**
     * Gets the location of an entity if specified or the constant location.
     *
     * @return the location of the particle.
     * @since 3.1.0
     */
    @Nullable
    public Location getLocation() {
        try {
            return locationCaller == null ? location : locationCaller.call();
        } catch (Exception e) {
            e.printStackTrace();
            return location;
        }
    }

    /**
     * Setter for particle location
     *
     * @param loc the location of the particle.
     * @since ZParticleDisplay 1.0.1
     */
    @Nullable
    public ZParticleDisplay withLocation(Location loc) {
        this.location = loc;
        return this;
    }

    /**
     * Sets a caller for location changes.
     *
     * @param locationCaller the caller to call to get the new location.
     * @return the same particle settings with the caller added.
     * @since 3.1.0
     */
    @Nonnull
    public ZParticleDisplay withLocationCaller(@Nullable Callable<Location> locationCaller) {
        this.locationCaller = locationCaller;
        return this;
    }

    /**
     * Saves an instance of an entity to track the location from.
     *
     * @param entity the entity to track the location from.
     * @return the location tracker entity.
     * @since 3.1.0
     */
    @Nonnull
    public ZParticleDisplay withEntity(@Nullable Entity entity) {
        return withLocationCaller(entity::getLocation);
    }

    /**
     * Changes the particle count of the particle settings.
     *
     * @param count the particle count.
     * @return the same particle display.
     * @since 3.0.0
     */
    @Nonnull
    public ZParticleDisplay withCount(int count) {
        this.count = count;
        return this;
    }

    /**
     * Check if this particle setting is a directional particle.
     *
     * @return true if the particle is directional, otherwise false.
     * @see #setDirectional(boolean)
     * @since 2.1.0
     */
    private boolean isDirectional() {
        return count == 0;
    }

    /**
     * When a particle is set to be directional it'll only spawn one particle and the xyz offset values are used for the direction of the particle.
     * <p>
     * Colored particles in 1.12 and below don't support this.
     *
     * @return the same particle display.
     * @see #isDirectional()
     * @since 1.1.0
     */
    @Nonnull
    public ZParticleDisplay setDirectional(boolean directional) {
        count = directional ? 0 : 1;
        return this;
    }

    /**
     * Set the xyz offset of the particle settings.
     *
     * @since 1.1.0
     */
    @Nonnull
    public ZParticleDisplay withOffset(double x, double y, double z) {
        offsetx = x;
        offsety = y;
        offsetz = z;
        return this;
    }

    /**
     * In most cases extra is the speed of the particles.
     *
     * @param extra the extra number.
     * @return the same particle display.
     * @since 3.0.1
     */
    @Nonnull
    public ZParticleDisplay withExtra(double extra) {
        this.extra = extra;
        return this;
    }

    /**
     * Adds color properties to the particle settings.
     *
     * @param color the RGB color of the particle.
     * @param size the size of the particle.
     * @return a colored particle.
     * @see #colored(Location, Color, float, Player...)
     * @since 3.0.0
     */
    @Nonnull
    public ZParticleDisplay withColor(@Nonnull Color color, float size) {
        this.data = new float[]{color.getRed(), color.getGreen(), color.getBlue(), size};
        return this;
    }

    /**
     * Gets the players to send the particle to.
     *
     * @return the array players to send the particle to.
     * @since ZParticleDisplay 1.0.0
     */
    @Nullable
    public Player[] getPlayers() {
        return players;
    }

    /**
     * Sets the players to send the particle to.
     *
     * @param players the array of players to send the particle to.
     * @since ZParticleDisplay 1.0.1
     */
    public void withPlayers(Player[] players) {
        this.players = players;
    }

    /**
     * Clones this particle settings.
     *
     * @return the cloned ZParticleDisplay.
     */
    @Nonnull
    @Override
    public ZParticleDisplay clone() {
        ZParticleDisplay display = new ZParticleDisplay(particle, locationCaller, (location == null ? null : cloneLocation(location)), count, offsetx, offsety, offsetz, extra, players);
        display.data = data;
        return display;
    }

    /**
     * We don't want to use {@link Location#clone()} since it doens't copy to constructor and Javas clone method is known to be inefficient and broken.
     *
     * @since 3.0.3
     */
    @Nonnull
    private Location cloneLocation(@Nonnull Location loc) {
        return new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    /**
     * Rotates the given xyz with the given rotation radians and adds the to the specified location.
     *
     * @param loc the location to add the rotated axis.
     * @return a cloned rotated location.
     * @since 3.0.0
     */
    @Nonnull
    private Location transform(@Nonnull Location loc, double x, double y, double z) {
        return cloneLocation(loc).add(x, y, z);
    }

    /**
     * Spawns the particle at the current location.
     *
     * @since 2.0.1
     */
    public void spawn() {
        spawn(getLocation());
    }

    /**
     * Adds xyz to the cloned loaction before spawning particle.
     *
     * @since 1.0.0
     */
    @Nonnull
    public Location spawn(double x, double y, double z) {
        return spawn(transform(getLocation(), x, y, z));
    }

    /**
     * Displays the particle in the specified location.
     *
     * @param loc the location to display the particle at.
     * @see #spawn(double, double, double)
     * @since 5.0.0
     */
    @Nonnull
    public Location spawn(@Nonnull Location loc) {
        if (data != null) {
            if (data instanceof float[] datas) {
                if (ISFLAT) {
                    Particle.DustOptions dust = new Particle.DustOptions(org.bukkit.Color.fromRGB((int) datas[0], (int) datas[1], (int) datas[2]), datas[3]);
                    if (players == null) {
                        loc.getWorld().spawnParticle(particle, loc, count, offsetx, offsety, offsetz, extra, dust);
                    } else {
                        for (Player player : players) {
                            player.spawnParticle(particle, loc, count, offsetx, offsety, offsetz, extra, dust);
                        }
                    }
                } else {
                    if (players == null) {
                        loc.getWorld().spawnParticle(particle, loc, count, (int) datas[0], (int) datas[1], (int) datas[2], datas[3]);
                    } else {
                        for (Player player : players) {
                            player.spawnParticle(particle, loc, count, (int) datas[0], (int) datas[1], (int) datas[2], datas[3]);
                        }
                    }
                }
            }
        } else {
            if (players == null) {
                loc.getWorld().spawnParticle(particle, loc, count, offsetx, offsety, offsetz, extra);
            } else {
                for (Player player : players) {
                    player.spawnParticle(particle, loc, count, offsetx, offsety, offsetz, extra);
                }
            }
        }
        return loc;
    }
}
