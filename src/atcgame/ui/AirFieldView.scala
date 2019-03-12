package atcgame.ui

import scala.swing._
import java.awt.Color
import java.io.File
import javax.imageio.ImageIO
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.Window.Type
import java.awt.event.ActionListener
import javax.swing.Timer


class AirFieldView
(parent: GameUI,
    title: String,
    width: Int, height: Int,
    offsetX: Int, offsetY: Int) 

extends ATCWindow(parent, title, width, height, offsetX, offsetY, Type.UTILITY) {
  val game = parent.game
  
  val fieldView = new Component {
    val planeImage = ImageIO.read(new File("img/plane2.png"))
    
    override def paintComponent(g: Graphics2D) = {
      game.planes.foreach(plane => {
        
    	  val at = new AffineTransform()
			  at.rotate(plane.facingAngle, planeImage.getWidth/2, planeImage.getHeight/2)
	  	  val op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR)
			  val image = op.filter(planeImage, null)    			  
        g.drawImage(planeImage, op, plane.x, plane.y)
      })
      //g.drawImage(planeImage, op, 300, 300)
    }
  }

  contents = fieldView
  this.peer.getContentPane().setBackground(Color.green)
  
  val listener = new ActionListener() {
    def actionPerformed(e: java.awt.event.ActionEvent) = {
      fieldView.repaint()
    }
  }
  
  val timer = new Timer(10, listener)
  timer.start()
  
  
    
  
}