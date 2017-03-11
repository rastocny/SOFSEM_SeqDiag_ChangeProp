package com.mlyncar.dp.comparison.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.change.Change;
import com.mlyncar.dp.comparison.change.ChangeLog;
import com.mlyncar.dp.comparison.exception.ComparisonException;
import com.mlyncar.dp.comparison.graph.SubgraphComparator;
import com.mlyncar.dp.transformer.entity.Graph;
import com.mlyncar.dp.transformer.entity.Node;
import com.mlyncar.dp.transformer.exception.GraphTransformationException;
import com.mlyncar.dp.transformer.service.TransformationService;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class ComparisonService {

    private final Logger logger = LoggerFactory.getLogger(ComparisonService.class);

	public ChangeLog compareUmlModelWithSourceCode() throws ComparisonException {

		TransformationService service = new TransformationService();
		try {	
			logger.debug("Starting to generate changes between graph structures.");
			Graph sourceCodeGraph = service.getGraphStructureFromSourceCode();
			Graph umlGraph = service.getGraphStructureFromConcreteDiagram("SequenceDiagramTest1");
		} catch (GraphTransformationException ex) {
			throw new ComparisonException("Comparison Service failed: Error while transforming diagram structure to graph.", ex);
		}
		return null;
	}
	
    public ChangeLog getChangesInTwoGraphs(String referenceInteractionId, String subInteractionId) {
        
/*        GraphCreator creator = new GraphCreatorImpl();
        //random uuid classes
        Graph subGraph = creator.createGraphFromId(UUID.randomUUID().toString());  
        Graph referenceGraph = creator.createGraphFromId(UUID.randomUUID().toString());
        
        ChangeLog changeLog = new ChangeLogImpl(subGraph, referenceGraph);
        //No changes detected - just return changelog
        if (isGraphSubgraph(referenceGraph, subGraph)) {
            return changeLog;
        }
        //check for changes in graph
        changeLog.changes().addAll(findChangesInGraph(referenceGraph, subGraph));
        return changeLog;
  */
    	return null;
    }

    public List<Change> findChangesInGraph(Graph referenceTree, Graph subTree) {
        throw new UnsupportedOperationException("Not supported yet");
    }
    
    public boolean isGraphSubgraph(Graph referenceTree, Graph subTree) {
        Node rootReferenceNode = referenceTree.getRootNode();
        Node rootSubTreeNode = subTree.getRootNode();
        SubgraphComparator comparator = new SubgraphComparator();
        return comparator.isSubTree(rootReferenceNode, rootSubTreeNode);
    }

}
