package fr.edencraft.wganvildamageflag;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import fr.edencraft.wganvildamageflag.listener.AnvilDurabilityListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class WGAnvilDamageFlag extends JavaPlugin {

	public static StateFlag ANVIL_DAMAGE_FLAG;

	private static WGAnvilDamageFlag INSTANCE;

	@Override
	public void onEnable() {
		INSTANCE = this;
		Bukkit.getPluginManager().registerEvents(new AnvilDurabilityListener(INSTANCE), this);
	}

	@Override
	public void onLoad() {
		// ... do your own plugin things, etc

		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		try {
			// create a flag with the name "my-custom-flag", defaulting to true
			StateFlag flag = new StateFlag("anvil-damage", true);
			registry.register(flag);
			ANVIL_DAMAGE_FLAG = flag; // only set our field if there was no error
		} catch (FlagConflictException e) {
			// some other plugin registered a flag by the same name already.
			// you can use the existing flag, but this may cause conflicts - be sure to check type
			Flag<?> existing = registry.get("anvil-damage");
			if (existing instanceof StateFlag) {
				ANVIL_DAMAGE_FLAG = (StateFlag) existing;
			} else {
				e.printStackTrace();
				Bukkit.getPluginManager().disablePlugin(this);
			}
		}
	}

	public static WGAnvilDamageFlag getINSTANCE() {
		return INSTANCE;
	}

	public WorldGuard getWorldGuard() {
		return WorldGuard.getInstance();
	}
}
