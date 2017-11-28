package edu.repetita.io.interpreters;

import edu.repetita.core.Demands;
import edu.repetita.core.Setting;
import edu.repetita.core.Topology;
import edu.repetita.io.ExternalSolverInterpreter;
import edu.repetita.paths.SRPaths;

public class SetSRPathsInterpreter extends ExternalSolverInterpreter {

	@Override
	public String name() {
		return "setSRPaths";
	}

	@Override
	public String getDescription(){
		return "Sets SR paths (i.e., list of intermediate nodes labels separated by a '-')" +
				"from the output of the run command " +
				"(the key field is the identifier of a demand, the value field is " +
				"the SR path to be set for the given demand)";
	}

	@Override
	public void elaborateRunCommandOutput(String outputString, Setting setting){
		Demands demands = setting.getDemands();
		Topology topology = setting.getTopology();

		// FIXME SRMaxLength should be set from the max size seen in the output file
		SRPaths paths = new SRPaths(demands, setting.getSRMaxLength());

		int demandField = this.getKeyField();
		int pathField = this.getValueField();

		String[] lines = outputString.split("\n");
		for (String l: lines){
			String[] data = l.split(this.getFieldSeparator());
			if (data.length > demandField && data.length > pathField) {
				String[] nodesList = data[pathField].split("-");
				int[] nodes = new int[nodesList.length];
				for (int i = 0; i < nodesList.length; i++){
					nodes[i] = topology.getNodeId(nodesList[i]);
				}
				int demandIndex = demands.getDemandIndex(data[demandField]);
				paths.setPath(demandIndex, nodes);
			}
		}
		setting.setSRPaths(paths);
	}
}
