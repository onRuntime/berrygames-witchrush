package net.berrygames.witchrush.listeners.players;

import net.berrygames.witchrush.WitchPlayer;
import net.berrygames.witchrush.WitchRush;
import net.berrygames.witchrush.game.GameState;
import net.berrygames.witchrush.team.TeamInfos;
import net.berrygames.witchrush.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class WitchEvent implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent e) {
        final TeamManager teamManager = WitchRush.get().getTeamManager();
        if(!WitchRush.get().getState().equals(GameState.WAITING) && !WitchRush.get().getState().equals(GameState.STARTING)){
            if ((e.getDamager() instanceof Player) && (e.getEntity() instanceof Witch)){
                final Player player = (Player)e.getDamager();
                final Witch witch = (Witch) e.getEntity();
                final TeamInfos playerTeamInfos = teamManager.getPlayerTeam(player);
                if (teamManager.getTeamBoss(playerTeamInfos).getWitch().equals(witch)) {
                    player.sendMessage("§cVous ne pouvez pas tuer votre boss !");
                    e.setCancelled(true);
                    return;
                }
                for (final TeamInfos teamInfos : TeamInfos.values()) {
                    if (teamManager.getTeamBoss(teamInfos).getWitch().equals(witch)) {
                        teamManager.getTeamBoss(teamInfos).getWitch().teleport(teamManager.getBossLocation(teamInfos));
                        Bukkit.getOnlinePlayers().forEach(playerOnline -> {
                            WitchPlayer witchPlayer = WitchPlayer.get(playerOnline);
                            if (teamManager.isPlayerInTeam(playerOnline, teamInfos) && !witchPlayer.isSpectator()) {
                                playerOnline.sendTitle("§cWitch attaquée", null);
                            }
                            if(WitchRush.get().getTeamManager().getBossEntityMap().get(teamInfos).getLife() > 0){
                                int life = (int) (WitchRush.get().getTeamManager().getBossEntityMap().get(teamInfos).getLife() - e.getDamage());
                                WitchRush.get().getTeamManager().getBossEntityMap().get(teamInfos).setLife(life);
                            }
                            if(WitchRush.get().getTeamManager().getBossEntityMap().get(teamInfos).getLife() > 0){
                                witch.remove();
                            }
                            return;
                        });
                    }

                }
            } else if (e.getDamager() instanceof Player && e.getEntity() instanceof Villager){
                e.setCancelled(true);
            }
        }
    }
}