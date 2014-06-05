import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class AnimatedPanel extends JPanel {

	private static final long serialVersionUID = -7564181037729291253L;
	private static final int PREF_W = 1200;
	private static final int PREF_H = 100;
	private static final int TIMER_DELAY = 15;
	private LinkedList<BufferedImage> images = new LinkedList<BufferedImage>();
	private BufferedImage rightarrow, leftarrow, uparrow, downarrow;
	private LinkedList<Integer> xLocs = new LinkedList<Integer>();
	private LinkedList<Integer> arrows = new LinkedList<Integer>();
	private int moveSpeed = 1;
	private long totalTime = 0, nextNewTime = 0;
	private static Random r;
	protected javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();
	protected Timer t;

	public AnimatedPanel() {
		r = new Random();
		try {
			rightarrow = ImageIO.read(new File("src/images/arrowright.gif"));
			leftarrow = ImageIO.read(new File("src/images/arrowleft.gif"));
			uparrow = ImageIO.read(new File("src/images/arrowup.gif"));
			downarrow = ImageIO.read(new File("src/images/arrowdown.gif"));
		} catch (IOException e) {
			System.exit(0);
		}

		t = new Timer(TIMER_DELAY, new ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent e) {
				int index = 0;
				boolean remove = false;
				totalTime += TIMER_DELAY;
				if (totalTime >= nextNewTime)
					addArrow();
				for(int imgX : xLocs)
				{
					xLocs.set(index, imgX - moveSpeed);
					if (imgX - moveSpeed <= 0)
					{
						remove = true;
					}
					index++;
				}
				if (remove)
				{
					xLocs.remove(0);
					images.remove(0);
					arrows.remove(0);
					fireMissedArrowEvent();
					remove = false;
				}
				repaint();
			};
		});
		t.start();
	}

	private void addArrow() {
		int arrow = r.nextInt(4);
		nextNewTime = totalTime + r.nextInt(1000) + 500 + (500 / moveSpeed);
		switch(arrow){
		case 0:
			images.add(rightarrow);
			arrows.add(arrow);
			xLocs.add(PREF_W);
			break;
		case 1:
			images.add(leftarrow);
			arrows.add(arrow);
			xLocs.add(PREF_W);
			break;
		case 2:
			images.add(downarrow);
			arrows.add(arrow);
			xLocs.add(PREF_W);
			break;
		case 3:
			images.add(uparrow);
			arrows.add(arrow);
			xLocs.add(PREF_W);
			break;
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(PREF_W, PREF_H);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		//Graphics2D g2 = (Graphics2D) g;
		g.setColor(Color.LIGHT_GRAY);
		//g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				//RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		int index = 0;
		for(BufferedImage image : images)
		{
			if (image != null) 
				g.drawImage(image, xLocs.get(index), 10, 65, 65, this);
			index++;
		}
	}	
	
	public boolean checkArrow(int arrow)
	{
		if (arrows.get(0) == arrow && isCentered())
		{
			xLocs.remove(0);
			images.remove(0);
			arrows.remove(0);
			return true;
		}
		return false;
	}
	
	private boolean isCentered()
	{
		return xLocs.get(0) < 80;
	}
	
	public void addMissedArrowListener(MissedArrowListener mal)
	{
		listenerList.add(MissedArrowListener.class, mal);
	}
	
	public void removeMissedArrowListener(MissedArrowListener mal)
	{
		listenerList.remove(MissedArrowListener.class, mal);
	}
	
	public void stopAnimation()
	{
		t.stop();
	}
	
	private void fireMissedArrowEvent()
	{
		for(MissedArrowListener listener : listenerList.getListeners(MissedArrowListener.class))
			listener.arrowMissed();
	}
}
