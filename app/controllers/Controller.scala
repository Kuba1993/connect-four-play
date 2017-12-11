package controllers

import javax.inject._

import de.htwg.se.connectfour.Main
import de.htwg.se.connectfour.mvc.controller.{Controller, GridController}
import de.htwg.se.connectfour.mvc.model.player.{RandomBotPlayer, RealPlayer}
import de.htwg.se.connectfour.mvc.model.types.CellType
import de.htwg.se.connectfour.mvc.view.GamingPlayers
import play.api.mvc._


@Singleton
class Controller @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  val localGridController = GridController(7, 6)
  val player1 = RealPlayer("David")
  val player2 = RandomBotPlayer(localGridController)
  val players = new GamingPlayers(player1, player2, localGridController)
  var cellType = CellType.FIRST
  def test = Action {
    Ok("test")
  }

  def connectfour = Action {
    Ok(localGridController.grid.toString())
  }

  def turn (id: Int) = Action {

    localGridController.checkAddCell(id, cellType)
    if(cellType == CellType.FIRST){
      cellType = CellType.SECOND
    }else{
      cellType = CellType.FIRST
    }
    Ok(localGridController.grid.toString())
  }

}