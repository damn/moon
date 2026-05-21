(ns game.reaction-txs.toggle-inventory-visible
  (:require [clojure.scene2d.stage :as stage]
            [clojure.scene2d.actor :as actor]))

(defn do!
  [{:keys [ctx/stage] :as ctx}]
  (-> stage
      (stage/find-actor "moon.ui.windows.inventory")
      actor/toggle-visible!)
  ctx)
