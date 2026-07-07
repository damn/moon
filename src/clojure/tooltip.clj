(ns clojure.tooltip
  (:require [clojure.pprint :refer [pprint]]))

(defn f ^String [property]
  (binding [*print-level* 2]
    (with-out-str
     (pprint property))))
