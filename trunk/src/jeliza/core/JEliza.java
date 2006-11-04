package jeliza.core;

import jeliza.answererwrapper.*;
import jeliza.markovwrapper.JElizaMarkov;

public class JEliza {

	public AnswerWrapper answerwrapper = null;

	public Answerer answerer = null;

	public JElizaMarkov mj = null;

	public JEliza() {
		answerwrapper = new AnswerWrapper();
		answerer = new Answerer();
		mj = new JElizaMarkov();
	}

}
