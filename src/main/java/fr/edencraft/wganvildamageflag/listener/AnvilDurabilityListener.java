package fr.edencraft.wganvildamageflag.listener;

import com.destroystokyo.paper.event.block.AnvilDamagedEvent;
import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import fr.edencraft.wganvildamageflag.WGAnvilDamageFlag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareAnvilEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AnvilDurabilityListener implements Listener {

	private final WorldGuard worldGuard;

	public AnvilDurabilityListener(WGAnvilDamageFlag instance) {
		this.worldGuard = instance.getWorldGuard();
	}

	@EventHandler
	public void onPlayerInteract(PrepareAnvilEvent event) {
		Location anvilLocation = event.getInventory().getLocation();
		if (anvilLocation == null) return;
		Block anvil = anvilLocation.getBlock();
		World world = anvilLocation.getWorld();

		RegionContainer regionContainer = worldGuard.getPlatform().getRegionContainer();
		RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(world));

		if (regionManager == null) return;

		List<String> applicableRegionsIDs = regionManager.getApplicableRegionsIDs(BlockVector3.at(anvil.getX(), anvil.getY(), anvil.getZ()));
		if (applicableRegionsIDs.isEmpty()) {
			applicableRegionsIDs.add("__global__");
		}

		List<ProtectedRegion> protectedRegionsList = new ArrayList<>();
		applicableRegionsIDs.forEach(s -> protectedRegionsList.add(regionManager.getRegion(s)));

		if (isApplicable(protectedRegionsList, WGAnvilDamageFlag.ANVIL_DAMAGE_FLAG)) {
			anvil.setType(Material.ANVIL);
		}
	}

	private boolean isApplicable(List<ProtectedRegion> protectedRegionList, StateFlag flag) {
		AtomicInteger maxPriority = new AtomicInteger(-1);
		AtomicBoolean finalValue = new AtomicBoolean(false);

		protectedRegionList.forEach(protectedRegion -> {
			if (protectedRegion.getPriority() >= maxPriority.get()) {
				maxPriority.set(protectedRegion.getPriority());
				StateFlag.State state = protectedRegion.getFlag(flag);

				if (state != null) {
					finalValue.set(state.equals(StateFlag.State.DENY));
				} else {
					finalValue.set(false);
				}
			}

		});

		return finalValue.get();
	}

}
