(ns moon.ui-actors.windows
  (:require [clojure.scene2d.actor :as actor]))

(defn create
  [ctx actors]
  (actor/create
   {:type :ui/group
    :group/actors (map (fn [[f & params]] (apply f ctx params)) actors)
    :actor/name "moon.ui.windows"}))
