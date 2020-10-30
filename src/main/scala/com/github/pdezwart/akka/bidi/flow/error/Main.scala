package com.github.pdezwart.akka.bidi.flow.error

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.{Directive, Directives, ExceptionHandler, Route}
import akka.stream.scaladsl.Sink
import com.typesafe.scalalogging.LazyLogging
import fr.davit.akka.http.metrics.core.HttpMetrics.enrichHttp
import fr.davit.akka.http.metrics.prometheus.PrometheusSettings

import scala.concurrent._
import scala.util._
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends LazyLogging {
  implicit val actorSystem = ActorSystem(name = "test")

  implicit def akkaExceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: Throwable =>
      logger.error(e.getMessage, e)
      complete(HttpResponse(StatusCodes.InternalServerError, entity = "500"))
  }

  val interface = "0.0.0.0"
  val port = 8080

  val pHttp = PrometheusHttp(PrometheusSettings.default)

  def main(args: Array[String]): Unit = {

    pHttp.startServerMetrics(interface = interface, port = 10008)

    val connectionSource = Http().newMeteredServerAt(
      interface = interface,
      port = port,
      pHttp.registry
    ).connectionSource()

    val futureBinding = connectionSource.to(Sink.foreachAsync(parallelism = 16) { connection =>
      try {
        // Future(connection.handleWithAsyncHandler(requestHandler))
        // SOLUTION:
        Future.successful(connection.handleWithAsyncHandler(requestHandler))
      } catch {
        case e: Throwable => logger.error(e.getMessage, e)
          Future(HttpResponse(status = StatusCodes.InternalServerError))
      }
    }).run()

    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        logger.info(s"Server online at http://${address.getHostString}:${address.getPort}/")
      case Failure(ex) =>
        logger.error("Failed to bind HTTP endpoint, terminating system", ex)
        actorSystem.terminate()
    }
  }

  def requestHandler(request: HttpRequest) : Future[HttpResponse] = {
    Future.successful(myCode(request))
  }

  def myCode(request: HttpRequest) : HttpResponse = {
    logger.info("Request received")
    Thread.sleep(1000)
    logger.info("Response sent")
    HttpResponse(status = StatusCodes.OK)
  }
}