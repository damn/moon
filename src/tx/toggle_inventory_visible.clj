(ns tx.toggle-inventory-visible
  (:require [clojure.gdx :as gdx]))

(defn f
  [{:keys [ctx/stage] :as ctx}]
  (let [inventory (gdx/find-actor (:stage/root stage) "moon.ui.windows.inventory")]
    (gdx/set-visible! inventory (not (gdx/visible? inventory))))
  nil)
