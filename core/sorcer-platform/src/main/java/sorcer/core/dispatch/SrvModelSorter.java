package sorcer.core.dispatch;

import org.codehaus.plexus.util.dag.CycleDetectedException;
import org.codehaus.plexus.util.dag.DAG;
import org.codehaus.plexus.util.dag.TopologicalSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sorcer.core.context.model.srv.SrvModel;
import sorcer.service.*;

import java.util.*;

/**
 * SORCER class
 * User: Pawel Rubach
 * Date: 23.11.2015
 *
 * Sort a list of entries in a model taking into account the dependencies
 *
 * This code is based on the ProjectSorter class from Apache Maven 2
 */
public class SrvModelSorter {

    private static Logger logger = LoggerFactory.getLogger(SrvModelSorter.class.getName());
    private final DAG dag;
    private final Map entryMap;
    private final Map<String, String> contextIdsMap;
    private final Map<String, String> revContextIdsMap;
    private List<Mogram> sortedEntries = null;
    private SrvModel srvModel;

    /**
     * Construct the ExertionSorter
     */
    public SrvModelSorter(SrvModel srvModel) throws ContextException, SortingException {

        dag = new DAG();
        entryMap = new HashMap();
        contextIdsMap = new HashMap<String, String>();
        revContextIdsMap = new HashMap<String, String>();
        this.srvModel = srvModel;

        //addVertex(this.srvModel);

        try {
            getMapping(this.srvModel);
            checkParentCycle(this.srvModel);

            List sortedProjects = new ArrayList();
            for (Iterator i = TopologicalSorter.sort(dag).iterator(); i.hasNext(); ) {
                String id = (String) i.next();
                sortedProjects.add(entryMap.get(id));
            }

            this.sortedEntries = Collections.unmodifiableList(sortedProjects);
            //reorderJob(this.srvModel, this.sortedEntries);
        } catch (CycleDetectedException ce) {
            throw new SortingException(ce.getMessage());
        }
    }

    /**
     * Return the reordered job
     *
     * @return
     */
    public SrvModel getSortedJob() {
        return srvModel;
    }


    /**
     * Helper method to build a tree of all exertion IDs in a tree - required by setFlow
     * @param sortedSubXrt
     * @return
     */
    private List<String> addSubExertions(List<Mogram> sortedSubXrt) {
        List<String> sortedSubsetIds = new ArrayList<String>();
        for (Mogram xrt : sortedSubXrt) {
            sortedSubsetIds.add(xrt.getId().toString());
            if (xrt instanceof Job)
                sortedSubsetIds.addAll(addSubExertions(((Job) xrt).getMograms()));
        }
        return sortedSubsetIds;
    }


    /**
     * Determine the Flow (PAR or SEQ) for exertions that have the Flow set to AUTO
     *
     * @param topXrt
     * @param sortedSubXrt
     * @return
     */
    private Strategy.Flow setFlow(Exertion topXrt, List<Mogram> sortedSubXrt) {
        List<String> sortedSubsetIds = addSubExertions(sortedSubXrt);

        int edges = 0;
        for (Mogram xrt : topXrt.getMograms()) {
            for (String depId : dag.getParentLabels(xrt.getId().toString())) {
                if (sortedSubsetIds.contains(depId)) {
                    edges++;
                    logger.debug("Edge: " + xrt.getName() + " parent: " + depId);
                }
            }
            for (String depId : dag.getChildLabels(xrt.getId().toString())) {
                if (sortedSubsetIds.contains(depId)) {
                    edges++;
                    logger.debug("Edge: " + xrt.getName() + " child: " + depId);
                }
            }
        }
        if (topXrt.getMograms().size() > 0)
            logger.debug("XRT " + topXrt.getName() + " has edges: " + edges);
        if (edges == 0) return Strategy.Flow.PAR;
        else return Strategy.Flow.SEQ;
    }

    /**
     * Actually rearrange the exertions in the job according to the sorting
     *
     * @param topXrt
     * @param sortedExertions
     * @throws CycleDetectedException
     * @throws ContextException
     */
    private void reorderJob(Exertion topXrt, List<Mogram> sortedExertions) {
        List<Mogram> sortedSubset = new ArrayList(sortedExertions);
        sortedSubset.retainAll(topXrt.getMograms());

        if (topXrt.getFlowType()!=null && topXrt.getFlowType().equals(Strategy.Flow.AUTO)) {
            ((ServiceExertion) topXrt).setFlowType(setFlow(topXrt, sortedSubset));
            logger.info("FLOW for exertion: " + topXrt.getName() + " set to: " + topXrt.getFlowType());
        }
        List<String> exertionsBefore = new ArrayList<String>();
        for (Mogram xrt : topXrt.getMograms())
                exertionsBefore.add(xrt.getName());

        List<String> exertionsAfter = new ArrayList<String>();
        for (Mogram xrt : sortedExertions)
            exertionsAfter.add(xrt.getName());
        if (!topXrt.getMograms().equals(sortedSubset)) {
            logger.info("Order of exertions for " + topXrt.getName() + " will be changed: ");
            logger.info("From: " + exertionsBefore);
            logger.info("To: " + exertionsAfter);
            topXrt.getMograms().removeAll(sortedSubset);
            topXrt.getMograms().addAll(sortedSubset);
        }


        for (Iterator i = topXrt.getMograms().iterator(); i.hasNext(); ) {
            Exertion xrt = (Exertion) i.next();
            if (xrt instanceof Job) {
                reorderJob(xrt, sortedExertions);
            }
        }
    }

    /**
     * Add the job and all inner exertions as vertexes
     *
     * @param topXrt
     * @throws SortingException
     */
    private void addVertex(Exertion topXrt) throws ContextException, SortingException {

        String id = topXrt.getId().toString();
        dag.addVertex(id);
        entryMap.put(id, topXrt);
        contextIdsMap.put(id, topXrt.getDataContext().getId().toString());
        revContextIdsMap.put(topXrt.getDataContext().getId().toString(), id);

        for (Iterator i = topXrt.getMograms().iterator(); i.hasNext(); ) {
            Exertion project = (Exertion) i.next();

            id = project.getId().toString();

            if (dag.getVertex(id) != null) {
                throw new SortingException("Exertion '" + project.getName() +
                        "'(" + id + ") is duplicated in the job: '" + topXrt.getName() + "' (" + topXrt.getId() + ")");
            }

            dag.addVertex(id);
            entryMap.put(id, project);
            contextIdsMap.put(id, project.getDataContext().getId().toString());
            revContextIdsMap.put(project.getDataContext().getId().toString(), id);

            if (project instanceof Job) {
                addVertex(project);
            }
        }
    }

    /**
     * Find the dependencies that result from the pipes specified between tasks
     *
     * @param topXrt
     * @throws CycleDetectedException
     * @throws SortingException
     */
    private void getMapping(SrvModel srvModel) throws CycleDetectedException, ContextException, SortingException {
        for (Iterator i = srvModel.getAllMograms().iterator(); i.hasNext(); ) {
            Exertion project = (Exertion) i.next();
            String id = project.getId().toString();
            String topId = srvModel.getId().toString();
            dag.addEdge(id, topId);

            Map<String, Map<String, String>> metaCtx = project.getDataContext().getMetacontext();
            Map<String, String> ctxMapping = metaCtx.get("cid");
            if (ctxMapping != null) {
                for (Map.Entry<String, String> mapping : ctxMapping.entrySet()) {
                    if (mapping.getValue() != null && mapping.getValue().length() > 0) {
                        String dependencyId = revContextIdsMap.get(mapping.getValue());
                        logger.debug("Map: " + mapping.getKey() + " to " + dependencyId);
                        if (dag.getVertex(dependencyId) != null) {
                            dag.addEdge(id, dependencyId);
                        }
                    }
                }
            }
            /*if (project instanceof Job) {
                getMapping(project);
            }*/
        }
    }

    /**
     * Check if there is an edge between the exertion and its parent
     *
     * @param topXrt
     * @throws CycleDetectedException
     * @throws SortingException
     */
    private void checkParentCycle(SrvModel topXrt) throws CycleDetectedException, ContextException, SortingException {
        if (topXrt.getDataContext().getParentId() != null) {
            String parentId = topXrt.getDataContext().getParentId().toString();
            if (dag.getVertex(parentId) != null) {
                // Parent is added as an edge, but must not cause a cycle - so we remove any other edges it has in conflict
                if (dag.hasEdge(parentId, topXrt.getId().toString())) {
                    dag.removeEdge(parentId, topXrt.getId().toString());
                }
                dag.addEdge(topXrt.getId().toString(), parentId);
            }
        }
    }
}

