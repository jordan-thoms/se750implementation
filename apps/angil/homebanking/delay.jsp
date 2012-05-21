
<%
		java.util.Enumeration names = request.getParameterNames();
		int counter = 0;
			for (int j=0;j<5000;j++){
				for (int i=0;i<50000;i++){
					counter++;
					counter--;
				}
			}
	String name = "";
	if (names != null) {
		String tmpString = names.nextElement().toString();
			name = tmpString;
		
		if (name.equals("transfer")) {
			response.sendRedirect("http://localhost:8080/examples/homebanking/transfer.html");
		}
		if (name.equals("status")) {
			response.sendRedirect("http://localhost:8080/examples/homebanking/status.html");
		}
		if (name.equals("depot")) {
			response.sendRedirect("http://localhost:8080/examples/homebanking/depot.html");
		}
		if (name.equals("logout")) {
			response.sendRedirect("http://localhost:8080/examples/homebanking/login.html");
		}
		
		//out.print(name);
		out.print("<br>");
		
	}
%>