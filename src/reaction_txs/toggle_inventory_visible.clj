(ns reaction-txs.toggle-inventory-visible
  (:require [gdx.stage :as stage]
            [clojure.gdx.scene2d.actor.toggle-visible :refer [toggle-visible!]]))

(defn f
  [{:keys [ctx/stage] :as ctx}]
  (-> stage
      (stage/find-actor "moon.ui.windows.inventory")
      toggle-visible!)
  ctx)
