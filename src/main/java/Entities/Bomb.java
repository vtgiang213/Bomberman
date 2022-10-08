package Entities;

import Cores.Map;
import Entities.BuffItem.*;
import Entities.Player.MainPlayer;
import Entities.Player.Player;
import Entities.Player.PlayerList;
import Entities.Terrain.Container;
import Entities.Terrain.Portal;
import Particles.BombExplosion;
import UI.InGameGUI.Defeat;
import UI.InGameGUI.InGame;
import UI.ScenceGraphController;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Bomb extends Entity {

    private float timeExplore = 4.0f;
    private static int flame;
    public Bomb(Vector3f position, int flame) {
        super(position, "Models/Bomb/bomb.obj");
        this.flame = flame;
        BombList.add(this);
    }

    public void onUpdate(float tpf) {
        timeExplore -= tpf;
        MainPlayer mainPlayer =(MainPlayer) PlayerList.getMainPlayer();
        if (mainPlayer == null) {
            setBlocked(true);
            return;
        }
        Vector2f mainPlayerCord = mainPlayer.getCord();
        Vector2f position = getCord();
        if (position.x != mainPlayerCord.x || position.y != mainPlayerCord.y) {
            setBlocked(true);
        }
    }

    public boolean isExplored() {
        return timeExplore <= 0f;
    }

    @Override
    public void remove() {
        explore();
        super.remove();
    }

    private void explore() {
        Vector2f position = getCord();
        int x = (int) position.x;
        int y = (int) position.y;
        Map.setObject(x, y, null);
        for (int i = x; i < Math.min(20, x + flame); ++ i) {
            checkKillPlayer(i, y);
            if (checkExplosion(i, y)) {
                break;
            }
        }

        for (int i = x; i > Math.max(-1, x - flame); -- i) {
            checkKillPlayer(i, y);
            if (checkExplosion(i, y)) {
                break;
            }
        }

        for (int i = y; i < Math.min(20, y + flame); ++ i) {
            checkKillPlayer(x, i);
            if (checkExplosion(x, i)) {
                break;
            }

        }

        for (int i = y; i > Math.max(-1, y - flame); -- i) {
            checkKillPlayer(x, i);
            if (checkExplosion(x, i)) {
                break;
            }
        }
    }

    private void checkKillPlayer(int x, int y) {
        List<Player> removeList = new ArrayList<>();
        for (Player player: PlayerList.getList()) {
            if ((int) player.getCord().x == x && (int) player.getCord().y == y) {
                removeList.add(player);
            }
        }
        for (Player player: removeList) {
            PlayerList.remove(player);
            if (player instanceof MainPlayer) {
                ScenceGraphController.setExtension(new Defeat(InGame.getLevel()));
            }
        }
    }

    private boolean checkExplosion(int x, int y) {
        BombExplosion bombExplosion = new BombExplosion(new Vector3f(x * 2f, 1, y * 2f));
        Entity cur = Map.getObject(x, y);
        if (cur != null) {
            if (cur instanceof Container) {
                cur.remove();
                Map.setObject(x, y, randomEntity(x, y));
            }
            return true;
        }
        return false;
    }

    private Entity randomEntity(int x, int y) {
        if (!Map.isHasPortal()) {
            int rand = (int) (Math.random() * Container.getCount());
            if(rand == 1) {
                Map.setHasPortal(true);
                return new Portal(new Vector3f(x * 2f, 1, y * 2f));
            }
        }
        int rand = (int) (Math.random() * 10);
        switch (rand) {
            case 0:
                return new BombExtend(new Vector3f(x * 2f, 1, y * 2f));
            case 1:
                return new FlameBuff(new Vector3f(x * 2f, 1, y * 2f));
            case 2:
                return new ShieldBuff(new Vector3f(x * 2f, 1, y * 2f));
            case 3:
                return new SpeedBuff(new Vector3f(x * 2f, 1, y * 2f));
            default:
                return null;

        }
    }
}
