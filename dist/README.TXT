The Search - version 1.0 

Authors: Caio GARCIA CANCIAN and Thales LOIOLA RAVELI

HOW TO LUNCH AND COMPILE THE APPLICATION
It is possible to recompile the project from scratch or to lunch TheSearch_v1.jar, in the "dist" folder.  
In both cases, we highly recommend checking if the "bigtext.txt" and "w2.txt" files are both in the main folder of the project
AND in the "dist" folder.
 

GENERAL INTRUCTIONS:
1. Enter your search query in the indicated text field and press the "Search" button to show the results.

2. In the properties menu, you can change some options:
	2.1 Correction algorithm: you can choose between Norvig or SymSpell algorithm. As SymSpell is faster, it is the default choice.
	2.2 External search engine: you can choose between Google and Bing. Google is the default choice.
	2.3 Levenshtein distance: it's the maximal edit distance considered during the candidates generation in query correction. The default is one, but
	you can choose two if you want more agressive corrections (may cause "overcorrection").

3. With the current ngram database, the software DOES NOT SUPPORT contracted forms such as: "I'll" or "isn't". 
For the correct behaviour of the software, prefer using the complete version of the same expressions ("I will" and "is not").

4. The intense, repetitive usage of the software may cause Google to block, for a short period of time, the user's access to its search engine.
This can be observed if the suggestions and statistics no longer change. To fix this, you can change to Bing and wait a little before going
back to Google.

PLEASE NOTE:
As the software has to charge different databases (dictionaries and ngrams) during its initialization, it's normal if this process takes 
some time (up to 5 - 10 seconds in some older computers) to finish. 