package com.jeroensteenbeeke.topiroll.beholder.web;

import com.jeroensteenbeeke.topiroll.beholder.BeholderApplication;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;

import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

public class DoorbellFilter implements Filter {
	public void destroy() {
	}

	public void doFilter(ServletRequest req,
			ServletResponse resp, FilterChain chain)
			throws ServletException, IOException {
		String username = req.getParameter("user_name");

		BeholderApplication.get().getBean(MapService.class).doorbell(username);

		PrintWriter writer = resp.getWriter();

		writer.print("200");
		writer.println();
	}

	public void init(FilterConfig config)
			throws ServletException {

	}

}
