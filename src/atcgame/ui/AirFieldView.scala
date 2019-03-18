package atcgame.ui

import scala.swing._
import java.awt.Color
import java.io.File
import javax.imageio.ImageIO
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
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
    val planeImage = ImageIO.read(new File("img/plane2.png"))
    val dX = planeImage.getWidth / 2
    val dY = planeImage.getHeight / 2
    
    override def paintComponent(g: Graphics2D) = {
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
  		g.setColor(Color.RED)
      g.drawOval(250, 250, 400, 400)
  		g.drawOval(0, 0, 900, 900)
  		g.drawOval(50, 50, 800, 800)
  		g.drawLine(0, 450, 900, 450)
  		g.drawLine(450, 0, 450, 900)
  		g.setColor(Color.BLACK)

  		game.planes.foreach(plane => {
  		  //translate to center of image and rotate around center
    	  val at = new AffineTransform()
			  at.translate(-dX, -dY)
			  at.rotate(plane.facingAngle, planeImage.getWidth/2, planeImage.getHeight/2)
	  	  val op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR)
			  //val image = op.filter(planeImage, null)    			  
        g.drawImage(planeImage, op, plane.x, plane.y)
        g.drawString("Plane", plane.x - 10, plane.y - 30)
      })
      //g.drawImage(planeImage, op, 300, 300)
    }
  }

  contents = fieldView
  this.peer.getContentPane().setBackground(Color.green)
  this.pack()
  
  val listener = new ActionListener() {
    def actionPerformed(e: java.awt.event.ActionEvent) = {
      fieldView.repaint()
    }
  }
  
  val timer = new Timer(10, listener)
  timer.start()
  
  
    
  
}