package org.sample.qbintg.repository;

import org.sample.qbintg.intg.model.VendorDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorDataRepository extends JpaRepository<VendorDto, Long>,IDataRepository  {

	VendorDto findByVendorId(final String vendorId);
}