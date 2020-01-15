package exercise3;

//Given a single linked list, group all odd notes together followed by the even nodes.
//Note: Run in O(1) space complexity and O(nodes) time complexity.

public class list_odd_even {  
 
	static class Node {
		int data;
		Node next;
	}
 
	static Node newNode(int key) { //����½ڵ�
		Node temp = new Node();
		temp.data = key;
		temp.next = null;
		return temp;
	}
	
	public static void main(String[] args) {
		
		//����Ŀ������
		Node head = newNode(4);
		head.next = newNode(5);
		head.next.next = newNode(6);  
		head.next.next.next = newNode(7);
		head.next.next.next.next = newNode(8);
		//head.next.next.next.next.next = newNode(9);
		//һ�� 5 ��Ԫ�� headָ��4������Ϊ4��5��6��7��8
		
		if (head == null)	System.out.print("null");
		
		Node odd = head;  //���������ָ��
		Node even = head.next;	//��¼ż���Žڵ�
		Node evenFirst = even;  //��¼ż���Žڵ�ͷ

		while (odd != null && even != null && (even.next) != null) {
			odd.next = even.next;	//������һ�� odd �ڵ�
			odd = even.next;
		    if (odd.next == null) {	//��û����һ�� even �ڵ�  
		        even.next = null;
		        break;
		    }
		    even.next = odd.next;	//������һ�� even �ڵ�
		    even = odd.next;  
		 }
		// �����������нڵ�󣬽� evenFirst �ӵ� odd ����
		odd.next = evenFirst;
		
		for(odd=head;odd!=null;odd=odd.next)
			System.out.println(odd.data+" ");
	}
	
}

