package atcgame

import java.awt.Graphics2D
import javax.imageio.ImageIO
import java.io.File
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp

class Runway(val exit1: (Int, Int), val exit2: (Int, Int)) {
  
  val rotation = {
    val x = Math.abs(exit1._1 - exit2._1)
    val y = exit2._2 - exit1._2//Math.abs(exit1._2 - exit2._2)
    Math.atan(y.toDouble / x) + Math.PI / 2
  }
  
  val length =  {
    Math.sqrt(Math.pow((exit1._1 - exit2._1), 2) + Math.pow((exit1._2 - exit2._2), 2))
  }
  
  def approachAngle(exit: (Int, Int)) = {
    if (exit == exit1) (rotation + Math.PI / 2) % (Math.PI * 2)
    else (rotation + Math.PI * 3 / 2) % (Math.PI * 2)
  }

  
  def draw(g: Graphics2D) {
    val at = new AffineTransform()
    at.translate(-Runway.dX, -Runway.dY)
    at.rotate(rotation, Runway.dX, Runway.dY)
    val op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR)
    var x = exit1._1
    var y = exit1._2
    for(i <- 0 to length.toInt / 32) {
    	g.drawImage(Runway.runwayImage, op, x, y)
    	x += (32 * Math.sin(rotation)).toInt
    	y -= (32 * Math.cos(rotation)).toInt
    }
  }
}

object Runway {
  private val runwayImage = ImageIO.read(new File("img/runway.png"))
  private val dX = runwayImage.getWidth / 2
  private val dY = runwayImage.getHeight / 2
}