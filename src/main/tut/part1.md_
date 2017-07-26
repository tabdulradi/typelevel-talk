## Sorting in Java

This is how we sort ArrayList of Strings in Java
```tut
import java.util._
val l = new ArrayList[String]()
l.add("Foo")
l.add("Bar")
l.add("Baz")

Collections.sort(l)
println(l)
```

Let's sort our custom class
```tut
case class Employee(name: String) extends Comparable[Employee] {
  override def compareTo(o: Employee): Int = this.name.compareTo(o.name)
}
val l2 = new ArrayList[Employee]
l2.add(Employee("Foo"))
l2.add(Employee("Bar"))
l2.add(Employee("Baz"))

Collections.sort(l2)
println(l2)
```

What if we don't own the class
```tut
final case class Employee(name: String)
```

```tut
class EmployeeComparator extends Comparator[Employee] {
  override def compare(o1: Employee, o2: Employee): Int =
    o1.name.compareTo(o2.name)
}

val l = new ArrayList[Employee]
l.add(Employee("Foo"))
l.add(Employee("Bar"))
l.add(Employee("Baz"))

Collections.sort(l, new EmployeeComparator)
println(l)
```

Small improvements: static Object
```tut
object EmployeeComparator extends Comparator[Employee] {
  override def compare(o1: Employee, o2: Employee): Int =
    o1.name.compareTo(o2.name)
}
```
Congratulations, you just defined your first type-class!
- Ad-hoc polymorphism 
- Move the logic outside the data structure to specialised classes/objects
------------------------------
## Back to Scala

```tut
final case class Employee(name: String)

// type-class
trait Comparator[T] {
  def compare(o1: T, o2: T): Int
}

object Collections {
  // Inefficient quick sort implementation
  def sort[T](xs: Seq[T], comparator: Comparator[T]): Seq[T] =  xs match {
    case Seq() => Seq.empty
    case head +: tail =>
      val (st, gte) = tail.partition(comparator.compare(xs.head, _) > 0)
      sort(st, comparator) ++ Seq(head) ++ sort(gte, comparator)
  }
}


object EmployeeComparator extends Comparator[Employee] {
  override def compare(o1: Employee, o2: Employee): Int =
    o1.name.compareTo(o2.name)
}

val l = Seq[Employee](
  Employee("Foo"),
  Employee("Bar"),
  Employee("Baz")
)

Collections.sort(l, EmployeeComparator)


```

Implicit
```tut
object Collections {
  // Inefficient quick sort implementation
  def sort[T](xs: Seq[T])(implicit comparator: Comparator[T]): Seq[T] =  xs match {
    case Seq() => Seq.empty
    case head +: tail =>
      val (st, gte) = tail.partition(comparator.compare(xs.head, _) > 0)
      sort(st) ++ Seq(head) ++ sort(gte)
  }
}

implicit object EmployeeComparator extends Comparator[Employee] {
  override def compare(o1: Employee, o2: Employee): Int =
    o1.name.compareTo(o2.name)
}

Collections.sort(l)

```

Implicit Scope

1. defined/imported at call-site
2. case class companion, if we own the case class
```tut
object Employee {
  implicit object EmployeeComparator extends Comparator[Employee] {
    override def compare(o1: Employee, o2: Employee): Int =
      o1.name.compareTo(o2.name)
  }
}
```
3. typeclass companion, for common types like String
```tut
object Comparator {
  implicit object EmployeeComparator extends Comparator[Employee] {
    override def compare(o1: Employee, o2: Employee): Int =
      o1.name.compareTo(o2.name)
  }
}
```

##Improvements
Depend on implicit string comparator rather than hard-coding it 
```tut
object Employee {
  implicit def employeeComparator(implicit stringComparator: Comparator[String]) = new Comparator[Employee] {
    override def compare(o1: Employee, o2: Employee): Int =
    stringComparator.compare(o1.name, o2.name)
  }
}

```

Conventions 
```tut
object Comparator {
  def apply[T](implicit instance: Comparator[T]): Comparator[T] = instance
  
  def instance[T](cmp: (T, T) => Int) = new Comparator[T] {
    override def compare(o1: T, o2: T): Int = cmp(o1, o2)
  }
}
```


--------------------------


We have a similar case class
```tut
final case class Song(title: String)
object Song {
  implicit def songComparator(implicit stringComparator: Comparator[String]) = new Comparator[Song] {
    override def compare(o1: Song, o2: Song): Int =
      stringComparator.compare(o1.title, o2.title)
  }
}

```

DRY
```tut
object Comparator {
  def delegateInstance[T, D](converter: T => D)(implicit delegateComparator: Comparator[D]) =
    instance[T]((o1, o2) => delegateComparator.compare(converter(o1), converter(o2)))
}

object Song {
  implicit def songComparator(implicit stringComparator: Comparator[String]) =
    Comparator.delegateInstance[Song, String](_.title)
}

object Employee {
  implicit def employeeComparator(implicit stringComparator: Comparator[String]) =
    Comparator.delegateInstance[Employee, String](_.name)
}

```
Still we have a small duplication.

Let's make the problem even harder

```tut
final case class Employee(name: String)
final case class Student(name: String, grade: Int)
final case class Song(author: String, title: String)
```
We started to hit Scala limitations.
No way to "abstract over arity"
This problem is solved by shapeless

----------------------------------------------------------

Normal List vs HList
```tut
"Hello" :: 4 :: true :: Nil
"Hello" :: 4 :: true :: HNil
```

```tut
val hlist = "Hello" :: 4 :: true :: HNil
hlist.head
hlist.tail.head
hlist.tail.tail.head
hlist.tail.tail.tail.head
```

```tut
Generic[Employee].to(Employee("Tam"))
```
Explore Generic (from, to, Repr)

Code that you probably don't want to write .. 
```tut
final case class Employee(name: String, salary: Int)
final case class Student(name: String, grade: Int)

val employee = Employee("Tam", 9999)
val employeeRepr = Generic[Employee].to(employee)
println(Generic[Student].from(employeeRepr))

```

----------------------------------

Typeclass instance for HList
```tut
val employee = Seq(
    Employee("Foo"),
    Employee("Bar"),
    Employee("Baz")
  )
  val employeeReprs = employee.map(Generic[Employee].to)

  Collections.sort(employeeReprs)
```

```tut
implicit def hListInstance(implicit stringComparator: Comparator[String]) =
    delegateInstance[String :: HNil, String](_.head)
```


```tut
implicit def hListInstance[Tail <: HList](implicit stringComparator: Comparator[String]) =
    delegateInstance[String :: Tail, String](_.head)
```

```tut
implicit def hListInstance[Tail <: HList, D](implicit stringComparator: Comparator[D]) =
    delegateInstance[D :: Tail, D](_.head)
```

Automatic derivation of Case class
```tut
implicit def genericInstance[T](
    implicit 
    gen: Generic[T]
  ) = instance[T] { (o1, o2) =>
    val oGen1 = gen.to(o1)
    val oGen2 = gen.to(o2)
    
    ???
  }
```

Type refinement
```tut
implicit def genericInstance[T, R](
  implicit
  gen: Generic[T] { type Repr = R },
  reprComparator: Comparator[R]
) = instance[T] { (o1, o2) =>
  reprComparator.compare(gen.to(o1), gen.to(o2))
}
```

Aux alias
```tut
implicit def genericInstance[T, R](
    implicit
    gen: Generic.Aux[T, R],
    reprComparator: Comparator[R]
  ) = instance[T] { (o1, o2) =>
    reprComparator.compare(gen.to(o1), gen.to(o2))
  }
```

-----------------------------------

Sort by all case class members
```tut
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
```
Doesn't work .. No termination 
 
HNil Instance
```tut
  implicit val hNilInstance = instance[HNil]((o1, o2) => 0)
```