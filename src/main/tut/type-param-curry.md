```tut
def sort[T](xs: Seq[T], comparator: Comparator[T]): Seq[T] =  xs match {
    case Seq() => Seq.empty
    case head +: tail =>
      val (st, gte) = tail.partition(comparator.compare(xs.head, _) > 0)
      sort(st, comparator) ++ Seq(head) ++ sort(gte, comparator)
  }
```