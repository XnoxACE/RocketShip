/*
 
https://drive.google.com/file/d/1ujF-j6jPH_okOwyfJf5eqE6ZxmtkHgXs/view?usp=sharing
 
https://drive.google.com/file/d/1N44MW4nNVh3vTBwHtbn6AxrXlvD2UHZl/view?usp=sharing
 
https://drive.google.com/file/d/10FRCDQ0T0m6BYQDrpShp-L609kwx6i2T/view?usp=sharing
 
https://drive.google.com/file/d/1QcMGhKfz0qoj_y-3W1UUAul3kiZspUhG/view?usp=sharing
 
*/
package rocketship;
import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.zip.CheckedInputStream;
 
public class RocketShip extends JFrame implements Runnable {
 
    boolean gameOver;
    boolean animateFirstTime = true;
    Image image;
    Graphics2D g;
   
    Image outerSpaceImage;
    int score;
    int highScore;
   
    Rocket rocket;
    int timeCount;
   
   // Missile missile;
   
    static RocketShip frame;
    public static void main(String[] args) {
        frame = new RocketShip();
        frame.setSize(Window.WINDOW_WIDTH, Window.WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
 
    public RocketShip() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button
 
 
                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });
 
    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });
 
    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {
         
        repaint();
      }
    });
 
        addKeyListener(new KeyAdapter() {
 
            public void keyPressed(KeyEvent e) {
                if (gameOver)
                    return;
               
                if (e.VK_UP == e.getKeyCode()) {
                    rocket.MoveVert(1);
                } else if (e.VK_DOWN == e.getKeyCode()) {
                    rocket.MoveVert(-1);
                } else if (e.VK_LEFT == e.getKeyCode()) {
                    rocket.MoveHoriz(-1);                    
                } else if (e.VK_RIGHT == e.getKeyCode()) {
                    rocket.MoveHoriz(1);
                } else if (e.VK_SPACE == e.getKeyCode()) {
                    //missile = new Missile(rocket);
                    Missile.Shoot(rocket);
                }
                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }
 
 
 
////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || Window.xsize != getSize().width || Window.ysize != getSize().height) {
            Window.xsize = getSize().width;
            Window.ysize = getSize().height;
            image = createImage(Window.xsize, Window.ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, Window.xsize, Window.ysize);
 
        int x[] = {Window.getX(0), Window.getX(Window.getWidth2()), Window.getX(Window.getWidth2()), Window.getX(0), Window.getX(0)};
        int y[] = {Window.getY(0), Window.getY(0), Window.getY(Window.getHeight2()), Window.getY(Window.getHeight2()), Window.getY(0)};
//fill border
        g.setColor(Color.white);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);
 
        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }
 
        g.drawImage(outerSpaceImage,Window.getX(0),Window.getY(0),Window.getWidth2(),Window.getHeight2(),this);
        Missile.Paint(g);
        rocket.Draw(g,this);
 
        Star.Paint(g,this);
        
        g.setColor(Color.black);
        g.setFont (new Font ("Arial",Font.PLAIN, 25));
        g.drawString("Score: " + score, 60, 60);
        
        g.setFont (new Font ("Arial",Font.PLAIN, 25));
        g.drawString("High Score: " + highScore, 600, 60);
       
        if (gameOver)        
        {
            g.setColor(Color.white);
            g.setFont (new Font ("Arial",Font.PLAIN, 50));
            g.drawString("GAME OVER", 60, 400);
        }                
       
        gOld.drawImage(image, 0, 0, null);
       
    }
 
////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.02;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {
 
 
        gameOver = false;
        score = 0;
        timeCount = 0;
        rocket = new Rocket();    
        Missile.Reset();
        Star.Reset();
    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (Window.xsize != getSize().width || Window.ysize != getSize().height) {
                Window.xsize = getSize().width;
                Window.ysize = getSize().height;
            }
            outerSpaceImage = Toolkit.getDefaultToolkit().getImage("./outerSpace.jpg");  
            Rocket.Init();            
            Star.Init();            
            reset();
        }
       
        if (gameOver)
            return;
        
        if(score > highScore)
            highScore = score;
       
        gameOver = Star.Collide(rocket.xpos,rocket.ypos);
       
        rocket.animate();
        Star.Animate(-rocket.xSpeed,timeCount);
        Missile.Animate(frame);
       
        timeCount++;
    }
 
////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
}
 
 
class Window {
   
    static final int WINDOW_WIDTH = 800;
    static final int WINDOW_HEIGHT = 600;
    static final int XBORDER = 0;
    static final int YBORDER = 40;
    static final int WINDOW_BORDER = 8;
    static final int YTITLE = 25;
    static int xsize = -1;
    static int ysize = -1;  
   
 /////////////////////////////////////////////////////////////////////////
    public static int getX(int x) {
        return (x + XBORDER);
    }
 
    public static int getY(int y) {
        return (y + YBORDER + YTITLE);
    }
 
    public static int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
   
   
    public static int getWidth2() {
        return (xsize - getX(0) - XBORDER);
    }
 
    public static int getHeight2() {
        return (ysize - getY(0) - YBORDER);
    }    
}
 
class Rocket {
    private static int maxSpeed = 10;
    private static Image rocketImage;
    int xpos;
    int ypos;
    int ySpeed;
    int xSpeed;
    boolean faceRight;
    static boolean rocketMove;
   
 
    Rocket()
    {
        faceRight = true;
        ySpeed = 0;
        xpos = Window.getWidth2()/2;
        ypos = Window.getHeight2()/2;
        
    }
 
    public static void Init()
    {
        
            
    
        rocketImage = Toolkit.getDefaultToolkit().getImage("./rocket.GIF");
        
        
    }
   
    public void MoveVert(int delta)
    {
        ySpeed += delta;
       
        if (ySpeed > maxSpeed)
            ySpeed = maxSpeed;
        if (ySpeed < -maxSpeed)
            ySpeed = -maxSpeed;
    }    
    public void MoveHoriz(int delta)
    {
        xSpeed += delta;
        if (xSpeed > 0)
            faceRight = true;
        if (xSpeed < 0)
            faceRight = false;
       
        if (xSpeed > maxSpeed)
            xSpeed = maxSpeed;        
        if (xSpeed < -maxSpeed)
            xSpeed = -maxSpeed;        
    }      
   
    public void animate()
    {
        ypos += ySpeed;
        if ( ypos >= Window.getHeight2() ||  ypos <= 0)
        {
            ySpeed = -ySpeed;
        }
        if(xSpeed != 0 || ySpeed != 0)
            rocketMove = true;
        if(xSpeed == 0 && ySpeed == 0)
            rocketMove = false;          
        
        if(rocketMove){
            rocketImage = Toolkit.getDefaultToolkit().getImage("./animRocket (1).GIF");
        }
        if(!rocketMove){
            rocketImage = Toolkit.getDefaultToolkit().getImage("./rocket.GIF");
        }
    }
   
    public void Draw(Graphics2D g,RocketShip thisObj)
    {
       
        if (faceRight)
            drawImage(g,thisObj,rocketImage,Window.getX(xpos),Window.getYNormal(ypos),0.0,2.0,2.0 );
        else
            drawImage(g,thisObj,rocketImage,Window.getX(xpos),Window.getYNormal(ypos),0.0,-2.0,2.0 );
 
    }
    public void drawImage(Graphics2D g,RocketShip thisObj,Image image,int xpos,int ypos,double rot,double xscale,double yscale) {
        int width = image.getWidth(thisObj);
        int height = image.getHeight(thisObj);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );
 
        g.drawImage(image,-width/2,-height/2,
        width,height,thisObj);
 
        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }    
}
 
class Star {
    private static ArrayList<Star> stars = new ArrayList<Star>();    
   
    private static Image starImage;
    int xpos;
    int ypos;
    int starSize;
    
 
    Star(int speed)
    {
        if (speed < 0)
            xpos = Window.getWidth2();
        else
            xpos = 0;
           
        ypos = (int)(Math.random()*Window.getHeight2());
        starSize =(int)(Math.random()*3) + 1;
    }
 
    public static void Reset()
    {    
        stars.clear();
    }
   
    public static void Init()
    {
        starImage = Toolkit.getDefaultToolkit().getImage("./starAnim.GIF");
       
    }
   
    public static boolean Collide(int rocketXPos,int rocketYPos)  
    {
        for (int i=0;i<stars.size();i++)
        {  
            if(stars.get(i).starSize == 1){
                if (rocketXPos < stars.get(i).xpos + 20 &&
                    rocketXPos > stars.get(i).xpos - 20 &&
                    rocketYPos < stars.get(i).ypos + 20 &&
                    rocketYPos > stars.get(i).ypos - 20)
                {
                    return (true);
                }
            }
            if(stars.get(i).starSize == 2){
                if (rocketXPos < stars.get(i).xpos + 25 &&
                    rocketXPos > stars.get(i).xpos - 25 &&
                    rocketYPos < stars.get(i).ypos + 25 &&
                    rocketYPos > stars.get(i).ypos - 25)
                {
                    return (true);
                }
            }
            if(stars.get(i).starSize == 3){
                if (rocketXPos < stars.get(i).xpos + 60 &&
                    rocketXPos > stars.get(i).xpos - 60 &&
                    rocketYPos < stars.get(i).ypos + 60 &&
                    rocketYPos > stars.get(i).ypos - 60)
                {
                    return (true);
                }
            }
        }
        return (false);
    }
           
    public static void Paint(Graphics2D g,RocketShip thisObj) {
        for (int i=0;i<stars.size();i++)
            stars.get(i).Draw(g, thisObj);
    }
   
    public static void Animate(int speed,int timeCount) {
       
        for (int i=0;i<stars.size();i++)
            stars.get(i).animate(speed,timeCount);
 
        if (timeCount % 50 == 49)
        {
            if (speed != 0)
                stars.add(new Star(speed));
        }        
    }    
   
   
   
    public void Draw(Graphics2D g,RocketShip thisObj)
    {
        if(starSize == 1)
            drawImage(g,thisObj,starImage,Window.getX(xpos),Window.getYNormal(ypos),0.0,1.0,1.0 );
        if(starSize == 2)
           drawImage(g,thisObj,starImage,Window.getX(xpos),Window.getYNormal(ypos),0.0,2.0,2.0 );
        if(starSize == 3)
           drawImage(g,thisObj,starImage,Window.getX(xpos),Window.getYNormal(ypos),0.0,2.0,2.0 );
 
    }
    public void drawImage(Graphics2D g,RocketShip thisObj,Image image,int xpos,int ypos,double rot,double xscale,double yscale) {
        int width = image.getWidth(thisObj);
        int height = image.getHeight(thisObj);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );
 
       
        g.drawImage(image,-width/2,-height/2,
        width,height,thisObj);
 
        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }    
   
    public void animate(int speed,int timeCount)
    {
        xpos += speed;
        if (xpos < 0 || xpos > Window.getWidth2())
            stars.remove(this);
        
        
    }    
    public static void RemoveStars(Missile missile, RocketShip frame)
    {
       
        
        for(int i = 0; i < stars.size();i++){
            
                if (missile.xpos < stars.get(i).xpos + (stars.get(i).starSize * 10) &&
                    missile.xpos > stars.get(i).xpos - (stars.get(i).starSize * 10) &&
                    missile.ypos < stars.get(i).ypos + (stars.get(i).starSize * 10) &&
                    missile.ypos > stars.get(i).ypos - (stars.get(i).starSize * 10))
                {
                    stars.remove(i);
                    frame.score++;
 
                }
            
            
            
        }
    }    
   
}
 
class Missile {
    private static ArrayList<Missile> missiles = new ArrayList<Missile>();    
    int xpos;
    int ypos;
    int speed;
 
    Missile(Rocket rocket)
    {
        if (rocket.faceRight)
            speed = 4;
        else
            speed = -4;
        xpos = rocket.xpos;
        ypos = rocket.ypos;
    }
 
    public static void Reset()
    {    
        missiles.clear();
        
    }
   
    public static void Init()
    {        
    }
   
    public static void Paint(Graphics2D g) {
        for (int i=0;i<missiles.size();i++)
        {
            missiles.get(i).Draw(g);
        }
    }
   
    public static void Animate(RocketShip frame) {
        for (int i=0;i<missiles.size();i++)
            missiles.get(i).animate(frame);
    }    
 
    public void Draw(Graphics2D g)
    {
        g.setColor(Color.red);
        drawMissile(g,Window.getX(xpos),Window.getYNormal(ypos),0.0,3.0,1.0 );
    }
    public void drawMissile(Graphics2D g,int xpos,int ypos,double rot,double xscale,double yscale) {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );  
 
        g.fillOval(-5,-5,10,10);
 
        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }    
   
    public void animate(RocketShip frame)
    {
        xpos += speed;
        if (xpos < 0 || xpos > Window.getWidth2())
            missiles.remove(this);

        
           
        Star.RemoveStars(this, frame);
    }  
    public static void Shoot(Rocket rocket)
    {
        missiles.add(new Missile(rocket));
 
    }    
}
 
 

