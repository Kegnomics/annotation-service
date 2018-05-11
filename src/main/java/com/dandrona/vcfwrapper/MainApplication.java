package com.dandrona.vcfwrapper;

import com.google.gson.Gson;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;


public class MainApplication {
	private final static Logger LOGGER = Logger.getLogger(MainApplication.class.getName());
	private static MainApplication instance = null;

	private MainApplication() {
	}

	public static MainApplication getInstance() {
		if (instance == null)
			instance = new MainApplication();
		return instance;
	}

	public String handleFileUpload(MultipartFile file) {
		File result = saveFile(file);
		if (result != null) {
			try {
				Process p = Runtime.getRuntime().exec("perl /home/sushii/Downloads/annovar/table_annovar.pl " + file.getOriginalFilename() + " /home/sushii/Downloads/annovar/humandb/ -buildver hg19 -out output -remove -protocol refGene,cytoBand,exac03,avsnp147,dbnsfp30a -operation g,r,f,f,f -nastring . -vcfinput");
				printProcessOutput(p);

				Map<String, List<DataLine>> finalMap = new HashMap<>();
				List<DataLine> data =  parseFile("output.hg19_multianno.vcf");
				finalMap.put("data", data);
				return new Gson().toJson(finalMap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	public File saveFile(MultipartFile file)
	{
		try {
			File convFile = new File(file.getOriginalFilename());
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
			return convFile;
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private void printProcessOutput(Process proc) throws IOException {

		BufferedReader stdInput = new BufferedReader(new
				InputStreamReader(proc.getInputStream()));

		BufferedReader stdError = new BufferedReader(new
				InputStreamReader(proc.getErrorStream()));

		System.out.println("Here is the standard output of the command:\n");
		String s = null;
		while ((s = stdInput.readLine()) != null) {
			System.out.println(s);
		}
		System.out.println("Here is the standard error of the command (if any):\n");
		while ((s = stdError.readLine()) != null) {
			System.out.println(s);
		}
	}

	private List<DataLine> parseFile(String filePath) throws IOException {
		Map<String, Info> infoMap = new HashMap<>();
		List<DataLine> dataLines = new ArrayList<>();
		String line;
		List<String> headers = new ArrayList<>();

		BufferedReader br = new BufferedReader(new FileReader(filePath));
		while ((line = br.readLine()) != null) {
			if (line.startsWith("##INFO") || line.startsWith("##FORMAT")) {
				String[] info  = line.split("<(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1)[1].split(">(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1)[0].split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				Info infoObj = new Info();
				for (String anInfo : info) {
					KeyValueObject obj = new KeyValueObject(anInfo, '=');
					infoObj.setField(obj.getKey(), obj.getValue());
					infoMap.put(infoObj.getID(), infoObj);
				}
			}
			else if (line.startsWith("##fileformat")) {
				//TODO
			}
			else if (line.startsWith("##FILTER")) {
				//TODO
			}
			else if (line.startsWith("##annotator")) {
				//TODO
			}
			else if (line.startsWith("##UnifiedGenotyper")) {
				//TODO
			}
			else if (line.startsWith("##contig")) {
				//TODO
			}
			else if (line.startsWith("##reference")) {
				//TODO
			}
			else if (line.startsWith("##source")) {
				//TODO
			}
			else if (line.startsWith("#CHROM")) {
				headers = Arrays.asList(line.split("#")[1].split("\t"));
			}
			else if (line.startsWith("##fileDate")) {

			}
			else if (line.startsWith("##dbSNP_BUILD_ID")){

			}
			else if (line.startsWith("##phasing")) {

			}
			else if (line.startsWith("##variationPropertyDocumentationUrl")) {

			}
			else if (line.startsWith("##")) {

			}
			else {
				DataLine dataLine = getDataLine(line, headers);
				dataLines.add(dataLine);
				LOGGER.log(Level.INFO, "Adding line " + dataLine.toString());
			}
		}

		return dataLines;
	}

	private DataLine getDataLine(String line, List<String> headers) {
		String[] split = line.split("\t");
		int length = split.length;
		DataLine dataLine = new DataLine();
		for (int i=0;i<headers.size();i++) {
			dataLine.setField(headers.get(i), split[i]);
		}
		return dataLine;
	}
}
