package exercise3;

//Given a linked list, return the node where the cycle begins. If there is no cycle, return null.
//Note: Do not modify the linked list. Without using extra space.

public class lined_list {
	Node head;	//ͷ�ڵ�
	
	class Node{
		int data;
		Node next;
		Node(int d)	//���캯��
		{
			data=d;
			next=null;
		}
	}
	
	public void addNode(int addData)	//�½� Node �ڵ�
	{
		Node new_node=new Node(addData);
		new_node.next=head;
		head=new_node;
	}
	
	public static void main(String[] args) {
		//����һ������������
		lined_list myList = new lined_list();
		myList.addNode(23);
		myList.addNode(24);
		myList.addNode(25);
		myList.addNode(26);
		myList.addNode(27);
		myList.addNode(28);	//һ�� 6 ��Ԫ�� headָ��28������Ϊ28��27��26��25��24��23��25��24��23��25...
		myList.head.next.next.next.next.next.next=myList.head.next.next.next;
		
		Node p=myList.head;	//ÿ��ǰ��һ��
		Node q=myList.head;	//ÿ��ǰ������
		boolean flag=false;	//�Ƿ��л�
		while(p!=null && q!=null && q.next!=null){
			p=p.next;
			q=q.next.next;
			if(p==q) {
				flag=true;
				break;
			}
		}
		if(flag) {	//�л�
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
