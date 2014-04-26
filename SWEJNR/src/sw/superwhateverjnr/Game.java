package sw.superwhateverjnr;

import android.util.DisplayMetrics;
import android.view.MotionEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import sw.superwhateverjnr.entity.Player;
import sw.superwhateverjnr.scheduling.Scheduler;
import sw.superwhateverjnr.settings.Settings;
import sw.superwhateverjnr.texture.DummyTextureLoader;
import sw.superwhateverjnr.texture.TextureLoader;
import sw.superwhateverjnr.texture.TextureMap;
import sw.superwhateverjnr.ui.GameView;
import sw.superwhateverjnr.util.IdAndSubId;
import sw.superwhateverjnr.world.DummyWorldLoader;
import sw.superwhateverjnr.world.Location;
import sw.superwhateverjnr.world.World;
import sw.superwhateverjnr.world.WorldLoader;

@Getter
@ToString
@EqualsAndHashCode
public class Game
{
	@Getter
	private static Game instance;
	
	private Player player;
	private Location minDisplayPoint;
	
	private int displayWidth;
	private int displayHeight;
	
	private int textureWidth;
	private int textureHeight;
	
	@Getter
	private GameView gameView;
	
	
	private WorldLoader worldLoader;
	private TextureLoader textureLoader;
	
	private World world;
	
	@Getter
	private Settings settings;
	
	@Getter
	private Scheduler scheduler;
	
	public Game()
	{
		instance=this;
		
		player=new Player(null);
		minDisplayPoint=new Location(0, 0);
		
		DisplayMetrics metrics = SWEJNR.getInstance().getResources().getDisplayMetrics();
		displayWidth=metrics.widthPixels;
		displayHeight=metrics.heightPixels;
		
		textureWidth=64;
		textureHeight=64;
		
		settings=new Settings();
		loadSettings();
		
		scheduler=new Scheduler();
		
		worldLoader=new DummyWorldLoader();
		try
		{
			world=worldLoader.loadWorld("bla");
		}
		catch(Exception e) {}
		
		
		textureLoader=new DummyTextureLoader();
		setupTextures();
		
		player.teleport(world.getSpawnLocation());
		
		gameView=new GameView(SWEJNR.getInstance());
		FullscreenActivity.getInstance().setContentView(gameView);
	}

	private void setupTextures()
	{
		TextureMap.loadTexture(new IdAndSubId(1, -1), textureLoader);
	}
	
	private void loadSettings()
	{
		
	}

	public void handleGameTouchEvent(MotionEvent event)
	{
		
		
	}
	public void handlePlayerMoveEvent(Location from, Location to)
	{
		
		
	}
}
