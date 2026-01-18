(ns moon.ui-actors.windows
  (:require [moon.ui :as ui]))

(defn create
  [ctx actors]
  (ui/actor
   {:type :ui/group
    :actor/name "moon.ui.windows"
    :group/actors (map (fn [[f & params]] (apply f ctx params)) actors)}))
