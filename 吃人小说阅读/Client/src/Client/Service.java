package Client;

import java.io.Serializable;

public interface Service<T extends Serializable> {
	void setInputData(T inputData);

	Service<? extends Serializable> execute() throws Exception;
}
