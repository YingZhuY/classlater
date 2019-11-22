package exercise3;

//Given a linked list, return the node where the cycle begins. If there is no cycle, return null.
//Note: Do not modify the linked list. Without using extra space.

public class lined_list {
	Node head;	//头节点
	
	class Node{
		int data;
		Node next;
		Node(int d)	//构造函数
		{
			data=d;
			next=null;
		}
	}
	
	public void addNode(int addData)	//新建 Node 节点
	{
		Node new_node=new Node(addData);
		new_node.next=head;
		head=new_node;
	}
	
	public static void main(String[] args) {
		//构造一个带环的链表
		lined_list myList = new lined_list();
		myList.addNode(23);
		myList.addNode(24);
		myList.addNode(25);
		myList.addNode(26);
		myList.addNode(27);
		myList.addNode(28);	//一共 6 个元素 head指向28，链表为28，27，26，25，24，23，25，24，23，25...
		myList.head.next.next.next.next.next.next=myList.head.next.next.next;
		
		Node p=myList.head;	//每次前进一步
		Node q=myList.head;	//每次前进两步
		boolean flag=false;	//是否有环
		while(p!=null && q!=null && q.next!=null){
			p=p.next;
			q=q.next.next;
			if(p==q) {
				flag=true;
				break;
			}
		}
		if(flag) {	//有环
			p=myList.head;
			while(p!=q)
			{
				p=p.next;
				q=q.next;
			}
			System.out.println(p.data);
		}
		else	System.out.println("null");
	}

}
