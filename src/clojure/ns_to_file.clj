(ns clojure.ns-to-file
  (:require [clojure.string :as str]))

(defn f [ns-string]
  (str "src/"
       (-> ns-string
           (str/replace "." "/")
           (str/replace "-" "_"))
       ".clj"))
