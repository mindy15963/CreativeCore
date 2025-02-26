package team.creative.creativecore.common.util.ingredient;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.ForgeRegistries;

public class CreativeIngredientMaterial extends CreativeIngredient {
    
    public Material material;
    
    public CreativeIngredientMaterial(Material material) {
        this.material = material;
    }
    
    public CreativeIngredientMaterial() {
        super();
    }
    
    @Override
    protected void writeExtra(CompoundTag nbt) {
        nbt.putString("material", getBlock().getRegistryName().toString());
    }
    
    @Override
    protected void readExtra(CompoundTag nbt) {
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("material")));
        if (block != null)
            material = block.defaultBlockState().getMaterial();
    }
    
    public Block getBlock() {
        for (Block block : ForgeRegistries.BLOCKS)
            if (block.defaultBlockState().getMaterial() == material)
                return block;
        return null;
    }
    
    @Override
    public ItemStack getExample() {
        return new ItemStack(getBlock());
    }
    
    public static BlockState getState(ItemStack stack) {
        Block block = Block.byItem(stack.getItem());
        if (block != null)
            return block.defaultBlockState();
        return null;
    }
    
    @Override
    public boolean is(CreativeIngredient info) {
        return info instanceof CreativeIngredientMaterial && ((CreativeIngredientMaterial) info).material == this.material;
    }
    
    @Override
    public boolean is(ItemStack stack) {
        BlockState state = getState(stack);
        if (state != null)
            return state.getMaterial() == this.material;
        return false;
    }
    
    @Override
    public boolean equals(CreativeIngredient object) {
        return object instanceof CreativeIngredientMaterial && ((CreativeIngredientMaterial) object).material == this.material;
    }
    
    @Override
    public CreativeIngredient copy() {
        return new CreativeIngredientMaterial(material);
    }
}
