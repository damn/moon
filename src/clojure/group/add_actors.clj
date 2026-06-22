(ns clojure.group.add-actors
  (:require [clojure.group.add-actor :refer [add-actor!]]))

(defn add-actors! [group actors]
  (run! #(add-actor! group %) actors))
