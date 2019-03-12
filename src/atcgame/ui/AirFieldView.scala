package atcgame.ui

import scala.swing._
import java.awt.Color
import java.io.File
import javax.imageio.ImageIO
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp


class AirFieldView
(parent: GameUI,
    title: String,
    width: Int, height: Int,
    offsetX: Int, offsetY: Int) 

extends ATCWindow(parent, title, width, height, offsetX, offsetY) {
  
    val fieldView = new Component {
      val planeImage = ImageIO.read(new File("img/plane2.png"))
      override def paintComponent(g: Graphics2D) = {
        val at = new AffineTransform()
        at.rotate(2, planeImage.getWidth/2, planeImage.getHeight/2)
        val op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR)
        g.drawImage(planeImage, 50, 50, null)
        val image = op.filter(planeImage, null)
        g.drawImage(image, 200, 200, null)
        at.rotate(2, planeImage.getWidth / 2, planeImage.getHeight / 2)
        g.drawImage(planeImage, op, 300, 300)
      }
    }
    
    contents = fieldView
    
    this.peer.getContentPane().setBackground(Color.green)
    
  
}