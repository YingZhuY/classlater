package exercise5;

import java.util.LinkedList;
import java.util.Queue;

public class bitree_depth {
	static class TreeNode{
		int data;
		TreeNode left;
		TreeNode right;
		TreeNode(int x){data=x;left=null;right=null;}
	}
	
	static TreeNode initTree(String[] tree_initial, int size)	//通过队列按照层次遍历，初始化一个二叉树
	{
		if (size<1)		return null;
		TreeNode[] nodes = new TreeNode[size];
		for (int i = 0; i < size; i++) {
			if (tree_initial[i] == null) {
				nodes[i] = null;	//初始化数组元素为 null 表示该位置为空
			} else {				//否则将 int 数据转换为TreeNode节点
				nodes[i] = new TreeNode(Integer.parseInt(tree_initial[i]));
			}
		}
		Queue<TreeNode> nodeQueue = new LinkedList<TreeNode>();		//初始化辅助队列
		nodeQueue.add(nodes[0]);		//队列操作详细见网站，Java的队列接口 https://www.geeksforgeeks.org/queue-interface-java/
	 
		TreeNode node;
		int index = 1;
		while (!nodeQueue.isEmpty())
		{
			//node = nodeQueue.element();		//element 队列的第一个元素，不进行弹出删除，带异常处理
			//nodeQueue.remove();				//删除，带异常处理
			node = nodeQueue.poll();			//队列中弹出一个元素作为当前根
			if(index<size) { 					//&& nodes[index]!=null
				node.left = nodes[index];
				nodeQueue.offer(nodes[index++]);
				//nodeQueue.offer(nodes[index++]);	//添加，带返回值；区别于add
				//node.left = nodeQueue.peek();		//查看队列头而不删除它；若队列为空，则返回 null
			}
			if(index<size) {
				node.right = nodes[index];
				nodeQueue.offer(nodes[index++]);
			}
		}
		//System.out.print(nodes[3].data);
		return nodes[0];
	}
	
	static int btdepth(TreeNode root) {				//主要的目标功能函数
        if (root == null) {
            return 0;
        }
        int ldep = btdepth(root.left);
        int rdep = btdepth(root.right);
        if (ldep > rdep) {
            return ldep + 1;
        } else {
            return rdep + 1;
        }
    }
	
	public static void main(String[] args) {
		String[] tree_initial = new String[] {"3","9","20",null,null,"15","7"};
		TreeNode root = initTree(tree_initial, tree_initial.length);
		int depth = btdepth(root);
		System.out.println(depth);
	}
}
