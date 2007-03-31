#ifndef JELIZA_string_compare
#define JELIZA_string_compare 1

/*
 * This is part of JEliza 2.0.
 * Copyright 2006 by Tobias Schulz
 * WWW: http://jeliza.ch.to/
 *
 * JEliza is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later
 * version.
 *
 * JEliza is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU GPL
 * along with JEliza (file "gpl.txt") ; if not, write
 * to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

#include <iostream>
#include <fstream>
#include <string>
#include <map>

#include <string>
#include <list>

#include <time.h>
#include <vector>

#include "util.cpp"
#include "arrays.cpp"

using namespace std;







class StringCompare {
	long double points;
	int x;
	int acc;
	int accBest;
	unsigned int y;
	int s1_size;
	int s2_size;
	long double res;

public:
	StringCompare (string s1, string s2)
	: points( ( compare(s1, s2) + compare(s2, s1) ) / 2)
	{
	}

	long double getPoints() {
		return points;
	}

    long double compare (string s1, string s2) {
        x = -1;
        acc = 0;
        accBest = 0;

        s1_size = (signed) s1.size();
        s2_size = (signed) s2.size();

        while (x < s1_size && x < s2_size) {
            accBest = accBest + 1;

            x++;

            if (s1[x] == s2[x]) {
                acc++;
                continue;
            }

            y = x + 1;
            if (y < s2_size && s1[x] == s2[y] && s1_size > 3) {
                acc++;
                continue;
            }

            y = x + 2;
            if (y < s2_size && s1[x] == s2[y] && s1_size > 3) {
                acc++;
                continue;
            }

            y = x + 3;
            if (y < s2_size && s1[x] == s2[y] && s1_size > 4) {
                acc++;
                continue;
            }

            y = x + 4;
            if (y < s2_size && s1[x] == s2[y] && s1_size > 5) {
                acc++;
                continue;
            }

            y = x + 5;
            if (y < s2_size && s1[x] == s2[y] && s1_size > 6) {
                acc++;
                continue;
            }

            y = x - 1;
            if (y < s2_size && s1[x] == s2[y] && s1_size > 3) {
                acc++;
                continue;
            }

            y = x - 2;
            if (y < s2_size && s1[x] == s2[y] && s1_size > 3) {
                acc++;
                continue;
            }

            y = x - 3;
            if (y < s2_size && s1[x] == s2[y] && s1_size > 4) {
                acc++;
                continue;
            }

            y = x - 4;
            if (y < s2_size && s1[x] == s2[y] && s1_size > 5) {
                acc++;
                continue;
            }

            y = x - 5;
            if (y < s2_size && s1[x] == s2[y] && s1_size > 6) {
                acc++;
                continue;
            }

        }

        if (s1_size == s2_size) {
            acc++;
            accBest++;
        }

        if (s1 == s2) {
            return 1000.0;
        }

////        cout << "acc     " << acc << endl;
////        cout << "accBest " << accBest << endl;

        res = 0.0;
        if (accBest > 0 and acc > 0) {
            res = 100 / accBest * acc;
        }
        if (res > 100) {
            res = 100.0;
        }

        return res;
    }

};


#endif

