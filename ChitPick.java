import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class ChitPick {

	public static void main(String[] args) {

		Random random = new Random();

		Scanner userInput = new Scanner(System.in);
		System.out.print("Enter the Number of Questions: ");
		
		String input = userInput.nextLine();
		int nOfQuestions = Integer.parseInt(input);
		
		ArrayList al = new ArrayList();

		// create an arraylist with the user entered size and fill it with numbers		
		if(nOfQuestions != 0)
			for(int i = 1; i <= nOfQuestions; i++)
				al.add(i);
		
//		System.out.println(al);
		
		System.out.println("** Press ENTER Key to get TOKENS **");
		
		while(al.size() !=0){
			
			int i = random.nextInt(al.size());
			// gap to look good
			System.out.print("");
			userInput.nextLine();
			
			System.out.println("Token Number => "+al.get(i));
			al.remove(i);
		}
	
		System.out.println("\n===Game Over===");
	}
	

}
