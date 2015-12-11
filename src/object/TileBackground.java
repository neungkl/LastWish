package object;

import java.util.ArrayList;

import render.Rendable;
import render.RendableHolder;
import render.StaticImageRendable;
import base.GameScreen;
import base.ZIndex;

public class TileBackground implements IObjectOnScreen {
	
	private ArrayList<Rendable> bg;
	
	public TileBackground(String file) {
		this(file, 1f);
	}
	
	public TileBackground(String file, float ratio) {
		
		bg = new ArrayList<>();
		
		StaticImageRendable tmp = new StaticImageRendable(file);
		int width = tmp.getWidth();
		int height = tmp.getHeight();

		for(int i=0; i<=GameScreen.WIDTH; i += width) {
			for(int j=0; j<=GameScreen.HEIGHT; j += height) {
				tmp = new StaticImageRendable(file, i, j);
				tmp.setZ(ZIndex.BACKGROUND);
				RendableHolder.getInstance().add(tmp);
				bg.add(tmp);
			}
		}
	}

	@Override
	public ArrayList<Rendable> getRendable() {
		return bg;
	}
}
