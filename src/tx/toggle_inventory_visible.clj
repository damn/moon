(ns tx.toggle-inventory-visible
  (:require [clojure.gdx.actor.set-visible :as set-visible]
            [clojure.gdx.actor.visible? :as visible?]
            [clojure.gdx.group.find-actor :as find-actor]))

(defn f [{:keys [ctx/stage]}]
  (let [inventory (find-actor/f (:stage/root stage) "moon.ui.windows.inventory")]
    (set-visible/f inventory (not (visible?/f inventory)))
    nil))
