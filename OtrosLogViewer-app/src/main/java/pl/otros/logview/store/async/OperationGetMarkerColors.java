package pl.otros.logview.store.async;

import pl.otros.logview.api.model.LogDataStore;
import pl.otros.logview.api.model.MarkerColors;

import java.util.concurrent.Callable;

class OperationGetMarkerColors implements Callable<MarkerColors> {
  private final LogDataStore logDataStore;
  private final int row;

  public OperationGetMarkerColors(LogDataStore logDataStore, int row) {
    this.logDataStore = logDataStore;
    this.row = row;
  }

  @Override
  public MarkerColors call() {
    return logDataStore.getMarkerColors(row);
  }
}
