package nl.knokko.utils;

import java.io.FileInputStream;
import java.nio.ByteBuffer;

import net.minecraft.server.v1_11_R1.Items;
import net.minecraft.server.v1_11_R1.NBTTagList;
import net.minecraft.server.v1_11_R1.NBTTagString;

import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Book {
	
	private static byte[] toBytes(String string){
		ByteBuffer buffer = ByteBuffer.allocate(1 + string.length() * 2);
		buffer.put((byte)(string.length() - 128));
		for(int i = 0; i < string.length(); i++)
			buffer.putChar(string.charAt(i));
		return buffer.array();
	}
	
	private static String fromBytes(FileInputStream input) throws Exception {
		int size = (byte) input.read() + 128;
		char[] chars = new char[size];
		byte[] data = new byte[size * 2];
		input.read(data);
		ByteBuffer buffer = ByteBuffer.wrap(data);
		for(int i = 0; i < chars.length; i++)
			chars[i] = buffer.getChar();
		return new String(chars);
	}
	
	private final String title;
	private final String author;
	
	private final String[] pages;

	public Book(String title, String author, String... pages) {
		this.title = title;
		this.author = author;
		this.pages = pages;
	}
	
	public Book(ItemStack item){
		net.minecraft.server.v1_11_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(item);
		net.minecraft.server.v1_11_R1.NBTTagCompound nbttc = nmsItemStack.getTag();
		title = nbttc.getString("title");
		author = nbttc.getString("author");
		NBTTagList list = nbttc.getList("pages", 8);
		pages = new String[list.size()];
		for(int i = 0; i < pages.length; i++)
			pages[i] = list.getString(i);
	}
	
	public Book(FileInputStream input) throws Exception {
		title = fromBytes(input);
		author = fromBytes(input);
		pages = new String[(byte) input.read() + 128];
		for(int i = 0; i < pages.length; i++)
			pages[i] = fromBytes(input);
	}
	
	public String[] getPages(){
		return pages;
	}
	
	public ItemStack toItemStack(){
		CraftItemStack is = CraftItemStack.asNewCraftStack(Items.WRITTEN_BOOK);
		net.minecraft.server.v1_11_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(is);
		net.minecraft.server.v1_11_R1.NBTTagCompound nbttc = new net.minecraft.server.v1_11_R1.NBTTagCompound();
		nbttc.setString("title", title);
		nbttc.setString("author", author);
		NBTTagList nbttaglist = new NBTTagList();
		if(pages.length == 0)
			nbttaglist.add(new NBTTagString("Empty book"));
		for(String page : pages)
			nbttaglist.add(new NBTTagString(page));
		nbttc.set("pages", nbttaglist);
		nmsItemStack.setTag(nbttc);
		return CraftItemStack.asBukkitCopy(nmsItemStack);
	}
	
	public void give(Player receiver){
		receiver.getInventory().addItem(toItemStack());
	}
	
	public byte[] toBytes(){
		byte[] titlea = toBytes(title);
		byte[] authora = toBytes(author);
		int size = titlea.length + authora.length;
		byte[][] pagesa = new byte[pages.length][];
		size++;
		for(int i = 0; i < pagesa.length; i++){
			pagesa[i] = toBytes(pages[i]);
			size += pagesa[i].length;
		}
		byte[] total = new byte[size];
		System.arraycopy(titlea, 0, total, 0, titlea.length);
		System.arraycopy(authora, 0, total, titlea.length, authora.length);
		total[titlea.length + authora.length] = (byte)(pages.length - 128);
		int index = titlea.length + authora.length + 1;
		for(int i = 0; i < pagesa.length; i++){
			System.arraycopy(pagesa[i], 0, total, index, pagesa[i].length);
			index += pagesa[i].length;
		}
		return total;
	}
}
