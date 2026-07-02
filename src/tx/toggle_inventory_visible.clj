(ns tx.toggle-inventory-visible
  (:require [clojure.gdx.actor.set-visible :as set-visible]
            [clojure.gdx.actor.visible? :as visible?])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn f
  [{:keys [ctx/stage] :as ctx}]
  (let [inventory (Group/.findActor (:stage/root stage) "moon.ui.windows.inventory")]
    (set-visible/f inventory (not (visible?/f inventory))))
  nil)
