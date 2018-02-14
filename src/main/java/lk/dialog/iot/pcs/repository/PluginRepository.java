package lk.dialog.iot.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lk.dialog.iot.pcs.model.Plugin;

@Repository
public interface PluginRepository extends JpaRepository<Plugin, Integer> {

    Plugin findOneByEndPoint(String endPoint);

    Plugin findOneByDeviceDefinitionId(int deviceDefinitionId);
}
