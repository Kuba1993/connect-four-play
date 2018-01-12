package controllers

import javax.inject._

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import de.htwg.se.connectfour.mvc.controller.GridController
import de.htwg.se.connectfour.mvc.model.player.{RandomBotPlayer, RealPlayer}
import de.htwg.se.connectfour.mvc.model.types.CellType
import de.htwg.se.connectfour.mvc.view.GamingPlayers
import play.api.http.websocket.Message
import play.api.libs.streams.ActorFlow
import play.api.mvc._

import scala.concurrent.Future


@Singleton
class Application @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  val localGridController = GridController(7, 6)
  val player1 = RealPlayer("David")
  val player2 = RandomBotPlayer(localGridController)
  val players = new GamingPlayers(player1, player2, localGridController)
  var cellType: CellType.Value = CellType.FIRST

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  def test = Action {
    Ok("test")
  }

  def index = Action {
    Ok(views.html.index.render("Index"))
  }

  def connectfour = Action {
    Ok(views.html.connectfour.render(localGridController))
  }

  def turn (id: Int) = Action {
    localGridController.checkAddCell(id, cellType)
    if(cellType == CellType.FIRST){
      cellType = CellType.SECOND
    }else{
      cellType = CellType.FIRST
    }
    Ok(views.html.connectfour.render(localGridController))
  }


   object WebSocketActorFactory{
    def create(out: ActorRef): Props ={
      Props(new WebSocketActor(out))
    }
  }
  def socket: WebSocket = WebSocket.accept[String, String]{ request =>
    ActorFlow.actorRef{out =>
      println("Connection established")
      WebSocketActorFactory.create(out)
    }
  }


}