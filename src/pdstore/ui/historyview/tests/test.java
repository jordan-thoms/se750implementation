package pdstore.ui.historyview.tests;

/* <applet code="centerCircle" height=300 width=300>   </applet> */
  
 import java.awt.*;
 import java.applet.*;

 public class test extends Applet
 {
      public void paint(Graphics g){
           Dimension d = getSize();
           int x = d.width/2;
           int y = d.height/2;
           int radius = (int) ((d.width < d.height) ? 0.4 * d.width : 0.4 * d.height);
           g.setColor(Color.cyan);
           g.fillOval(x-radius, y-radius, 2*radius, 2*radius);
           g.setColor(Color.BLACK);
           g.drawOval(x-radius-10, y-radius-10, 2*radius+20, 2*radius+20);
        }
 }
