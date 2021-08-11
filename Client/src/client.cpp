/*
 * BBclient.cpp
 *
 *  Created on: Jan 7, 2018
 *      Author: awadi
 */
#include "../include/connectionHandler.h"
using namespace std;
#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include <boost/thread.hpp>
using boost::asio::ip::tcp;
using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;

static void readkeyboard(ConnectionHandler* connectionHandler) {
	while (1) {

		const short bufsize = 1024;
		char buf[bufsize];
		std::cin.getline(buf, bufsize);
		std::string line(buf);
		if (!connectionHandler->sendLine(line)) {
			return;
		}
		// connectionHandler.sendLine(line) appends '\n' to the message. Therefor we send len+1 bytes.
	}

}

void start(ConnectionHandler* conn) {

	boost::thread readfromkeybaord(readkeyboard, conn);
	std::string answer;
	while (1) {
		if (!conn->getLine(answer)) {
			break;
		}

		int len = answer.length();
		// A C string must end with a 0 char delimiter.  When we filled the answer buffer from the socket
		// we filled up to the \n char - we must make sure now that a 0 char is also present. So we truncate last character.
		answer.resize(len - 1);
                std::cout<<answer<<std::endl;
		if (answer == "ACK signout succeeded") {
                    return;
		}
		answer = "";
	}
	return;
}
int main(int argc, char *argv[]) {

	if (argc < 3) {
	 std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
	 return -1;
	 }
	 std::string host = argv[1];
	 short port = atoi(argv[2]);

	ConnectionHandler* connectionHandler = new ConnectionHandler(
			host,port);
	if (!connectionHandler->connect()) {
		return 1;

	}
	start(connectionHandler);
	delete connectionHandler;
	return 0;

}

