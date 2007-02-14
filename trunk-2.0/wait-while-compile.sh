#!/bin/sh

x=0
while [ "$(ps aux | grep c++ | grep -v grep)n" != "n" ]; do
	sleep 1
done
