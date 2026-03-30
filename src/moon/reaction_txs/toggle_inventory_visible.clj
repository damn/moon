(ns moon.reaction-txs.toggle-inventory-visible
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.group :as group]
            [moon.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn do!
  [{:keys [ctx/stage] :as ctx}]
  (-> stage
      Stage/.getRoot
      (group/find-actor "moon.ui.windows")
      (group/find-actor "moon.ui.windows.inventory")
      actor/toggle-visible!)
  ctx)
