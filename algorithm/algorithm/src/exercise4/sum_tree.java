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
	
	TreeNode initTree(int elements[], int size)	//ͨ�����а��ղ�α�������ʼ��һ��������
	{
		if (size<1)		return null;	//��̬����size��С��ָ������
		TreeNode[] nodes = new TreeNode[size];
		for (int i = 0; i < size; i++) {
			if (elements[i] == 0) {
				nodes[i] = null;	//��ʼ������Ԫ��Ϊ0��ʾ��λ��Ϊ��
			} else {				//���� int ����ת��ΪTreeNode�ڵ�
				nodes[i] = new TreeNode(elements[i]);
			}
		}
		Queue<TreeNode> nodeQueue = new LinkedList<TreeNode>();		//��ʼ����������
		nodeQueue.add(nodes[0]);		//���в�����ϸ����վ��Java�Ķ��нӿ� https://www.geeksforgeeks.org/queue-interface-java/
	 
		TreeNode node;
		int index = 1;
		while (!nodeQueue.isEmpty())
		{
			//node = nodeQueue.element();		//element ���еĵ�һ��Ԫ�أ������е���ɾ�������쳣����
			//nodeQueue.remove();				//ɾ�������쳣����
			node = nodeQueue.poll();			//�����е���һ��Ԫ����Ϊ��ǰ��
			if(index<size) { 					//&& nodes[index]!=null
				node.left = nodes[index];
				nodeQueue.offer(nodes[index++]);
				//nodeQueue.offer(nodes[index++]);	//��ӣ�������ֵ��������add
				//node.left = nodeQueue.peek();		//�鿴����ͷ����ɾ������������Ϊ�գ��򷵻� null
			}
			if(index<size) {
				node.right = nodes[index];
				nodeQueue.offer(nodes[index++]);
			}
		}
		//System.out.print(nodes[3].data);
		return nodes[0];
	}
	
	TreeNode do_merge(TreeNode tree1, TreeNode tree2) {		//��Ҫ��Ŀ�깦�ܺ���
		if(tree1 == null)	return tree2;
		if(tree2 == null)	return tree1;
		tree1.data = tree1.data + tree2.data;
		tree1.left = do_merge(tree1.left,tree2.left);		//�ݹ鴦��������
		tree1.right = do_merge(tree1.right,tree2.right);	//�ݹ鴦��������
		return tree1;
	}

	static void print_tree(TreeNode root, int level)	//�ݹ��ӡ������
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
		System.out.println("Before_tree_one��");
		print_tree(root_one,level_one);
		System.out.println();
		System.out.println("Before_tree_two��");
		print_tree(root_two,level_two);
		System.out.println();
		TreeNode root_result = tree3.do_merge(root_one,root_two);
		//System.out.println(root_result.data);
		System.out.println("After_tree��");
		print_tree(root_result,level_result);
	}

}
