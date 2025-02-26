package team.creative.creativecore.common.level;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FakeChunkCache extends ChunkSource {
    private static final Logger LOGGER = LogManager.getLogger();
    private final LevelChunk emptyChunk;
    private final LevelLightEngine lightEngine;
    private volatile FakeChunkCache.Storage storage;
    public final CreativeLevel level;
    
    public FakeChunkCache(CreativeLevel level, int radius) {
        this.level = level;
        this.emptyChunk = new EmptyLevelChunk(level, new ChunkPos(0, 0));
        this.lightEngine = new LevelLightEngine(this, true, level.dimensionType().hasSkyLight());
        this.storage = new FakeChunkCache.Storage(calculateStorageRange(radius));
    }
    
    @Override
    public LevelLightEngine getLightEngine() {
        return this.lightEngine;
    }
    
    private static boolean isValidChunk(@Nullable LevelChunk p_104439_, int p_104440_, int p_104441_) {
        if (p_104439_ == null) {
            return false;
        } else {
            ChunkPos chunkpos = p_104439_.getPos();
            return chunkpos.x == p_104440_ && chunkpos.z == p_104441_;
        }
    }
    
    public void drop(int p_104456_, int p_104457_) {
        if (this.storage.inRange(p_104456_, p_104457_)) {
            int i = this.storage.getIndex(p_104456_, p_104457_);
            LevelChunk levelchunk = this.storage.getChunk(i);
            if (isValidChunk(levelchunk, p_104456_, p_104457_)) {
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Unload(levelchunk));
                this.storage.replace(i, levelchunk, (LevelChunk) null);
            }
            
        }
    }
    
    @Override
    @Nullable
    public LevelChunk getChunk(int p_104451_, int p_104452_, ChunkStatus p_104453_, boolean p_104454_) {
        if (this.storage.inRange(p_104451_, p_104452_)) {
            LevelChunk levelchunk = this.storage.getChunk(this.storage.getIndex(p_104451_, p_104452_));
            if (isValidChunk(levelchunk, p_104451_, p_104452_)) {
                return levelchunk;
            }
        }
        
        return p_104454_ ? this.emptyChunk : null;
    }
    
    @Override
    public BlockGetter getLevel() {
        return this.level;
    }
    
    @Nullable
    public LevelChunk replaceWithPacketData(int p_171616_, int p_171617_, ChunkBiomeContainer p_171618_, FriendlyByteBuf p_171619_, CompoundTag p_171620_, BitSet p_171621_) {
        if (!this.storage.inRange(p_171616_, p_171617_)) {
            LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", p_171616_, p_171617_);
            return null;
        } else {
            int i = this.storage.getIndex(p_171616_, p_171617_);
            LevelChunk levelchunk = this.storage.chunks.get(i);
            ChunkPos chunkpos = new ChunkPos(p_171616_, p_171617_);
            if (!isValidChunk(levelchunk, p_171616_, p_171617_)) {
                levelchunk = new LevelChunk(this.level, chunkpos, p_171618_);
                levelchunk.replaceWithPacketData(p_171618_, p_171619_, p_171620_, p_171621_);
                this.storage.replace(i, levelchunk);
            } else {
                levelchunk.replaceWithPacketData(p_171618_, p_171619_, p_171620_, p_171621_);
            }
            
            LevelChunkSection[] alevelchunksection = levelchunk.getSections();
            LevelLightEngine levellightengine = this.getLightEngine();
            levellightengine.enableLightSources(chunkpos, true);
            
            for (int j = 0; j < alevelchunksection.length; ++j) {
                LevelChunkSection levelchunksection = alevelchunksection[j];
                int k = this.level.getSectionYFromSectionIndex(j);
                levellightengine.updateSectionStatus(SectionPos.of(p_171616_, k, p_171617_), LevelChunkSection.isEmpty(levelchunksection));
            }
            
            this.level.onChunkLoaded(chunkpos);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Load(levelchunk));
            return levelchunk;
        }
    }
    
    @Override
    public void tick(BooleanSupplier p_104447_) {}
    
    public void updateViewCenter(int p_104460_, int p_104461_) {
        this.storage.viewCenterX = p_104460_;
        this.storage.viewCenterZ = p_104461_;
    }
    
    public void updateViewRadius(int p_104417_) {
        int i = this.storage.chunkRadius;
        int j = calculateStorageRange(p_104417_);
        if (i != j) {
            FakeChunkCache.Storage clientchunkcache$storage = new FakeChunkCache.Storage(j);
            clientchunkcache$storage.viewCenterX = this.storage.viewCenterX;
            clientchunkcache$storage.viewCenterZ = this.storage.viewCenterZ;
            
            for (int k = 0; k < this.storage.chunks.length(); ++k) {
                LevelChunk levelchunk = this.storage.chunks.get(k);
                if (levelchunk != null) {
                    ChunkPos chunkpos = levelchunk.getPos();
                    if (clientchunkcache$storage.inRange(chunkpos.x, chunkpos.z)) {
                        clientchunkcache$storage.replace(clientchunkcache$storage.getIndex(chunkpos.x, chunkpos.z), levelchunk);
                    }
                }
            }
            
            this.storage = clientchunkcache$storage;
        }
        
    }
    
    private static int calculateStorageRange(int p_104449_) {
        return Math.max(2, p_104449_) + 3;
    }
    
    @Override
    public String gatherStats() {
        return this.storage.chunks.length() + ", " + this.getLoadedChunksCount();
    }
    
    @Override
    public int getLoadedChunksCount() {
        return this.storage.chunkCount;
    }
    
    @Override
    public void onLightUpdate(LightLayer p_104436_, SectionPos p_104437_) {
        Minecraft.getInstance().levelRenderer.setSectionDirty(p_104437_.x(), p_104437_.y(), p_104437_.z());
    }
    
    @OnlyIn(Dist.CLIENT)
    final class Storage {
        private final AtomicReferenceArray<LevelChunk> chunks;
        private final int chunkRadius;
        private final int viewRange;
        private volatile int viewCenterX;
        private volatile int viewCenterZ;
        private int chunkCount;
        
        Storage(int p_104474_) {
            this.chunkRadius = p_104474_;
            this.viewRange = p_104474_ * 2 + 1;
            this.chunks = new AtomicReferenceArray<>(this.viewRange * this.viewRange);
        }
        
        int getIndex(int p_104482_, int p_104483_) {
            return Math.floorMod(p_104483_, this.viewRange) * this.viewRange + Math.floorMod(p_104482_, this.viewRange);
        }
        
        protected void replace(int p_104485_, @Nullable LevelChunk p_104486_) {
            LevelChunk levelchunk = this.chunks.getAndSet(p_104485_, p_104486_);
            if (levelchunk != null) {
                --this.chunkCount;
                FakeChunkCache.this.level.unload(levelchunk);
            }
            
            if (p_104486_ != null) {
                ++this.chunkCount;
            }
            
        }
        
        protected LevelChunk replace(int p_104488_, LevelChunk p_104489_, @Nullable LevelChunk p_104490_) {
            if (this.chunks.compareAndSet(p_104488_, p_104489_, p_104490_) && p_104490_ == null) {
                --this.chunkCount;
            }
            
            FakeChunkCache.this.level.unload(p_104489_);
            return p_104489_;
        }
        
        boolean inRange(int p_104501_, int p_104502_) {
            return Math.abs(p_104501_ - this.viewCenterX) <= this.chunkRadius && Math.abs(p_104502_ - this.viewCenterZ) <= this.chunkRadius;
        }
        
        @Nullable
        protected LevelChunk getChunk(int p_104480_) {
            return this.chunks.get(p_104480_);
        }
    }
}