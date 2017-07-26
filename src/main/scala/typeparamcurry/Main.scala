package typeparamcurry

import shapeless._

trait DefaultValue[A] {
  def value: A
}
object DefaultValue {
  def instance[A](a: A) = new DefaultValue[A] { 
      override def value = a 
  }
  implicit def optionsInstance[T] = instance[Option[T]](None)

  implicit val hNil = instance[HNil](HNil)
  implicit def hCons[H, T <: HList](
      implicit 
      headDefault: DefaultValue[H],
      tailDefault: DefaultValue[T]
  ) = instance[H :: T](headDefault.value :: tailDefault.value)
}


object v1 {
  case class Employee(name: String)
}

object v2 {
  case class Employee(name: String, salary: Option[Int])
}


object Main extends App {

  def migrate[
    A, 
    B, 
    ARepr <: HList, 
    BRepr <: HList,
    Added <: HList
  ](a: A)(
    implicit
    aGen: Generic.Aux[A, ARepr],
    bGen: Generic.Aux[B, BRepr],
    diff: ops.hlist.Diff.Aux[BRepr, ARepr, Added],
    default: DefaultValue[Added],
    prepend: ops.hlist.Prepend.Aux[ARepr, Added, BRepr]
  ): B = bGen.from(prepend(aGen.to(a), default.value))

  // println(migrate(new v1.Employee("foo"), dummy))
}
