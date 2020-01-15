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
	
	static TreeNode initTree(String[] tree_initial, int size)	//ͨ�����а��ղ�α�������ʼ��һ��������
	{
		if (size<1)		return null;
		TreeNode[] nodes = new TreeNode[size];
		for (int i = 0; i < size; i++) {
			if (tree_initial[i] == null) {
				nodes[i] = null;	//��ʼ������Ԫ��Ϊ null ��ʾ��λ��Ϊ��
			} else {				//���� int ����ת��ΪTreeNode�ڵ�
				nodes[i] = new TreeNode(Integer.parseInt(tree_initial[i]));
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
	
	static int btdepth(TreeNode root) {				//��Ҫ��Ŀ�깦�ܺ���
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
