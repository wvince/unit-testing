package victor.testing.design.roles.service;

import lombok.RequiredArgsConstructor;
import victor.testing.design.roles.model.Parcel;
import victor.testing.design.roles.model.TrackingProvider;
import victor.testing.design.roles.repo.TrackingProviderRepo;

import java.util.List;

@RequiredArgsConstructor
public class TrackingService {
   private final TrackingProviderRepo trackingProviderRepo;

   public void markDepartingWarehouse(int warehouseId, Parcel parcel) {
      List<TrackingProvider> trackingProviders = trackingProviderRepo.findByAwb(parcel.getAwb());

      for (TrackingProvider trackingProvider : trackingProviders) {
         System.out.println("Report " + parcel.getAwb() + " departing warehouse " + warehouseId
                            + " to " + trackingProvider.getId());
      }
   }
}
