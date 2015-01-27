import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._

object CarrdenInventoryBuild extends Build {
  val Organization = "edu.allegheny"
  val Name = "Carrden Inventory"
  val Version = "0.0.0"
  val ScalaVersion = "2.11.4"
  val ScalatraVersion = "2.3.0"
  val SlickVersion = "3.0.0"

  lazy val project = Project (
    "carrden-inventory",
    file("."),
    settings = ScalatraPlugin.scalatraWithJRebel ++ scalateSettings ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      libraryDependencies ++= Seq(
        "org.scalatra"        %%  "scalatra"          % ScalatraVersion,
        "org.scalatra"        %%  "scalatra-scalate"  % ScalatraVersion,
        "org.scalatra"        %%  "scalatra-specs2"   % ScalatraVersion   % "test",
        "com.typesafe.slick"  %%  "slick"             % SlickVersion,
        "ch.qos.logback"      %   "logback-classic"   % "1.1.2"           % "runtime",
        "org.eclipse.jetty"   %   "jetty-webapp"      % "9.1.5.v20140505" % "container",
        "org.eclipse.jetty"   %   "jetty-plus"        % "9.1.5.v20140505" % "container",
        "javax.servlet"       %   "javax.servlet-api" % "3.1.0"
      ),
      scalateTemplateConfig in Compile <<= (sourceDirectory in Compile){ base =>
        Seq(
          TemplateConfig(
            base / "webapp" / "WEB-INF" / "templates",
            Seq.empty,  /* default imports should be added here */
            Seq(
              Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", importMembers = true, isImplicit = true)
            ),  /* add extra bindings here */
            Some("templates")
          )
        )
      }
    )
  )
}
