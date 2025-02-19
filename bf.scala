// Main Part 5 about an Interpreter for 
// the Brainf*** language
//==============================================


object M5a {

// representation of BF memory 

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

def compute(prog: String, pc: Int, mp: Int, mem: Mem): Mem = {
  if (pc >= prog.length) mem
  else prog(pc) match {
    case '+' => compute(prog, pc + 1, mp, write(mem, mp, (sread(mem, mp) + 1) % 256))
    case '-' => compute(prog, pc + 1, mp, write(mem, mp, (sread(mem, mp) - 1 + 256) % 256))
    case '>' => compute(prog, pc + 1, mp + 1, mem)
    case '<' => compute(prog, pc + 1, mp - 1, mem)
    case '[' if sread(mem, mp) == 0 => compute(prog, jumpRight(prog, pc + 1, 0), mp, mem)
    case '[' => compute(prog, pc + 1, mp, mem)
    case ']' if sread(mem, mp) != 0 => compute(prog, jumpLeft(prog, pc - 1, 0), mp, mem)
    case ']' => compute(prog, pc + 1, mp, mem)
    case _ => compute(prog, pc + 1, mp, mem)
  }
}

def run(prog: String, m: Mem = Map()): Mem = {
  compute(prog, 0, 0, m)
}

def generate(msg: List[Char]): String = {
  val parts = msg.map(c => 
    "+" * c.toInt + "." + "[-]>"
  )
  parts.mkString.dropRight(1)
}
}
