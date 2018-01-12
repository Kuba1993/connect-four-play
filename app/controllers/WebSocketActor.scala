package controllers

import akka.actor.{Actor, ActorRef}


class WebSocketActor(out: ActorRef) extends Actor {
  def receive = {
    case msg: String =>
      out!msg
      println(msg)
  }

}

