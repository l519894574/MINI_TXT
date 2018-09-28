package Services;

import Client.BaseServices;
import Client.ServerFactory;
import Client.Service;
import Common_constants.Constants;

import java.io.Serializable;
import java.util.Scanner;

//客服端START列表
public class StartService extends BaseServices<Serializable> {
	private String OUTPUT_SIGN_LINE = "-------------------------------------\n";
	private String OUTPUT_TEXT_SELECT = "请选择： ";
	private String OUTPUT_TEXT_TITLE = "欢 迎 使 用 吃 人 在 线 TXT 阅 读 器 \n";
	private String OUTPUT_TEXT_LOGIN = "1.登录\n";
	private String OUTPUT_TEXT_REGISTER = "2.注册\n";
	private String OUTPUT_TEXT_LOGOUT = "3.退出\n";
	private String OUTPUT_TEXT_THANK = "谢 谢 使 用！";
	private StringBuilder MENU_START = new StringBuilder(OUTPUT_TEXT_TITLE)
			.append(OUTPUT_SIGN_LINE).append(OUTPUT_TEXT_LOGIN)
			.append(OUTPUT_TEXT_REGISTER).append(OUTPUT_TEXT_LOGOUT)
			.append(OUTPUT_SIGN_LINE).append(OUTPUT_TEXT_SELECT);

	@Override
	public Service<? extends Serializable> execute() throws Exception{
		System.out.print(MENU_START);
		Scanner scanner = new Scanner(System.in);
		String choice = null;
		while (true) {
			choice = scanner.nextLine().trim();
			switch (choice) {
				case "1":
					return ServerFactory.getServices(Constants.LOGIN);
				case "2":
					return ServerFactory.getServices(Constants.REGISTER);
				case "3":
					System.out.println(OUTPUT_TEXT_THANK);
					System.exit(0);
				default:
					System.out.print(INVALIDINPUT);
					break;
			}
		}
	}

}
