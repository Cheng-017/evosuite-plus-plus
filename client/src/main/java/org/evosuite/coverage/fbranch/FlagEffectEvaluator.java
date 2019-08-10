package org.evosuite.coverage.fbranch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.ga.metaheuristics.RuntimeRecord;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.ControlDependency;
import org.evosuite.graphs.cfg.RawControlFlowGraph;
import org.evosuite.setup.Call;
import org.evosuite.setup.CallContext;
import org.evosuite.testcase.execution.ExecutionResult;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

public class FlagEffectEvaluator {
	public static double calculateInterproceduralFitness(BytecodeInstruction flagCallInstruction, List<Call> callContext,
			BranchCoverageGoal branchGoal, ExecutionResult result/*, BranchCoverageGoal globalGoal*/) {
		RawControlFlowGraph calledGraph = flagCallInstruction.getCalledCFG();
		String signature = flagCallInstruction.getCalledMethodsClass() + "." + flagCallInstruction.getCalledMethod();
		if (calledGraph == null) {
			RuntimeRecord.methodCallAvailabilityMap.put(signature, false);
			return 1;
		}
		RuntimeRecord.methodCallAvailabilityMap.put(signature, true);

		Set<BytecodeInstruction> exits = calledGraph.determineExitPoints();

		List<Double> iteratedFitness = new ArrayList<>();
		List<List<Integer>> loopContext = identifyLoopContext(result, callContext);
		
//		removeConflictsLoopContext(branchGoal, loopContext, globalGoal);
		sortLength(loopContext);
		
		for(List<Integer> branchTrace: loopContext) {
			
			List<Double> returnFitnessList = new ArrayList<>();
			for (BytecodeInstruction returnIns : exits) {
				if(returnIns.isReturn()) {
					List<Double> fList = new ReturnFitnessEvaluator().
							calculateReturnInsFitness(returnIns, branchGoal, calledGraph, result, callContext, branchTrace);
					returnFitnessList.addAll(fList);					
				}
				
			}
			
			double fit = FitnessAggregator.aggreateFitenss(returnFitnessList);
//			return fit;
			iteratedFitness.add(fit);
		}
		
//		System.currentTimeMillis();
		
		if(iteratedFitness.isEmpty()) {
			return 10000000d;
		}
		
//		return Collections.min(iteratedFitness);
		return FitnessAggregator.aggreateFitenss(iteratedFitness);
	}
	
	/**
	 * for debugging reason
	 * @param loopContext
	 */
	public static void sortLength(List<List<Integer>> loopContext) {
		Collections.sort(loopContext, new Comparator<List<Integer>>() {

			@Override
			public int compare(List<Integer> o1, List<Integer> o2) {
				return o1.size() - o2.size();
			}
		});
	}

	/**
	 * I keep the method here as I cannot remember why I need to write such a check.
	 * By right, that should always be the case.
	 * 
	 * @param branchGoal
	 * @param loopContext
	 * @param globalGoal
	 */
	public static void removeConflictsLoopContext(BranchCoverageGoal branchGoal, List<List<Integer>> loopContext, BranchCoverageGoal globalGoal) {
		Set<ControlDependency> parentDependencies = globalGoal.getBranch().getInstruction().getControlDependencies();
		Iterator<List<Integer>> iter = loopContext.iterator();
		while(iter.hasNext()) {
			List<Integer> branchTrace = iter.next();
			if(branchTrace.isEmpty()) {
				continue;
			}
			
			int lastExercisedBranchID = branchTrace.get(branchTrace.size()-1); 
			
			/**
			 * at least one parent dependency must be the same with the last exercised branch 
			 */
			boolean isValidBranchID = false;
			for(ControlDependency cd: parentDependencies) {
				if(cd.getBranch().getActualBranchId()==lastExercisedBranchID) {
					isValidBranchID = true;
					break;
				}
			}
			
			if(!isValidBranchID) {
				iter.remove();
			}
		}
		
	}

	public static List<List<Integer>> identifyLoopContext(ExecutionResult result, List<Call> callContext) {
		List<List<Integer>> loopContext = new ArrayList<>();
		for(Map<CallContext, Map<List<Integer>, Double>> context: result.getTrace().getContextIterationTrueMap().values()) {
			for(CallContext key: context.keySet()) {
				boolean isCompatible = getCompatible(key, callContext);
				if(isCompatible) {
					Map<List<Integer>, Double> iterationTable = context.get(key);
					for(List<Integer> brancTrace: iterationTable.keySet()){
						if(!loopContext.contains(brancTrace)) {
							loopContext.add(brancTrace);
						}
					}
				}
				
			}
		}
		
		return loopContext;
	}
	
	public static boolean getCompatible(CallContext key, List<Call> callContext) {
		if(key.getContext().size() == callContext.size()+1) {
			for(int i=0; i<callContext.size(); i++) {
				if(!key.getContext().get(i).equals(callContext.get(i))) {
					return false;
				}
			}			
			return true;
		}
		return false;
	}
	
	public static FlagEffectResult checkFlagEffect(BranchCoverageGoal goal) {
		Branch branch = goal.getBranch();
		BytecodeInstruction ins = branch.getInstruction();
		
		int numberOfOperands = getOperands(ins);
		
		if(numberOfOperands == 1) {
			BytecodeInstruction defIns = ins.getSourceOfStackInstruction(0);
			if(defIns.isMethodCall()) {
				return checkFlagEffect(defIns);
			}
		}
		else if(numberOfOperands == 2){
			BytecodeInstruction defIns1 = ins.getSourceOfStackInstruction(1);
			BytecodeInstruction defIns2 = ins.getSourceOfStackInstruction(2);
			
			if(defIns1.isMethodCall()) {
				FlagEffectResult r = checkFlagEffect(defIns1);
				if(r.hasFlagEffect) {
					if(defIns2.isConstant() || defIns2.isLoadConstant()) {
						return r;						
					}
				}
			}
			
			if(defIns2.isMethodCall()) {
				FlagEffectResult r = checkFlagEffect(defIns2);
				if(r.hasFlagEffect) {
					if(defIns1.isConstant() || defIns1.isLoadConstant()) {
						return r;						
					}
				}
			}
		}
		
		return new FlagEffectResult(ins, false, null);
	}

	public static FlagEffectResult checkFlagEffect(BytecodeInstruction defIns) {
		RawControlFlowGraph calledGraph = defIns.getCalledCFG();
//		String signature = defIns.getCalledMethodsClass() + "." + defIns.getCalledMethod();
		if (calledGraph == null) {
			return new FlagEffectResult(defIns, false, null);
		}
		
		for(BytecodeInstruction exit: calledGraph.determineExitPoints()) {
			List<BytecodeInstruction> returnDefs = exit.getSourceOfStackInstructions(0);
			if(returnDefs.size()>1) {
				System.currentTimeMillis();
			}
			BytecodeInstruction returnDef = returnDefs.get(0);
			
			if(returnDef.isConstant() || returnDef.isLoadConstant()) {
				
				Call callInfo = new Call(defIns.getClassName(), defIns.getMethodName(), 
						defIns.getInstructionId());
				callInfo.setLineNumber(defIns.getLineNumber());
				FlagEffectResult result = new FlagEffectResult(defIns, true, callInfo);
				return result;
			}
		}
		
		
		return new FlagEffectResult(defIns, false, null);
	}

	private static int getOperands(BytecodeInstruction ins) {
		AbstractInsnNode node = ins.getASMNode();
		if(node instanceof JumpInsnNode) {
			JumpInsnNode jNode = (JumpInsnNode)node;
			if(jNode.getOpcode() == Opcodes.IFEQ ||
					jNode.getOpcode() == Opcodes.IFGE ||
					jNode.getOpcode() == Opcodes.IFGT ||
					jNode.getOpcode() == Opcodes.IFLE ||
					jNode.getOpcode() == Opcodes.IFNE ||
					jNode.getOpcode() == Opcodes.IFNONNULL ||
					jNode.getOpcode() == Opcodes.IFNULL) {
				return 1;
			}
			else {
				return 2;
			}
		}
		return 0;
	}
	
//	public static FlagEffectResult isFlagMethod(BranchCoverageGoal goal) {
//		BytecodeInstruction instruction = goal.getBranch().getInstruction();
//		
//		BytecodeInstruction interproceduralFlagCall = instruction.getSourceOfStackInstruction(0);
//		boolean isInterproceduralFlag = false;
//		Call callInfo = null;
//		if (interproceduralFlagCall != null && interproceduralFlagCall.getASMNode() instanceof MethodInsnNode) {
//			MethodInsnNode mNode = (MethodInsnNode) interproceduralFlagCall.getASMNode();
//			String desc = mNode.desc;
//			String returnType = getReturnType(desc);
//			isInterproceduralFlag = returnType.equals("Z");
//			callInfo = new Call(instruction.getClassName(), instruction.getMethodName(), 
//					interproceduralFlagCall.getInstructionId());
//			callInfo.setLineNumber(instruction.getLineNumber());
//		}
//
//		FlagEffectResult result = new FlagEffectResult(interproceduralFlagCall,
//				isInterproceduralFlag, callInfo);
//		return result;
//	}
	
	public static String getReturnType(String signature) {
		String r = signature.substring(signature.indexOf(")") + 1);
		return r;
	}
}