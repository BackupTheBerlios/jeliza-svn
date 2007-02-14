#ifndef JELIZA_arrays
#define JELIZA_arrays 1

/*
 * This is part of JEliza 2.0.
 * Copyright 2006 by Tobias Schulz
 * WWW: http://jeliza.ch.to/
 * 
 * JEliza is free software; you can redistribute it and/or      
 * modify it under the terms of the GNU Lesser General Public    
 * License as published by the Free Software Foundation; either  
 * version 2.1 of the License, or (at your option) any later     
 * version.                                                      
 *                                                               
 * JEliza is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of    
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.          
 * See the GNU Lesser General Public License for more details.   
 *                                                               
 * You should have received a copy of the GNU LGPL               
 * along with JEliza (file "lgpl.txt") ; if not, write           
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

using namespace std;

template<typename Typ>
class MyArray {
    std::vector<Typ> m_vector;
    
public:
//    typedef std::vector<Typ>::size_type size_type;
    typedef int size_type;
    
    // explicit MyArray (size_type groesse)
    MyArray (size_type groesse)
    : m_vector(groesse)
    {}
    
    MyArray (const MyArray& ma)
        : m_vector(ma.m_vector)
    {}
    
    MyArray()
    {}
    
    MyArray& operator= (const MyArray& ma) {
        m_vector = ma.m_vector;
        return (*this);
    }
    
    size_type size() const {
        return (m_vector.size());
    }
    
    Typ& operator[] (int pos) {
        if (pos >= 0)
            return (m_vector[pos]);
        else
            return (m_vector[m_vector.size() + pos]);
    }

    void add (Typ t) {
        m_vector[m_vector.size()] = t;
    }
};

class StringArray {
    std::vector<string> m_vector;
    int m_lastindex;
    
public:
    typedef int size_type;
    
    explicit StringArray (size_type groesse)
    : m_vector(groesse), m_lastindex(0)
    {}
    
    StringArray (const StringArray& ma)
        : m_vector(ma.m_vector), m_lastindex(0)
    {}
    
    StringArray()
    {}
    
    StringArray& operator= (const StringArray& ma) {
        m_vector = ma.m_vector;
        return (*this);
    }
    
    size_type size() const {
        return (m_vector.size());
    }
    
    string& operator[] (int pos) {
        if (pos >= 0)
            return (m_vector[pos]);
        else
            return (m_vector[m_vector.size() + pos]);
    }

    void add (string t) {
        m_vector[m_lastindex] = t;
        m_lastindex++;
    }

    void set (int x, string t) {
        m_vector[x] = t;
    }
};


#endif

