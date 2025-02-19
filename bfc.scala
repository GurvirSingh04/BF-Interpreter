// Main Part 5 about a "Compiler" for the Brainf*** language
//============================================================


object M5b {

// !!! Copy any function you need from file bf.scala !!!
//
// If you need any auxiliary function, feel free to 
// implement it, but do not make any changes to the
// templates below.


// DEBUGGING INFORMATION FOR COMPILERS!!!
//
// Compiler, even real ones, are fiendishly difficult to get
// to produce correct code. One way to debug them is to run
// example programs ``unoptimised''; and then optimised. Does
// the optimised version still produce the same result?


// for timing purposes
def time_needed[T](n: Int, code: => T) = {
  val start = System.nanoTime()
  for (i <- 0 until n) code
  val end = System.nanoTime()
  (end - start)/(n * 1.0e9)
}


type Mem = Map[Int, Int]

import io.Source
import scala.util._

def load_bff(name: String): String = {
  try {
    val source = Source.fromFile(name)
    val content = source.mkString
    source.close()
    content
  } catch {
    case _: Exception => ""
  }
}

def sread(mem: Mem, mp: Int) : Int = {
    mem.getOrElse(mp, 0)
}

def write(mem: Mem, mp: Int, v: Int) : Mem = {
    mem.updated(mp, v)
}

def jumpRight(prog: String, pc: Int, level: Int): Int = {
  if (pc >= prog.length) prog.length
  else prog(pc) match {
    case '[' => jumpRight(prog, pc + 1, level + 1)
    case ']' if level == 0 => pc + 1
    case ']' => jumpRight(prog, pc + 1, level - 1)
    case _ => jumpRight(prog, pc + 1, level)
  }
}

def jumpLeft(prog: String, pc: Int, level: Int): Int = {
  if (pc < 0) -1
  else prog(pc) match {
    case ']' => jumpLeft(prog, pc - 1, level + 1)
    case '[' if level == 0 => pc + 1
    case '[' => jumpLeft(prog, pc - 1, level - 1)
    case _ => jumpLeft(prog, pc - 1, level)
  }
}

def jtable(pg: String): Map[Int, Int] = {
  def process(pos: Int, stack: List[Int], table: Map[Int, Int]): Map[Int, Int] = {
    if (pos >= pg.length) table
    else pg(pos) match {
      case '[' => 
        process(pos + 1, pos :: stack, table)
      case ']' if stack.nonEmpty => 
        val open = stack.head
        val right = jumpRight(pg, open + 1, 0)
        val left = jumpLeft(pg, pos - 1, 0)
        process(pos + 1, stack.tail, table + (open -> right) + (pos -> left))
      case _ => 
        process(pos + 1, stack, table)
    }
  }
  process(0, Nil, Map())
}

def compute2(pg: String, tb: Map[Int, Int], pc: Int, mp: Int, mem: Mem) : Mem = {
  if (pc >= pg.length) mem
  else pg(pc) match {
    case '+' => compute2(pg, tb, pc + 1, mp, write(mem, mp, (sread(mem, mp) + 1) % 256))
    case '-' => compute2(pg, tb, pc + 1, mp, write(mem, mp, (sread(mem, mp) - 1 + 256) % 256))
    case '>' => compute2(pg, tb, pc + 1, mp + 1, mem)
    case '<' => compute2(pg, tb, pc + 1, mp - 1, mem)
    case '[' if sread(mem, mp) == 0 => compute2(pg, tb, tb(pc), mp, mem)
    case '[' => compute2(pg, tb, pc + 1, mp, mem)
    case ']' if sread(mem, mp) != 0 => compute2(pg, tb, tb(pc), mp, mem)
    case ']' => compute2(pg, tb, pc + 1, mp, mem)
    case _ => compute2(pg, tb, pc + 1, mp, mem)
  }
}

def run2(pg: String, m: Mem = Map()) : Mem = {
    compute2(pg, jtable(pg), 0, 0, m)
}

def optimise(s: String): String = {
  s.replaceAll("""[^<>+\-.\[\]]""", "")
   .replaceAll("""\[-\]""", "0")
}

def compute3(pg: String, tb: Map[Int, Int], pc: Int, mp: Int, mem: Mem): Mem = {
  if (pc >= pg.length) mem
  else pg(pc) match {
    case '+' => compute3(pg, tb, pc + 1, mp, write(mem, mp, (sread(mem, mp) + 1) % 256))
    case '-' => compute3(pg, tb, pc + 1, mp, write(mem, mp, (sread(mem, mp) - 1 + 256) % 256))
    case '>' => compute3(pg, tb, pc + 1, mp + 1, mem)
    case '<' => compute3(pg, tb, pc + 1, mp - 1, mem)
    case '[' if sread(mem, mp) == 0 => compute3(pg, tb, tb(pc), mp, mem)
    case '[' => compute3(pg, tb, pc + 1, mp, mem)
    case ']' if sread(mem, mp) != 0 => compute3(pg, tb, tb(pc), mp, mem)
    case ']' => compute3(pg, tb, pc + 1, mp, mem)
    case '0' => compute3(pg, tb, pc + 1, mp, write(mem, mp, 0))
    case _ => compute3(pg, tb, pc + 1, mp, mem)
  }
}

def run3(pg: String, m: Mem = Map()): Mem = {
  val optimised = optimise(pg)
  compute3(optimised, jtable(optimised), 0, 0, m)
}

def combine(s: String): String = {
  List('+', '-', '<', '>')
    .foldLeft(s)((acc, cmd) => 
      s"""\\${cmd}+""".r.replaceAllIn(acc, m => {
        val len = m.matched.length
        if (len > 26) {
          val groups = len / 26
          val rem = len % 26
          s"$cmd${"Z" * groups}${if (rem > 0) s"$cmd${('A' + rem - 1).toChar}" else ""}"
        } else {
          s"$cmd${('A' + len - 1).toChar}"
        }
      }))
}

def compute4(pg: String, tb: Map[Int, Int], pc: Int, mp: Int, mem: Mem): Mem = {
  if (pc >= pg.length) mem
  else pg(pc) match {
    case '+' if pc + 1 < pg.length => 
      val n = pg(pc + 1).toInt - 'A'.toInt + 1
      compute4(pg, tb, pc + 2, mp, write(mem, mp, (sread(mem, mp) + n) % 256))
    case '-' if pc + 1 < pg.length => 
      val n = pg(pc + 1).toInt - 'A'.toInt + 1
      compute4(pg, tb, pc + 2, mp, write(mem, mp, (sread(mem, mp) - n + 256) % 256))
    case '>' if pc + 1 < pg.length => 
      val n = pg(pc + 1).toInt - 'A'.toInt + 1
      compute4(pg, tb, pc + 2, mp + n, mem)
    case '<' if pc + 1 < pg.length => 
      val n = pg(pc + 1).toInt - 'A'.toInt + 1
      compute4(pg, tb, pc + 2, mp - n, mem)
    case '[' if sread(mem, mp) == 0 => compute4(pg, tb, tb(pc), mp, mem)
    case '[' => compute4(pg, tb, pc + 1, mp, mem)
    case ']' if sread(mem, mp) != 0 => compute4(pg, tb, tb(pc), mp, mem)
    case ']' => compute4(pg, tb, pc + 1, mp, mem)
    case '0' => compute4(pg, tb, pc + 1, mp, write(mem, mp, 0))
    case _ => compute4(pg, tb, pc + 1, mp, mem)
  }
}

def run4(pg: String, m: Mem = Map()): Mem = {
  val processed = combine(optimise(pg))
  compute4(processed, jtable(processed), 0, 0, m)
}
}
