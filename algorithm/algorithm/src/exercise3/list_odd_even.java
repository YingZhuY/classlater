package exercise3;

//Given a single linked list, group all odd notes together followed by the even nodes.
//Note: Run in O(1) space complexity and O(nodes) time complexity.

public class list_odd_even {  
 
	static class Node {
		int data;
		Node next;
	}
 
	static Node newNode(int key) { //添加新节点
		Node temp = new Node();
		temp.data = key;
		temp.next = null;
		return temp;
	}
	
	public static void main(String[] args) {
		
		//构造目标链表
		Node head = newNode(4);
		head.next = newNode(5);
		head.next.next = newNode(6);  
		head.next.next.next = newNode(7);
		head.next.next.next.next = newNode(8);
		//head.next.next.next.next.next = newNode(9);
		//一共 5 个元素 head指向4，链表为4，5，6，7，8
		
		if (head == null)	System.out.print("null");
		
		Node odd = head;  //遍历链表的指针
		Node even = head.next;	//记录偶数号节点
		Node evenFirst = even;  //记录偶数号节点头

		while (odd != null && even != null && (even.next) != null) {
			odd.next = even.next;	//链接下一个 odd 节点
			odd = even.next;
		    if (odd.next == null) {	//当没有下一个 even 节点  
		        even.next = null;
		        break;
		    }
		    even.next = odd.next;	//链接下一个 even 节点
		    even = odd.next;  
		 }
		// 当遍历完所有节点后，将 evenFirst 加到 odd 链后
		odd.next = evenFirst;
		
		for(odd=head;odd!=null;odd=odd.next)
			System.out.println(odd.data+" ");
	}
	
}

