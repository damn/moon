(ns moon.ui-actors.windows
  (:require [moon.ui :as ui]))

(defn create
  [ctx actors]
  (ui/create
   {:type :ui/group
    :group/actors (map (fn [[f & params]] (apply f ctx params)) actors)
    :actor/name "moon.ui.windows"}))
