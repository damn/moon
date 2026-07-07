(ns clojure.windows
  (:require
            [clojure.set-name] [clojure.group :as group]))

(defn create [ctx actor-fns]
  (let [group (group/new)]
    (run! #(group/add-actor! group %) (for [f actor-fns] (f ctx)))
    (doto group
      (clojure.set-name/f "moon.ui.windows"))))
