# DiscoverSWRLRulesFromKB
There are two main project:
1) SWRLRulesFromOntology: thanks to the use of genetic alghorithms and the OWL API, it is possible to extract information from the ontology loaded.

   These new knowledge is in the form of SWRL rules. 
   
   The genetic algorithm evolves the population containing the classes and the properties of the ontology, discovering new patterns, until the best remain.
   
   The rules are in a file *.txt .
   
   Input: file *.owl (ontology)
   
   Output: file *.txt (rules)
   
2) SWRLRulesFromRDF: it is an evolution of the SWRLRulesFromOntology. 
   The classes, the properties and the individuals are taken directly from the RDF graphs and not from a file *.owl.
   It is necessary to query the SPARQL Endpoint, without using the OWL API. 
   The results are the same of the other algorithm, but they are constantly updated, since it takes data directly from the graphs, without having to download them.
   After discovering the new rules, the algorithm does other queries to understand if the rules are consistent with the RDF graphs.
   If the answer is yes, the rule will be added to the output file, otherwise it will be deleted.
   Input: file *.txt with the graphs to query (if any).
   Output: file *.txt with the rules.
