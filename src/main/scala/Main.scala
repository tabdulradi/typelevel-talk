import shapeless._

trait Comparator[T] {
  def compare(o1: T, o2: T): Int
}

object Comparator {
  def apply[T](implicit instance: Comparator[T]): Comparator[T] = instance

  def instance[T](c: (T, T) => Int) = new Comparator[T] {
    override def compare(o1: T, o2: T): Int = c(o1, o2)
  }

  implicit val hnilComparator = instance[HNil]((_, _) => 0)

  implicit def hlistComparator[Head, Tail <: HList](
    implicit
    headComparator: Comparator[Head],
    tailComparator: Comparator[Tail]
  ) = instance[Head :: Tail] {
    (o1, o2) =>
      val r = headComparator.compare(o1.head, o2.head)
      if (r == 0)
        tailComparator.compare(o1.tail, o2.tail)
      else
        r
  }

  implicit def genericComparator[T, R](
    implicit
    gen: Generic[T] { type Repr = R},
    comp: Comparator[R]
  ) = Comparator[T] {
    (o1, o2) =>
      val o1Gen = gen.to(o1)
      val o2Gen = gen.to(o2)
      comp.compare(o1Gen, o2Gen)
  }

  implicit val stringComparator = instance[String](_ compareTo _)
  implicit val intComparator = instance[Int](_ - _)
}

object Collections {
  def sort[T](xs: Seq[T])(implicit comparator: Comparator[T]): Seq[T] =  xs match {
    case Seq() => Seq.empty
    case head +: tail =>
      val (st, gte) = tail.partition(comparator.compare(xs.head, _) > 0)
      sort(st) ++ Seq(head) ++ sort(gte)
  }
}

final case class Employee(name: String, salary: Boolean)
final case class Song(title: String)



object Main extends App {

  5 :: 5 :: HNil

  //
  //  println(Collections.sort(Seq(
  //    "FOo",
  //    "Bar",
  //    "Baz"
  //  )))
  //
  //  val x = Comparator[String]
  //

  // println(
  //   Collections.sort(
  //     Seq(
  //       Employee("Foo", 1),
  //       Employee("Foo", 31),
  //       Employee("Foo", 521),
  //       Employee("Foo", 21),
  //       Employee("Foo", 51),
  //       Employee("Bar", 2),
  //       Employee("Baz", 99)
  //     )))

    println(Collections.sort(Seq(
      Song("FOo"),
      Song("Bar"),
      Song("Baz")
    )))
}
