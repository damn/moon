(ns scene2d.group.add-actors
  (:require [scene2d.group.add-actor :refer [add-actor!]]))

(defn add-actors! [group actors]
  (run! #(add-actor! group %) actors))
