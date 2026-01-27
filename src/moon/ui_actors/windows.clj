(ns moon.ui-actors.windows
  (:require [moon.ui :as ui]))

(defn create
  [ctx actors]
  (doto (ui/actor
         {:type :ui/group
          :group/actors (map (fn [[f & params]] (apply f ctx params)) actors)})
    (.setName "moon.ui.windows")))
