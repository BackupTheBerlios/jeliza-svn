package interpreter.test;

import java.io.FileNotFoundException;

import interpreter.base.*;

public class TestInterpreter {

	public static void main(String[] args) {
		Interpreter in = new Interpreter();
		try {
			in.useInterpreter("test.jel");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		AnswerPack ans = in.get(args[0], args[1], args[2], args[3]);
		System.out.println(args[0] + " " + args[1] + " " + args[2] + " "
				+ args[3]);
		System.out.println("sub:  " + ans.sub);
		System.out.println("verb: " + ans.verb);
		System.out.println("obj:  " + ans.obj);
		System.out.println("qwrd: " + ans.qwrd);
	}

}
