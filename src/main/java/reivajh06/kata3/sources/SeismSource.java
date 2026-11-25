package reivajh06.kata3.sources;

import reivajh06.kata3.dtos.SeismDto;

import java.util.List;

public interface SeismSource {

	List<SeismDto> requestSeisms();
}
