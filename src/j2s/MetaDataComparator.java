package j2s;

import java.util.Comparator;

public class MetaDataComparator implements Comparator<MetaData> {

	@Override
	public int compare(MetaData arg0, MetaData arg1) {
		float one = arg0.getLinearScore();
		float two = arg1.getLinearScore();
				
		if (one < two) {
			return -1;
		}
		else {
			return 1; 
		}
	}

}
