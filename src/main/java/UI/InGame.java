package UI;

import Cores.GameEnvironment;
import Cores.Map;
import Entities.BombList;
import Entities.Player.PlayerList;
import Input.PlayerInput;
import Input.SystemInput;
import Particles.BombExplosionList;
import UI.PlayerStatus.BombStatusBar;
import UI.ScenceGraph;

import java.io.FileNotFoundException;

public class InGame extends ScenceGraph {
    private int level;
    public InGame(int level) {
        this.level = level;
    }
    @Override
    public void update(float tpf) {
        PlayerList.onUpdate(tpf);
        BombList.onUpdate(tpf);
        BombExplosionList.onUpdate(tpf);
    }

    @Override
    public void display() {
        GameEnvironment.initalize();
        try {
            Map.initalize(level);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        }
       // SystemInput.initalize();
        PlayerInput.initalize();
    }

    @Override
    public void remove() {
        PlayerList.removeAll();
        BombList.removeAll();
        BombExplosionList.removeAll();
        GameEnvironment.remove();
        setDisplayed(false);
    }
}
