import java.util.Vector;
import java.util.Scanner;
import java.util.Locale;

public class Main 
{
    public static void main(String[] args) 
    {
        Locale.setDefault(new Locale("en", "US"));

		Scanner sc = new Scanner(System.in);

        int oSeqLen = sc.nextInt();
		Vector<Integer> oSeq = new Vector<Integer>();

		for (int i = 0; i < oSeqLen; i++) 
		{
			oSeq.add(sc.nextInt());
		}
    
        HMM t = new HMM(new double[][] {{0.4, 0.2, 0.2, 0.2}, {0.2, 0.4, 0.2, 0.2}, {0.2, 0.2, 0.4, 0.2}, {0.2, 0.2, 0.2, 0.4}}, 
                        new double[][] {{0.4, 0.2, 0.2, 0.2}, {0.2, 0.4, 0.2, 0.2}, {0.2, 0.2, 0.4, 0.2}, {0.2, 0.2, 0.2, 0.4}}, 
                        new double[] {0.241896, 0.266086, 0.249153, 0.242864}, 
                        oSeq);

        t.computeBaumWelch();

        int N = 4;
        int T = 4;

        System.out.print(N + " " + N + " ");
		for (int i = 0; i < N; i++) 
		{
			for (int j = 0; j < N; j++) 
			{
				System.out.print(t.AMat[i][j] + " ");
			}
		}

		System.out.println();

		System.out.print(N + " " + T + " ");
		for (int i = 0; i < N; i++) 
		{
			for (int j = 0; j < T; j++) 
			{
				System.out.print(t.BMat[i][j] + " ");
			}
		}

		System.out.println();
    } 
}

