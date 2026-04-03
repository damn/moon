(ns moon.inventory-window)

(defprotocol InventoryWindow
  (set-item! [_ cell {:keys [texture-region tooltip-text]} skin])
  (remove-item! [_ cell]))

