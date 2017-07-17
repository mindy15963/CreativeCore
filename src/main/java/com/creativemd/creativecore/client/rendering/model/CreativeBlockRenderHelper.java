package com.creativemd.creativecore.client.rendering.model;

import java.util.HashMap;

import com.creativemd.creativecore.core.CreativeCoreClient;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CreativeBlockRenderHelper {
	
	public static HashMap<ResourceLocation, Block> blocks = new HashMap<>();
	public static HashMap<ResourceLocation, Item> items = new HashMap<>();
	
	public static void registerCreativeRenderedItem(Item item)
	{
		items.put(item.getRegistryName(), item);
		CreativeCoreClient.registerItemRenderer(item);
		//CreativeCoreClient.registerItemColorHandler(item);
	}
	
	public static void registerCreativeRenderedBlock(Block block)
	{
		blocks.put(block.getRegistryName(), block);
		CreativeCoreClient.registerBlockItem(block);
		//CreativeCoreClient.registerBlockColorHandler(block);
	}
	
}
