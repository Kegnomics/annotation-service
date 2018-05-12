package com.dandrona.vcfwrapper;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Data
public class DataLine {
	private String chromosome;
	private long position;
	private String ID;
	private List<String> referenceBaseList;
	private List<String> alternateBaseList;
	private double quality;
	private String filter;
	private Map<String, String> info;
	private boolean isExon = false;
	private boolean isSilent = false;
	private String format;
	private String gene;
	private String AF;
	private String AFR_AF;
	private String AMR_AF;
	private String EAS_AF;
	private String EUR_AF;
	private String SAS_AF;
	private String CLNSIG;
	private String CLNDISDB;

	public DataLine() {
		this.referenceBaseList = new ArrayList<>();
		this.alternateBaseList = new ArrayList<>();
		this.info = new HashMap<>();
	}

	public void setField(String fieldName, String fieldValue) {
		switch(fieldName) {
			case "CHROM":
				setChromosome(fieldValue);
				break;
			case "POS":
				if (fieldValue.equals(".")){
					setPosition(-1);
				}
				else setPosition(Long.valueOf(fieldValue));
				break;
			case "ID":
				setID(fieldValue);
				break;
			case "REF":
				setReferenceBaseList(fieldValue);
				break;
			case "ALT":
				setAlternateBaseList(fieldValue);
				break;
			case "QUAL":
				setQuality(fieldValue);
				break;
			case "FILTER":
				setFilter(fieldValue);
				break;
			case "INFO":
				setInfo(fieldValue);
				break;
			case "FORMAT":
				setFormat(fieldValue);
				break;
			default:
				setExtraInfo(fieldValue);
				break;

		}
	}

	private void setExtraInfo(String fieldValue) {
		String[] ids = this.format.split(":");
		String[] values = fieldValue.split(":");
		for (int i=0;i<ids.length;i++) {
			if (!values[i].equals("."))
				this.info.put(ids[i], values[i]);
		}
	}

	private void setInfo(String fieldValue) {
		String[] infoArray = fieldValue.split(";");
		for (int i=0;i<infoArray.length;i++) {
			if (infoArray[i].contains("=")) {
				KeyValueObject keyValueObject = new KeyValueObject(infoArray[i], '=');
				if (!keyValueObject.getValue().equals("."))
					this.info.put(keyValueObject.getKey(), keyValueObject.getValue());
			}
			else {
				if (infoArray[i].equals("EXON"))
					this.isExon = true;
				else if(infoArray[i].equals("Silent"))
					this.isSilent = true;
			}
		}
	}

	private void setQuality(String fieldValue) {
		if (fieldValue.equals("."))
			this.quality = -1; //unknown
		else
			this.quality = Double.valueOf(fieldValue);
	}

	private void setReferenceBaseList(String fieldValue) {
		if (fieldValue.length() > 1) {
			if (fieldValue.contains("<") && fieldValue.contains(">")) {
				//TODO It's an ID, we should get it from somewhere
			}
			else if (fieldValue.contains(",")){
				String[] bases = fieldValue.split(",");
				this.referenceBaseList.addAll(Arrays.asList(bases));
			}
			else {
				this.referenceBaseList.add(fieldValue);
			}
		}
		else {
			this.referenceBaseList.add(fieldValue);
		}
	}

	private void setAlternateBaseList(String fieldValue) {
		if (fieldValue.length() > 1) {
			if (fieldValue.contains("<") && fieldValue.contains(">")) {
				//TODO It's an ID, we should get it from somewhere
			}
			else if (fieldValue.contains(",")){
				String[] bases = fieldValue.split(",");
				this.alternateBaseList.addAll(Arrays.asList(bases));
			}
			else {
				this.alternateBaseList.add(fieldValue);
			}
		}
		else {
			this.alternateBaseList.add(fieldValue);
		}
	}

	private void setChromosome(String chromosome) {
		if (chromosome.contains("<") && chromosome.contains(">")) {
//            this.chromosome = contigs.get(chromosome.split("<")[1].split(">")[0]);
		}
		else {
			this.chromosome = chromosome;
		}
	}

	private boolean isIDMissing() {
		return this.ID.equals(".");
	}

	public List<String[]> getOutput() {
		List<String[]> result = new ArrayList<>();
		if (getReferenceBaseList().size() != 1 || getAlternateBaseList().size() != 1) {
			for (String base : getReferenceBaseList()) {
				for (String alt : getAlternateBaseList()) {
					if (alt.length() > base.length())
						addInsertion(result, base, alt);
					else if (alt.length() < base.length())
						addDeletion(result, base, alt);
					else
						addSingleReplacement(result, base, alt);
				}
			}
		}
		else {
			String base = getReferenceBaseList().get(0);
			String alt = getAlternateBaseList().get(0);
			if (isInsertion(base, alt))
				addInsertion(result, base, alt);
			else if (isDeletion(base, alt))
				addDeletion(result, base, alt);
			else
				addSingleReplacement(result, base, alt);
		}
		return result;
	}

	private void addSingleReplacement(List<String[]> result, String base, String alt) {
		String[] line = new String[]{getChromosome(),
				String.valueOf(getPosition()),
				String.valueOf(getPosition()),
				base,
				alt,
				getGene(),
				AF,
				AFR_AF,
				AMR_AF,
				EAS_AF,
				EUR_AF,
				SAS_AF,
				CLNSIG,
				CLNDISDB};
		result.add(line);
	}

	private void addDeletion(List<String[]> result, String base, String alt) {
		String diff = StringUtils.difference(alt, base);
		int indexOfDifference = StringUtils.indexOfDifference(base, alt);

		String[] line = new String[] {
				getChromosome(),
				String.valueOf(getPosition() + indexOfDifference),
				String.valueOf(getPosition() + indexOfDifference + diff.length() - 1),
				diff,
				"-",
				getGene(),
				AF,
				AFR_AF,
				AMR_AF,
				EAS_AF,
				EUR_AF,
				SAS_AF,
				CLNSIG,
				CLNDISDB
		};
		result.add(line);
	}

	private void addInsertion(List<String[]> result, String base, String alt) {
		String diff = StringUtils.difference(base, alt);
		int indexOfDifference = StringUtils.indexOfDifference(base, alt);
		String[] line = new String[]{
				getChromosome(),
				String.valueOf(getPosition() + indexOfDifference - 1),
				String.valueOf(getPosition() + indexOfDifference - 1),
				"-",
				diff,
				getGene(),
				AF,
				AFR_AF,
				AMR_AF,
				EAS_AF,
				EUR_AF,
				SAS_AF,
				CLNSIG,
				CLNDISDB};
		result.add(line);
	}

	private boolean isInsertion(String base, String alt) {
		return base.length() < alt.length();
	}

	private boolean isDeletion(String base, String alt) {
		return base.length() > alt.length();
	}

	public String getChromosomeNumber() {
		return getChromosome().replaceAll("chr", "");
	}

	public String getGeneInfo() {
		StringBuilder result = new StringBuilder();
		for (String key : info.keySet()) {
			if (key.equals("GENEINFO")) {
				String geneInfo = info.get(key);
				if (geneInfo.contains("|")) {
					String[] split = geneInfo.split("\\|");
					for (int i = 0;i<split.length;i++) {
						result.append(split[i].split(":")[0]);
						if (i+1 != split.length)
							result.append(";");
					}
				}
				else {
					result.append(geneInfo.split(":")[0]);
				}
			}
		}
		return result.toString();
	}

	public String getRSID() {
		for (String key : info.keySet()) {
			if (key.equals("RS")) {
				String RSID = info.get(key);
				return "rs" + RSID;
			}
		}

		return "";
	}

	public void setCLNSIG(String CLNSIG) {
		this.CLNSIG = CLNSIG;
	}

	public void setCLNDISDB(String CLNDISDB) {
		this.CLNDISDB = CLNDISDB;
	}
}