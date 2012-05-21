<%

	//Back to manu
	int counter = 0;
	for (int j=0;j<1000;j++){
		for (int i=0;i<50000;i++){
			counter++;
			counter--;
		}
	}
	response.sendRedirect("http://localhost:8080/examples/homebanking/menu.html");
%>