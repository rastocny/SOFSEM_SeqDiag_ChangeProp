package com.mlyncar.dp.comparison.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.core.GraphComparator;
import com.mlyncar.dp.comparison.core.impl.GraphComparatorImpl;
import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.comparison.entity.ChangeLog;
import com.mlyncar.dp.comparison.exception.ComparisonException;
import com.mlyncar.dp.comparison.test.ComparisonTestHelper;
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
			//Graph sourceCodeGraph = service.getGraphStructureFromSourceCode();
			Graph umlGraph1 = service.getGraphStructureFromConcreteDiagram("Interaction1");
			Graph umlGraph2 = service.getGraphStructureFromConcreteDiagram("Interaction2");
			GraphComparator comparator = new GraphComparatorImpl();
			ChangeLog log = comparator.compareGraphStructures(umlGraph2, umlGraph1);
			ComparisonTestHelper.printChanges(log);			
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
        GraphComparatorImpl comparator = new GraphComparatorImpl();
        return comparator.isSubTree(rootReferenceNode, rootSubTreeNode);
    }

}
