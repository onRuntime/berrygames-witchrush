package net.berrygames.witchrush.listeners.entities;

import net.berrygames.witchrush.WitchRush;
import net.berrygames.witchrush.game.GameState;
import net.berrygames.witchrush.team.TeamsInfos;
import net.berrygames.witchrush.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class EntityDeath implements Listener {

    private TeamManager teamManager = WitchRush.get().getTeamManager();

    @EventHandler
    public void onEntityDeath(final EntityDeathEvent e) {
        if(e.getEntity() instanceof Witch){
            switch (GameState.getStatus()){
                case LOBBY:
                    break;
                case GAME:
                    final Witch witch = (Witch) e.getEntity();
                    TeamsInfos teamInfos = TeamsInfos.JAUNE;
                    for (final ItemStack itemStack : e.getDrops()) {
                        itemStack.setType(Material.AIR);
                    }
                    for (final TeamsInfos teamInfosList : TeamsInfos.values()) {
                        if (teamManager.getTeamBoss(teamInfosList).getWitch().equals(witch)) {
                            teamInfos = teamInfosList;
                        }
                    }
                    if(teamManager.getTeamBoss(teamInfos).getWitch().equals(witch)){
                        Bukkit.broadcastMessage(WitchRush.prefix()+" Le boss des "+teamInfos.getChatColor()+teamInfos.getTeamName().toUpperCase()+"s §dest mort");
                        final TeamsInfos finalTeamInfos = teamInfos;
                        Bukkit.getOnlinePlayers().forEach(pls -> {
                            pls.playSound(pls.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0f, 1.0f);
                            if(teamManager.isPlayerInTeam(pls, finalTeamInfos)){
                                pls.sendMessage("§4Votre boss est mort !");
                                pls.sendMessage("§4Ne mourrez pas");
                                pls.sendTitle("§cAttention","§c§oVotre boss est mort !");
                                teamManager.getBossEntityMap().remove(finalTeamInfos);
                            }
                        });
                    }
                    break;
                case END:
                    break;
            }
        }
    }
}
