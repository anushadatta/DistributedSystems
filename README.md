# Distributed Banking System 

> CZ4013 Distributed Systems \
> School of Computer Science and Engineering \
> Nanyang Technological University

This distributed banking system follows the client-server architecture, which rests on principles of interprocess communication and remote invocation to facilitate communication between clients and a server using the User Datagram Protocol (UDP).

### System Services

1. Create Account

2. Close Account

3. Deposit/Withdrawal Funds

4. View Account Balance [Idempotent]

5. Transfer Funds [Non-Idempotent]

6. Monitor Updates (Register Callback)


### Transmission Modes 

In addition to normal transmission, the system allows simulation of transmission loss by specifying socket probability of packet loss and socket timeout in seconds. 

* Normal Transmission

* Sending Loss Transmission

* Receiving Loss Transmission

### Invocation Sematics

Further experimentation has been conducted to determine the robustness of inbuilt error handling and the effects of different invocation semantics on fault-tolerance.

* At-Least-Once

* At-Most-Once


### Team

* Anusha Datta
* Amrita Ravishankar 
* Truong Cong Cuong


