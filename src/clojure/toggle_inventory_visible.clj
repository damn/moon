(ns clojure.toggle-inventory-visible
  (:require [clojure.group :as group]
            [clojure.actor :as actor]))

(defn f [{:keys [ctx/stage]}]
  (let [inventory (group/find-actor (:stage/root stage) "moon.ui.windows.inventory")]
    (actor/set-visible! inventory (not (actor/visible? inventory)))
    nil))
