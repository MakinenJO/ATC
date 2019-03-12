//A base class for all windows in the game

package atcgame.ui

import scala.swing._
import scala.swing.Frame
import javax.swing.WindowConstants
import java.awt.Window.Type
import java.awt.Dimension

abstract class ATCWindow
(parent: GameUI, title: String,
    width: Int, height: Int,
    offsetX: Int, offsetY: Int) 

extends Frame {
  this.peer.setType(Type.UTILITY)
  this.peer.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
  
  visible = true
  
  this.peer.setTitle(title)
  
  size = new Dimension(width, height)
  preferredSize = size
  
  this.peer.setLocation(offsetX, offsetY)
  
  override def closeOperation() = {
    parent.end()
  }
  
  
}