package atcgame

import java.awt.Graphics2D
import javax.imageio.ImageIO
import java.io.File
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.Color

class Runway(val exit1: Exit, val exit2: Exit) {
  
  val rotation = {
    val x = Math.abs(exit1.x - exit2.x)
    val y = exit2.y - exit1.y//Math.abs(exit1._2 - exit2._2)
    Math.atan(y.toDouble / x) + Math.PI / 2
  }
  
  val length =  {
    Math.sqrt(Math.pow((exit1.x - exit2.x), 2) + Math.pow((exit1.y - exit2.y), 2))
  }
  
  def approachAngle(exit: Exit) = {
    if (exit == exit1) (rotation + Math.PI / 2) % (Math.PI * 2)
    else (rotation + Math.PI * 3 / 2) % (Math.PI * 2)
  }

  
  def draw(g: Graphics2D) {
    val at = new AffineTransform()
    at.translate(-Runway.dX, -Runway.dY)
    at.rotate(rotation, Runway.dX, Runway.dY)
    val op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR)
    var x = exit1.x
    var y = exit1.y
    for(i <- 0 to length.toInt / 32) {
    	g.drawImage(Runway.runwayImage, op, x, y)
    	x += (32 * Math.sin(rotation)).toInt
    	y -= (32 * Math.cos(rotation)).toInt
    }
    g.setColor(Color.YELLOW)
    g.drawString(exit1.name, exit1.x - 10, exit1.y - 45)
    g.drawString(exit2.name, exit2.x - 10, exit2.y - 45)
    g.setColor(Color.BLACK)
  }
}

case class Exit(x: Int, y: Int, name: String, var selected: Boolean = false)

object Runway {
  private val runwayImage = ImageIO.read(new File("img/runway.png"))
  private val dX = runwayImage.getWidth / 2
  private val dY = runwayImage.getHeight / 2
}