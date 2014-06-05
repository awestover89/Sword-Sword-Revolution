import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameWindow extends JPanel implements MissedArrowListener{

	private static final long serialVersionUID = 8121004801717571097L;
	private static JLabel mainWindow, arrowBox;
	private static ImageIcon forward, backward, left, right, ready, miss, basicmiss, critmiss, death, dead;
	protected static JFrame frame;
	protected static ScheduledFuture<?> scheduledFuture, scheduledFutureArrowMover;
	protected static ScheduledExecutorService scheduledExecutorService, scheduledExecutorServiceArrowMover;
	protected static AnimatedPanel arrows;
	protected static int missesLeft = 20;

	public static int x = 1200;

	public GameWindow(){
		buildImages();
		mainWindow = new JLabel(ready);
		arrows = new AnimatedPanel();
		arrows.addMissedArrowListener(this);
		arrows.setSize(1200, 100);
		arrows.setLayout(new FlowLayout(FlowLayout.LEFT));
		arrowBox = new JLabel("");
		arrowBox.setBounds(0, 0, 80, 80);
		arrowBox.setPreferredSize(new Dimension(80, 80));
		arrowBox.setBorder(BorderFactory.createLineBorder(Color.red));
		arrows.add(arrowBox);
		this.setSize(1200,800);
		this.setLayout(new BorderLayout());
		this.add(mainWindow, BorderLayout.CENTER);
		this.add(arrows, BorderLayout.SOUTH);
	}

	private void buildImages() {
		forward = new ImageIcon("src/images/forward.gif");
		backward = new ImageIcon("src/images/backward.gif");
		left = new ImageIcon("src/images/left.gif");
		right = new ImageIcon("src/images/right.gif");
		ready = new ImageIcon("src/images/ready.gif");
		miss = new ImageIcon("src/images/miss.gif");
		basicmiss = new ImageIcon("src/images/basicmiss.gif");
		critmiss = new ImageIcon("src/images/critmiss.gif");
		death = new ImageIcon("src/images/death.gif");
		dead = new ImageIcon("src/images/dead.gif");
	}

	public static void main(String[] args)
	{
		GameWindow g = new GameWindow();
		frame = new JFrame("Sword Sword Revolution");
		frame.add(g);
		frame.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e) { processKey(e.getKeyCode()); }

			public void keyReleased(KeyEvent e) {  }

			public void keyTyped(KeyEvent e) {  }
		});
		frame.setSize(1200,800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static void processKey(int key)
	{
		if (missesLeft >= 0){
			try { 
				scheduledFuture.cancel(true); 
			} catch(Exception ex){}
			switch(key)
			{
			case 37:
				if (arrows.checkArrow(1))
					handleHit(left);
				else
					handleMiss(basicmiss);
				break;
			case 38:
				if (arrows.checkArrow(3))
					handleHit(forward);
				else
					handleMiss(basicmiss);
				break;
			case 39:
				if (arrows.checkArrow(0))
					handleHit(right);
				else
					handleMiss(basicmiss);
				break;
			case 40:
				if (arrows.checkArrow(2))
					handleHit(backward);
				else
					handleMiss(basicmiss);
				break;
			}
		}
	}

	private static void handleMiss(ImageIcon missType) {
		missesLeft--;
		if (missesLeft < 0)
		{
			mainWindow.setIcon(death);
			frame.repaint();
			scheduledExecutorService =
					Executors.newScheduledThreadPool(1);

			scheduledFuture =
					scheduledExecutorService.schedule(new Runnable() {
						public void run() {
							mainWindow.setIcon(dead);
						}
					},
					2,
					TimeUnit.SECONDS);

			scheduledExecutorService.shutdown();
			arrows.stopAnimation();
		}
		else
		{
			mainWindow.setIcon(missType);
			frame.repaint();
			scheduledExecutorService =
					Executors.newScheduledThreadPool(1);

			scheduledFuture =
					scheduledExecutorService.schedule(new Runnable() {
						public void run() {
							mainWindow.setIcon(ready);
						}
					},
					1,
					TimeUnit.SECONDS);

			scheduledExecutorService.shutdown();
		}
	}
	
	private static void handleHit(ImageIcon moveType)
	{
		if (missesLeft >= 0)
		{
			mainWindow.setIcon(moveType);
			frame.repaint();
			scheduledExecutorService =
					Executors.newScheduledThreadPool(1);

			scheduledFuture =
					scheduledExecutorService.schedule(new Runnable() {
						public void run() {
							mainWindow.setIcon(ready);
						}
					},
					1,
					TimeUnit.SECONDS);

			scheduledExecutorService.shutdown();
		}
	}

	@Override
	public void arrowMissed() {
		try { 
			scheduledFuture.cancel(true); 
		} catch(Exception ex){}
		handleMiss(critmiss);
	}

}
