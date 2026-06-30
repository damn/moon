(ns tx.toggle-inventory-visible
  (:require [clojure.gdx :as gdx]))

(defn f
  [{:keys [ctx/stage] :as ctx}]
  (let [inventory (gdx/find-actor (:stage/root stage) "moon.ui.windows.inventory")]
    (com.badlogic.gdx.scenes.scene2d.Actor/.setVisible inventory (not (com.badlogic.gdx.scenes.scene2d.Actor/.isVisible inventory))))
  nil)
