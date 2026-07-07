(ns clojure.windows
  (:require [clojure.group :as group]
            [clojure.actor :as actor]))

(defn create [ctx actor-fns]
  (let [group (group/new)]
    (run! #(group/add-actor! group %) (for [f actor-fns] (f ctx)))
    (doto group
      (actor/set-name! "moon.ui.windows"))))
