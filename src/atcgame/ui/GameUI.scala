package atcgame.ui

import atcgame._

import scala.swing._
import java.awt.Color
import javax.swing.JInternalFrame
import javax.swing.JDesktopPane
import javax.swing.JFrame
import java.awt.Window.Type
import javax.swing.WindowConstants
import javax.swing.UIManager
import javax.imageio.ImageIO
import java.io.File
import java.awt.event.ActionListener
import javax.swing.Timer

class GameUI extends SimpleSwingApplication {
  
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)

	var game = new Game()
	
  val arrivalsView = new FlightListView(this, "Arrivals", 400, 500, 10, 10, Arrivals)
  val departuresView = new FlightListView(this, "Departures", 400, 500, 10, 510, Departures)
  
  val airfieldView = new AirFieldView(this, "Airport", 1100, 1000, 400, 10)
  
  val gameInfoView = new GameInfoView(this, "Air Traffic Control", 400, 400, 1500, 200) {
    
    val windows = Vector(arrivalsView, departuresView, airfieldView)
		
		reactions += {
      case w: event.WindowDeiconified => {
        windows.foreach(_.peer.setState(java.awt.Frame.NORMAL))
      }
      case v: event.WindowIconified => {
        //TODO: pause game?
        windows.foreach(_.peer.setState(java.awt.Frame.ICONIFIED))
      }
      case x: event.WindowActivated => {
        windows.foreach(_.peer.toFront())
      }
    }
  }
  
  
  def top = gameInfoView
  
  
  
  
  val gameListener = new ActionListener() {
    def actionPerformed(e: java.awt.event.ActionEvent) = {
      game.step()
    }
  }
  
  val gameTimer = new Timer(4, gameListener)
  gameTimer.start()
  
  
  val UIlistener = new ActionListener() {
    def actionPerformed(e: java.awt.event.ActionEvent) = {
      updateViews()
      if(game.isLost) loseGame()
    }
  }
  
  val UITimer = new Timer(100, UIlistener)
  UITimer.start()
  
  def updateViews() = {
    arrivalsView.updateContents()
    departuresView.updateContents()
    gameInfoView.update()
  }
  
  def newGame() = {
    game.start()
  }
  
  def loseGame(): Unit = {
    gameTimer.stop()
    UITimer.stop()
    airfieldView.timer.stop()
    Dialog.showMessage(null,
        "Oh no, you crashed!\nYour points: " + game.points,
        "Game Over")
  }
  
  
  def end() = {
    val result = Dialog.showConfirmation(null, "Do you want to quit?", "Quit", Dialog.Options.YesNo)
    if(result == Dialog.Result.Ok) sys.exit()
  }
  
  def restart() = {
    gameTimer.stop()
    UITimer.stop()
    airfieldView.timer.stop()
    game = new Game()
    game.start()
    departuresView.clear()
    arrivalsView.clear()
    UITimer.start()
    gameTimer.start()
    airfieldView.timer.start()
  }

}