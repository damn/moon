(ns gdl.impl
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn load! []
  (->> "gdl.impl.edn"
       io/resource
       slurp
       edn/read-string
       (run! require)))
