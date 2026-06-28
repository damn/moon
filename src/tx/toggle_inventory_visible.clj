(ns tx.toggle-inventory-visible
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group)))

(defn f
  [{:keys [ctx/stage] :as ctx}]
  (let [inventory (Group/.findActor (:stage/root stage) "moon.ui.windows.inventory")]
    (Actor/.setVisible inventory (not (Actor/.isVisible inventory))))
  nil)
