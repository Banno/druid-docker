$TTL 14400
dev.banno.com.	14400	IN	SOA	ns2.banno.com.	ops.banno.com.	(
						1 ;Serial Number
						14400 ;refresh
						7200 ;retry
						1209600 ;expire
						86400 ;minimum
)
dev.banno.com.	 14400	IN	NS	ns2.banno.com.
dev.banno.com.	 14400	IN	NS	ns1.banno.com.
dev.banno.com.	 14400	IN	A	192.168.59.103
*.dev.banno.com. 14400	IN	A	192.168.59.103
