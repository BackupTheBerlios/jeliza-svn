/** \file UdpSocket.cpp
 **	\date  2004-02-13
 **	\author grymse@alhem.net
**/
/*
Copyright (C) 2004-2006  Anders Hedstrom

This library is made available under the terms of the GNU GPL.

If you would like to use this library in a closed-source application,
a separate license agreement is available. For information about
the closed-source license agreement for the C++ sockets library,
please visit http://www.alhem.net/Sockets/license.html and/or
email license@alhem.net.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
#ifdef _WIN32
#pragma warning(disable:4786)
#include <stdlib.h>
#else
#include <errno.h>
#endif
#include <stdio.h>

#include "ISocketHandler.h"
#include "UdpSocket.h"
#include "Utility.h"
#include "Ipv4Address.h"
#include "Ipv6Address.h"
// include this to see strange sights
//#include <linux/in6.h>


#ifdef SOCKETS_NAMESPACE
namespace SOCKETS_NAMESPACE {
#endif


UdpSocket::UdpSocket(ISocketHandler& h, int ibufsz, bool ipv6) : Socket(h)
, m_ibuf(new char[ibufsz])
, m_ibufsz(ibufsz)
, m_bind_ok(false)
, m_port(0)
, m_last_size_written(-1)
{
#ifdef IPPROTO_IPV6
	SetIpv6(ipv6);
#endif
}


UdpSocket::~UdpSocket()
{
	Close();
	delete[] m_ibuf;
}


int UdpSocket::Bind(port_t &port, int range)
{
#ifdef IPPROTO_IPV6
	if (IsIpv6())
	{
		Ipv6Address ad(port);
		return Bind(ad, range);
	}
#endif
	Ipv4Address ad(port);
	return Bind(ad, range);
}


int UdpSocket::Bind(const std::string& intf, port_t &port, int range)
{
#ifdef IPPROTO_IPV6
	if (IsIpv6())
	{
		Ipv6Address ad(intf, port);
		if (ad.IsValid())
		{
			return Bind(ad, range);
		}
		SetCloseAndDelete();
		return -1;
	}
#endif
	Ipv4Address ad(intf, port);
	if (ad.IsValid())
	{
		return Bind(ad, range);
	}
	SetCloseAndDelete();
	return -1;
}


int UdpSocket::Bind(ipaddr_t a, port_t &port, int range)
{
	Ipv4Address ad(a, port);
	return Bind(ad, range);
}


#ifdef IPPROTO_IPV6
int UdpSocket::Bind(in6_addr a, port_t &port, int range)
{
	Ipv6Address ad(a, port);
	return Bind(ad, range);
}
#endif


int UdpSocket::Bind(SocketAddress& ad, int range)
{
	if (GetSocket() == INVALID_SOCKET)
	{
		Attach(CreateSocket(ad.GetFamily(), SOCK_DGRAM, "udp"));
	}
	if (GetSocket() != INVALID_SOCKET)
	{
		SetNonblocking(true);
		int n = bind(GetSocket(), ad, ad);
		int tries = range;
		while (n == -1 && tries--)
		{
			ad.SetPort(ad.GetPort() + 1);
			n = bind(GetSocket(), ad, ad);
		}
		if (n == -1)
		{
			Handler().LogError(this, "bind", Errno, StrError(Errno), LOG_LEVEL_FATAL);
			SetCloseAndDelete();
			return -1;
		}
		m_bind_ok = true;
		m_port = ad.GetPort();
		return 0;
	}
	return -1;
}


/** if you wish to use Send, first Open a connection */
bool UdpSocket::Open(ipaddr_t l, port_t port)
{
	Ipv4Address ad(l, port);
	return Open(ad);
}


bool UdpSocket::Open(const std::string& host, port_t port)
{
#ifdef IPPROTO_IPV6
	if (IsIpv6())
	{
		Ipv6Address ad(host, port);
		if (ad.IsValid())
		{
			return Open(ad);
		}
		return false;
	}
#endif
	Ipv4Address ad(host, port);
	if (ad.IsValid())
	{
		return Open(ad);
	}
	return false;
}


#ifdef IPPROTO_IPV6
bool UdpSocket::Open(struct in6_addr& a, port_t port)
{
	Ipv6Address ad(a, port);
	return Open(ad);
}
#endif


bool UdpSocket::Open(SocketAddress& ad)
{
	if (GetSocket() == INVALID_SOCKET)
	{
		Attach(CreateSocket(ad.GetFamily(), SOCK_DGRAM, "udp"));
	}
	if (GetSocket() != INVALID_SOCKET)
	{
		SetNonblocking(true);
		if (connect(GetSocket(), ad, ad) == -1)
		{
			Handler().LogError(this, "connect", Errno, StrError(Errno), LOG_LEVEL_FATAL);
			SetCloseAndDelete();
			return false;
		}
		SetConnected();
		return true;
	}
	return false;
}


void UdpSocket::CreateConnection()
{
#ifdef IPPROTO_IPV6
	if (IsIpv6())
	{
		if (GetSocket() == INVALID_SOCKET)
		{
			SOCKET s = CreateSocket(AF_INET6, SOCK_DGRAM, "udp");
			if (s == INVALID_SOCKET)
			{
				return;
			}
			SetNonblocking(true, s);
			Attach(s);
		}
		return;
	}
#endif
	if (GetSocket() == INVALID_SOCKET)
	{
		SOCKET s = CreateSocket(AF_INET, SOCK_DGRAM, "udp");
		if (s == INVALID_SOCKET)
		{
			return;
		}
		SetNonblocking(true, s);
		Attach(s);
	}
}


/** send to specified address */
void UdpSocket::SendToBuf(const std::string& h, port_t p, const char *data, int len, int flags)
{
#ifdef IPPROTO_IPV6
	if (IsIpv6())
	{
		Ipv6Address ad(h, p);
		if (ad.IsValid())
		{
			SendToBuf(ad, data, len, flags);
		}
		return;
	}
#endif
	Ipv4Address ad(h, p);
	if (ad.IsValid())
	{
		SendToBuf(ad, data, len, flags);
	}
}


/** send to specified address */
void UdpSocket::SendToBuf(ipaddr_t a, port_t p, const char *data, int len, int flags)
{
	Ipv4Address ad(a, p);
	SendToBuf(ad, data, len, flags);
}


#ifdef IPPROTO_IPV6
void UdpSocket::SendToBuf(in6_addr a, port_t p, const char *data, int len, int flags)
{
	Ipv6Address ad(a, p);
	SendToBuf(ad, data, len, flags);
}
#endif


void UdpSocket::SendToBuf(SocketAddress& ad, const char *data, int len, int flags)
{
	if (GetSocket() == INVALID_SOCKET)
	{
		Attach(CreateSocket(ad.GetFamily(), SOCK_DGRAM, "udp"));
	}
	if (GetSocket() != INVALID_SOCKET)
	{
		SetNonblocking(true);
		if ((m_last_size_written = sendto(GetSocket(), data, len, flags, ad, ad)) == -1)
		{
			Handler().LogError(this, "sendto", Errno, StrError(Errno), LOG_LEVEL_ERROR);
		}
	}
}


void UdpSocket::SendTo(const std::string& a, port_t p, const std::string& str, int flags)
{
	SendToBuf(a, p, str.c_str(), (int)str.size(), flags);
}


void UdpSocket::SendTo(ipaddr_t a, port_t p, const std::string& str, int flags)
{
	SendToBuf(a, p, str.c_str(), (int)str.size(), flags);
}


#ifdef IPPROTO_IPV6
void UdpSocket::SendTo(in6_addr a, port_t p, const std::string& str, int flags)
{
	SendToBuf(a, p, str.c_str(), (int)str.size(), flags);
}
#endif


void UdpSocket::SendTo(SocketAddress& ad, const std::string& str, int flags)
{
	SendToBuf(ad, str.c_str(), (int)str.size(), flags);
}


/** send to connected address */
void UdpSocket::SendBuf(const char *data, size_t len, int flags)
{
	if (!IsConnected())
	{
		Handler().LogError(this, "SendBuf", 0, "not connected", LOG_LEVEL_ERROR);
		return;
	}
	if ((m_last_size_written = send(GetSocket(), data, (int)len, flags)) == -1)
	{
		Handler().LogError(this, "send", Errno, StrError(Errno), LOG_LEVEL_ERROR);
	}
}


void UdpSocket::Send(const std::string& str, int flags)
{
	SendBuf(str.c_str(), (int)str.size(), flags);
}


void UdpSocket::OnRead()
{
#ifdef IPPROTO_IPV6
	if (IsIpv6())
	{
		struct sockaddr_in6 sa;
		socklen_t sa_len = sizeof(sa);
		int n = recvfrom(GetSocket(), m_ibuf, m_ibufsz, 0, (struct sockaddr *)&sa, &sa_len);
		int q = 10; // receive max 10 at one cycle
		while (n > 0)
		{
			if (sa_len != sizeof(sa))
			{
				Handler().LogError(this, "recvfrom", 0, "unexpected address struct size", LOG_LEVEL_WARNING);
			}
			this -> OnRawData(m_ibuf, n, (struct sockaddr *)&sa, sa_len);
			if (!q--)
				break;
			//
			n = recvfrom(GetSocket(), m_ibuf, m_ibufsz, 0, (struct sockaddr *)&sa, &sa_len);
		}
		if (n == -1)
		{
#ifdef _WIN32
			if (Errno != WSAEWOULDBLOCK)
#else
			if (Errno != EWOULDBLOCK)
#endif
				Handler().LogError(this, "recvfrom", Errno, StrError(Errno), LOG_LEVEL_ERROR);
		}
		return;
	}
#endif
	struct sockaddr_in sa;
	socklen_t sa_len = sizeof(sa);
	int n = recvfrom(GetSocket(), m_ibuf, m_ibufsz, 0, (struct sockaddr *)&sa, &sa_len);
	int q = 10;
	while (n > 0)
	{
		if (sa_len != sizeof(sa))
		{
			Handler().LogError(this, "recvfrom", 0, "unexpected address struct size", LOG_LEVEL_WARNING);
		}
		this -> OnRawData(m_ibuf, n, (struct sockaddr *)&sa, sa_len);
		if (!q--)
			break;
		//
		n = recvfrom(GetSocket(), m_ibuf, m_ibufsz, 0, (struct sockaddr *)&sa, &sa_len);
	}
	if (n == -1)
	{
#ifdef _WIN32
		if (Errno != WSAEWOULDBLOCK)
#else
		if (Errno != EWOULDBLOCK)
#endif
			Handler().LogError(this, "recvfrom", Errno, StrError(Errno), LOG_LEVEL_ERROR);
	}
}


void UdpSocket::SetBroadcast(bool b)
{
	int one = 1;
	int zero = 0;

	if (GetSocket() == INVALID_SOCKET)
	{
		CreateConnection();
	}
	if (b)
	{
		if (setsockopt(GetSocket(), SOL_SOCKET, SO_BROADCAST, (char *) &one, sizeof(one)) == -1)
		{
			Handler().LogError(this, "SetBroadcast", Errno, StrError(Errno), LOG_LEVEL_WARNING);
		}
	}
	else
	{
		if (setsockopt(GetSocket(), SOL_SOCKET, SO_BROADCAST, (char *) &zero, sizeof(zero)) == -1)
		{
			Handler().LogError(this, "SetBroadcast", Errno, StrError(Errno), LOG_LEVEL_WARNING);
		}
	}
}


bool UdpSocket::IsBroadcast()
{
	int is_broadcast = 0;
	socklen_t size;

	if (GetSocket() == INVALID_SOCKET)
	{
		CreateConnection();
	}
	if (getsockopt(GetSocket(), SOL_SOCKET, SO_BROADCAST, (char *)&is_broadcast, &size) == -1)
	{
		Handler().LogError(this, "IsBroadcast", Errno, StrError(Errno), LOG_LEVEL_WARNING);
	}
	return is_broadcast != 0;
}


void UdpSocket::SetMulticastTTL(int ttl)
{
	if (GetSocket() == INVALID_SOCKET)
	{
		CreateConnection();
	}
	if (setsockopt(GetSocket(), SOL_IP, IP_MULTICAST_TTL, (char *)&ttl, sizeof(int)) == -1)
	{
		Handler().LogError(this, "SetMulticastTTL", Errno, StrError(Errno), LOG_LEVEL_WARNING);
	}
}


int UdpSocket::GetMulticastTTL()
{
	int ttl = 0;
	socklen_t size = sizeof(int);

	if (GetSocket() == INVALID_SOCKET)
	{
		CreateConnection();
	}
	if (getsockopt(GetSocket(), SOL_IP, IP_MULTICAST_TTL, (char *)&ttl, &size) == -1)
	{
		Handler().LogError(this, "GetMulticastTTL", Errno, StrError(Errno), LOG_LEVEL_WARNING);
	}
	return ttl;
}


void UdpSocket::SetMulticastLoop(bool x)
{
	if (GetSocket() == INVALID_SOCKET)
	{
		CreateConnection();
	}
#ifdef IPPROTO_IPV6
	if (IsIpv6())
	{
		int val = x ? 1 : 0;
		if (setsockopt(GetSocket(), IPPROTO_IPV6, IPV6_MULTICAST_LOOP, (char *)&val, sizeof(int)) == -1)
		{
			Handler().LogError(this, "SetMulticastLoop", Errno, StrError(Errno), LOG_LEVEL_WARNING);
		}
		return;
	}
#endif
	int val = x ? 1 : 0;
	if (setsockopt(GetSocket(), SOL_IP, IP_MULTICAST_LOOP, (char *)&val, sizeof(int)) == -1)
	{
		Handler().LogError(this, "SetMulticastLoop", Errno, StrError(Errno), LOG_LEVEL_WARNING);
	}
}


bool UdpSocket::IsMulticastLoop()
{
	if (GetSocket() == INVALID_SOCKET)
	{
		CreateConnection();
	}
#ifdef IPPROTO_IPV6
	if (IsIpv6())
	{
		int is_loop = 0;
		socklen_t size = sizeof(int);
		if (getsockopt(GetSocket(), IPPROTO_IPV6, IPV6_MULTICAST_LOOP, (char *)&is_loop, &size) == -1)
		{
			Handler().LogError(this, "IsMulticastLoop", Errno, StrError(Errno), LOG_LEVEL_WARNING);
		}
		return is_loop ? true : false;
	}
#endif
	int is_loop = 0;
	socklen_t size = sizeof(int);
	if (getsockopt(GetSocket(), SOL_IP, IP_MULTICAST_LOOP, (char *)&is_loop, &size) == -1)
	{
		Handler().LogError(this, "IsMulticastLoop", Errno, StrError(Errno), LOG_LEVEL_WARNING);
	}
	return is_loop ? true : false;
}


void UdpSocket::AddMulticastMembership(const std::string& group, const std::string& local_if, int if_index)
{
	if (GetSocket() == INVALID_SOCKET)
	{
		CreateConnection();
	}
#ifdef IPPROTO_IPV6
	if (IsIpv6())
	{
		struct ipv6_mreq x;
		struct in6_addr addr;
		if (Utility::u2ip( group, addr ))
		{
			x.ipv6mr_multiaddr = addr;
			x.ipv6mr_interface = if_index;
			if (setsockopt(GetSocket(), IPPROTO_IPV6, IPV6_ADD_MEMBERSHIP, (char *)&x, sizeof(struct ipv6_mreq)) == -1)
			{
				Handler().LogError(this, "AddMulticastMembership", Errno, StrError(Errno), LOG_LEVEL_WARNING);
			}
		}
		return;
	}
#endif
	struct ip_mreq x; // ip_mreqn
	ipaddr_t addr;
	if (Utility::u2ip( group, addr ))
	{
		memcpy(&x.imr_multiaddr.s_addr, &addr, sizeof(addr));
		Utility::u2ip( local_if, addr);
		memcpy(&x.imr_interface.s_addr, &addr, sizeof(addr));
//		x.imr_ifindex = if_index;
		if (setsockopt(GetSocket(), SOL_IP, IP_ADD_MEMBERSHIP, (char *)&x, sizeof(struct ip_mreq)) == -1)
		{
			Handler().LogError(this, "AddMulticastMembership", Errno, StrError(Errno), LOG_LEVEL_WARNING);
		}
	}
}


void UdpSocket::DropMulticastMembership(const std::string& group, const std::string& local_if, int if_index)
{
	if (GetSocket() == INVALID_SOCKET)
	{
		CreateConnection();
	}
#ifdef IPPROTO_IPV6
	if (IsIpv6())
	{
		struct ipv6_mreq x;
		struct in6_addr addr;
		if (Utility::u2ip( group, addr ))
		{
			x.ipv6mr_multiaddr = addr;
			x.ipv6mr_interface = if_index;
			if (setsockopt(GetSocket(), IPPROTO_IPV6, IPV6_DROP_MEMBERSHIP, (char *)&x, sizeof(struct ipv6_mreq)) == -1)
			{
				Handler().LogError(this, "DropMulticastMembership", Errno, StrError(Errno), LOG_LEVEL_WARNING);
			}
		}
		return;
	}
#endif
	struct ip_mreq x; // ip_mreqn
	ipaddr_t addr;
	if (Utility::u2ip( group, addr ))
	{
		memcpy(&x.imr_multiaddr.s_addr, &addr, sizeof(addr));
		Utility::u2ip( local_if, addr);
		memcpy(&x.imr_interface.s_addr, &addr, sizeof(addr));
//		x.imr_ifindex = if_index;
		if (setsockopt(GetSocket(), SOL_IP, IP_DROP_MEMBERSHIP, (char *)&x, sizeof(struct ip_mreq)) == -1)
		{
			Handler().LogError(this, "DropMulticastMembership", Errno, StrError(Errno), LOG_LEVEL_WARNING);
		}
	}
}


#ifdef IPPROTO_IPV6
void UdpSocket::SetMulticastHops(int hops)
{
	if (GetSocket() == INVALID_SOCKET)
	{
		CreateConnection();
	}
	if (!IsIpv6())
	{
		Handler().LogError(this, "SetMulticastHops", 0, "Ipv6 only", LOG_LEVEL_ERROR);
		return;
	}
	if (setsockopt(GetSocket(), IPPROTO_IPV6, IPV6_MULTICAST_HOPS, (char *)&hops, sizeof(int)) == -1)
	{
		Handler().LogError(this, "SetMulticastHops", Errno, StrError(Errno), LOG_LEVEL_WARNING);
	}
}


int UdpSocket::GetMulticastHops()
{
	if (GetSocket() == INVALID_SOCKET)
	{
		CreateConnection();
	}
	if (!IsIpv6())
	{
		Handler().LogError(this, "SetMulticastHops", 0, "Ipv6 only", LOG_LEVEL_ERROR);
		return -1;
	}
	int hops = 0;
	socklen_t size = sizeof(int);
	if (getsockopt(GetSocket(), IPPROTO_IPV6, IPV6_MULTICAST_HOPS, (char *)&hops, &size) == -1)
	{
		Handler().LogError(this, "GetMulticastHops", Errno, StrError(Errno), LOG_LEVEL_WARNING);
	}
	return hops;
}
#endif // IPPROTO_IPV6


bool UdpSocket::IsBound()
{
	return m_bind_ok;
}


void UdpSocket::OnRawData(const char *buf, size_t len, struct sockaddr *sa, socklen_t sa_len)
{
}


port_t UdpSocket::GetPort()
{
	return m_port;
}


int UdpSocket::GetLastSizeWritten()
{
	return m_last_size_written;
}


#ifdef SOCKETS_NAMESPACE
}
#endif
