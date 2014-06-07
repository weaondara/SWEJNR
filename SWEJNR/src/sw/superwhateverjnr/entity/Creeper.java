package sw.superwhateverjnr.entity;

import java.util.Map;
import java.util.Random;

import lombok.SneakyThrows;
import sw.superwhateverjnr.Game;
import sw.superwhateverjnr.block.Block;
import sw.superwhateverjnr.block.BlockFactory;
import sw.superwhateverjnr.block.Material;
import sw.superwhateverjnr.util.Rectangle;
import sw.superwhateverjnr.world.Location;
import sw.superwhateverjnr.world.World;

public class Creeper extends Entity
{
	private final static int MAXCOUNTERWALK = 3; 
	
	private static Player player;
	private static Game game;
	private static World world;
	
	private static boolean isindistance = false;
	private static boolean islavainfront = false;

	private static boolean isgoingright = false;
	private static boolean isgoinghorizontal = false;
	private static boolean isgoingup = false;
	private static boolean isgoingvertical = false;
	
	private final static double runningMin = 0.75;
	private final static double runningMax = 2.25;
	private final static double runPower = 0.0015;
	private final static double jumpPower = 7.0;
	private static double distance = 0.0;
	private final static double radius = 5.0;
	
	private static double[] randomtimewalk = {0.0, 0.0};
	private static int counterright = 0; // e.g. counterright is 3, the next time the entitiy "must" go left!!!
	private static int counterleft = 0; // and set counterright = 0 and then countleft++
	private static boolean israndomgoing = false;
	private static boolean israndomgoingright = false;
	
	private static double[] randomtimejump = {0.0, 0.0};
	private static boolean israndomjump = false;
	private static boolean israndomjumpcompleted = false;
	
	private static boolean triggerexplosion = false;

	private static boolean[] ischanged = {false,false,false,false,false,false};

	private Random random = new Random();
	
	public Creeper(int id, EntityType type, Location location, Map<String, Object> extraData)
	{
		super(EntityType.CREEPER, location, extraData);
		player=Game.getInstance().getPlayer();
		game=Game.getInstance();
	}

	@Override
	protected void die()
	{
		
	}
	
	@Override
	public void tick()
	{
		super.tick();
		trigger();
		randomJump(false);
		randomWalk(true);
		jumpIfWall();
		stopIfLava();
		//swimIfWater();
		tickMove();
	}
	@SneakyThrows
	protected void trigger()
	{	
		double centerxplayer = player.getLocation().getX();
		double centerxmonster = getLocation().getX();
		double centeryplayer = player.getLocation().getY();
		double centerymonster = getLocation().getY();
		distance = Math.sqrt(Math.pow(centerxplayer - centerxmonster, 2.0) + Math.pow(centeryplayer - centerymonster, 2.0));
		//double distance = Math.sqrt(Math.pow(Math.abs(centerxplayer) - Math.abs(centerxmonster), 2.0) + Math.pow(Math.abs(centeryplayer) - Math.abs(centerymonster), 2.0));

		if (!triggerexplosion && (distance < radius))
		{
			world=game.getWorld();
			BlockFactory bf = BlockFactory.getInstance();
			//world.createExplosion(new Location(11,10), 3, 1);
			game.getWorld().setBlockAt(9, 10, bf.create(Material.AIR.getId(), (byte)0, 9, 10, game.getWorld(), null));
			triggerexplosion = !triggerexplosion;
			System.out.println("BOOMMM!!!!");
		}
		if (distance < radius)
		{
			isindistance = true;
			if (centerxplayer > centerxmonster)
			{
				setMovingright(true);
				setMovingleft(false);
				isgoingright = true;
				isgoinghorizontal = true;
			}
			else if (centerxplayer < centerxmonster)
			{
				setMovingright(false);
				setMovingleft(true);
				isgoingright = false;
				isgoinghorizontal = true;
			}
			else
			{
				isgoinghorizontal = false;
			}
			
			if (centeryplayer > centerymonster)
			{
				isgoingup = true;
				isgoingvertical = true;
			}
			else if (centeryplayer < centerymonster)
			{
				isgoingup = false;
				isgoingvertical = true;
			}
			else
			{
				isgoingvertical = false;
			}
		}
		else
		{
			isindistance = false;
			/*
			if (!israndomgoing)
			{
				setMovingright(false);
				setMovingleft(false);
			}
			*/
		}
	}
	
	protected void randomJump(boolean dorandomjump)
	{
		if (isindistance && dorandomjump)
		{
			// not completed
			if (isOnGround() && israndomjump)
			{
				jump();
				israndomjump = false;
			}
			else if (isOnGround() && !israndomjump)
			{
				israndomjumpcompleted = true;
			}
			if  ((randomtimejump[0] > randomtimejump[1]) && israndomjumpcompleted)
			{
				israndomjump = true;
				israndomjumpcompleted = false;
				randomtimejump[0] = 0.0;
				randomtimejump[1] = roundNumber(random.nextDouble() * 3 + 2.0, 3);
				//System.out.print("RANDOMJUMP: "+"israndomjump = "+israndomjump+" "+"israndomjumpcompleted = "+israndomjumpcompleted+" "+"randomtimejump_1 = "+randomtimejump[1]+"\n");
			}
			long now=System.currentTimeMillis();
			randomtimejump[0] += (double)(now - getLastMoveTime()) / 1000.0;
		}
		else
		{
			israndomjump = false;
			israndomjumpcompleted = false;
		}
	}
	
	protected void randomWalk(boolean dorandomwalk)
	{
		if (!isindistance && dorandomwalk)
		{
			if (randomtimewalk[0] < randomtimewalk[1])
			{
				if (israndomgoing)
				{
					if (israndomgoingright)
					{
						setMovingright(true);
						setMovingleft(false);
					}
					else
					{
						setMovingright(false);
						setMovingleft(true);
					}
				}
				else
				{
					setMovingright(false);
					setMovingleft(false);
				}
			}
			else
			{
				setMovingright(false);
				setMovingleft(false);
				randomtimewalk[0] = 0.0;
				randomtimewalk[1] = roundNumber(random.nextDouble() * 2 + 1.5, 3);
				israndomgoingright = random.nextBoolean();
				israndomgoing = !israndomgoing;
				if (israndomgoing && israndomgoingright)
				{
					counterright++;
					counterleft = 0;
				}
				else if (israndomgoing && !israndomgoingright)
				{
					counterright = 0;
					counterleft++;
				}
				if (counterright > MAXCOUNTERWALK)
				{
					counterright = 0;
					counterleft = 1;
					israndomgoingright = false;
				}
				if (counterleft > MAXCOUNTERWALK)
				{
					counterright = 1;
					counterleft = 0;
					israndomgoingright = true;
				}
				// Test output!
				//System.out.print("RANDOMWALK: counterright = "+counterright+" "+"counterleft = "+counterleft+" "+"israndomgoing = "+israndomgoing+" "+"israndomgoingright = "+israndomgoingright+" "+"randomtimewalk_1 = "+randomtimewalk[1]+"\n");
			}
			long now=System.currentTimeMillis();
			randomtimewalk[0] += (double)(now - getLastMoveTime()) / 1000.0;
		}
		else
		{
			israndomgoing = false;
		}
	}
	
	protected void jumpIfWall()
	{
		if (isgoinghorizontal || israndomgoing)
		{
			double monsterblockx = location.getX();
			double monsterblocky = location.getY();
			double addx = 0.7;
			double addy = 0.0;
			
			Material materialx0 = game.getWorld().getBlockAt(new Location(monsterblockx, monsterblocky + addy)).getType();
			Material materialxp1 = game.getWorld().getBlockAt(new Location(monsterblockx + addx, monsterblocky + addy)).getType();
			Material materialxm1 = game.getWorld().getBlockAt(new Location(monsterblockx - addx, monsterblocky + addy)).getType();
			/*
			if (isOnGround())
			{
				System.out.print("blockxm1 = "+blockxm1+"   "+"blockx0 = "+blockx0+"   "+"blockxp1 = "+blockxp1+"\n");
			}
			*/
			if ((ischanged[0] != (isgoingright || israndomgoingright)) ||
				(ischanged[1] != (materialxp1 != materialx0)) ||
				(ischanged[2] != ((double)(monsterblockx - (int)monsterblockx)) > (-addx)) ||
				(ischanged[3] != (!isgoingright || !israndomgoingright)) ||
				(ischanged[4] != (materialxm1 != materialx0)) ||
				(ischanged[5] != ((double)(monsterblockx - (int)monsterblockx) < addx)))
			{
				ischanged[0] = (isgoingright || israndomgoingright);
				ischanged[1] = (materialxp1 != materialx0);
				ischanged[2] = ((double)(monsterblockx - (int)monsterblockx)) > (-addx);
				ischanged[3] = (!isgoingright || !israndomgoingright);
				ischanged[4] = (materialxm1 != materialx0);
				ischanged[5] = ((double)(monsterblockx - (int)monsterblockx) < addx);
				//System.out.println("right "+"= "+ischanged[0]+"  "+"materialpx1= "+ischanged[1]+"  "+"1-addx= "+ischanged[2]+"  "+"!right="+ischanged[3]+"  "+"materialmx1="+ischanged[4]+"  "+"addx="+ischanged[5]);
			}
			
			if (!(distance < 0.2) &&
					(
					(((isgoingright && !player.isOnGround()) || israndomgoingright) && (materialxp1 != materialx0)) ||
					(((!isgoingright && !player.isOnGround()) || !israndomgoingright)
							&& (materialxm1 != materialx0))
					)
				)
			{
				jump();
			}
		}
	}
	
	protected void stopIfLava()
	{
		//stop instantly!!!
	}
	
	/*protected void swimIfWater()
	{
		if (isindistance)
		{
			//swim to catch player
		}
		else
		{
			//swim away from player
		}
	}*/
	
	private void tickMove()
	{
		if(location==null || world()==null)
		{
			return;
		}
		Rectangle bounds=getHitBox();
		long now=System.currentTimeMillis();
		long time=now-getLastMoveTime();
		
		double vx=velocity.getX();
		if(isMovingleft() && !isMovingright())
		{
			if(vx>-runningMin)
			{
				vx=-runningMin;
			}
			else
			{
				vx*=(1+runPower*time*(runningMax+vx));
			}
		}
		else if(isMovingright() && !isMovingleft())
		{
			if(vx<runningMin)
			{
				vx=runningMin;
			}
			else
			{
				vx*=(1+runPower*time*(runningMax-vx));
			}
		}
		else //x decelerate
		{
			double d=runPower*time*(Math.abs(vx)+runningMin);
			d*=3;
			if(d>1)
			{
				d=1;
			}
			else if(d<0)
			{
				d=0;
			}
			
			vx*=(1-d);
		}

		getVelocity().setX(vx);
		setLastMoveTime(now);

		
		
		
		
		float multiplier=0.01F;
		float playerwidth=(float) (Math.abs(bounds.getMin().getX()-bounds.getMax().getX()));
		
		//world check
		double x=location.getX();
		x+=velocity.getX()*multiplier;
		if(x<0)
		{
			x=0;
			velocity.setX(0);
		}
		if(x>=world().getWidth())
		{
			x=world().getWidth()-0.0000001;
			velocity.setX(0);
		}
		
		//block check
		try
		{
			Location l1=new Location(x-playerwidth/2,location.getY());
			Location l2=new Location(x-playerwidth/2,location.getY()+1);
			Block b1=world().getBlockAt(l1);
			Block b2=world().getBlockAt(l2);
			if(b1.getType().isSolid() || b2.getType().isSolid())
			{
				if(velocity.getX()<0)
				{
					x=Math.ceil(x-playerwidth/2)+playerwidth/2;
					velocity.setX(0);
				}
			}
		}
		catch(Exception e){}
		
		try
		{
			Location l3=new Location(x+playerwidth/2,location.getY());
			Location l4=new Location(x+playerwidth/2,location.getY()+1);
			Block b3=world().getBlockAt(l3);
			Block b4=world().getBlockAt(l4);
			if(b3.getType().isSolid() || b4.getType().isSolid())
			{
				if(velocity.getX()>0)
				{
					x=Math.floor(x+playerwidth/2)-playerwidth/2;
					velocity.setX(0);
				}
			}
		}
		catch(Exception e){}
			
		location.setX(x);
	}
	
	double roundNumber(double number, int digits)
	{
		return (double)((int)(number * (double)Math.pow(10.0, (double)digits))) / (double)Math.pow(10.0, (double)digits);
	}
}