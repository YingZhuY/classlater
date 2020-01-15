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
	
	TreeNode do_find(TreeNode root, int key) {		//��Ҫ��Ŀ�깦�ܺ���
		if(root==null)	return null;
		if(root.data > key)		return do_find(root.left,key);
		else if(root.data < key)	return do_find(root.right,key);
		return root;
	}
	
	int btdepth(TreeNode root) {				//�ݹ���������߶�
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
		int tree_initial[] = {4,2,7,1,3};
		find_node tree1=new find_node();
		find_node tree2=new find_node();
		TreeNode root = tree1.initTree(tree_initial, tree_initial.length);
		int level_one = tree1.btdepth(root);
		System.out.println("Before_tree_initial��");
		print_tree(root,level_one);
		System.out.println();
		TreeNode root_result = tree1.do_find(root,2);
		int level_result = tree2.btdepth(root_result);
		//System.out.println(root_result.data);
		System.out.println("After_tree��");
		print_tree(root_result,level_result);
	}

}
