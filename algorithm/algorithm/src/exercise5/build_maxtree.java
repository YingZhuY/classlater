package exercise5;

public class build_maxtree {
	
	static class TreeNode{
		int data;
		TreeNode left;
		TreeNode right;
		TreeNode(int x){data=x;left=null;right=null;}
	}
	
	static int btdepth(TreeNode root) {				//�ݹ���������߶�
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
	
    public static TreeNode build_max(int[] nums) {	//��Ҫ��Ŀ�깦�ܺ���
        if(nums == null || nums.length == 0){
            return null;
        }
        TreeNode node = new TreeNode(nums[0]);
        TreeNode curMax = node;
        int curIndex = 0;
        for(int i = 1; i < nums.length ; i++){
            //������ԭ�����Ľڵ���������
            if(nums[i] > nums[curIndex]){
                TreeNode temp = new TreeNode(nums[i]);
                temp.left = curMax;
                curMax = temp;
                curIndex = i;
            } else {
                //С�������ԭ���ڵ�����������ҵ�����ʵ�λ��
                TreeNode temp = curMax;
                while(temp.right != null){
                    TreeNode right = temp.right;
                    if(right.data > nums[i]){
                        temp = temp.right;
                    } else {
                        TreeNode temp1 = new TreeNode(nums[i]);
                        temp1.left = right;
                        temp.right = temp1;
                        break;
                    }
                }
                if(temp.right == null){
                    temp.right = new TreeNode(nums[i]);
                }
            }
        }
        return curMax;
    }
	
	public static void main(String[] args) {
		int tree_initial[] = {3,2,1,6,0,5};
		TreeNode root = build_max(tree_initial);
		int depth = btdepth(root);
		print_tree(root,depth);
	}

}
