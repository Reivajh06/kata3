package reivajh06.kata3;

import reivajh06.kata3.dtos.SeismDto;

import java.util.List;

public interface SeismSource {

	List<SeismDto> requestSeisms();
}
