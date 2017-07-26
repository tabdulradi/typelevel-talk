package typeparamcurry

import shapeless._


// def foo(a: Int)(b: String) = ???
// val bar = foo(5) _
// bar("Hello")


// def foo(a: Int, b: String) = ???
// val bar = foo(5, _: String)
// bar("Hello")


// def foo[A][B] = ???


// def foo[A, B] = ???
// val bar = foo[A, _]

// def bar[B] = foo[Int, B]
// bar[String]

// def foo[A] = new {
//     def apply[B] = ???
// }
// foo[Int][String]
// val bar = foo[Int]
// bar[String]

object migration {
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

def migrate[
  A, 
  B, 
  ARepr <: HList, 
  BRepr <: HList,
  Added <: HList
](a: A, dummy: => B)(
  implicit
  aGen: Generic.Aux[A, ARepr],
  bGen: Generic.Aux[B, BRepr],
  diff: ops.hlist.Diff.Aux[BRepr, ARepr, Added],
  default: DefaultValue[Added],
  prepend: ops.hlist.Prepend.Aux[ARepr, Added, BRepr]
): B = bGen.from(prepend(aGen.to(a), default.value))


    // def migrate[B] = new {
    //     def apply[
    //         A,
    //         ARepr <: HList, 
    //         BRepr <: HList,
    //         Added <: HList
    //     ](a: A)(
    //         implicit
    //         aGen: Generic.Aux[A, ARepr],
    //         bGen: Generic.Aux[B, BRepr],
    //         diff: ops.hlist.Diff.Aux[BRepr, ARepr, Added],
    //         default: DefaultValue[Added],
    //         prepend: ops.hlist.Prepend.Aux[ARepr, Added, BRepr]
    //     ): B  = bGen.from(prepend(aGen.to(a), default.value))
    // }

    // trait MyMigration[A, B] extends (A => B)
    // object MyMigration {
    //     implicit def instance[
    //         A,
    //         B,
    //         ARepr <: HList, 
    //         BRepr <: HList,
    //         Added <: HList
    //     ](
    //         implicit
    //         aGen: Generic.Aux[A, ARepr],
    //         bGen: Generic.Aux[B, BRepr],
    //         diff: ops.hlist.Diff.Aux[BRepr, ARepr, Added],
    //         default: DefaultValue[Added],
    //         prepend: ops.hlist.Prepend.Aux[ARepr, Added, BRepr]
    //     )  = new MyMigration[A, B] {
    //         override def apply(a: A): B = 
    //           bGen.from(prepend(aGen.to(a), default.value))
    //     }
    // }

    // def migrate[A, B](value: A)(implicit migrate: MyMigration[A, B]): B = 
    //     migrate(value)
    
}

// package protocol.v1
// case class Employee(name: String)

object model {
    object v1 {
        case class Employee(name: String)
    }

    object v2 {
        case class Employee(name: String, salary: Option[Int])
    }
}

object Main extends App {
    import model._
    import migration._


    lazy val dummy: v2.Employee = ???
    println(migrate(new v1.Employee("X"), dummy))
}
