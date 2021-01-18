# UDP P2P Projects

This repository contains two distinct UDP-based peer-to-peer (P2P) applications implemented in Java:

## 1. UDP P2P Chat Application

A real-time chat application that enables direct communication between peers using UDP protocol. This application demonstrates the implementation of a decentralized messaging system without the need for a central server.

### Features:
- Direct peer-to-peer communication
- Real-time message exchange
- Command-line interface
- Support for multiple chat sessions
- Basic error handling and message acknowledgment

### Technical Details:
- Built using Java's networking APIs
- Implements UDP protocol for message transmission
- Handles peer discovery and connection management
- Includes message formatting and parsing
- Uses Java's DatagramSocket and DatagramPacket classes

## 2. UDP P2P Data Transfer

A file transfer application that allows direct file sharing between peers using UDP protocol. This application showcases efficient data transfer mechanisms in a P2P network.

### Features:
- Direct file transfer between peers
- Support for large file transfers
- Progress tracking and status updates
- Basic error recovery mechanisms
- File integrity verification

### Technical Details:
- Implements UDP for data transmission using Java's networking APIs
- Uses chunk-based file transfer
- Includes checksum verification
- Handles packet loss and retransmission
- Supports multiple concurrent transfers

## Project Structure
```
UDP Final Project/
├── UDP P2P Chat/           # UDP P2P Chat Application
│   └── src/
│       └── uni/lu/networks/finalproject/udp/
│           ├── execs/      # Executable classes
│           └── nonexec/    # Non-executable classes
├── UDP P2P Data Transfer/  # UDP P2P Data Transfer
│   └── src/
│       └── uni/lu/networks/finalproject/udp/
│           ├── execs/      # Executable classes
│           └── nonexec/    # Non-executable classes
└── README.md               # This file
```

## Requirements
- Java JDK 8 or higher
- Standard Java libraries

## License
This project is open-source and available under the MIT License.