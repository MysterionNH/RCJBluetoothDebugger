#include <vector>
#include <errno.h>
#include <cstdint>
#include <cstring>
#include <fcntl.h>
#include <stdio.h>
#include <iostream>
#include <string.h>
#include <unistd.h>

#include <sys/stat.h>
#include <sys/types.h>
#include <sys/socket.h>

#include <bluetooth/bluetooth.h>
#include <bluetooth/l2cap.h>
#include <bluetooth/hci.h>
#include <bluetooth/hci_lib.h>

int initConnection(std::string address);
void log(std::string msg);
std::string getNextBluetoothMessage();

// bluetooth communication
int socketConnection = 0;
char buffer[1024] = { 0 };

// inter-proccess communication
int num = -1, fifo = -1;

int main(int argc, char **argv) {
	// init bluetooth connection
	if (initConnection(argv[1])) return -1;

	// open the pipe
	int fifo;
	if ((fifo = open("/tmp/fifoBlueJava", O_WRONLY)) < 0) {
	   printf("%s\n", strerror(errno));
	   return -1;
	}

	while (true) {
		std::string msg = getNextBluetoothMessage();
		if ((num = write(fifo, msg.c_str(), msg.length())) < 0) {
			printf("ERROR: %s\n", strerror(errno));
		}
	}
	close(fifo);

	return system("rm /tmp/fifoBlueJava");
}

std::string getNextBluetoothMessage() {
	// empty buffer
	std::memset(buffer, 0, sizeof(buffer));

	// read data from the client
	int bytes_read = read(socketConnection, buffer, sizeof(buffer));

	// if there was anything, write to message queue
	if (bytes_read > 0) {
		return std::string(buffer) + "\n";
	}
	return "";
}

int initConnection(std::string address) {
	// power the local bluetooth device on
	log("Turn bluetooth device on");
	system("echo \"power on\" | bluetoothctl");
	log("");

	log("Trying to connect to " + address);

	struct sockaddr_l2 addr = { 0 };
	int status = -1;

	// allocate a socket
	socketConnection = socket(AF_BLUETOOTH, SOCK_SEQPACKET, BTPROTO_L2CAP);

	// set the connection parameters (who to connect to)
	addr.l2_family = AF_BLUETOOTH;
	addr.l2_psm = htobs(0x1001);
	str2ba(address.c_str(), &addr.l2_bdaddr);

	// attempt connection
	status = connect(socketConnection, (struct sockaddr *)&addr, sizeof(addr));

	// if connection is successful, this is our address
	if (status == 0) {
		log("Connected to " + address);
		return 0;
	}
	else {
		log("Error connecting: " + std::string(std::strerror(errno)));
	}

	// power the local bluetooth device off
	log("Turn bluetooth device off");
	system("echo \"power off\" | bluetoothctl");
	log("");

	return -1;
}

void log(std::string msg) {
	std::cout << msg << std::endl;
}
