(ns moon.reaction-txs.toggle-inventory-visible
  (:require [moon.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d Group
                                            Stage)))

(defn do!
  [{:keys [ctx/stage] :as ctx}]
  (-> stage
      Stage/.getRoot
      (Group/.findActor "moon.ui.windows")
      (Group/.findActor "moon.ui.windows.inventory")
      actor/toggle-visible!)
  ctx)
