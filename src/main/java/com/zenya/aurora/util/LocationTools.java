package com.zenya.aurora.util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public final class LocationTools {

    /**
     *
     * @param loc Location to check
     * @return The centre of the block at a location
     */
    public static Location getBlockCentre(Location loc) {
        return new Location(loc.getWorld(), (int) loc.getX() + 0.5, (int) loc.getY() + 0.5, (int) loc.getZ() + 0.5);
    }

    /**
     *
     * @param block Block location to check
     * @return The centre of the block at a location
     */
    public static Location getBlockCentre(Block block) {
        return new Location(block.getWorld(), block.getLocation().getX() + 0.5, block.getLocation().getY() + 0.5, block.getLocation().getZ() + 0.5);
    }

    /**
     *
     * @param locCoord World coordinate (double) to convert
     * @return An int chunk coordinate
     */
    public static int toChunkCoord(double locCoord) {
        return (int) Math.floor(locCoord) >> 4;
    }

    /**
     *
     * @param chunkCoord Chunk coordinate (int) to convert
     * @param worldCoord World coordinate (double) to append
     * @return An int chunk coordinate
     */
    public static int toWorldCoord(int chunkCoord, int worldCoord) {
        return (chunkCoord << 4) + (worldCoord % 16);
    }

    /**
     *
     * @param chunk The centre reference chunk
     * @param radius Square radius to add around centre chunk
     * @return An array of nearby chunks within the radius
     */
    public static ChunkContainer[] getSurroundingChunks(Chunk chunk, int radius) {
        return getSurroundingChunks((new ChunkContainer()).fromChunk(chunk), radius);
    }

    /**
     *
     * @param chunk The centre reference chunk container
     * @param radius Square radius to add around centre chunk
     * @return An array of nearby chunks within the radius
     */
    public static ChunkContainer[] getSurroundingChunks(ChunkContainer chunk, int radius) {
        ArrayList<ChunkContainer> nearbyChunks = new ArrayList<>();
        int cX = chunk.getX();
        int cZ = chunk.getZ();

        for (int x = cX - radius; x <= cX + radius; x++) {
            for (int z = cZ - radius; z <= cZ + radius; z++) {
                nearbyChunks.add(new ChunkContainer(chunk.getWorld(), x, z));
            }
        }
        return nearbyChunks.toArray(new ChunkContainer[0]);
    }

    /**
     *
     * @param nearbyChunks An array of chunks to check
     * @return An array containing 2 location bounds
     */
    private static Location[] getLocationBounds(ChunkContainer[] nearbyChunks) {
        //Get chunk bounds
        ChunkContainer c1 = nearbyChunks[0];
        ChunkContainer c2 = nearbyChunks[0];
        for (ChunkContainer c : nearbyChunks) {
            if (c.getX() < c1.getX() && c.getZ() < c1.getZ()) {
                c1 = c; //Lower chunk bound
            }
            if (c.getX() > c1.getX() && c.getZ() > c1.getZ()) {
                c2 = c; //Upper chunk bound
            }
        }

        //Get location bounds
        Location lowerbound = c1.toLocation(0, 0, 0);
        Location upperbound = c2.toLocation(15, 255, 15);
        return new Location[]{lowerbound, upperbound};
    }

    /**
     *
     * @param chunks An array of chunks to check
     * @return An array of available locations to spawn particles
     */
    public static CompletableFuture<Location[]> getParticleLocations(ChunkContainer[] chunks) {
        return getParticleLocations(chunks, 80, 100);
    }

    /**
     *
     * @param chunks An array of chunks to check
     * @param y1 First y-bound
     * @param y2 Second y-bound
     * @return An array of available locations to spawn particles
     */
    public static CompletableFuture<Location[]> getParticleLocations(ChunkContainer[] chunks, int y1, int y2) {
        return getParticleLocations(chunks, y1, y2, 20, 0.5f, true);
    }

    /**
     *
     * @param chunks An array of chunks to check
     * @param y1 First y-bound
     * @param y2 Second y-bound
     * @param distance Minimum distance between each particle group
     * @param randMultiplier Extent of randomness in distance between particle groups
     * @param shuffle Whether returned locations should be randomised
     * @return An array of available locations to spawn particles
     */
    public static CompletableFuture<Location[]> getParticleLocations(ChunkContainer[] chunks, double y1, double y2, double distance, double randMultiplier, boolean shuffle) {
        return CompletableFuture.supplyAsync(() -> {
            //Get location bounds
            Location[] locBounds = getLocationBounds(chunks);
            Location l1 = locBounds[0];
            Location l2 = locBounds[1];
            double lowerX = l1.getX();
            double higherX = l2.getX();
            double lowerY = Math.min(y1, y2);
            double higherY = Math.max(y1, y2);
            double lowerZ = l1.getZ();
            double higherZ = l2.getZ();

            //Add to list of available locations
            ArrayList<Location> locList = new ArrayList<>();
            for (double x = lowerX; x < higherX; x += distance) {
                for (double z = lowerZ; z < higherZ; z += distance) {
                    double locY = ThreadLocalRandom.current().nextDouble(lowerY, higherY);
                    double locX = x + ThreadLocalRandom.current().nextDouble() * randMultiplier * distance;
                    double locZ = z + ThreadLocalRandom.current().nextDouble() * randMultiplier * distance;
                    locList.add(new Location(l1.getWorld(), locX, locY, locZ));
                }
            }

            Location[] locs = locList.toArray(new Location[locList.size()]);
            //Randomise locations
            if (shuffle) {
                int n = locs.length;
                for (int i = 0; i < n; i++) {
                    int randomValue = i + ThreadLocalRandom.current().nextInt(n - i);
                    Location randomElement = locs[randomValue];
                    locs[randomValue] = locs[i];
                    locs[i] = randomElement;
                }
            }
            return locs;
        });
    }
}
