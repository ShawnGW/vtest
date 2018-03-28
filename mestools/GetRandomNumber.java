package mestools;

public class GetRandomNumber {
	public static String getRandomNumber(int length) {
		StringBuffer SB=new StringBuffer();
		for (int i = 0; i < length; i++) {
			int RandomNumber=(int) ((Math.random()*6)/1);
			SB.append(RandomNumber);
		}
		return SB.toString();
	}
}
