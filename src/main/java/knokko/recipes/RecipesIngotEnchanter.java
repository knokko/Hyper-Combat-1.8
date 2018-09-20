package knokko.recipes;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import knokko.items.HyperItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Maps;

public class RecipesIngotEnchanter {
	
	private static final RecipesIngotEnchanter enchantingBase = new RecipesIngotEnchanter();
	
    private final Map enchantList = Maps.newHashMap();
    private final Map experienceList = Maps.newHashMap();

    public static RecipesIngotEnchanter instance(){
        return enchantingBase;
    }

    private RecipesIngotEnchanter(){
       addEnchantingRecipe("gemSilicon", "dyeBlue", new ItemStack(HyperItems.blueSilicon), 1);
       addEnchantingRecipe("ingotIron", "dyeBlue", new ItemStack(HyperItems.blueIron), 1);
       addEnchantingRecipe("ingotCopper", "dyeYellow", new ItemStack(HyperItems.yellowCopper), 1);
    }
    
    public void addEnchantingRecipe(Object item1, Object item2, ItemStack result, float xp){
    	enchantList.put(new Object[]{item1, item2}, result);
    	experienceList.put(result, Float.valueOf(xp));
    }

    public ItemStack getEnchantingResult(Object stack1, Object stack2){
        Iterator iterator = enchantList.entrySet().iterator();
        Entry entry;
        do {
            if (!iterator.hasNext())
                return null;
            entry = (Entry)iterator.next();
        }
        while (!areArrayItemStacksEqual(new Object[]{stack1, stack2}, (Object[]) entry.getKey()));
        return (ItemStack)entry.getValue();
    }
    
    private boolean areArrayItemStacksEqual(Object[] o1, Object[] o2){
    	return areItemsEqual(o1[0], o2[0]) && areItemsEqual(o1[1], o2[1]);
    }
    
    private boolean areItemsEqual(Object o1, Object o2){
    	if(o2 instanceof String){
    		if(o1 instanceof String)
    			return ((String) o1).matches((String) o2);
    		if(o1 instanceof ItemStack){
    			int[] ids = OreDictionary.getOreIDs((ItemStack) o1);
    			int t = 0;
    			while(t < ids.length){
    				String id = OreDictionary.getOreName(ids[t]);
    				if(id.equals(o2))
    					return true;
    				++t;
    			}
    		}
    		return false;
    	}
    	if(o2 instanceof ItemStack && o1 instanceof ItemStack)
    		return areItemStacksEqual((ItemStack)o1, (ItemStack) o2);
    	return false;
    }

    private boolean areItemStacksEqual(ItemStack s1, ItemStack s2){
    	if(s1 == null || s2 == null){
    		if(s2 == null && s1 == null)
    			return true;
    		return false;
    	}
        return s2.getItem() == s1.getItem() && (s2.getMetadata() == 32767 || s2.getMetadata() == s1.getMetadata());
    }

    public Map getEnchantingList(){
        return enchantList;
    }

    public float getEnchantingExperience(Object result){
        Iterator iterator = experienceList.entrySet().iterator();
        Entry entry;
        do {
            if (!iterator.hasNext())
                return 0.0F;
            entry = (Entry)iterator.next();
        }
        while (!areItemsEqual(result, entry.getKey()));
        return ((Float)entry.getValue()).floatValue();
    }
}
