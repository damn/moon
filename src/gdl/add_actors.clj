(ns gdl.add-actors
  (:require [gdl.add-actor :refer [add-actor!]]))

(defn add-actors! [group actors]
  (run! #(add-actor! group %) actors))
