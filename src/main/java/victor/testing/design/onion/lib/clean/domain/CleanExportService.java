package victor.testing.design.onion.lib.clean.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import victor.testing.design.onion.lib.Contract;
import victor.testing.design.onion.lib.Contract.Status;

import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@RequiredArgsConstructor
public class CleanExportService {
  private final GenerateExcel generateExcel;
  @Value("${warning.amount.threshold}") // from application.properties
  public double warningAmountThreshold;


  public void exportContracts(List<Contract> contracts) {
    List<ContractForExport> forExport = contracts.stream().map(this::toExport).collect(Collectors.toList());
    generateExcel.generateXls(forExport);
  }

  private ContractForExport toExport(Contract contract) {
    boolean showWarning = contract.getStatus() == Status.ACTIVE
                          && contract.getLastPayment().isBefore(now().minusDays(60))
                          && contract.getRemainingValue() > warningAmountThreshold;
    return new ContractForExport(
            contract.getNumber(),
            showWarning,
            contract.getName()
    );
  }
}
