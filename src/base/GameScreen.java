package base;

import input.InputFlag;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import render.RendableHolder;

public class GameScreen extends JComponent {
	
	private static final long serialVersionUID = 8847233362005632459L;
	public static int FRAMERATE = 60;
	
	public static int WIDTH;
	public static int HEIGHT;
	
	public GameScreen() {
		initializeInputListener();
	}
	
	private void initializeInputListener() {
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					InputFlag.set(InputFlag.MOUSE_LEFT, false);
				} else if(SwingUtilities.isRightMouseButton(e)) {
					InputFlag.set(InputFlag.MOUSE_RIGHT, false);
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					InputFlag.set(InputFlag.MOUSE_LEFT, true);
				} else if(SwingUtilities.isRightMouseButton(e)) {
					InputFlag.set(InputFlag.MOUSE_RIGHT, true);
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					InputFlag.set(InputFlag.MOUSE_LEFT_CLICK, true);
				} else if(SwingUtilities.isRightMouseButton(e)) {
					InputFlag.set(InputFlag.MOUSE_RIGHT_CLICK, true);
				}
			}
		});
		
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				InputFlag.setMousePosition(e.getX(), e.getY());
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});
		
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {
				InputFlag.set(InputFlag.KEYBOARD, e.getKeyCode(), false);
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				InputFlag.set(InputFlag.KEYBOARD, e.getKeyCode(), true);
			}
		});
	}
	
	public void update() {
		GameState.getInstance().update();
	}
	
	public void updateScreenSize() {
		WIDTH = this.getWidth();
		HEIGHT = this.getHeight();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		RendableHolder.getInstance().draw(g2d);
	}
}
