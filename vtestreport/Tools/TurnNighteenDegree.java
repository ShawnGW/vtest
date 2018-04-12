package Tools;

public class TurnNighteenDegree {
	public static String[][] Turn(String[][] Mapcell,Integer max_X,Integer max_Y)
	{
		String[][] Result=new String[max_Y][max_X];
		for (int j = 0; j < max_Y; j++) {
		for (int i = 0; i < max_X; i++) {
			Result[j][i]=Mapcell[max_X-1-i][j];
		}
		}
		return Result;
	}
}
