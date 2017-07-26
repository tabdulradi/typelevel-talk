lazy val baseSettings: Seq[Setting[_]] = Seq(
  scalaVersion       := "2.12.1",
  scalaOrganization := "org.typelevel",
  scalacOptions     ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-language:higherKinds",
    "-language:implicitConversions", "-language:existentials",
    "-unchecked",
    "-Xfatal-warnings",
    "-Xlint",
    "-Yno-adapted-args",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Xfuture",
    "-Yliteral-types"
  ),
  resolvers += Resolver.sonatypeRepo("releases"),

  libraryDependencies ++= Seq(
    "com.chuusai" %% "shapeless" % "2.3.2",
    "org.scala-lang" % "scala-reflect" % "2.12.1",
    "com.datastax.cassandra" % "cassandra-driver-core" % "3.0.0",
    "com.lihaoyi" %% "fastparse" % "0.4.2",
    "org.scalameta" %% "scalameta" % "1.6.0"
  )
)

lazy val `typelevel-cql` = project.in(file("."))
  .settings(moduleName := "typelevel-cql")
  .settings(baseSettings: _*)
  .aggregate(core, slides)
  .dependsOn(core, slides)

lazy val core = project
  .settings(moduleName := "typelevel-cql-core")
  .settings(baseSettings: _*)


lazy val slides = project
  .settings(moduleName := "typelevel-cql-slides")
  .settings(baseSettings: _*)
  .settings(tutSettings: _*)
  .settings(
    tutSourceDirectory := baseDirectory.value / "tut",
    tutTargetDirectory := baseDirectory.value / "tut-out",
    watchSources ++= (tutSourceDirectory.value ** "*.html").get
  ).dependsOn(core)
