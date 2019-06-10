package zhongchiedu.controller.WebRepair.utils;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import sun.misc.BASE64Decoder;

public class FileUtil {
	public static MultipartFile[] base64ToMultipart(String...base64) {
		 MultipartFile[] files=new MultipartFile[base64.length/2];
		for (int x = 0;x < base64.length; x += 2) {
			try {
				BASE64Decoder decoder = new BASE64Decoder();
				byte[] b = new byte[0];
				b = decoder.decodeBuffer(base64[x+1]);
				
				for(int i = 0; i < b.length; i++) {
					if (b[i] < 0) {
						b[i] += 256;
					}
				}
				files[x/2]=(new BASE64DecodedMultipartFile(b, base64[x]));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return files;
	}
}
