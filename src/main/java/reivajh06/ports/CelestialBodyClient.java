package reivajh06.ports;

import reivajh06.dtos.CelestialBodyDto;

public interface CelestialBodyClient extends AutoCloseable {

	CelestialBodyDto requestCelestialBody(String name);
}
