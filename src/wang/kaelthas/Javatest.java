package wang.kaelthas;

public class Javatest {
	public static void main(String[] args) {
		String result="ptuiCB('66','0','','0','二维码未失效。(2208317380)', '')";
		 result=result.replaceAll("'","");
		result=result.substring(7, result.length()-1);
		
		System.out.println(result);
		String resultArr[]=result.split(",");
		for (String string : resultArr) {
			System.out.println(string+"---");
		}
		
	}
}
