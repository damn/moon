(ns moon.reaction-txs.toggle-inventory-visible
  (:require [moon.ui.actor :as actor]
            [moon.ui.group :as group])
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn do!
  [{:keys [ctx/stage] :as ctx}]
  (-> stage
      Stage/.getRoot
      (group/find-actor "moon.ui.windows")
      (group/find-actor "moon.ui.windows.inventory")
      actor/toggle-visible!)
  ctx)
