package frame;

import input.InputFlag;
import input.MapParentListener;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import object.appear.TileBackground;
import object.appear.base.Bazuka;
import object.appear.base.Farm;
import object.appear.base.Ironworks;
import object.appear.base.Light;
import object.appear.base.Logger;
import object.appear.base.MainBase;
import object.appear.base.Shooter1;
import object.appear.base.Shooter2;
import object.appear.base.Shooter3;
import object.appear.base.Shooter4;
import object.appear.base.Sniper;
import object.appear.base.Tank;
import object.appear.base.Warehouse;
import object.appear.bullet.BazukaBullet;
import object.appear.bullet.FastBullet;
import object.appear.zombie.OdinaryZombie;
import object.structure.Base;
import object.structure.BaseAttack;
import object.structure.BaseElement;
import object.structure.BaseShooter;
import object.structure.Bullet;
import object.structure.IAttackable;
import object.structure.IObjectOnScreen;
import object.structure.IPhysical;
import object.structure.Zombie;
import render.RendableHolder;
import render.rendable.CircleRendable;
import render.rendable.Rendable;
import render.rendable.StaticImageRendable;
import essential.Config;
import essential.GameScreen;
import essential.ZIndex;
import frame.logic.GameResource;
import frame.logic.TimeCounter;

public class GameFrame implements Frame {
	
	private static final int bottomHeight = 100;
	private static int centerX, centerY;

	private GameControlPanel controlPanel;

	private MainBase mainBase;
	private ArrayList<Base> baseList;
	private ArrayList<Bullet> bulletList;
	private ArrayList<Zombie> zombieList;
	
	private CircleRendable showRang;
	private static final Color showRangCol = new Color(7, 230, 226, 50);
	
	private IObjectOnScreen currentShowingStat = null;
	private Base dragAndDropObj = null;
	
	private int zombieLevel;
	
	public GameFrame() {
		
		TimeCounter.start();
		GameResource.instance.reset();
		
		RendableHolder.add(new TileBackground("game_bg"));
		
		centerX = GameScreen.WIDTH / 2;
		centerY = (GameScreen.HEIGHT - bottomHeight) / 2;
		
		baseList = new ArrayList<>();
		bulletList = new ArrayList<>();
		zombieList = new ArrayList<>();
		
		initializeRendableObject();
		
		controlPanel = new GameControlPanel(this, GameScreen.HEIGHT - bottomHeight, bottomHeight);
		controlPanel.updateAvailable(baseList);
		
		zombieLevel = 1;
	}
	
	private void initializeRendableObject() {
		mainBase = new MainBase(centerX, centerY, 0.25f);
		mainBase.setName("Main Base");
		mainBase.getSingleRendable().addMouseInteractiveListener(
			new MapParentListener<Base>(mainBase) {
				@Override
				public void onClick(StaticImageRendable object, Base parent) {
					currentShowingStat = parent;
				}
				
			}
		);
		
		baseList.add(mainBase);
		RendableHolder.add(mainBase);
		
		showRang = new CircleRendable(0, 0, 0, showRangCol, ZIndex.EXTERNAL_INFO);
		showRang.setVisible(false);
		RendableHolder.add(showRang);
	}
	
	public void spawnNewBase(String name) {
		
		Base obj = null;
		
		switch(name) {
		case "farm" :
			obj = new Farm(1f);
			break;
		case "ironworks" :
			obj = new Ironworks(0.8f);
			break;
		case "logger" :
			obj = new Logger(0.8f);
			break;
		case "warehouse" : 
			obj = new Warehouse(0.8f);
			break;
		case "shooter1" :
			obj = new Shooter1(0.8f);
			break;
		case "shooter2" :
			obj = new Shooter2(0.8f);
			break;
		case "shooter3" :
			obj = new Shooter3(0.8f);
			break;
		case "shooter4" :
			obj = new Shooter4(0.8f);
			break;
		case "bazuka" :
			obj = new Bazuka(0.4f);
			break;
		case "sniper" :
			obj = new Sniper(0.35f);
			break;
		case "light" :
			obj = new Light(0.65f);
			break;
		case "tank" :
			obj = new Tank(0.3f);
		}
		
		if(obj == null) {
			System.out.println("no name found in spawnNewBase : " + name);
			return ;
		}
		
		for(Rendable each : RendableHolder.getInstance().getRendableList()) {
			each.setListen(false);
		}
		
		RendableHolder.add((IObjectOnScreen) obj);
			
		dragAndDropObj = obj;
	}
	
	public ArrayList<Base> getBaseList() {
		return baseList;
	}
	
	public ArrayList<Bullet> getBulletList() {
		return bulletList;
	}
	
	public ArrayList<Zombie> getZombieList() {
		return zombieList;
	}
	
	private void clearDestroyedObject() {
		
		boolean isBaseRemove = false;
		
		for(Iterator<Base> it = baseList.iterator(); it.hasNext(); ) {
			Base cur = it.next();
			if(cur.isDestroy() || cur.isDie()) {
				isBaseRemove = true;
				cur.destroy();
				it.remove();
				cur = null;
			}
		}
		
		if(isBaseRemove) {
			updateStat();
		}
		
		for(Iterator<Bullet> it = bulletList.iterator(); it.hasNext(); ) {
			Bullet cur = it.next();
			if(cur.isDestroy() || cur.isOutOfArea()) {
				cur.destroy();
				it.remove();
				cur = null;
			}
		}
		
		for(Iterator<Zombie> it = zombieList.iterator(); it.hasNext(); ) {
			Zombie cur = it.next();
			if(cur.isDestroy()) {
				cur.destroy();
				it.remove();
				cur = null;
			}
		}
	}

	private void updateStat() {
		GameResource.instance.updateBaseStat(baseList);
		GameResource.instance.updateStatRender();
		
		controlPanel.updateAvailable(baseList);
	}
	
	@Override
	public void update() {
		
		clearDestroyedObject();
		
		if(dragAndDropObj != null) {
			dragAndDropObj.getSingleRendable().setPos(InputFlag.getMouseX(), InputFlag.getMouseY());
			
			if(InputFlag.getTrigger(InputFlag.MOUSE_LEFT)) {
				dragAndDropObj.getSingleRendable().addMouseInteractiveListener(
					new MapParentListener<Base>(dragAndDropObj){
						@Override
						public void onClick(StaticImageRendable object, Base parent) {
							currentShowingStat = parent;
						}
						
					}
				);
				
				if(Config.DEBUG) {
					RendableHolder.add(new CircleRendable(
						dragAndDropObj.getPosX(), 
						dragAndDropObj.getPosY(), 
						dragAndDropObj.getPhysicalRadius(), 
						Color.RED, 
						Integer.MAX_VALUE
					));
				}
				
				baseList.add(dragAndDropObj);
				GameResource.instance.addIron(-dragAndDropObj.getIronRequire());
				GameResource.instance.addWood(-dragAndDropObj.getWoodRequire());
				
				
				
				dragAndDropObj = null;
			} else if(InputFlag.getTrigger(InputFlag.MOUSE_RIGHT)) {
				RendableHolder.remove(dragAndDropObj);
				dragAndDropObj = null;
			}
			
			for(Rendable each : RendableHolder.getInstance().getRendableList()) {
				each.setListen(true);
			}
			
			updateStat();
		}
		
		if(currentShowingStat != null) {
			if(currentShowingStat instanceof IPhysical && ((IPhysical) currentShowingStat).isDestroy()) {
				currentShowingStat = null;
				showRang.setVisible(false);
				controlPanel.statClear();
			} else {
				controlPanel.showStat(currentShowingStat);
			
				if(currentShowingStat instanceof BaseAttack && !(currentShowingStat instanceof BaseShooter)) {
					BaseAttack ba = (BaseAttack) currentShowingStat;
					if(ba.getRang() != Integer.MAX_VALUE) {
						showRang.setVisible(true);
						showRang.setPos(ba.getPosX(), ba.getPosY());
						showRang.setRadius(ba.getRang());
					}
				} else {
					showRang.setVisible(false);
				}
			}
		}
		
		for(Base base : baseList) {
			if(base instanceof BaseShooter) {
				((BaseShooter) base).rotateTo(InputFlag.getMouseX(), InputFlag.getMouseY());
			}
			
			if(base instanceof IAttackable) {
				IAttackable atk = (IAttackable) base;
				atk.increaseTime();
				if(atk.isAttack()) {
					atk.attack(this);
				}
			}
		}
		
		for(Bullet bullet : bulletList) {
			bullet.update();
			for(Zombie zombie : zombieList) {
				if(bullet.isHitTest(zombie)) {
					if(!(bullet instanceof BazukaBullet || bullet instanceof FastBullet))
						bullet.destroy();
					bullet.attack(zombie);
				}
			}
		}
		
		for(Zombie zombie : zombieList) {
			zombie.update(baseList);
		}
		
		if(TimeCounter.shouldSpawnZombie()) {
			for(int i=0; i<5; i++) {
				Zombie zombie = new OdinaryZombie(zombieLevel);
				zombie.addMouseInteractiveListener(new MapParentListener<Zombie>(zombie){
	
					@Override
					public void onClick(StaticImageRendable object, Zombie parent) {
						currentShowingStat = parent;
					}
					
				});
				zombieList.add(zombie);
				RendableHolder.add(zombie);
				TimeCounter.setShouldSpawnZombie(false);
			}
			
			zombieLevel++;
		}
		
		if(TimeCounter.isNewSecond()) {
			
			for(Zombie zombie : zombieList) {
				zombie.updateCombatStatus();
			}
			
			for(Base base : baseList) {
				if(base instanceof BaseElement) {
					GameResource.instance.addIron(((BaseElement) base).getGiveIron());
					GameResource.instance.addWood(((BaseElement) base).getGiveWood());
					GameResource.instance.updateStatRender();
				}
			}
			
			TimeCounter.setNewSecond(false);
		}
	}
	
	@Override
	public void pause() {
	}

	@Override
	public void destroy() {
		
		TimeCounter.stop();
		
		baseList.clear();
		bulletList.clear();
		zombieList.clear();
		mainBase.destroy();
	}

}