package exercise4;

import java.util.LinkedList;
import java.util.Queue;

public class find_node {
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
	
	TreeNode do_find(TreeNode root, int key) {		//主要的目标功能函数
		if(root==null)	return null;
		if(root.data > key)		return do_find(root.left,key);
		else if(root.data < key)	return do_find(root.right,key);
		return root;
	}
	
	int btdepth(TreeNode root) {				//递归求二叉树高度
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
		int tree_initial[] = {4,2,7,1,3};
		find_node tree1=new find_node();
		find_node tree2=new find_node();
		TreeNode root = tree1.initTree(tree_initial, tree_initial.length);
		int level_one = tree1.btdepth(root);
		System.out.println("Before_tree_initial：");
		print_tree(root,level_one);
		System.out.println();
		TreeNode root_result = tree1.do_find(root,2);
		int level_result = tree2.btdepth(root_result);
		//System.out.println(root_result.data);
		System.out.println("After_tree：");
		print_tree(root_result,level_result);
	}

}
