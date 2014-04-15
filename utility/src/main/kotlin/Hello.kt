package demo;
import java.util.*;

class Hello {

    class object {
        fun main (args: Array<String>) {
            println("HelloWorld")

            val s = Arrays.asList("a", "b", "c", "d")

            s
            .filter { it.length() > 0 }
            .forEach { print("Hello, $it!\n") }
        }
    }
}