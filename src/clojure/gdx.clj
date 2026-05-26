(ns clojure.gdx
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn load! []
  (->> "clojure.gdx.edn"
       io/resource
       slurp
       edn/read-string
       (run! require)))
