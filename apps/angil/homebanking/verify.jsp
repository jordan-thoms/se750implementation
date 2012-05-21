<%
		int counter = 0;
	for (int j=0;j<10000;j++){
		for (int i=0;i<50000;i++){
			counter++;
			counter--;
		}
	}
	String username = request.getParameter("username");
	String password = request.getParameter("password");
	if (username.trim().equals("auckland") && password.trim().equals("a")) {
		response.sendRedirect("http://localhost:8080/examples/homebanking/menu.html");
	} else {
		response.sendRedirect("http://localhost:8080/examples/homebanking/login.html");
	}
%>