(ns reaction-txs.toggle-inventory-visible
  (:require [gdx.stage :as stage]
            [gdx.scenes.scene2d.actor :as actor]))

(defn f
  [{:keys [ctx/stage] :as ctx}]
  (-> stage
      (stage/find-actor "moon.ui.windows.inventory")
      actor/toggle-visible!)
  ctx)
