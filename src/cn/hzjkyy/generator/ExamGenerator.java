package cn.hzjkyy.generator;

import cn.hzjkyy.model.User;

public class ExamGenerator extends JlcGenerator {

	public ExamGenerator(User user) {
		super(user);
	}

	@Override
	public String getJkid() {
		return "A001707";
	}
}
