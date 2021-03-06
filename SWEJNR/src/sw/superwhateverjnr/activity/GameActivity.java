package sw.superwhateverjnr.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import lombok.Getter;
import sw.superwhateverjnr.Game;
import sw.superwhateverjnr.world.DummyWorldLoader;
import sw.superwhateverjnr.world.PackedWorldLoader;
import sw.superwhateverjnr.world.RandomWorldLoader;
import sw.superwhateverjnr.world.WorldLoader;

public class GameActivity extends Activity
{
	@Getter
    private static GameActivity instance;
    private WakeLock wl;
	
	private Game game;
	public GameActivity()
	{
		super();
		instance=this;
	}
	
	public void init(String worldLoader, String world)
	{
		WorldLoader wl;
		if(worldLoader.equalsIgnoreCase("packed"))
		{
			wl=new PackedWorldLoader();
		}
		else if(worldLoader.equalsIgnoreCase("random"))
		{
			wl=new RandomWorldLoader();
		}
		else
		{
			wl=new DummyWorldLoader();
		}
		
		game = new Game(this);
		game.init();
		game.setWorldLoader(wl);
		game.loadWorld(world);
	}
	
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle b) 
    {
        super.onCreate(b);
        String worldloader=this.getIntent().getExtras().getString("worldloader");
        String worldname=this.getIntent().getExtras().getString("worldname");
        init(worldloader, worldname);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "SWEJNR");
    }
    
    @Override
    protected void onPause() 
    {
    	game.disable();
    	super.onPause();
        wl.release();
    }
    @Override
    protected void onResume() 
    {
        wl.acquire();
    	super.onResume();
    	game.enable();
    }
    
    @Override
    protected void onStop() 
    {
        if(wl.isHeld())
        {
            wl.release();
        }
        super.onStop();
    }
    @Override
    protected void onDestroy() 
    {
        game.close();
        super.onDestroy();
    }
}
