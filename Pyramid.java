import java.util.Scanner;

public class Pyramid {

	public static void main(String[] args) {

		Scanner user_input = new Scanner( System.in );
		
		System.out.print("Enter Pyramid Size: ");
		String input = user_input.next( );
		
		int max = Integer.parseInt(input) + 1;

		for (int i = 1; i < max; i++) {
// for design			
			int spc = max-i;
			while(spc>1){
				System.out.print("\t");
				spc--;
			}
// for decrement			
			if(i==1)
			System.out.print(i);
			
			int j=i;
			while(i>1 & j>=1){
				System.out.print(j+"\t");
				j--;
			}
// for increment			
			j++;
			
			if(i>1 & j==1){
				while(j<i){
					j++;
					System.out.print(j+"\t");
				}
			}
// for design			
			System.out.println();
		}

	}

}
