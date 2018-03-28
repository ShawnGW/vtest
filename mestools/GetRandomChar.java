package mestools;
/**
 * @author shawn.sun
 * @category IT
 * @version 1.0
 * @since 2018.3.15
 */
public class GetRandomChar {
	public static String getRandomChar(int length) {
		StringBuffer SB=new StringBuffer();
		for (int i = 0; i < length; i++) {
			int RandomNumber=(int) ((Math.random()*25)/1)+65;
			SB.append((char)RandomNumber);
		}
		return SB.toString();
	}
}
