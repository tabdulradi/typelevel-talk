import shapeless._

final case class Employee(id: Int, name: String)
final case class Student(name: String, grade: Int)
final case class Song(author: String, title: String)


// type-class
trait Comparator[T] {
  def compare(o1: T, o2: T): Int
}
object Comparator {
  def apply[T](implicit instance: Comparator[T]): Comparator[T] = instance

  def instance[T](cmp: (T, T) => Int) = new Comparator[T] {
    override def compare(o1: T, o2: T): Int = cmp(o1, o2)
  }

  def delegateInstance[T, D](converter: T => D)(implicit delegateComparator: Comparator[D]) =
    instance[T]((o1, o2) => delegateComparator.compare(converter(o1), converter(o2)))

  implicit val hNilInstance = instance[HNil]((o1, o2) => 0)

  implicit def hListInstance[H, Tail <: HList](
    implicit
    headComparator: Comparator[H],
    tailComparator: Comparator[Tail]
  ) = instance[H :: Tail]{ (o1, o2) =>
    val cmp = headComparator.compare(o1.head, o2.head)
    if (cmp == 0)
      tailComparator.compare(o1.tail, o2.tail)
    else
      cmp
  }

  implicit def genericInstance[T, R](
    implicit
    gen: Generic.Aux[T, R],
    reprComparator: Comparator[R]
  ) = instance[T] { (o1, o2) =>
    reprComparator.compare(gen.to(o1), gen.to(o2))
  }

  implicit def intComparator = instance[Int](_ - _)

  implicit object StringComparator extends Comparator[String] {
    override def compare(o1: String, o2: String): Int =
      o1.compareTo(o2)
  }
}

object Collections {
  // Inefficient quick sort implementation
  def sort[T](xs: Seq[T])(implicit comparator: Comparator[T]): Seq[T] =  xs match {
    case Seq() => Seq.empty
    case head +: tail =>
      val (st, gte) = tail.partition(comparator.compare(xs.head, _) > 0)
      sort(st) ++ Seq(head) ++ sort(gte)
  }
}

object Main extends App {


  println(Collections.sort(Seq(
    1,
    2,
    3
  )))

  println(Collections.sort(Seq(
    "Foo",
    "Bar",
    "Baz"
  )))

  val employees = Seq(
    Employee(1, "Foo"),
    Employee(2, "Bar"),
    Employee(3, "Baz")
  )

  println(Collections.sort(employees))

  val students = Seq(
    Student("Foo", 49),
    Student("Foo", 99),
    Student("Foo", 19),
    Student("Bar", 19),
    Student("Baz", 49)
  )

  println(Collections.sort(students))


  println(Collections.sort(Seq(
    Song("Foo", "X"),
    Song("Bar", "Y"),
    Song("Baz", "A")
  )))
}
