package exercise4;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class sum_tree {
	static class TreeNode{
		int data;
		TreeNode left;
		TreeNode right;
		TreeNode(int x){data=x;left=null;right=null;}
	}
	
	TreeNode initTree(int elements[], int size)	//通过队列按照层次遍历，初始化一个二叉树
	{
		if (size<1)		return null;	//动态申请size大小的指针数组
		TreeNode[] nodes = new TreeNode[size];
		for (int i = 0; i < size; i++) {
			if (elements[i] == 0) {
				nodes[i] = null;	//初始化数组元素为0表示该位置为空
			} else {				//否则将 int 数据转换为TreeNode节点
				nodes[i] = new TreeNode(elements[i]);
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
	
	TreeNode do_merge(TreeNode tree1, TreeNode tree2) {		//主要的目标功能函数
		if(tree1 == null)	return tree2;
		if(tree2 == null)	return tree1;
		tree1.data = tree1.data + tree2.data;
		tree1.left = do_merge(tree1.left,tree2.left);		//递归处理左子树
		tree1.right = do_merge(tree1.right,tree2.right);	//递归处理右子树
		return tree1;
	}

	static void print_tree(TreeNode root, int level)	//递归打印二叉树
	{
	  if (root.right!=null)
		  print_tree(root.right, level + 1);
	  System.out.println();
	  for (int i = 0; i < level * 6; ++i)
		  System.out.print(" ");
	  System.out.print(root.data);
	  if (root.left!=null)
		  print_tree(root.left, level + 1);
	}
	
	public static void main(String[] args) {
		int tree_one[] = {1,3,2,5};
		int tree_two[] = {2,1,3,0,4,0,7};
		sum_tree tree1=new sum_tree();
		sum_tree tree2=new sum_tree();
		sum_tree tree3=new sum_tree();
		TreeNode root_one = tree1.initTree(tree_one, tree_one.length);
		//System.out.println(root_one.left.left.data);
		TreeNode root_two = tree2.initTree(tree_two, tree_two.length);
		int level_one = 3;
		int level_two = 3;
		int level_result = Math.max(level_one, level_two);
		System.out.println("Before_tree_one：");
		print_tree(root_one,level_one);
		System.out.println();
		System.out.println("Before_tree_two：");
		print_tree(root_two,level_two);
		System.out.println();
		TreeNode root_result = tree3.do_merge(root_one,root_two);
		//System.out.println(root_result.data);
		System.out.println("After_tree：");
		print_tree(root_result,level_result);
	}

}
