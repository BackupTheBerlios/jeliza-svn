StringArray* getSentences (string savetype) {
		m_sentenceCount = 0;
		string buffer;
		int r = 0;
		ifstream in2(m_file.c_str());
		while (in2) {
			getline(in2, buffer);
			r++;
		}
		StringArray* arr = new StringArray(r);
		
		ifstream in(m_file.c_str());
		if (!in) {
			cerr << "Fehler beim Oeffnen einer JEliza-Datei" << endl;
			return arr;
		}
		while (in) {
			getline(in, buffer);
			if (buffer.size() > 1) {
				string s("?");
				if (!Util::contains(buffer, s)) {
					(*arr)[m_sentenceCount] = Util::strip(buffer);
					m_sentenceCount++;
				}
			}
		}
		
		StringArray* arr2 = new StringArray(m_sentenceCount);
		for (int x = 0; x < m_sentenceCount; x++) {
			(*arr2)[x] = (*arr)[x];
		}
		
		delete(arr);
		return arr2;
	}
	
	
		/*
	 * The old answering algorithm
	 */
	string askOLD(string frage) { // ####################################### => OLD
		frage = Util::umwandlung(frage);
		frage = Util::replace(frage, string("?"), string(""));
		frage = Util::strip(frage);
		
		vector<string> woerter;
		Util::split(frage, " ", woerter);
		
		long double best = -1;
		vector<string>* replies = new vector<string>();

		for (int z = 0; z < m_sentenceCount; z++) {
			string sentence = (*JEliza::m_jd.m_sents)[z];
			sentence = Util::strip(sentence);
			if (sentence.size() == 0) {
				continue;
			}
			
			vector<string> woerter2;
			Util::split(sentence, " ", woerter2);
			string last = "";
			
			long double points = 0.0;
			long double hatWasGebracht = 0.0;
					
			for (unsigned int a = 0; a < woerter2.size(); a++) {
				string wort2 = woerter2[a];
				
				long double points2 = 0;
				
				for (unsigned int y = 0; y < woerter.size(); y++) {
					string wort = woerter[y];
			
					StringCompare sc(wort, wort2);
					points2 += sc.getPoints();
				}
				
				points2 = points2 / woerter.size();
				
				if (points2 > best) {
					best = points2;
					
					for (int f = 0; f < points2 / 20; f++) {
						replies->push_back(sentence);
					}
				}
				
					
			}
			
		}
		
		string answer = "";
		if (replies->size() > 0) {
			srand((unsigned) time(NULL));
			int ran = rand() % replies->size();
			answer = (*replies)[ran];
		}
		else if (m_sentenceCount > 0) {
			srand((unsigned) time(NULL));
			int ran = rand() % m_sentenceCount;
			answer = (*JEliza::m_jd.m_sents)[ran];
			cout << endl;
		}
		else {
			answer = "Erzähl mir mehr darüber!";
			cout << endl;
		}
		
		return answer;
	}