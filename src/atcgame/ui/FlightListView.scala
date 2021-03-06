package atcgame.ui

import java.awt.Window.Type

import scala.collection.mutable.HashMap
import scala.swing.BoxPanel
import scala.swing.Orientation
import scala.swing.ScrollPane

import atcgame.Plane




sealed trait ViewType
case object Arrivals extends ViewType
case object Departures extends ViewType



class FlightListView
(parent: GameUI,
    title: String,
    width: Int, height: Int,
    offsetX: Int, offsetY: Int,
    val viewType: ViewType) 
    
extends ATCWindow(parent, title, width, height, offsetX, offsetY, Type.UTILITY) {
  val planes = HashMap[Plane, FlightListItem]()
  val mainLayout = new ScrollPane()
  mainLayout.horizontalScrollBarPolicy = ScrollPane.BarPolicy.Never
  val layout = new BoxPanel(Orientation.Vertical)
  mainLayout.contents = layout
  
  contents = mainLayout

  
  def clear() = {
    layout.contents.clear()
    peer.revalidate()
    repaint()
  }
  
  def addPlane(p: Plane) = {
    
    val newItem = viewType match {
      case Arrivals => FlightListItem.arrival(game, p)
      case Departures => FlightListItem.departure(game, p)
    }
    planes(p) = newItem
    layout.contents += newItem
    //pack()
    //TODO: use these for animating removal of flightlistitem
    //layout.peer.add(Box.createVerticalStrut(40))
    //val sep = new Separator(Orientation.Horizontal)
    //sep.peer.setSize(40,40)
    //sep.peer.setMaximumSize(new Dimension(40,40))
    //layout.contents += sep
    peer.revalidate()
  }
  
  
  def removePlane(p: Plane) = {
    layout.contents -= planes(p)
    planes.remove(p)
    peer.revalidate()
    repaint()
    p.selected = false
  }
  
  
  def updateContents() {
    planes.values.foreach(_.updateContents())
    
    viewType match {
      
      case Arrivals => {
        game.handleNewArrivals().foreach(plane => addPlane(plane))
        val toRemove = planes.keys.filter(_.hasArrived)
        toRemove.foreach(p => removePlane(p))
      }
      
      case Departures => {
        game.handleNewDepartures().foreach(plane => addPlane(plane))
        val toRemove = planes.keys.filter(_.hasDeparted)
        toRemove.foreach(p => {removePlane(p); game.removePlane(p)})
      }
      
    }
  }
  
  
}


