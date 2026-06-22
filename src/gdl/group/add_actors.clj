(ns gdl.group.add-actors
  (:require [gdl.group.add-actor :refer [add-actor!]]))

(defn add-actors! [group actors]
  (run! #(add-actor! group %) actors))
