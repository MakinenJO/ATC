package atcgame.ui

import scala.swing._
import java.awt.Color
import java.awt.RenderingHints
import java.awt.Window.Type
import java.awt.event.ActionListener
import javax.swing.Timer


class AirFieldView
(parent: GameUI,
    title: String,
    width: Int, height: Int,
    offsetX: Int, offsetY: Int) 

extends ATCWindow(parent, title, width, height, offsetX, offsetY, Type.UTILITY) {
  def game = parent.game
  
  
  val fieldView = new Component {
    
    override def paintComponent(g: Graphics2D) = {
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      //g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED)
  		//g.setColor(Color.RED)
      g.drawOval(250, 250, 400, 400)
  		g.drawOval(0, 0, 900, 900)
  		g.drawOval(50, 50, 800, 800)
  		g.drawLine(0, 450, 900, 450)
  		g.drawLine(450, 0, 450, 900)
  		g.setColor(Color.BLACK)

  		game.drawAirfield(g)
  		
      //g.drawImage(planeImage, op, 300, 300)
    }
  }

  contents = fieldView
  this.peer.getContentPane().setBackground(new Color(0,100,0))
  this.pack()
  
  val listener = new ActionListener() {
    def actionPerformed(e: java.awt.event.ActionEvent) = {
      fieldView.repaint()
    }
  }
  
  val timer = new Timer(8, listener)
  timer.start()
  
  
    
  
}