(ns clojure.toggle-inventory-visible
  (:require
            [clojure.set-visible]
            [clojure.visible] [clojure.group :as group]))

(defn f [{:keys [ctx/stage]}]
  (let [inventory (group/find-actor (:stage/root stage) "moon.ui.windows.inventory")]
    (clojure.set-visible/f inventory (not (clojure.visible/f inventory)))
    nil))
