(ns tx.toggle-inventory-visible
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.group.find-actor :as find-actor]))

(defn f [{:keys [ctx/stage]}]
  (let [inventory (find-actor/f (:stage/root stage) "moon.ui.windows.inventory")]
    (actor/set-visible! inventory (not (actor/visible? inventory)))
    nil))
