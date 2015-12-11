package render;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class RenderHelper {
	public static final int LEFT = 0;
	public static final int CENTER = 1;
	public static final int RIGHT = 2;
	public static final int TOP = 0;
	public static final int MIDDLE = 4;
	public static final int BOTTOM = 8;
	public static final int CENTER_MIDDLE = CENTER | MIDDLE;
	
//	static final AlphaComposite transcluentWhite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
//	static final AlphaComposite opaque = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
	
	private static final int getOffsetX(int x, int width, int align) {
		if((align & CENTER) != 0) {
			x -= width / 2;
		} else if((align & RIGHT) != 0) {
			x -= width;
		}
		return x;
	}
	
	private static final int getOffsetY(int y, int height, int align) {
		if((align & MIDDLE) != 0) {
			y -= height / 2;
		} else if((align & BOTTOM) != 0) {
			y -= height;
		}
		return y;
	}
	
	public static void draw(Graphics2D g, BufferedImage img, int x, int y, int width, int height, int align) {
		x = getOffsetX(x, width, align);
		y = getOffsetY(y, height, align);
		
		g.drawImage(img, x, y, width, height, null);
	}
	
	public static void drawHoverEffect(Graphics2D g, BufferedImage img, int x, int y, int width, int height, int align) {
		x = getOffsetX(x, width, align);
		y = getOffsetY(y, height, align);
		
		BufferedImage newImg = new BufferedImage(
			img.getColorModel(),
			img.copyData(null),
			img.isAlphaPremultiplied(),
			null
		);
		RescaleOp rescaleOp = new RescaleOp(2f, 1, null);
		newImg = rescaleOp.filter(newImg, newImg);
		g.drawImage(newImg, x, y, width, height, null);
	}
	
	public static void drawString(Graphics2D g, String text, int x, int y, int width, int height, int align) {
		x = getOffsetX(x, width, align);
		y = getOffsetY(y, height, align);
		
		g.drawString(text, x, y);
	}
	
	public static boolean isHitTest(int x, int y, int sx, int sy, int width, int height, int align) {
		sx = getOffsetX(sx, width, align);
		sy = getOffsetY(sy, height, align);
		
		if(sx <= x && x <= sx + width && sy <= y && y <= sy + height) {
			return true;
		}
		return false;
	}
}
