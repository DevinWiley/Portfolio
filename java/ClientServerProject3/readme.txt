compiling:
	javac -d ./ Client.java
	javac -d ./ Server.java

running:
	ensure that images that will be transfered are in the server/ directory

	java server/Server 57463	//will run server program

					//will run client program
	java client/Client cslinux1.utdallas.edu 57463

other details:
	the images will be saved in client/ directory with
	name of Image + number + .jpg
	the number in the image name will depend on the
	order the images transfered, to the client