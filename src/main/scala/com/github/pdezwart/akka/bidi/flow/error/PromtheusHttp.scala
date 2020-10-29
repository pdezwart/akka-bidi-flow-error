package com.github.pdezwart.akka.bidi.flow.error

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.directives.PathDirectives.{path => pathD, _}
import akka.http.scaladsl.server.{Directive, Directives, Route}
import fr.davit.akka.http.metrics.core.HttpMetrics.enrichHttp
import fr.davit.akka.http.metrics.core.scaladsl.server.HttpMetricsDirectives
import fr.davit.akka.http.metrics.prometheus.{PrometheusRegistry, PrometheusSettings}
import io.prometheus.client.CollectorRegistry

import scala.concurrent.{ExecutionContext, Future}

/**
 * Implemented from:
 * https://github.com/RustedBones/akka-http-metrics
 */
case class PrometheusHttp(
                           settings: PrometheusSettings = PrometheusSettings.default,
                           prometheus: CollectorRegistry = CollectorRegistry.defaultRegistry
                         ) {

  lazy val registry: PrometheusRegistry = PrometheusRegistry(prometheus, settings)

  def startServerMetrics(
                          interface: String,
                          port: Int,
                          directive: Directive[Unit] = Directives.get & pathD("prometheus" / "metrics")
                        )(implicit actorSystem: ActorSystem, ec: ExecutionContext): Future[Http.ServerBinding] = {
    Http()
      .newServerAt(interface = interface, port = port)
      .bindFlow(metricsRoute(directive))
  }

  def metricsRoute(directive: Directive[Unit] = Directives.get & pathD("prometheus" / "metrics")): Route = {
    import fr.davit.akka.http.metrics.prometheus.marshalling.PrometheusMarshallers._
    directive(HttpMetricsDirectives.metrics(registry))
  }
}
