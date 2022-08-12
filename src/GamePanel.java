import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.Random;


import javax.swing.JPanel;

public class GamePanel extends JPanel implements ActionListener
{
	static final int WINDOW_WIDTH = 860;
	static final int WINDOW_HEIGHT = 600;
	static final int CELL_SIZE = 20;
	static final int GAME_UNIT = (WINDOW_WIDTH*WINDOW_HEIGHT)/CELL_SIZE;
	static final int RATE = 100; 
	final int x[] = new int[GAME_UNIT];
	final int y[] = new int[GAME_UNIT];
	int snakeSegments = 6; 
	int eatenApples = 0;
	int appleX;
	int appleY;
	char direction = 'R'; 
	boolean gameIsActive = false;
	boolean menuIsOpen = true;
	Timer timer;
	Random random;
	Font forcedSquare;
	static boolean isPaused = false;
	private BufferedImage snakeImage;
	
	public GamePanel()
	{
		try
		{
			forcedSquare = Font.createFont(Font.TRUETYPE_FONT, new File("FORCED SQUARE.ttf")).deriveFont(70f);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,new File("FORCED SQUARE.ttf")));
		}
		catch(IOException | FontFormatException e)
		{
			e.printStackTrace();
		}
		
		try 
		{
			snakeImage = ImageIO.read(new File("snake.png"));
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
				
		
		random = new Random();
		this.setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
		this.setBackground(new Color(154,197,3));
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
	}
	
	public void startGame()
	{
		newApple();//first app
		gameIsActive = true;
		timer = new Timer(RATE,this);
		timer.start();
		x[0] = 200;
		y[0] = 140;
	}
	public void pause()
	{
		isPaused = true;
		timer.stop();
	}
	public void resume()
	{
		isPaused = false;
		timer.start();
	}
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		draw(g);
	}
	public void draw(Graphics g)
	{	
		if(menuIsOpen == true && gameIsActive == false)
		{
			g.drawImage(snakeImage,0,0,null);
			g.setColor(Color.black);
			g.setFont(forcedSquare.deriveFont(36f));
			g.drawString("PRESS [ENTER] TO PLAY", 224, 575);
		}
		if (gameIsActive) 
		{ 
			Graphics2D frame = (java.awt.Graphics2D) g.create();
			frame.setStroke(new java.awt.BasicStroke(10));
			frame.setColor(Color.black);
			frame.drawRect(35, 95, 790, 470);
			
			g.setColor(Color.black);
			g.fillRect(30, 60, 800, 10);
			
			g.setColor(Color.black);
			g.fillOval(appleX, appleY, CELL_SIZE-3, CELL_SIZE-3);
			
			for (int i = 0; i < snakeSegments; i++) 
			{
				if (i == 0)//snake's head
				{
					g.setColor(new Color(154,197,3));
					g.fillRect(x[i],y[i], CELL_SIZE, CELL_SIZE);
					g.setColor(Color.black);
					g.fillRect(x[i]+1, y[i]+1, 18, 18);
					g.setColor(new Color(145,197,3));
					g.fillRect(x[i]+8,y[i]+8, 5, 5);
				} 
				else//snake's body
				{					
					g.setColor(new Color(154,197,3));
					g.fillRect(x[i],y[i], CELL_SIZE, CELL_SIZE);
					g.setColor(Color.black);
					g.fillRect(x[i]+1, y[i]+1, 18, 18);
					g.setColor(new Color(154,197,3));
					g.fillRect(x[i]+4, y[i]+4, 12, 12);
					g.setColor(Color.black);
					g.fillRect(x[i]+6, y[i]+6, 8, 8);					
				}
			}
			
			g.setColor(Color.black);
			g.setFont(forcedSquare);
			String result = String.format("%04d",eatenApples);
			g.drawString(result, 30, 50);
			
		}
		else if(gameIsActive == false && menuIsOpen == false)
		{
			gameOver(g);
		}
	}
	public void newApple()
	{
		appleX = random.nextInt((int)((WINDOW_WIDTH-60-40)/CELL_SIZE))*CELL_SIZE + 40;
		appleY = random.nextInt((int)((WINDOW_HEIGHT-60-100)/CELL_SIZE))*CELL_SIZE + 100;		
		
		System.out.println(appleX + "," + appleY);
		
	}
	public void move()
	{
		for(int i=snakeSegments;i>0;i--)
		{
			x[i] = x[i-1];
			y[i] = y[i-1];
		}
		
		switch(direction)
		{
		case 'U':
			y[0] = y[0] - CELL_SIZE;
			break;
		case 'D':
			y[0] = y[0] + CELL_SIZE;
			break;
		case 'L':
			x[0] = x[0] - CELL_SIZE;
			break;
		case 'R':
			x[0] = x[0] + CELL_SIZE;
			break;
		}
	}
	public void checkApple()
	{
		if((x[0] == appleX) && (y[0] == appleY))
		{
			snakeSegments++;
			eatenApples++;
			newApple();
		}
	}
	public void checkForCollision()
	{
		for(int i = snakeSegments;i>0;i--) //check if snake's head touches it's body
		{
			if((x[0] == x[i])&&(y[0]==y[i]))
			{
				gameIsActive = false;
			}
		}
		
		if(x[0] < 40)//left wall collision check
		{
			gameIsActive = false;
		}
		if(x[0] > 800)//right wall collision check
		{
			gameIsActive = false;
		}
		if(y[0] < 100)//upper wall collision check
		{
			gameIsActive = false;
		}
		if(y[0] > 540)//bottom wall collision check
		{
			gameIsActive = false;
		}
		
		if(!gameIsActive) //stops game
		{
			timer.stop();
		}
	}
	public void gameOver(Graphics g)
	{
		//GAME OVER TEXT
		g.setColor(Color.black); 
		g.setFont(forcedSquare); 
		g.drawString("GAME OVER", 245,220);
		
		//SCORE TEXT
		g.setColor(Color.black); 
		g.setFont(forcedSquare.deriveFont(40f));
		String rezultat = String.format("%04d",eatenApples);
		g.drawString("SCORE: " + rezultat, 325,294);
		
		//PLAY AGAIN TEXT
		g.setColor(Color.black); 
		g.setFont(forcedSquare.deriveFont(25f));;
		g.drawString("PRESS [R] TO PLAY AGAIN", 272,360);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		if(gameIsActive == true)
		{
			move();
			checkApple();
			checkForCollision();
		}
		repaint();
	}
	
	public class MyKeyAdapter extends KeyAdapter
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			switch(e.getKeyCode())
			{
			case KeyEvent.VK_LEFT:
				if(direction !='R' && isPaused == false && menuIsOpen == false)
				{
					direction = 'L';
				}
				break;
			case KeyEvent.VK_RIGHT:
				if(direction !='L' && isPaused == false && menuIsOpen == false)
				{
					direction = 'R';
				}
				break;
			case KeyEvent.VK_UP:
				if(direction !='D' && isPaused == false && menuIsOpen == false)
				{
					direction = 'U';
				}
				break;
			case KeyEvent.VK_DOWN:
				if(direction !='U' && isPaused == false && menuIsOpen == false)
				{
					direction = 'D';
				}
				break;
			case KeyEvent.VK_ESCAPE://pause game
				if(isPaused == true)
					resume();
				else
					pause();
				break;
			case KeyEvent.VK_R: //restart game
				if (gameIsActive == false && menuIsOpen == false)
				{
					gameIsActive = true;
					startGame();
					x[0] = 200;//set head on x position
					y[0] = 140;//set head on y position
					eatenApples = 0;
					snakeSegments = 6;
					direction = 'R'; //initial moving direction
				}
				break;
			case KeyEvent.VK_ENTER://change view from menu to game
				if(gameIsActive == false && menuIsOpen == true)
					startGame();
				menuIsOpen = false;
				break;
			
			}
		}
	}

}
