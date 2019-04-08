package atcgame.ui

import atcgame._

import java.awt.Window.Type
import java.awt.GridLayout
import scala.swing._
import javax.swing.BoxLayout
import java.awt.Color
import java.awt.event.ActionListener
import javax.swing.Timer
import javax.swing.Box
import javafx.scene.layout.Region


class FlightListView
(parent: GameUI,
    title: String,
    width: Int, height: Int,
    offsetX: Int, offsetY: Int) 
    
extends ATCWindow(parent, title, width, height, offsetX, offsetY, Type.UTILITY) {
  val mainLayout = new ScrollPane()
  mainLayout.horizontalScrollBarPolicy = ScrollPane.BarPolicy.Never
  val layout = new BoxPanel(Orientation.Vertical)
  
  mainLayout.contents = layout
  
  
  contents = mainLayout

  def addPlane(p: Plane) = {
    layout.contents += new FlightListItem(p)
    //pack()
    //TODO: use these for animating removal of flightlistitem
    //layout.peer.add(Box.createVerticalStrut(40))
    //val sep = new Separator(Orientation.Horizontal)
    //sep.peer.setSize(40,40)
    //sep.peer.setMaximumSize(new Dimension(40,40))
    //layout.contents += sep
    peer.revalidate()
  }
}