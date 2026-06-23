(ns group.add-actors
  (:require [group.add-actor :refer [add-actor!]]))

(defn add-actors! [group actors]
  (run! #(add-actor! group %) actors))
