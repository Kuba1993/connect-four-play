package controllers

import javax.inject._

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import de.htwg.se.connectfour.mvc.controller._
import de.htwg.se.connectfour.mvc.model.player.{RandomBotPlayer, RealPlayer}
import de.htwg.se.connectfour.mvc.model.types.CellType
import de.htwg.se.connectfour.mvc.view.GamingPlayers
import play.api.http.websocket.Message
import play.api.libs.json.{Format, Json}
import play.api.libs.streams.ActorFlow
import play.api.mvc._

import scala.collection.mutable.ListBuffer
import scala.swing.Reactor


@Singleton
class Application @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  val localGridController = GridController(7, 6)
  val player1 = RealPlayer("Player1")
  val player2 = RealPlayer("Player2")
  val players = new GamingPlayers(player1, player2, localGridController)
  var cellType: CellType.Value = CellType.FIRST
  var updatingMessage = "update"

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val notification: Format[GridController] = Json.format[GridController]


  localGridController.reactions += {
    case _: PlayerWon => updatingMessage = "Player "+ players.previousPlayer.name+ " won"
    case _: Draw => updatingMessage = "draw"
    case _: FilledColumn => updatingMessage = "column is filled"
    case _: InvalidMove => updatingMessage = "invalid move"
  }

  def test = Action {
    Ok("test")
  }

  def index = Action {
    Ok(views.html.index("Index"))
  }

  def news = Action {
    Ok(views.html.news(""))
  }

  def help = Action  {
    Ok(views.html.help(""))
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
      WebSocketActorFactory.create(out)
    }
  }

  class WebSocketActor(out: ActorRef) extends Actor {
    def receive = {
      case msg: String =>
        println("received: "+ msg)
        if(msg == "newGame"){
          println("starting new game")
          startNewGame()
          sendJson()
        }
        else if(msg.toInt >= 0 && msg.toInt <= 6){
          println(msg)
          println(updatingMessage)
          players.applyTurn(msg.toInt)
          sendJson()
        }
        if(updatingMessage.startsWith("Player")){
          out!updatingMessage
        }
        if (updatingMessage == "column is filled") {
          out ! updatingMessage
          updatingMessage = "update"
        }
        if (updatingMessage == "draw") {
          out ! updatingMessage
          updatingMessage = "update"
        }
      println("test")
    }
    def sendJson() ={
      out!Json.toJson(getList()).toString()
    }
  }

  def getList(): List[String] ={
    var listBuffer = new ListBuffer[String]()
    for (i <- 0 to localGridController.rows-1){
      for (j <- 0 to localGridController.columns-1){
        listBuffer += localGridController.cell(j,i).cellType.toString()
      }
    }
    val list = listBuffer.toList
    list
  }

  def newGame = Action{
    localGridController.createEmptyGrid(localGridController.columns, localGridController.rows)
    Ok(Json.toJson(getList()).toString())
  }

  def startNewGame(): Unit = {
    localGridController.createEmptyGrid(localGridController.columns, localGridController.rows)
    updatingMessage = "update"
  }

}